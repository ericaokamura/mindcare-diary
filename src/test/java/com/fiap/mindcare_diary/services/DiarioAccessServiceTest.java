package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.models.enums.UserRole;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiarioAccessServiceTest {
    @Test void permitsOnlyOwnerOrLinkedProfessionalForReading() {
        var repo = mock(PacienteRepository.class); var access = new DiarioAccessService(repo);
        var user = new Usuario(); user.setNomeUsuario("ana"); user.setAtivo(true); user.setUserRole(UserRole.PACIENTE);
        var auth = UsernamePasswordAuthenticationToken.authenticated(Optional.of(user), null, List.of());
        assertDoesNotThrow(() -> access.requireRead(auth, "ana"));
        assertThrows(ResponseStatusException.class, () -> access.requireRead(auth, "outra"));
        assertThrows(ResponseStatusException.class, () -> access.requireWrite(auth, "outra"));
        user.setUserRole(UserRole.PROFISSIONAL);
        assertThrows(ResponseStatusException.class, () -> access.requireRead(auth, "outra"));
        when(repo.existsByNomeUsuarioAndProfissionais_NomeUsuario("outra", "ana")).thenReturn(true);
        assertDoesNotThrow(() -> access.requireRead(auth, "outra"));
        assertThrows(ResponseStatusException.class, () -> access.requireWrite(auth, "outra"));
        user.setUserRole(UserRole.ADMIN);
        assertThrows(ResponseStatusException.class, () -> access.requireRead(auth, "outra"));
    }
}
