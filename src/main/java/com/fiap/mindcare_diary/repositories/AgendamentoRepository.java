package com.fiap.mindcare_diary.repositories;

import com.fiap.mindcare_diary.models.Consulta;
import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.enums.TipoProfissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgendamentoRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findByProfissionalAndDataHoraConsultaAfter(Profissional profissional, LocalDateTime dataHora);

    Optional<Consulta> findByProfissionalAndDataHoraConsulta(Profissional profissional, LocalDateTime dataHoraAgendamento);

    List<Consulta> findByDataHoraConsultaBefore(LocalDateTime now);

    List<Consulta> findByProfissionalTipoProfissionalAndDataHoraConsultaAfter(TipoProfissional tipoProfissional, LocalDateTime dataHoraAgendamento);
}
