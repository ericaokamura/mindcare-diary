package com.fiap.mindcare_diary.repositories;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    List<Paciente> findAllByProfissional(Profissional profissional);

    Optional<Paciente> findByNomeUsuario(String nomeUsuario);
}
