package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.PacienteNaoEncontradoException;
import com.fiap.mindcare_diary.mappers.RelatorioSemanalMapper;
import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.RegistroDiario;
import com.fiap.mindcare_diary.models.RelatorioSemanal;
import com.fiap.mindcare_diary.models.dtos.RelatorioSemanalDTO;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.RegistroDiarioRepository;
import com.fiap.mindcare_diary.repositories.RelatorioSemanalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hibernate.internal.util.collections.ArrayHelper.forEach;

@Service
public class RelatorioSemanalService {

    @Autowired
    private RelatorioSemanalRepository relatorioSemanalRepository;

    @Autowired
    private RegistroDiarioRepository registroDiarioRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    public RelatorioSemanalDTO gerarRelatorioSemanal(String nomeUsuario, RelatorioSemanalDTO dto) {
        Optional<Paciente> optionalPaciente = this.pacienteRepository.findByNomeUsuario(nomeUsuario);
        if(optionalPaciente.isPresent()) {
            Paciente paciente = optionalPaciente.get();
            RelatorioSemanal relatorioSemanal = new RelatorioSemanal();
            relatorioSemanal.setRelatorioIA("Relatório gerado automaticamente por IA.");
            relatorioSemanal.setDataHoraCriacao(LocalDateTime.now());
            relatorioSemanal.setPaciente(paciente);
            relatorioSemanal.setFaixaDeDatas(dto.getFaixaDeDatas());
            relatorioSemanal.setObservacoes(dto.getObservacoes());
            relatorioSemanal.setRecomendacoes(dto.getRecomendacoes());
            List<RegistroDiario> todosRegistrosDiarios = this.registroDiarioRepository.findAllByPaciente(paciente);
            int countPontosPositivos = 0;
            int countDificuldadesDesafios = 0;
            List<RegistroDiario> registrosDiarios = new ArrayList<>();
            String[] datas = dto.getFaixaDeDatas().split("ˆ");
            for(RegistroDiario registroDiario : todosRegistrosDiarios) {
                if(registroDiario.getDataHoraCriacao().isAfter(LocalDateTime.parse(datas[0])) && registroDiario.getDataHoraCriacao().isBefore(LocalDateTime.parse(datas[1]))) {
                    registrosDiarios.add(registroDiario);
                    if(registroDiario.getPontosPositivos() != null && !registroDiario.getPontosPositivos().isBlank()){
                        countPontosPositivos++;
                    } else if(registroDiario.getDificuldadesDesafios() != null && !registroDiario.getDificuldadesDesafios().isBlank()){
                        countDificuldadesDesafios++;
                    }
                }
            }
            relatorioSemanal.setTotalPositivos(countPontosPositivos);
            relatorioSemanal.setTotalNegativos(countDificuldadesDesafios);
            relatorioSemanal.setRegistrosDiarios(registrosDiarios);
            relatorioSemanalRepository.save(relatorioSemanal);
            return RelatorioSemanalMapper.convertModelToDTO(relatorioSemanal);
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }
    }

    public List<RelatorioSemanalDTO> retornarRelatoriosSemanaisPorPaciente(String nomeUsuario) {
        Optional<Paciente> optionalPaciente = this.pacienteRepository.findByNomeUsuario(nomeUsuario);
        if(optionalPaciente.isPresent()) {
            return RelatorioSemanalMapper.convertModelListToDTOList(this.relatorioSemanalRepository.findAllByPaciente(optionalPaciente.get()));
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }
    }
}
