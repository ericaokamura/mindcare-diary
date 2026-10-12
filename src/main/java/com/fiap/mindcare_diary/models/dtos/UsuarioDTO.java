package com.fiap.mindcare_diary.models.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioDTO {

    private String nomeUsuario;

    private String senha;

    private String nomeCompleto;

    private String dataNascimento;

    private String genero;

    private boolean ativo;

    private String dataHoraAtivacao;

    private String token;

    private String userRole;

}
