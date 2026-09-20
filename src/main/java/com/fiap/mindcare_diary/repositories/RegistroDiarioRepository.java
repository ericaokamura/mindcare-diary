package com.fiap.mindcare_diary.repositories;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.RegistroDiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistroDiarioRepository extends JpaRepository<RegistroDiario, Long> {

    Optional<RegistroDiario> findByPacienteAndIdRequisicao(Paciente paciente, UUID idRequisicao);

    RegistroDiario findByPacienteId(Long id);

    List<RegistroDiario> findAllByPaciente(Paciente paciente);

}
