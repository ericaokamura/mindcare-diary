package com.fiap.mindcare_diary.repositories;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.RelatorioSemanal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelatorioSemanalRepository extends JpaRepository<RelatorioSemanal, Long> {

    List<RelatorioSemanal> findAllByPaciente(Paciente paciente);
}
