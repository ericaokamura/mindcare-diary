package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.PacienteNaoEncontradoException;
import com.fiap.mindcare_diary.mappers.RelatorioSemanalMapper;
import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.dtos.RelatorioSemanalDTO;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.RelatorioSemanalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RelatorioSemanalService {

    @Autowired
    private RelatorioSemanalRepository relatorioSemanalRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    public List<RelatorioSemanalDTO> gerarRelatoriosSemanais(Long idPaciente) {
        Optional<Paciente> optionalPaciente = this.pacienteRepository.findById(idPaciente);
        if(optionalPaciente.isPresent()) {
            return RelatorioSemanalMapper.convertModelListToDTOList(this.relatorioSemanalRepository.findAllByPaciente(optionalPaciente.get()));
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }
    }
}
