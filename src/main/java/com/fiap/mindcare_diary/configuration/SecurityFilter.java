package com.fiap.mindcare_diary.configuration;

import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.repositories.UsuarioRepository;
import com.fiap.mindcare_diary.services.TokenService;
import com.fiap.mindcare_diary.utils.JWTUtils;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenJWT = recuperarToken(request);
        if (tokenJWT != null) {
            String subject = tokenService.getSubject(tokenJWT);
            Date expirationDate = tokenService.getExpirationDate(tokenJWT);
            Optional<Usuario> usuario = usuarioRepository.findByNomeUsuario(subject);
            if(usuario.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (new JWTUtils().validateToken(subject, expirationDate, usuario.get())) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(usuario, usuario.get().getPassword(), usuario.get().getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }

}
