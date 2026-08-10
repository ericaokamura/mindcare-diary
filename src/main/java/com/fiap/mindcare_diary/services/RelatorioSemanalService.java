package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.PacienteNaoEncontradoException;
import com.fiap.mindcare_diary.exceptions.RelatorioSemanalNaoExistenteException;
import com.fiap.mindcare_diary.mappers.RelatorioSemanalMapper;
import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.RegistroDiario;
import com.fiap.mindcare_diary.models.RelatorioSemanal;
import com.fiap.mindcare_diary.models.dtos.RegistroDiarioDTO;
import com.fiap.mindcare_diary.models.dtos.RelatorioSemanalDTO;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.RegistroDiarioRepository;
import com.fiap.mindcare_diary.repositories.RelatorioSemanalRepository;
import com.fiap.mindcare_diary.utils.DataLoader;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hibernate.internal.util.collections.ArrayHelper.forEach;

@Service
public class RelatorioSemanalService {

    private final RelatorioSemanalRepository relatorioSemanalRepository;

    private final RegistroDiarioRepository registroDiarioRepository;

    private final PacienteRepository pacienteRepository;

    private final ChatClient chatClient;

    private final PgVectorStore pgVectorStore;

    private final DataLoader dataLoader;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final static String DELIMITER = "^";

    public RelatorioSemanalService(RelatorioSemanalRepository relatorioSemanalRepository, RegistroDiarioRepository registroDiarioRepository, PacienteRepository pacienteRepository, ChatClient.Builder builder, PgVectorStore pgVectorStore, DataLoader dataLoader) {
        this.relatorioSemanalRepository = relatorioSemanalRepository;
        this.registroDiarioRepository = registroDiarioRepository;
        this.pacienteRepository = pacienteRepository;
        this.chatClient = builder.defaultAdvisors(new QuestionAnswerAdvisor(pgVectorStore)).build();
        this.pgVectorStore = pgVectorStore;
        this.dataLoader = dataLoader;
    }

    public RelatorioSemanalDTO gerarRelatorioSemanal(String nomeUsuario) {

        //String relatorioIA = this.gerarRelatorioIA(nomeUsuario);

        Optional<Paciente> optionalPaciente = this.pacienteRepository.findByNomeUsuario(nomeUsuario);
        if(optionalPaciente.isPresent()) {
            Paciente paciente = optionalPaciente.get();
            RelatorioSemanal relatorioSemanal = new RelatorioSemanal();
            relatorioSemanal.setRelatorioIA("Relatório gerado por IA");
            relatorioSemanal.setDataHoraCriacao(LocalDateTime.now());
            relatorioSemanal.setPaciente(paciente);
            String faixaDeDatas = formatter.format(LocalDate.now().minusDays(7)) + DELIMITER + formatter.format(LocalDate.now());
            relatorioSemanal.setFaixaDeDatas(faixaDeDatas);
            relatorioSemanal.setObservacoes("");
            relatorioSemanal.setRecomendacoes("");
            List<RegistroDiario> todosRegistrosDiarios = this.registroDiarioRepository.findAllByPaciente(paciente);
            int countPontosPositivos = 0;
            int countDificuldadesDesafios = 0;
            List<RegistroDiario> registrosDiarios = new ArrayList<>();
            for(RegistroDiario registroDiario : todosRegistrosDiarios) {
                if(registroDiario.getDataHoraCriacao().isAfter(LocalDateTime.now().minusDays(7)) && registroDiario.getDataHoraCriacao().isBefore(LocalDateTime.now())) {
                    registrosDiarios.add(registroDiario);
                    if(registroDiario.getPontosPositivos() != null && !registroDiario.getPontosPositivos().isBlank()){
                        countPontosPositivos++;
                    } else if(registroDiario.getDificuldadesDesafios() != null && !registroDiario.getDificuldadesDesafios().isBlank()){
                        countDificuldadesDesafios++;
                    }
                }
            }
            relatorioSemanal.setTotalPositivos(countPontosPositivos);
            relatorioSemanal.setTotalNegativos(countDificuldadesDesafios);
            relatorioSemanal.setRegistrosDiarios(registrosDiarios);
            relatorioSemanalRepository.save(relatorioSemanal);
            return RelatorioSemanalMapper.convertModelToDTO(relatorioSemanal);
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }
    }

    public List<RelatorioSemanalDTO> retornarRelatoriosSemanaisPorPaciente(String nomeUsuario) {
        Optional<Paciente> optionalPaciente = this.pacienteRepository.findByNomeUsuario(nomeUsuario);
        if(optionalPaciente.isPresent()) {
            return RelatorioSemanalMapper.convertModelListToDTOList(this.relatorioSemanalRepository.findAllByPaciente(optionalPaciente.get()));
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }
    }

    public void atualizarRelatorioSemanal(RelatorioSemanalDTO relatorioSemanalDTO) {
        Optional<Paciente> optionalPaciente = this.pacienteRepository.findByNomeUsuario(relatorioSemanalDTO.getPaciente().getNomeUsuario());
        if(optionalPaciente.isPresent()) {
            Optional<RelatorioSemanal> relatorioSemanalOptional = this.relatorioSemanalRepository.findByPacienteAndFaixaDeDatas(optionalPaciente.get(), relatorioSemanalDTO.getFaixaDeDatas());
            if(relatorioSemanalOptional.isPresent()) {
                RelatorioSemanal relatorioSemanal = relatorioSemanalOptional.get();
                relatorioSemanal.setRecomendacoes(relatorioSemanalDTO.getRecomendacoes());
                this.relatorioSemanalRepository.save(relatorioSemanal);
            } else {
                throw new RelatorioSemanalNaoExistenteException("Relatório semanal não encontrado.");
            }
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }

    }

    private String gerarRelatorioIA(String nomeUsuario) {

        String question =
                "Você é um psicólogo/psiquiatra experiente especializado em análise de registros de saúde mental. " +
                        "Com base nos últimos 7 registros diários do paciente cadastrado com o usuário '" + nomeUsuario + "', " +
                        "gere um relatório clínico objetivo e acolhedor contendo:\n\n" +
                        "1. Resumo geral da semana.\n" +
                        "2. Humor predominante e sua evolução ao longo dos dias.\n" +
                        "3. Principais emoções identificadas.\n" +
                        "4. Possíveis gatilhos emocionais ou situações recorrentes que impactaram o bem-estar.\n" +
                        "5. Estratégias de enfrentamento ou recursos positivos mencionados pelo paciente.\n" +
                        "6. Sinais de melhora, estabilidade ou agravamento emocional.\n" +
                        "7. Temas recorrentes observados nos relatos.\n" +
                        "8. Recomendações e pontos de atenção para o profissional responsável.\n\n" +
                        "Utilize linguagem profissional, empática e baseada exclusivamente nas informações fornecidas pelos registros. " +
                        "Não realize diagnósticos médicos ou psiquiátricos. " +
                        "Caso não existam informações suficientes para alguma conclusão, informe explicitamente essa limitação.";

        dataLoader.loadRelatoriosSemanaisIntoVectorStore(nomeUsuario);

        List<Document> relevantDocs = pgVectorStore.similaritySearch(question);

        if (relevantDocs.isEmpty()) {
            return "⚠️ Nenhum documento relevante foi encontrado no vetor. Não é possível responder à pergunta.";
        }

        System.out.println("📄 Documentos retornados pelo pgVector:");
        relevantDocs.forEach(doc -> System.out.println(doc.getFormattedContent()));

        String context = relevantDocs.stream()
                .map(Document::getFormattedContent)
                .reduce("", (a, b) -> a + "\n" + b);

        String promptText = String.format("""
        Baseando-se no seguinte contexto, responda à pergunta.
        Se não puder responder com base no contexto, diga "Não tenho informação suficiente."

        Contexto: %s

        Pergunta: %s
        """, context, question);

        return chatClient.prompt(new Prompt(promptText))
                .user(question)
                .call()
                .content();
    }
}
