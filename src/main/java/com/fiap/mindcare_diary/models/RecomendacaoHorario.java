package com.fiap.mindcare_diary.models;

import com.fiap.mindcare_diary.models.enums.TipoProfissional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class RecomendacaoHorario {

    private String dataHoraConsulta;

    private String especialidade;

    private Double score;

}
