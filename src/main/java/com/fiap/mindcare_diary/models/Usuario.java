package com.fiap.mindcare_diary.models;

import com.fiap.mindcare_diary.models.enums.Sexo;
import com.fiap.mindcare_diary.models.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeUsuario;

    private String senha;

    private String nomeCompleto;

    private LocalDate dataNascimento;

    private Sexo genero;

    private boolean ativo;

    private LocalDateTime dataHoraAtivacao;

    private String token;

    @Enumerated(value = EnumType.STRING)
    private UserRole userRole;

}
