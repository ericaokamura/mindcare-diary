package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.PacienteNaoEncontradoException;
import com.fiap.mindcare_diary.exceptions.ProfissionalNaoEncontradoException;
import com.fiap.mindcare_diary.mappers.PacienteMapper;
import com.fiap.mindcare_diary.mappers.ProfissionalMapper;
import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.models.enums.EstadoPaciente;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    public void salvarCadastroPaciente(PacienteDTO pacienteDTO) {
        Paciente paciente = PacienteMapper.convertDTOToModel(pacienteDTO);
        paciente.setAtivo(true);
        paciente.setDataHoraAtivacao(LocalDateTime.now());
        this.pacienteRepository.save(paciente);
    }

    public PacienteDTO retornarCadastroPaciente(String nomeUsuario) {
        Optional<Paciente> optionalPaciente = pacienteRepository.findByNomeUsuario(nomeUsuario);
        if(optionalPaciente.isPresent()) {
            return PacienteMapper.convertModelToDTO(optionalPaciente.get());
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }
    }

    public PacienteDTO selecionarProfissional(Long idProfissional, Long idPaciente) {
        Optional<Paciente> optionalPaciente = pacienteRepository.findById(idPaciente);
        if(optionalPaciente.isPresent()) {
            Paciente paciente = optionalPaciente.get();
            Optional<Profissional> optionalProfissional = profissionalRepository.findById(idProfissional);
            if(optionalProfissional.isPresent()) {
                Profissional profissional = optionalProfissional.get();
                paciente.setProfissional(profissional);
                pacienteRepository.save(paciente);
                return PacienteMapper.convertModelToDTO(paciente);
            } else {
                throw new ProfissionalNaoEncontradoException("Profissional não encontrado.");
            }
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }
    }

    public PacienteDTO atualizarEstadoPaciente(Long idProfissional, Long idPaciente, String estadoPaciente) {
        Optional<Paciente> optionalPaciente = pacienteRepository.findById(idPaciente);
        if(optionalPaciente.isPresent()) {
            Paciente paciente = optionalPaciente.get();
            Optional<Profissional> optionalProfissional = profissionalRepository.findById(idProfissional);
            if(optionalProfissional.isPresent()) {
                paciente.setEstadoPaciente(EstadoPaciente.valueOf(estadoPaciente));
                pacienteRepository.save(paciente);
                return PacienteMapper.convertModelToDTO(paciente);
            } else {
                throw new ProfissionalNaoEncontradoException("Profissional não encontrado.");
            }
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }

    }
}
