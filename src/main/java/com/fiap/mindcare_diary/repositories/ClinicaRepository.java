package com.fiap.mindcare_diary.repositories;

import com.fiap.mindcare_diary.models.Clinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClinicaRepository extends JpaRepository<Clinica, Long> {

    Optional<Clinica> findById(Long id);
}
