package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.UsuarioNaoEncontradoException;
import com.fiap.mindcare_diary.models.AuditoriaLog;
import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.repositories.AuditoriaLogRepository;
import com.fiap.mindcare_diary.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Service
public class AuditoriaLogService {

    @Autowired
    private AuditoriaLogRepository auditoriaLogRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void salvarLog(AuditoriaLog log) {
        this.auditoriaLogRepository.save(log);
    }

    public List<AuditoriaLog> retornarLogsPorMensagemContendoOrdernarPorDataHoraAuditoriaDesc(String nomeUsuario) {
        return this.auditoriaLogRepository.findByMensagemContainingOrderByDataHoraAuditoriaDesc(nomeUsuario) ;
    }

}