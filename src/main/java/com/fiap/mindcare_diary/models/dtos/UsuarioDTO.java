package com.fiap.mindcare_diary.models.dtos;

import com.fiap.mindcare_diary.models.TipoUsuario;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UsuarioDTO {

    private String nomeUsuario;

    private String senha;

    private String tipoUsuario;

    private String nomeCompleto;

    private LocalDate dataNascimento;

    private String enderecoCompleto;

    private String cpf;

    private LocalDateTime dataCriacaoCadastro;

}
