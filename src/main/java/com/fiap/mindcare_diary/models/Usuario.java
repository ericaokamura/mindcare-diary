package com.fiap.mindcare_diary.models;

import com.fiap.mindcare_diary.models.enums.Sexo;
import com.fiap.mindcare_diary.models.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
public class Usuario implements UserDetails {

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

    private boolean bloqueado;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authoritiesList = new ArrayList<>();
        this.userRole.getAuthorities().forEach(authority -> authoritiesList.add(new SimpleGrantedAuthority(authority.name())));
        return authoritiesList;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return nomeUsuario;
    }
}
