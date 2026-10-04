package com.fiap.mindcare_diary.services;
import com.fiap.mindcare_diary.controllers.HistoricoController;
import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.models.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class HistoricoControllerTest {
    @Test void httpPreservaStatusEFalhaInesperadaNaoExpoeDetalhes() throws Exception {
        var service = mock(HistoricoService.class);
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(new HistoricoController(service))
            .setControllerAdvice(new com.fiap.mindcare_diary.exceptions.ApplicationExceptionHandler(), new com.fiap.mindcare_diary.exceptions.MiaExceptionHandler()).build();
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/meu-diario/historico"))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isUnauthorized());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/meu-diario/historico").param("inicio","invalido"))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest());
        var user = new Usuario(); user.setId(9L); user.setAtivo(true); user.setUserRole(UserRole.PACIENTE);
        when(service.buscar(9L,null,null,null,null,null,0,20)).thenThrow(new RuntimeException("DETALHE_SQL_SECRETO"));
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/meu-diario/historico")
            .principal(new UsernamePasswordAuthenticationToken(user,null,java.util.List.of())))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("DETALHE_SQL_SECRETO"))));
    }
    @Test void exigeSessaoEPacienteAtivo() {
        var service = mock(HistoricoService.class); var controller = new HistoricoController(service);
        assertThrows(ResponseStatusException.class, () -> controller.buscar(null,null,null,null,null,null,0,20));
        var user = new Usuario(); user.setId(9L); user.setAtivo(true); user.setUserRole(UserRole.PROFISSIONAL);
        var auth = new UsernamePasswordAuthenticationToken(user,null,java.util.List.of());
        assertThrows(ResponseStatusException.class, () -> controller.buscar(auth,null,null,null,null,null,0,20));
        verifyNoInteractions(service);
    }
    @Test void usaIdDaSessaoENaoUmIdEnviadoPeloCliente() {
        var service = mock(HistoricoService.class); var controller = new HistoricoController(service);
        var user = new Usuario(); user.setId(9L); user.setAtivo(true); user.setUserRole(UserRole.PACIENTE);
        controller.buscar(new UsernamePasswordAuthenticationToken(java.util.Optional.of(user),null,java.util.List.of()),null,null,null,null,null,0,20);
        verify(service).buscar(9L,null,null,null,null,null,0,20);
    }
}
