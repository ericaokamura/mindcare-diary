package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.ProfissionalJaCadastradoException;
import com.fiap.mindcare_diary.exceptions.ProfissionalNaoEncontradoException;
import com.fiap.mindcare_diary.mappers.ClinicaMapper;
import com.fiap.mindcare_diary.mappers.ConsultaMapper;
import com.fiap.mindcare_diary.mappers.PacienteMapper;
import com.fiap.mindcare_diary.mappers.ProfissionalMapper;
import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.models.enums.Sexo;
import com.fiap.mindcare_diary.models.enums.TipoProfissional;
import com.fiap.mindcare_diary.models.enums.UserRole;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfissionalService {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void salvarCadastroProfissional(ProfissionalDTO profissionalDTO) {
        Optional<Profissional> optionalProfissional = profissionalRepository.findByNomeUsuario(profissionalDTO.getNomeUsuario());
        if(optionalProfissional.isPresent()) {
            throw new ProfissionalJaCadastradoException("");
        } else {
            Profissional profissional = ProfissionalMapper.convertDTOToModel(profissionalDTO);
            profissional.setSenha(passwordEncoder.encode(profissionalDTO.getSenha()));
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
            Profissional profissional = optionalProfissional.get();
            List<Paciente> allPacientes = pacienteRepository.findAll();
            List<Paciente> pacientesPorProfissional = allPacientes.stream().filter(paciente -> paciente.getProfissionais().contains(profissional)).collect(Collectors.toUnmodifiableList());
            return PacienteMapper.convertModelListToDTOList(pacientesPorProfissional);
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

    public List<ProfissionalDTO> buscarProfissionaisPorTipo(String tipoProfissional) {
        List<ProfissionalDTO>  profissionais = new ArrayList<>();
        this.profissionalRepository.findAll().forEach(profissional -> {
            if(profissional.getTipoProfissional().toString().equals(tipoProfissional)) {
                profissionais.add(ProfissionalMapper.convertModelToDTO(profissional));
            }
        });
        return profissionais;

    }

    public void atualizarDadosProfissional(String nomeUsuario, ProfissionalDTO profissionalDTO) {
        Optional<Profissional> optionalProfissional = profissionalRepository.findByNomeUsuario(nomeUsuario);
        if(optionalProfissional.isPresent()) {
            Profissional profissional = optionalProfissional.get();
            profissional.setNomeCompleto(profissionalDTO.getNomeCompleto());
            profissional.setSenha(profissionalDTO.getSenha());
            profissional.setGenero(Sexo.valueOf(profissionalDTO.getGenero()));
            profissional.setAtivo(profissional.isAtivo());
            profissional.setUserRole(UserRole.valueOf(profissionalDTO.getUserRole()));
            profissional.setDataNascimento(LocalDate.parse(profissionalDTO.getDataNascimento()));
            profissional.setDataHoraAtivacao(LocalDateTime.parse(profissionalDTO.getDataHoraAtivacao()));
            profissional.setConsultas(ConsultaMapper.convertDTOListToModelList(profissionalDTO.getConsultas()));
            profissional.setPacientes(PacienteMapper.convertDTOListToModelList(profissionalDTO.getPacientes()));
            profissional.setClinicas(ClinicaMapper.convertDTOListToModelList(profissionalDTO.getClinicas()));
            profissional.setRegistroProfissional(profissional.getRegistroProfissional());
            profissional.setTipoProfissional(TipoProfissional.valueOf(profissionalDTO.getTipoProfissional()));
            profissional.setEspecialidades(profissionalDTO.getEspecialidades());
            profissional.setAbordagens(profissionalDTO.getAbordagens());
            profissional.setModalidades(profissionalDTO.getModalidades());
            profissionalRepository.save(profissional);
        } else {
            throw new ProfissionalNaoEncontradoException("Profissional não encontrado.");
        }

    }
}
