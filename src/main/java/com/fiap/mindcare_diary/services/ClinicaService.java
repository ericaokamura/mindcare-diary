package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.ClinicaNaoExistenteException;
import com.fiap.mindcare_diary.mappers.ClinicaMapper;
import com.fiap.mindcare_diary.mappers.ConsultaMapper;
import com.fiap.mindcare_diary.mappers.PacienteMapper;
import com.fiap.mindcare_diary.mappers.ProfissionalMapper;
import com.fiap.mindcare_diary.models.Clinica;
import com.fiap.mindcare_diary.models.Consulta;
import com.fiap.mindcare_diary.models.dtos.ClinicaDTO;
import com.fiap.mindcare_diary.models.dtos.ConsultaDTO;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.models.enums.PlanoAssinatura;
import com.fiap.mindcare_diary.repositories.ClinicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClinicaService {

    @Autowired
    private ClinicaRepository clinicaRepository;

    public ClinicaDTO retornarClinicaPorId(Long clinicaId) {
        Optional<Clinica> clinicaOptional = clinicaRepository.findById(clinicaId);
        if(clinicaOptional.isEmpty()) {
            throw new ClinicaNaoExistenteException("Clínica não existente.");
        }
        return ClinicaMapper.convertModelToDTO(clinicaOptional.get());
    }

    public List<ProfissionalDTO> retornarProfissionaisPorClinicaId(Long clinicaId) {
        Optional<Clinica> clinicaOptional = clinicaRepository.findById(clinicaId);
        if(clinicaOptional.isEmpty()) {
            throw new ClinicaNaoExistenteException("Clínica não existente.");
        }
        return ProfissionalMapper.convertModelListToDTOList(clinicaOptional.get().getProfissionais());
    }

    public List<PacienteDTO> retornarPacientesPorClinicaId(Long clinicaId) {
        Optional<Clinica> clinicaOptional = clinicaRepository.findById(clinicaId);
        if(clinicaOptional.isEmpty()) {
            throw new ClinicaNaoExistenteException("Clínica não existente.");
        }
        return PacienteMapper.convertModelListToDTOList(clinicaOptional.get().getPacientes());
    }

    public List<ConsultaDTO> retornarConsultasPorClinicaId(Long clinicaId) {
        Optional<Clinica> clinicaOptional = clinicaRepository.findById(clinicaId);
        if(clinicaOptional.isEmpty()) {
            throw new ClinicaNaoExistenteException("Clínica não existente.");
        }
        return ConsultaMapper.convertModelListToDTOList(clinicaOptional.get().getConsultas());
    }

    public Double retornarFaturamentoPorClinicaIdPorAnoMes(Long clinicaId, Long ano, Long mes) {
        Optional<Clinica> clinicaOptional = clinicaRepository.findById(clinicaId);
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

    public void cadastrarClinica(ClinicaDTO clinicaDTO) {
        clinicaRepository.save(ClinicaMapper.convertDTOToModel(clinicaDTO));
    }

    public Double retornarReceitaAposDescontosPorClinicaIdPorAnoMes(Long clinicaId, Long ano, Long mes) {
        Optional<Clinica> clinicaOptional = clinicaRepository.findById(clinicaId);
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

    public void atualizarDadosClinica(Long clinicaId, ClinicaDTO clinicaDTO) {
        Optional<Clinica> clinicaOptional = clinicaRepository.findById(clinicaId);
        if(clinicaOptional.isEmpty()) {
            throw new ClinicaNaoExistenteException("Clínica não existente.");
        }
        Clinica clinica = clinicaOptional.get();
        clinica.setNome(clinicaDTO.getNome());
        clinica.setEndereco(clinicaDTO.getEndereco());
        clinica.setPacientes(PacienteMapper.convertDTOListToModelList(clinicaDTO.getPacientes()));
        clinica.setConsultas(ConsultaMapper.convertDTOListToModelList(clinicaDTO.getConsultas()));
        clinica.setProfissionais(ProfissionalMapper.convertDTOListToModelList(clinicaDTO.getProfissionais()));
        clinica.setPlanoAssinatura(PlanoAssinatura.valueOf(clinicaDTO.getPlanoAssinatura().name()));
        clinica.setTaxaComissao(clinicaDTO.getTaxaComissao());
        clinicaRepository.save(clinica);
    }
}
