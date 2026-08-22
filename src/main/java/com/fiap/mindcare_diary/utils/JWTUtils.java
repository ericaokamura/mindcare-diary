package com.fiap.mindcare_diary.utils;

import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JWTUtils {

    public boolean validateToken(String subject, Date expirationDate, Usuario usuario) {
        return subject.equals(usuario.getNomeUsuario()) && !isTokenExpired(expirationDate);
    }

    private boolean isTokenExpired(Date expirationDate) {
        return expirationDate.before(new Date());
    }
}
