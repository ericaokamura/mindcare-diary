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
        return calcularFaturamentoPorAnoMes(clinicaOptional.get().getConsultas(), ano, mes);
    }

    private Double calcularFaturamentoPorAnoMes(List<Consulta> consultas, Long ano, Long mes) {
        Double faturamento = 0.0;
        List<Consulta> consultasPorAnoMes = consultas.stream().filter(
                                                        consulta ->
                                                        consulta.getDataHoraConsulta().getMonthValue() == mes &&
                                                        consulta.getDataHoraConsulta().getYear() == ano &&
                                                        consulta.isAtendida() && !consulta.isCancelada()).collect(Collectors.toList());

        for(Consulta consulta : consultasPorAnoMes) {
            faturamento += consulta.getValorConsulta();
        }
        return faturamento;
    }
}
