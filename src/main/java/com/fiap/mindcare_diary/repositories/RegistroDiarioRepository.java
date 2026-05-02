package com.fiap.mindcare_diary.repositories;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.RegistroDiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroDiarioRepository extends JpaRepository<RegistroDiario, Long> {

    RegistroDiario findByPacienteId(Long id);

    List<RegistroDiario> findAllByPaciente(Paciente paciente);

}
