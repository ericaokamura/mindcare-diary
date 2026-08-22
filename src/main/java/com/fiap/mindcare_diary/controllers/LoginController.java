package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.exceptions.UsuarioBloqueadoException;
import com.fiap.mindcare_diary.models.AuditoriaLog;
import com.fiap.mindcare_diary.models.DadosAutenticacao;
import com.fiap.mindcare_diary.models.DadosTokenJWT;
import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.repositories.UsuarioRepository;
import com.fiap.mindcare_diary.services.AuditoriaLogService;
import com.fiap.mindcare_diary.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("login")
public class LoginController {

    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AuditoriaLogService auditoriaLogService;

    @PostMapping()
    public ResponseEntity efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByNomeUsuario(dados.nomeUsuario());
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();

            AuditoriaLog auditoriaLog = new AuditoriaLog();
            auditoriaLog.setMensagem("Dados do login: " + "nomeUsuario: " + dados.nomeUsuario() + ", senha: " + dados.senha());
            auditoriaLog.setHttpMethod(HttpMethod.POST);
            auditoriaLog.setDataHoraAuditoria(LocalDateTime.now());
            List<AuditoriaLog> logs = this.auditoriaLogService.retornarLogsPorMensagemContendoOrdernarPorDataHoraAuditoriaDesc(dados.nomeUsuario());
            if (!logs.isEmpty()) {
                auditoriaLog.setNTentativas(logs.get(0).getNTentativas() + 1);
            } else {
                auditoriaLog.setNTentativas(1);
            }
            if (auditoriaLog.getNTentativas() > 3) {
                if (!usuario.isBloqueado()) {
                    usuario.setBloqueado(true);
                    usuarioRepository.save(usuario);
                }
                throw new UsuarioBloqueadoException("Usuário com conta bloqueada.");
            }

            if (BCrypt.checkpw(dados.senha(), usuario.getSenha())) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(dados.nomeUsuario(), dados.senha(), usuario.getAuthorities());
                Authentication authentication = manager.authenticate(authenticationToken);
                String tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());
                auditoriaLog.setNTentativas(0);
                this.auditoriaLogService.salvarLog(auditoriaLog);
                return ResponseEntity.ok(new DadosTokenJWT(tokenJWT, usuario.getUserRole().name()));
            }
            this.auditoriaLogService.salvarLog(auditoriaLog);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

