package com.fiap.mindcare_diary.models.dtos;

import com.fiap.mindcare_diary.models.enums.Sexo;
import com.fiap.mindcare_diary.models.enums.UserRole;
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

    private Sexo genero;

    private boolean ativo;

    private String dataHoraAtivacao;

    private String token;

    private UserRole userRole;

}
