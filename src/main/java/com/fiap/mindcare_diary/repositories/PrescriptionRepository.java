package com.fiap.mindcare_diary.repositories;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.Prescription;
import com.fiap.mindcare_diary.models.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    Optional<Prescription> findByPacienteAndProfissionalAndNumber(Paciente paciente, Profissional profissional, String number);
}
