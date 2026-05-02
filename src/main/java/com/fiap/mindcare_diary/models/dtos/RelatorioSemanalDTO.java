package com.fiap.mindcare_diary.models.dtos;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.RegistroDiario;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RelatorioSemanalDTO {

    private PacienteDTO paciente;

    private String faixaDeDatas;

    private List<RegistroDiarioDTO> registrosDiarios;

    private String observacoes;

    private String recomendacoes;

    private String relatorioIA;

    private String dataHoraCriacao;
}
