package com.fiap.mindcare_diary.models;

import com.fiap.mindcare_diary.models.enums.NivelHumor;
import com.fiap.mindcare_diary.models.enums.OrigemRegistro;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_registro_paciente_requisicao", columnNames = {"paciente_id", "id_requisicao"}))
@Getter
@Setter
public class RegistroDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @Enumerated(EnumType.STRING)
    private NivelHumor nivelHumor;

    private String pontosPositivos;

    private String dificuldadesDesafios;

    private LocalDateTime dataHoraCriacao;

    @Column(name = "texto_confirmado")
    private String textoConfirmado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @org.hibernate.annotations.ColumnDefault("'TRADITIONAL'")
    private OrigemRegistro origem = OrigemRegistro.TRADITIONAL;

    @Column(name = "id_requisicao")
    private UUID idRequisicao;
}
