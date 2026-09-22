package com.fiap.mindcare_diary.services.mia;

import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.models.enums.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;

public final class MiaAuthentication {
    private MiaAuthentication() {}

    public static Usuario requireActiveUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Autenticação necessária.");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Optional<?> optional) principal = optional.orElse(null);
        if (!(principal instanceof Usuario user) || !user.isAtivo() || user.isBloqueado()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso não permitido.");
        }
        return user;
    }

    public static Usuario requirePatient(Authentication authentication) {
        Usuario user = requireActiveUser(authentication);
        if (user.getUserRole() != UserRole.PACIENTE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso permitido apenas ao paciente ativo.");
        }
        return user;
    }
}
