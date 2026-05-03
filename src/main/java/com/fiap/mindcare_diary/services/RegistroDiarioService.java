package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.PacienteNaoEncontradoException;
import com.fiap.mindcare_diary.mappers.RegistroDiarioMapper;
import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.RegistroDiario;
import com.fiap.mindcare_diary.models.dtos.RegistroDiarioDTO;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.RegistroDiarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RegistroDiarioService {

    @Autowired
    private RegistroDiarioRepository registroDiarioRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    public void salvarRegistroDiario(String nomeUsuario, RegistroDiarioDTO registroDiarioDTO) {
        RegistroDiario registroDiario = RegistroDiarioMapper.convertDTOToModel(registroDiarioDTO);
        Optional<Paciente> optionalPaciente = this.pacienteRepository.findByNomeUsuario(nomeUsuario);
        if(optionalPaciente.isPresent()) {
            registroDiario.setPaciente(optionalPaciente.get());
            registroDiario.setDataHoraCriacao(LocalDateTime.now());
            this.registroDiarioRepository.save(registroDiario);
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }
    }

    public List<RegistroDiarioDTO> retornarRegistrosDiarios(String nomeUsuario) {
        Optional<Paciente> optionalPaciente = this.pacienteRepository.findByNomeUsuario(nomeUsuario);
        if(optionalPaciente.isPresent()) {
            return RegistroDiarioMapper.convertModelListToDTOList(this.registroDiarioRepository.findAllByPaciente(optionalPaciente.get()));
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }
    }
}
