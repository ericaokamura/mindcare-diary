package com.fiap.mindcare_diary.models;

import com.fiap.mindcare_diary.models.enums.NivelHumor;
import com.fiap.mindcare_diary.models.enums.OrigemRegistro;
import java.util.UUID;
import jakarta.persistence.*;
import com.fiap.mindcare_diary.security.storage.EncryptedConverters;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Lob
    @Column(name = "pontos_positivos_enc", columnDefinition = "CLOB")
    @Convert(converter = EncryptedConverters.Pontos.class)
    private String pontosPositivos;

    @Lob
    @Column(name = "dificuldades_desafios_enc", columnDefinition = "CLOB")
    @Convert(converter = EncryptedConverters.Dificuldades.class)
    private String dificuldadesDesafios;

    private LocalDateTime dataHoraCriacao;

    @Lob
    @Column(name = "texto_confirmado_enc", columnDefinition = "CLOB")
    @Convert(converter = EncryptedConverters.Texto.class)
    private String textoConfirmado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @org.hibernate.annotations.ColumnDefault("'TRADITIONAL'")
    private OrigemRegistro origem = OrigemRegistro.TRADITIONAL;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "id_requisicao", length = 36)
    private UUID idRequisicao;
}
