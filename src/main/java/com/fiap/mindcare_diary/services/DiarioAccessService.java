package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.models.enums.UserRole;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.services.mia.MiaAuthentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DiarioAccessService {

    private final PacienteRepository patients;
    public DiarioAccessService(PacienteRepository patients) { this.patients = patients; }

    public void requireWrite(Authentication authentication, String username) {
        var user = MiaAuthentication.requirePatient(authentication);
        if (!user.getNomeUsuario().equals(username)) deny();
    }

    @Transactional(readOnly = true)
    public void requireRead(Authentication authentication, String username) {
        var user = MiaAuthentication.requireActiveUser(authentication);
        if (user.getUserRole() == UserRole.PACIENTE && user.getNomeUsuario().equals(username)) return;
        if (user.getUserRole() == UserRole.PROFISSIONAL && patients.existsByNomeUsuarioAndProfissionais_NomeUsuario(username, user.getNomeUsuario())) return;
        deny();
    }

    private void deny() { throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso ao diário não permitido."); }
}
