package com.fiap.mindcare_diary.models;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Profissional extends Usuario {

    private String identificadorProfissional;

}
