package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.PacienteNaoEncontradoException;
import com.fiap.mindcare_diary.exceptions.RelatorioSemanalNaoExistenteException;
import com.fiap.mindcare_diary.mappers.RelatorioSemanalMapper;
import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.RegistroDiario;
import com.fiap.mindcare_diary.models.RelatorioSemanal;

import com.fiap.mindcare_diary.models.dtos.RelatorioSemanalDTO;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.RegistroDiarioRepository;
import com.fiap.mindcare_diary.repositories.RelatorioSemanalRepository;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class RelatorioSemanalService {

    private final RelatorioSemanalRepository relatorioSemanalRepository;

    private final RegistroDiarioRepository registroDiarioRepository;

    private final PacienteRepository pacienteRepository;

    private final ChatClient chatClient;



    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final static String DELIMITER = "^";

    private Random random = new Random();

    public RelatorioSemanalService(RelatorioSemanalRepository relatorioSemanalRepository,
                                   RegistroDiarioRepository registroDiarioRepository,
                                   PacienteRepository pacienteRepository,
                                   ChatClient.Builder builder) {
        this.relatorioSemanalRepository = relatorioSemanalRepository;
        this.registroDiarioRepository = registroDiarioRepository;
        this.pacienteRepository = pacienteRepository;
        this.chatClient = builder.build();
    }

    public RelatorioSemanalDTO gerarRelatorioSemanal(String pacienteNomeUsuario) {

        Integer numero = 100000 + random.nextInt(900000);

        LocalDateTime fim = LocalDateTime.now();
        LocalDateTime inicio = fim.minusDays(7);
        Optional<Paciente> optionalPaciente = this.pacienteRepository.findByNomeUsuario(pacienteNomeUsuario);
        if(optionalPaciente.isPresent()) {
            Paciente paciente = optionalPaciente.get();
            if (paciente.getEncerradaEm() != null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.GONE, "Conta encerrada.");
            List<RegistroDiario> registrosDiarios = this.registroDiarioRepository.carregarUltimosRegistrosDiarios(pacienteNomeUsuario).stream()
                    .filter(r -> r.getDataHoraCriacao() != null && !r.getDataHoraCriacao().isBefore(inicio) && !r.getDataHoraCriacao().isAfter(fim)).toList();
            String relatorioIA = "";
            if(registrosDiarios != null && !registrosDiarios.isEmpty()) {
                relatorioIA = gerarRelatorioIA(registrosDiarios);
            }
            RelatorioSemanal relatorioSemanal = new RelatorioSemanal();
            relatorioSemanal.setRelatorioIA(relatorioIA);
            relatorioSemanal.setDataHoraCriacao(fim);
            relatorioSemanal.setPaciente(paciente);
            relatorioSemanal.setFaixaDeDatas(formatter.format(inicio) + DELIMITER + formatter.format(fim));
            relatorioSemanal.setObservacoes("");
            relatorioSemanal.setRecomendacoes("");
            int countPontosPositivos = 0;
            int countDificuldadesDesafios = 0;
            for (RegistroDiario registro : registrosDiarios) {
                if (registro.getPontosPositivos() != null && !registro.getPontosPositivos().isBlank()) countPontosPositivos++;
                else if (registro.getDificuldadesDesafios() != null && !registro.getDificuldadesDesafios().isBlank()) countDificuldadesDesafios++;
            }
            relatorioSemanal.setTotalPositivos(countPontosPositivos);
            relatorioSemanal.setTotalNegativos(countDificuldadesDesafios);
            relatorioSemanal.setRegistrosDiarios(registrosDiarios);
            relatorioSemanal.setNumero(numero.toString());
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
            Optional<RelatorioSemanal> relatorioSemanalOptional = this.relatorioSemanalRepository.findByPacienteAndNumero(optionalPaciente.get(), relatorioSemanalDTO.getNumero());
            if(relatorioSemanalOptional.isPresent()) {
                RelatorioSemanal relatorioSemanal = relatorioSemanalOptional.get();
                relatorioSemanal.setRecomendacoes(relatorioSemanalDTO.getRecomendacoes());
                relatorioSemanal.setObservacoes(relatorioSemanalDTO.getObservacoes());
                this.relatorioSemanalRepository.save(relatorioSemanal);
            } else {
                throw new RelatorioSemanalNaoExistenteException("Relatório semanal não encontrado.");
            }
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }

    }

    private String gerarRelatorioIA(List<RegistroDiario> registros) {

        String question = "Você é um psicólogo/psiquiatra experiente especializado em análise de registros de saúde mental. " +
                "Com base em TODOS os registros diários do paciente dos últimos 7 dias, " +
                "gere um relatório clínico objetivo e acolhedor contendo:\n\n" +
                "1. Resumo geral da semana.\n" +
                "2. Humor predominant e sua evolução ao longo dos dias.\n" +
                "3. Principais emoções identificadas.\n" +
                "4. Possíveis gatilhos emocionais ou situações recorrentes que impactaram o bem-estar.\n" +
                "5. Estratégias de enfrentamento ou recursos positivos mencionados pelo paciente.\n" +
                "6. Sinais de melhora, estabilidade ou agravamento emocional.\n" +
                "7. Temas recorrentes observados nos relatos.\n" +
                "8. Recomendações e pontos de atenção para o profissional responsável.\n\n" +
                "Utilize linguagem profissional, empática e baseada exclusivamente nas informações fornecidas pelos registros. " +
                "Não realize diagnósticos médicos ou psiquiátricos. " +
                "Caso não existam informações suficientes para alguma conclusão, informe explicitamente essa limitação.";

        // Context exists only in memory; never duplicate patient narratives in a vector table or logs.
        String context = registros.stream().map(r -> "Data: " + r.getDataHoraCriacao()
                + "; humor: " + r.getNivelHumor() + "; relato: " + java.util.Objects.toString(r.getTextoConfirmado(), "")
                + "; pontos positivos: " + java.util.Objects.toString(r.getPontosPositivos(), "")
                + "; dificuldades: " + java.util.Objects.toString(r.getDificuldadesDesafios(), ""))
                .collect(java.util.stream.Collectors.joining("\n"));
        if (context.length() > 120000) throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY, "Volume de registros muito grande para gerar o relatório em uma chamada.");

        String promptText = promptText(context, question);

        return chatClient.prompt(new Prompt(promptText))
                .user(question)
                .call()
                .content();
    }

    public static String promptText(String context, String question) {
        return String.format("""
        Baseando-se no seguinte contexto, responda à pergunta.
        Se não puder responder com base no contexto, diga "Não tenho informação suficiente."

        Contexto: %s

        Pergunta: %s
        """, context, question);
    }
}
