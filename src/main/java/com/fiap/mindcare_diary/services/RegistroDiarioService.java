package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.PacienteNaoEncontradoException;
import com.fiap.mindcare_diary.mappers.RegistroDiarioMapper;
import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.RegistroDiario;
import com.fiap.mindcare_diary.models.dtos.RegistroDiarioDTO;
import com.fiap.mindcare_diary.models.enums.NivelHumor;
import com.fiap.mindcare_diary.repositories.AlertaNivelHumorPacienteRepository;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.RegistroDiarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegistroDiarioService {

    private final RegistroDiarioRepository registroDiarioRepository;

    private final PacienteRepository pacienteRepository;

    private final AlertaNivelHumorPacienteRepository alertaNivelHumorPacienteRepository;

    public RegistroDiarioService(RegistroDiarioRepository registroDiarioRepository, PacienteRepository pacienteRepository, AlertaNivelHumorPacienteRepository alertaNivelHumorPacienteRepository) {
        this.registroDiarioRepository = registroDiarioRepository;
        this.pacienteRepository = pacienteRepository;
        this.alertaNivelHumorPacienteRepository = alertaNivelHumorPacienteRepository;
    }

    public void salvarRegistroDiario(String nomeUsuario, RegistroDiarioDTO registroDiarioDTO) {
        if(NivelHumor.PESSIMO.equals(NivelHumor.valueOf(registroDiarioDTO.getNivelHumor()))) {
            alertaNivelHumorPacienteRepository.registraAlertaNivelHumorPaciente(registroDiarioDTO.getPaciente().getNomeUsuario());
        }
        RegistroDiario registroDiario= RegistroDiarioMapper.convertDTOToModel(registroDiarioDTO);
        Optional<Paciente> optionalPaciente = this.pacienteRepository.findByNomeUsuario(nomeUsuario);
        if(optionalPaciente.isPresent()) {
            registroDiario.setPaciente(optionalPaciente.get());
            registroDiario.setIdRequisicao(UUID.randomUUID());
            registroDiario.setDataHoraCriacao(LocalDateTime.now());
            this.registroDiarioRepository.saveAndFlush(registroDiario);
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }
    }

    public List<RegistroDiarioDTO> retornarRegistrosDiarios(String nomeUsuario) {
        Optional<Paciente> optionalPaciente = this.pacienteRepository.findByNomeUsuario(nomeUsuario);
        if(optionalPaciente.isPresent()) {
            return RegistroDiarioMapper.convertModelListToDTOList(this.registroDiarioRepository.carregarTodosRegistrosDiarios(optionalPaciente.get().getNomeUsuario()));
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }
    }
}
