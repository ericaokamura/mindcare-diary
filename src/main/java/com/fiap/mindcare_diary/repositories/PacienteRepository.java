package com.fiap.mindcare_diary.repositories;

import com.fiap.mindcare_diary.models.Clinica;
import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Paciente p where p.nomeUsuario = :username")
    Optional<Paciente> findForDiaryUpdate(@Param("username") String username);

    boolean existsByNomeUsuarioAndProfissionais_NomeUsuario(String username, String professionalUsername);

    Optional<Paciente> findByNomeUsuario(String nomeUsuario);

    List<Paciente> findByClinica(Clinica clinica);
}
