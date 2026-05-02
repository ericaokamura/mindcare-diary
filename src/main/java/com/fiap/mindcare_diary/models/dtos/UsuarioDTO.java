package com.fiap.mindcare_diary.models.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UsuarioDTO {

    private String nomeUsuario;

    private String senha;

    private String nomeCompleto;

    private String dataNascimento;

    private String dataHoraAtivacao;

}
