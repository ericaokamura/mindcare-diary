package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.configuration.SecurityConfiguration;
import com.fiap.mindcare_diary.configuration.SecurityFilter;
import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.models.enums.UserRole;
import com.fiap.mindcare_diary.repositories.UsuarioRepository;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.RegistroDiarioRepository;
import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.RegistroDiario;
import com.fiap.mindcare_diary.services.TokenService;
import com.fiap.mindcare_diary.services.mia.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MiaController.class)
@Import({SecurityConfiguration.class, SecurityFilter.class, MiaService.class,
        MiaIntentClassifier.class, MiaResponseValidator.class, MiaRegistroService.class})
class MiaControllerTest {
    @Autowired MockMvc mvc;
    @MockBean TokenService tokens;
    @MockBean UsuarioRepository usuarios;
    @MockBean MiaAiService ai;
    @MockBean PacienteRepository patients;
    @MockBean RegistroDiarioRepository records;
    private Usuario patient;

    @BeforeEach
    void setUp() {
        patient = new Usuario();
        patient.setNomeUsuario("paciente-teste");
        patient.setUserRole(UserRole.PACIENTE);
        patient.setAtivo(true);
        when(tokens.getSubject("test-token")).thenReturn("paciente-teste");
        when(tokens.getExpirationDate("test-token")).thenReturn(new Date(System.currentTimeMillis() + 60000));
        when(usuarios.findByNomeUsuario("paciente-teste")).thenReturn(Optional.of(patient));
    }

    @Test
    void usesExistingJwtFilterAndDoesNotCacheOrEchoPatientContent() throws Exception {
        mvc.perform(post("/mia/mensagens").header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"mensagem\":\"Me dê um conselho\"}"))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.assistente").value("MIA"))
                .andExpect(jsonPath("$.papel").value("Assistente de registro"))
                .andExpect(jsonPath("$.fallback").value(false))
                .andExpect(jsonPath("$.paciente").doesNotExist())
                .andExpect(jsonPath("$.token").doesNotExist());
        verifyNoInteractions(ai);
    }

    @Test
    void rejectsMissingAuthentication() throws Exception {
        mvc.perform(post("/mia/mensagens").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mensagem\":\"Olá\"}"))
                .andExpect(status().is4xxClientError());
        verifyNoInteractions(ai);
    }

    @Test
    void rejectsProfessionalAndBlockedPatient() throws Exception {
        patient.setUserRole(UserRole.PROFISSIONAL);
        sendValidMessageExpectForbidden();
        patient.setUserRole(UserRole.PACIENTE);
        patient.setBloqueado(true);
        sendValidMessageExpectForbidden();
        verifyNoInteractions(ai);
    }

    @Test
    void rejectsMissingMessageAndMalformedJson() throws Exception {
        for (String body : new String[]{"{}", "{\"mensagem\":null}", "{\"mensagem\":\"  \"}", "invalid"}) {
            mvc.perform(post("/mia/mensagens").header("Authorization", "Bearer test-token")
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(ai);
    }

    @Test
    void malformedContentIsNotEchoedInErrorResponse() throws Exception {
        mvc.perform(post("/mia/mensagens").header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON).content("{relato-pessoal-sigiloso"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.mensagem").value("Corpo JSON inválido."));
    }

    @Test
    void savesConfirmedTextUnderAuthenticatedPatient() throws Exception {
        var p = new Paciente(); p.setNomeUsuario("paciente-teste"); p.setAtivo(true);
        when(patients.findForDiaryUpdate("paciente-teste")).thenReturn(Optional.of(p));
        doNothing().when(records).saveAndFlush(any());
        mvc.perform(post("/mia/registros").header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idRequisicao\":\"0afc7e20-3210-4ec0-8b04-7a52047f04c0\",\"textoConfirmado\":\"Meu relato revisado\",\"nomeUsuario\":\"outro\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.origem").value("CHAT"))
                .andExpect(jsonPath("$.textoConfirmado").value("Meu relato revisado"))
                .andExpect(jsonPath("$.paciente.nomeUsuario").value("paciente-teste"))
                .andExpect(header().string("Cache-Control", "no-store"));
        verifyNoInteractions(ai);
        verify(patients, never()).findForDiaryUpdate("outro");
    }

    private void sendValidMessageExpectForbidden() throws Exception {
        mvc.perform(post("/mia/mensagens").header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"mensagem\":\"Olá\"}"))
                .andExpect(status().isForbidden());
    }
}
