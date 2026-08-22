package com.fiap.mindcare_diary.models;

import com.fiap.mindcare_diary.models.enums.AuditAction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class AuditoriaLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mensagem;

    @Enumerated(EnumType.STRING)
    private AuditAction action;

    private HttpMethod httpMethod;

    private Integer nTentativas = 0;

    private LocalDateTime dataHoraAuditoria;

}
