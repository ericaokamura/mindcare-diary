package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.ProfissionalJaCadastradoException;
import com.fiap.mindcare_diary.exceptions.ProfissionalNaoEncontradoException;
import com.fiap.mindcare_diary.mappers.PacienteMapper;
import com.fiap.mindcare_diary.mappers.ProfissionalMapper;
import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProfissionalService {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    public void salvarCadastroProfissional(ProfissionalDTO profissionalDTO) {
        Optional<Profissional> optionalProfissional = profissionalRepository.findByNomeUsuario(profissionalDTO.getNomeUsuario());
        if(optionalProfissional.isPresent()) {
            throw new ProfissionalJaCadastradoException("");
        } else {
            Profissional profissional = ProfissionalMapper.convertDTOToModel(profissionalDTO);
            profissional.setDataHoraAtivacao(LocalDateTime.now());
            this.profissionalRepository.save(profissional);
        }
    }

    public List<ProfissionalDTO> retornarProfissionais() {
        return ProfissionalMapper.convertModelListToDTOList(this.profissionalRepository.findAll());
    }

    public List<PacienteDTO> retornarPacientesPorProfissional(String nomeUsuario) {
        Optional<Profissional> optionalProfissional = profissionalRepository.findByNomeUsuario(nomeUsuario);
        if(optionalProfissional.isPresent()) {
            return PacienteMapper.convertModelListToDTOList((optionalProfissional.get().getPacientes()));
        } else {
            throw new ProfissionalNaoEncontradoException("Profissional não encontrado.");
        }
    }

    public ProfissionalDTO retornarProfissional(String nomeUsuario) {
        Optional<Profissional> optionalProfissional = profissionalRepository.findByNomeUsuario(nomeUsuario);
        if(optionalProfissional.isPresent()) {
            return ProfissionalMapper.convertModelToDTO(optionalProfissional.get());
        } else {
            throw new ProfissionalNaoEncontradoException("Profissional não encontrado.");
        }

    }

    public List<ProfissionalDTO> buscarProfissionaisProTipo(String tipoProfissional) {
        List<ProfissionalDTO>  profissionais = new ArrayList<>();
        this.profissionalRepository.findAll().forEach(profissional -> {
            if(profissional.getTipoProfissional().toString().equals(tipoProfissional)) {
                profissionais.add(ProfissionalMapper.convertModelToDTO(profissional));
            }
        });
        return profissionais;

    }
}
