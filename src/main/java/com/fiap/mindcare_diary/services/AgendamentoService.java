package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.AgendamentoNaoPodeSerRealizadoException;
import com.fiap.mindcare_diary.exceptions.PacienteNaoEncontradoException;
import com.fiap.mindcare_diary.exceptions.ProfissionalNaoEncontradoException;
import com.fiap.mindcare_diary.exceptions.UsuarioNaoEncontradoException;
import com.fiap.mindcare_diary.mappers.ConsultaMapper;
import com.fiap.mindcare_diary.models.*;
import com.fiap.mindcare_diary.models.dtos.ConsultaDTO;
import com.fiap.mindcare_diary.models.enums.TipoProfissional;
import com.fiap.mindcare_diary.repositories.AgendamentoRepository;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.ProfissionalRepository;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AgendamentoService {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private PushNotificationService pushNotificationService;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private Random random = new Random();

    public void salvarAgendamento(ConsultaDTO consultaDTO) {

        Integer numero = 100000 + random.nextInt(900000);

        LocalDateTime dataHoraAgendamento = LocalDateTime.parse(consultaDTO.getDataHoraConsulta(), formatter);
        LocalDate dataAgendamento = dataHoraAgendamento.toLocalDate();

        if (dataHoraAgendamento.getDayOfWeek().getValue() >= 6) {
            throw new AgendamentoNaoPodeSerRealizadoException("Agendamento não pode ser realizado no fim de semana.");
        }

        if (dataHoraAgendamento.isBefore(LocalDateTime.now())) {
            throw new AgendamentoNaoPodeSerRealizadoException("Agendamento precisa ser realizado em data/horário futuro.");
        }

        if (dataHoraAgendamento.isBefore(LocalDateTime.of(dataAgendamento, LocalTime.of(8, 0))) ||
                dataHoraAgendamento.isAfter(LocalDateTime.of(dataAgendamento, LocalTime.of(18, 0)))) {
            throw new AgendamentoNaoPodeSerRealizadoException("Agendamento precisa ser realizado entre 8h e 18h.");
        }

        Optional<Paciente> optionalPaciente = this.pacienteRepository.findByNomeUsuario(consultaDTO.getPaciente().getNomeUsuario());
        if (optionalPaciente.isPresent()) {
            Paciente paciente = optionalPaciente.get();
            Optional<Profissional> optionalProfissional = this.profissionalRepository.findByNomeUsuario(consultaDTO.getProfissional().getNomeUsuario());
            if (optionalProfissional.isPresent()) {
                Profissional profissional = optionalProfissional.get();
                Optional<Consulta> consultaOptional = this.agendamentoRepository.findByProfissionalAndDataHoraConsulta(profissional, LocalDateTime.parse(consultaDTO.getDataHoraConsulta()));
                if (consultaOptional.isPresent()) {
                    throw new AgendamentoNaoPodeSerRealizadoException("Já existe um agendamento para essa data e hora.");
                }
                paciente.getProfissionais().add(optionalProfissional.get());
                profissional.getPacientes().add(paciente);
                Clinica clinica = profissional.getClinica();
                Consulta consulta = ConsultaMapper.convertDTOToModel(consultaDTO);
                consulta.setProfissional(optionalProfissional.get());
                consulta.setPaciente(paciente);
                consulta.setDataHoraConsulta(LocalDateTime.parse(consultaDTO.getDataHoraConsulta()));
                consulta.setClinica(clinica);
                consulta.setNumber(numero.toString());
                this.agendamentoRepository.save(consulta);
                this.pacienteRepository.save(paciente);
                this.profissionalRepository.save(profissional);
            } else {
                throw new ProfissionalNaoEncontradoException("Profissional não encontrado.");
            }
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }
    }

    public List<RecomendacaoHorario> informarHorariosParaProfissionalEDataInformada(String nomeUsuario, String dataInformada) {

        List<LocalTime> horariosDisponiveis = Arrays.asList(LocalTime.of(8, 0), LocalTime.of(9,0), LocalTime.of(10,0), LocalTime.of(11,0), LocalTime.of(13,0), LocalTime.of(14,0), LocalTime.of(15,0), LocalTime.of(16,0), LocalTime.of(17,0), LocalTime.of(18,0));

        LocalDate dataInformadaConvertida = LocalDate.parse(dataInformada);
        if(dataInformadaConvertida.getDayOfWeek().getValue() >= 6) {
            throw new AgendamentoNaoPodeSerRealizadoException("Data informada não pode ser no fim de semana.");
        }

        Optional<Profissional> optionalProfissional = this.profissionalRepository.findByNomeUsuario(nomeUsuario);
        if(optionalProfissional.isPresent()) {
            Profissional profissional = optionalProfissional.get();
            List<Consulta> consultasFuturasAgendadasParaProfissionalEDataInformada = this.agendamentoRepository.findByProfissionalAndDataHoraConsultaAfter(profissional, LocalDateTime.of(dataInformadaConvertida, LocalTime.of(8, 0)));
            List<LocalDateTime> horariosAgendados = consultasFuturasAgendadasParaProfissionalEDataInformada.stream().map(Consulta::getDataHoraConsulta).collect(Collectors.toList());

            List<RecomendacaoHorario> recomendacoes = new ArrayList<>();
            for(LocalTime horario : horariosDisponiveis) {
                if(!horariosAgendados.contains(LocalDateTime.of(dataInformadaConvertida, horario))) {
                    recomendacoes.add(new RecomendacaoHorario(formatter.format(LocalDateTime.of(dataInformadaConvertida, horario)), profissional.getTipoProfissional().name(), 1.0));
                }
            }
            return recomendacoes;
        } else {
            throw new ProfissionalNaoEncontradoException("Profissional não encontrado.");
        }
    }

    public List<RecomendacaoHorario> recomendarHorariosParaSemanaCorrente(String tipoProfissional) {

        TipoProfissional tipoProfissionalConvertido = TipoProfissional.valueOf(tipoProfissional);

        List<LocalTime> horariosDisponiveis = Arrays.asList(LocalTime.of(8, 0), LocalTime.of(9,0), LocalTime.of(10,0), LocalTime.of(11,0), LocalTime.of(13,0), LocalTime.of(14,0), LocalTime.of(15,0), LocalTime.of(16,0), LocalTime.of(17,0), LocalTime.of(18,0));
        List<Integer> diasDisponiveis = Arrays.asList(1, 2, 3, 4, 5);

        List<Consulta> consultasPassadas = this.agendamentoRepository.findByDataHoraConsultaBefore(LocalDateTime.now()); // consultas passadas
        List<Consulta> consultasPassadasAtendidas = consultasPassadas.stream().filter(consulta -> consulta.isAtendida()).collect(Collectors.toList()); // consultas passadas atendidas
        double scoreConfiabilidadePaciente = 0.0;
        if(consultasPassadas.size() == 0) {
            scoreConfiabilidadePaciente = 0.0;
        } else {
            scoreConfiabilidadePaciente = 0.2 * (consultasPassadasAtendidas.size()/consultasPassadas.size());
        }

        List<RecomendacaoHorario> recomendacoes = new ArrayList<>();
        List<Consulta> consultasFuturasAgendadas = this.agendamentoRepository.findByProfissionalTipoProfissionalAndDataHoraConsultaAfter(tipoProfissionalConvertido, LocalDateTime.now());
        List<LocalDateTime> horariosAgendados = consultasFuturasAgendadas.stream().map(Consulta::getDataHoraConsulta).collect(Collectors.toList());

        for(Integer dayOfWeek : diasDisponiveis) {
            int daysToAdd = dayOfWeek;
            LocalDate dataRecomendada = null;  // data a ser recomendada
            int todayDayOfWeek = LocalDate.now().getDayOfWeek().getValue(); // dia da semana do dia de hoje

            if(todayDayOfWeek == 5) { // se hoje é sexta-feira, recomenda agendamento para segunda-feira seguinte
                dataRecomendada = LocalDate.now().plusDays(3);
            } else if(todayDayOfWeek == 6) { // se hoje é sábado,recomenda agendamento para segunda-feira seguinte
                dataRecomendada = LocalDate.now().plusDays(2);
            } else if(todayDayOfWeek == 7) { // se hoje é domingo, recomenda agendamento para segunda-feira seguinte
                dataRecomendada = LocalDate.now().plusDays(1);
            } else if(todayDayOfWeek + daysToAdd < 6) { // se soma (todayDayOfWeek+daysToAdd) maior que 5, não recomenda esta data, ou seja, quando hoje (dia do agendamento) é dia de semana e não é sexta-feira, só recomenda datas da mesma semana.
                dataRecomendada = LocalDate.now().plusDays(daysToAdd);
            } else {
                continue;
            }

            List<Consulta> consultasMesmoDiaSemana = consultasPassadas.stream().filter(consulta -> consulta.getDataHoraConsulta().getDayOfWeek().getValue() == daysToAdd).collect(Collectors.toList());
            List<Consulta> consultasMesmoDiaSemanaAtendidas = consultasMesmoDiaSemana.stream().filter(consulta -> consulta.isAtendida()).collect(Collectors.toList());
            double scoreDiaSemana = 0.0;
            if(consultasMesmoDiaSemana.size() == 0) {
                scoreDiaSemana = 0.0;
            } else {
                scoreDiaSemana = 0.3 * (consultasMesmoDiaSemanaAtendidas.size()/consultasMesmoDiaSemana.size());
            }

            for(LocalTime horario : horariosDisponiveis) {

                List<Consulta> consultasMesmoHorario = consultasPassadas.stream().filter(consulta -> consulta.getDataHoraConsulta().toLocalTime().equals(horario)).collect(Collectors.toList());
                List<Consulta> consultasMesmoHorarioAtendidas = consultasMesmoHorario.stream().filter(consulta -> consulta.isAtendida()).collect(Collectors.toList());

                double scoreHorario = 0.0;
                if(consultasMesmoHorario.size() == 0) {
                    scoreHorario = 0.0;
                } else {
                    scoreHorario = 0.5 * (consultasMesmoHorarioAtendidas.size()/consultasMesmoHorario.size());
                }

                double score = scoreHorario + scoreDiaSemana + scoreConfiabilidadePaciente;

                if(!horariosAgendados.contains(LocalDateTime.of(dataRecomendada, horario))) {
                    recomendacoes.add(new RecomendacaoHorario(formatter.format(LocalDateTime.of(dataRecomendada, horario)), tipoProfissionalConvertido.name(), score));
                }
            }
        }

        return recomendacoes;
    }

    public List<ConsultaDTO> carregarConsultas(String nomeUsuario) {
        List<ConsultaDTO> consultas = new ArrayList<>();
        pacienteRepository.findByNomeUsuario(nomeUsuario).ifPresent(paciente -> {
            consultas.addAll(paciente.getConsultas().stream().map(ConsultaMapper::convertModelToDTO).collect(Collectors.toList()));
        });
        return consultas;
    }
}
