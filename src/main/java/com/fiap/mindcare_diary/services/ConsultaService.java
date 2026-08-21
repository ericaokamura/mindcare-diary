package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.ConsultaNaoEncontradaException;
import com.fiap.mindcare_diary.exceptions.PacienteNaoEncontradoException;
import com.fiap.mindcare_diary.exceptions.ProfissionalNaoEncontradoException;
import com.fiap.mindcare_diary.mappers.ClinicaMapper;
import com.fiap.mindcare_diary.mappers.ConsultaMapper;
import com.fiap.mindcare_diary.mappers.PacienteMapper;
import com.fiap.mindcare_diary.mappers.ProfissionalMapper;
import com.fiap.mindcare_diary.models.Consulta;
import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.dtos.ConsultaDTO;
import com.fiap.mindcare_diary.repositories.ConsultaRepository;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    public List<ConsultaDTO> retornarConsultasPorPaciente(String nomeUsuario) {
        Optional<Paciente> optionalPaciente = pacienteRepository.findByNomeUsuario(nomeUsuario);
        if(optionalPaciente.isPresent()) {
            List<Consulta> consultas = consultaRepository.findAllByPaciente(optionalPaciente.get());
            return ConsultaMapper.convertModelListToDTOList(consultas);
        }
        throw new PacienteNaoEncontradoException("Paciente não encontrado.");
    }

    public List<ConsultaDTO> retornarConsultasPorProfissional(String nomeUsuario) {
        Optional<Profissional> optionalProfissional = profissionalRepository.findByNomeUsuario(nomeUsuario);
        if(optionalProfissional.isPresent()) {
            List<Consulta> consultas = consultaRepository.findAllByProfissional(optionalProfissional.get());
            return ConsultaMapper.convertModelListToDTOList(consultas);
        }
        throw new ProfissionalNaoEncontradoException("Profissional não encontrado.");
    }

    public void atualizarConsulta(Long consultaId, ConsultaDTO consultaDTO) {
        Optional<Consulta> consulta = consultaRepository.findById(consultaId);
        if(consulta.isPresent()) {
            Consulta consultaConsulta = consulta.get();
            consultaConsulta.setAtendida(consultaDTO.isAtendida());
            consultaConsulta.setPaciente(PacienteMapper.convertDTOToModel(consultaDTO.getPaciente()));
            consultaConsulta.setClinica(ClinicaMapper.convertDTOToModel(consultaDTO.getClinica()));
            consultaConsulta.setProfissional(ProfissionalMapper.convertDTOToModel(consultaDTO.getProfissional()));
            consultaConsulta.setCancelada(consultaDTO.isCancelada());
            consultaRepository.save(consultaConsulta);
        }
        throw new ConsultaNaoEncontradaException("Consulta não encontrada.");
    }
}
