package com.fiap.mindcare_diary.repositories;

import com.fiap.mindcare_diary.models.Consulta;
import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findAllByPaciente(Paciente paciente);

    List<Consulta> findAllByProfissional(Profissional profissional);
}
