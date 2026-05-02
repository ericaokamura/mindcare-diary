package com.fiap.mindcare_diary.models.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class UsuarioDTO {

    private String nomeUsuario;

    private String senha;

    private String nomeCompleto;

    private String dataNascimento;

    private String dataHoraAtivacao;

}
