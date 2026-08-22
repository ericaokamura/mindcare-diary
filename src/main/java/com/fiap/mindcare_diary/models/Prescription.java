package com.fiap.mindcare_diary.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;

    private LocalDate issueDate;

    private LocalDate  expirationDate;

    private Long daysRemaining;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "profissional_id")
    private Profissional profissional;

    @ElementCollection
    @CollectionTable(
            name = "prescription_medicines",
            joinColumns = @JoinColumn(name = "prescription_id")
    )
    @Column(name = "medicines")
    private List<String> medicines = new ArrayList<>();

    private boolean controlled;

    private boolean valid;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "prescription_document_id")
    private PrescriptionDocument prescriptionDocument;

}
