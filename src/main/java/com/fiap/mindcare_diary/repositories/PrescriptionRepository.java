package com.fiap.mindcare_diary.repositories;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    Optional<Prescription> findByPacienteAndNumber(Paciente paciente, String number);
}
