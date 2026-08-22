package com.fiap.mindcare_diary.repositories;

import com.fiap.mindcare_diary.models.AuditoriaLog;
import com.fiap.mindcare_diary.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaLogRepository extends JpaRepository<AuditoriaLog, Long> {

    List<AuditoriaLog> findByMensagemContainingOrderByDataHoraAuditoriaDesc(String mensagem);

}
