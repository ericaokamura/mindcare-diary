package com.fiap.mindcare_diary.models;

import jakarta.persistence.*;
import com.fiap.mindcare_diary.security.storage.EncryptedConverters;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class RelatorioSemanal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    private String faixaDeDatas;

    @ManyToMany
    private List<RegistroDiario> registrosDiarios;

    @Lob
    @Column(name = "observacoes_enc", columnDefinition = "CLOB")
    @Convert(converter = EncryptedConverters.Observacoes.class)
    private String observacoes;

    @Lob
    @Column(name = "recomendacoes_enc", columnDefinition = "CLOB")
    @Convert(converter = EncryptedConverters.Recomendacoes.class)
    private String recomendacoes;

    @Lob
    @Column(name = "relatorio_ia_enc", columnDefinition = "CLOB")
    @Convert(converter = EncryptedConverters.Relatorio.class)
    private String relatorioIA;

    private LocalDateTime dataHoraCriacao;

    private int totalPositivos;

    private int totalNegativos;

    @Lob
    @Column(name = "resumo_enc", columnDefinition = "CLOB")
    @Convert(converter = EncryptedConverters.Resumo.class)
    private String resumo;
}
