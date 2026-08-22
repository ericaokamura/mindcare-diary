package com.fiap.mindcare_diary.models.dtos;

import com.fiap.mindcare_diary.models.Profissional;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PrescriptionDTO {

    private String number;

    private String issueDate;

    private String  expirationDate;

    private Long daysRemaining;

    private ProfissionalDTO profissional;

    private List<String> medicines;

    private boolean controlled;

    private boolean valid;

    private PrescriptionDocumentDTO prescriptionDocument;
}
