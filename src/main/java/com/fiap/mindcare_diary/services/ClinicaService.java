package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.ClinicaNaoExistenteException;
import com.fiap.mindcare_diary.mappers.ClinicaMapper;
import com.fiap.mindcare_diary.mappers.ConsultaMapper;
import com.fiap.mindcare_diary.mappers.PacienteMapper;
import com.fiap.mindcare_diary.mappers.ProfissionalMapper;
import com.fiap.mindcare_diary.models.Clinica;
import com.fiap.mindcare_diary.models.Consulta;
import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.dtos.ClinicaDTO;
import com.fiap.mindcare_diary.models.dtos.ConsultaDTO;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.models.enums.PlanoAssinatura;
import com.fiap.mindcare_diary.repositories.ClinicaRepository;
import com.fiap.mindcare_diary.repositories.ConsultaRepository;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClinicaService {

    @Autowired
    private ClinicaRepository clinicaRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ClinicaDTO retornarClinicaPorCnpj(String clinicaCnpj) {
        Optional<Clinica> clinicaOptional = clinicaRepository.findByCnpj(clinicaCnpj);
        if(clinicaOptional.isEmpty()) {
            throw new ClinicaNaoExistenteException("Clínica não existente.");
        }
        return ClinicaMapper.convertModelToDTO(clinicaOptional.get());
    }

    public List<ProfissionalDTO> retornarProfissionaisPorClinicaCnpj(String clinicaCnpj) {
        Optional<Clinica> clinicaOptional = clinicaRepository.findByCnpj(clinicaCnpj);
        if(clinicaOptional.isEmpty()) {
            throw new ClinicaNaoExistenteException("Clínica não existente.");
        }
        List<Profissional> profissionals = profissionalRepository.findByClinica(clinicaOptional.get());
        return ProfissionalMapper.convertModelListToDTOList(profissionals);
    }

    public List<PacienteDTO> retornarPacientesPorClinicaCnpj(String clinicaCnpj) {
        Optional<Clinica> clinicaOptional = clinicaRepository.findByCnpj(clinicaCnpj);
        if(clinicaOptional.isEmpty()) {
            throw new ClinicaNaoExistenteException("Clínica não existente.");
        }
        return PacienteMapper.convertModelListToDTOList(clinicaOptional.get().getPacientes());
    }

    public List<ConsultaDTO> retornarConsultasPorClinicaCnpj(String clinicaCnpj) {
        Optional<Clinica> clinicaOptional = clinicaRepository.findByCnpj(clinicaCnpj);
        if(clinicaOptional.isEmpty()) {
            throw new ClinicaNaoExistenteException("Clínica não existente.");
        }
        return ConsultaMapper.convertModelListToDTOList(clinicaOptional.get().getConsultas());
    }

    public Double retornarFaturamentoPorClinicaCnpjPorAnoMes(String clinicaCnpj, Long ano, Long mes) {
        Optional<Clinica> clinicaOptional = clinicaRepository.findByCnpj(clinicaCnpj);
        if(clinicaOptional.isEmpty()) {
            throw new ClinicaNaoExistenteException("Clínica não existente.");
        }
        return calcularFaturamentoPorAnoMes(clinicaOptional.get(), ano, mes);
    }

    private Double calcularFaturamentoPorAnoMes(Clinica clinica, Long ano, Long mes) {
        Double faturamento = 0.0;
        List<Consulta> consultasPorAnoMes = clinica.getConsultas().stream().filter(
                                                        consulta ->
                                                        consulta.getDataHoraConsulta().getMonthValue() == mes &&
                                                        consulta.getDataHoraConsulta().getYear() == ano &&
                                                        consulta.isAtendida() && !consulta.isCancelada()).collect(Collectors.toList());

        for(Consulta consulta : consultasPorAnoMes) {
            faturamento += consulta.getValorConsulta();
        }
        return faturamento;
    }

    @Transactional
    public void cadastrarClinica(ClinicaDTO clinicaDTO) {
        Clinica clinica = ClinicaMapper.convertDTOToModel(clinicaDTO);
        clinicaRepository.save(clinica);
        List<Profissional> profissionals = clinicaDTO.getProfissionais().stream().map(profissionalDTO -> ProfissionalMapper.convertDTOToModel(profissionalDTO)).collect(Collectors.toList());
        profissionals.forEach(profissional -> {
            profissional.setClinica(clinica);
            profissional.setSenha(passwordEncoder.encode(profissional.getSenha()));
            profissional.setDataHoraAtivacao(LocalDateTime.now());
            profissionalRepository.save(profissional);
            clinica.getProfissionais().add(profissional);
            clinicaRepository.save(clinica);
        });
    }

    public Double retornarReceitaAposDescontosPorClinicaCnpjPorAnoMes(String clinicaCnpj, Long ano, Long mes) {
        Optional<Clinica> clinicaOptional = clinicaRepository.findByCnpj(clinicaCnpj);
        if(clinicaOptional.isEmpty()) {
            throw new ClinicaNaoExistenteException("Clínica não existente.");
        }
        return calcularReceitaAposDescontosPorAnoMes(clinicaOptional.get(), ano, mes);
    }

    private Double calcularReceitaAposDescontosPorAnoMes(Clinica clinica, Long ano, Long mes) {
        Double faturamento = 0.0;
        List<Consulta> consultasPorAnoMes = clinica.getConsultas().stream().filter(
                consulta ->
                        consulta.getDataHoraConsulta().getMonthValue() == mes &&
                                consulta.getDataHoraConsulta().getYear() == ano &&
                                consulta.isAtendida() && !consulta.isCancelada()).collect(Collectors.toList());

        for(Consulta consulta : consultasPorAnoMes) {
            faturamento += consulta.getValorConsulta();
        }
        Double taxaComissao = clinica.getTaxaComissao();
        return faturamento * (1-taxaComissao);
    }

    public void atualizarDadosClinica(String clinicaCnpj, ClinicaDTO clinicaDTO) {
        Optional<Clinica> clinicaOptional = clinicaRepository.findByCnpj(clinicaCnpj);
        if(clinicaOptional.isEmpty()) {
            throw new ClinicaNaoExistenteException("Clínica não existente.");
        }
        Clinica clinica = clinicaOptional.get();
        clinica.setNome(clinicaDTO.getNome());
        clinica.setEndereco(clinicaDTO.getEndereco());
        clinica.setPacientes(PacienteMapper.convertDTOListToModelList(clinicaDTO.getPacientes()));
        clinica.setProfissionais(ProfissionalMapper.convertDTOListToModelList(clinicaDTO.getProfissionais()));
        clinica.setConsultas(ConsultaMapper.convertDTOListToModelList(clinicaDTO.getConsultas()));
        clinica.setPlanoAssinatura(PlanoAssinatura.valueOf(clinicaDTO.getPlanoAssinatura()));
        clinica.setTaxaComissao(clinicaDTO.getTaxaComissao());
        clinicaRepository.save(clinica);
    }

    public List<ClinicaDTO> retornarClinicas() {
        List<ClinicaDTO> clinicas = new ArrayList<>();
        clinicaRepository.findAll().forEach(clinica -> {
            clinicas.add(ClinicaMapper.convertModelToDTO(clinica));
        });
        return clinicas;
    }

    public ClinicaDTO retornarClinicaPorNome(String nome) {
        Optional<Clinica> clinicaOptional = clinicaRepository.findByNome(nome);
        if(clinicaOptional.isEmpty()) {
            throw new ClinicaNaoExistenteException("Clínica não existente.");
        }
        Clinica clinica = clinicaOptional.get();
        profissionalRepository.findByClinica(clinica).forEach(profissional -> {
            clinica.getProfissionais().add(profissional);
        });
        List<Paciente> pacientes = pacienteRepository.findByClinicasContains(clinica);
        List<Consulta> consultas = consultaRepository.findAllByClinica(clinica);
        ClinicaDTO clinicaDTO = ClinicaMapper.convertModelToDTO(clinica);
        clinicaDTO.setConsultas(ConsultaMapper.convertModelListToDTOList(consultas));
        clinicaDTO.setPacientes(PacienteMapper.convertModelListToDTOList(pacientes));
        clinicaDTO.setProfissionais(ProfissionalMapper.convertModelListToDTOList(clinica.getProfissionais()));
        return clinicaDTO;

    }
}
