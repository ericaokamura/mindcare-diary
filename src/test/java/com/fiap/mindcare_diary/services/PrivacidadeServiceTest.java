package com.fiap.mindcare_diary.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.mindcare_diary.models.*;
import com.fiap.mindcare_diary.models.enums.*;
import com.fiap.mindcare_diary.repositories.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.zip.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {"spring.sql.init.mode=never", "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"}, showSql = false)
@Import({PrivacidadeService.class, PrivacidadeServiceTest.Config.class})
class PrivacidadeServiceTest {
    @TestConfiguration static class Config {
        @Bean PasswordEncoder passwords() { return new BCryptPasswordEncoder(); }
        @Bean ObjectMapper json() { return new ObjectMapper().findAndRegisterModules(); }
    }
    @Autowired PrivacidadeService service;
    @Autowired UsuarioRepository users;
    @Autowired RegistroDiarioRepository registros;
    @Autowired EntityManager em;
    @Autowired PasswordEncoder passwords;
    private Paciente paciente(String nome) {
        var p = new Paciente(); p.setNomeUsuario(nome); p.setAtivo(true); p.setUserRole(UserRole.PACIENTE);
        p.setSenha(passwords.encode("senha-teste")); p.setToken("SEGREDO-FCM");
        return users.saveAndFlush(p);
    }
    private void relato(Paciente p, String text, OrigemRegistro origem) {
        var r = new RegistroDiario(); r.setPaciente(p); r.setDataHoraCriacao(LocalDateTime.now());
        r.setNivelHumor(NivelHumor.BOM); r.setOrigem(origem);
        if (origem == OrigemRegistro.CHAT) r.setTextoConfirmado(text); else r.setPontosPositivos(text);
        registros.saveAndFlush(r);
    }
    private Map<String, byte[]> unzip(byte[] data) throws IOException {
        var result = new HashMap<String, byte[]>();
        try (var zip = new ZipInputStream(new ByteArrayInputStream(data))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) result.put(entry.getName(), zip.readAllBytes());
        }
        return result;
    }
    @Test void exportaAmbosModosSomenteDoTitularSemCredenciais() throws Exception {
        var a = paciente("A"); var b = paciente("B");
        relato(a, "MEU-CHAT", OrigemRegistro.CHAT); relato(a, "MEU-DIARIO", OrigemRegistro.TRADITIONAL);
        relato(b, "SEGREDO-OUTRO-PACIENTE", OrigemRegistro.CHAT);
        service.registrarAceite(a, PrivacidadeService.VERSAO);
        String text = new String(unzip(service.exportar(a.getId(), "senha-teste")).get("dados.json"), StandardCharsets.UTF_8);
        assertTrue(text.contains("MEU-CHAT")); assertTrue(text.contains("MEU-DIARIO"));
        assertFalse(text.contains("SEGREDO-OUTRO-PACIENTE")); assertFalse(text.contains("SEGREDO-FCM"));
        assertFalse(text.contains(a.getSenha())); assertFalse(text.contains("senha-teste"));
        assertTrue(text.contains("hashDocumentos"));
    }
    @Test void exportaPdfExistenteSemUsarNomeExternoComoCaminho() throws Exception {
        var p = paciente("pdf");
        var rx = new Prescription(); rx.setPaciente(p); rx.setNumber("1"); em.persist(rx);
        var doc = new PrescriptionDocument(); doc.setPrescription(rx); doc.setNomeArquivo("../../escape.pdf");
        doc.setArquivoPdf("%PDF-teste".getBytes(StandardCharsets.UTF_8)); em.persist(doc);
        rx.setPrescriptionDocument(doc); em.flush();
        var zip = unzip(service.exportar(p.getId(), "senha-teste"));
        assertArrayEquals(doc.getArquivoPdf(), zip.get("prescricoes/" + doc.getId() + ".pdf"));
        assertTrue(zip.keySet().stream().noneMatch(n -> n.contains("..")));
    }
    @Test void senhaErradaNaoExportaNemEncerra() {
        var p = paciente("senha");
        assertThrows(ResponseStatusException.class, () -> service.exportar(p.getId(), "errada"));
        assertThrows(ResponseStatusException.class, () -> service.encerrar(p.getId(), "errada"));
        assertNull(p.getEncerradaEm()); assertTrue(p.isAtivo());
    }
    @Test void encerramentoRegistraProtocoloPreservaDadosParaAnaliseEImpedeExportacao() {
        var p = paciente("encerrar"); relato(p, "guardar-analise", OrigemRegistro.CHAT);
        var result = service.encerrar(p.getId(), "senha-teste");
        assertNotNull(result.get("protocolo")); assertNotNull(p.getEncerradaEm());
        assertFalse(p.isAtivo()); assertTrue(p.isBloqueado()); assertNull(p.getToken());
        assertEquals(1, registros.findAllByPaciente(p).size());
        assertEquals(result.get("protocolo"), service.encerrar(p.getId(), "senha-teste").get("protocolo"));
        assertThrows(ResponseStatusException.class, () -> service.exportar(p.getId(), "senha-teste"));
    }
    @Test void aceiteTemVersaoDataHashERejeitaVersaoAntiga() {
        var p = paciente("aceite");
        service.registrarAceite(p, PrivacidadeService.VERSAO);
        var a = em.createQuery("select a from AceiteTermos a", AceiteTermos.class).getSingleResult();
        assertEquals(64, a.getHashDocumentos().length()); assertNotNull(a.getAceitoEm());
        assertThrows(ResponseStatusException.class, () -> service.registrarAceite(p, "antiga"));
    }
    @Test void perfilProfissionalNaoRecebeDadosDePacientes() throws Exception {
        var p = new Profissional(); p.setNomeUsuario("prof"); p.setAtivo(true); p.setUserRole(UserRole.PROFISSIONAL);
        p.setSenha(passwords.encode("senha-teste")); users.saveAndFlush(p);
        relato(paciente("pac"), "SEGREDO-CLINICO", OrigemRegistro.CHAT);
        String text = new String(unzip(service.exportar(p.getId(), "senha-teste")).get("dados.json"), StandardCharsets.UTF_8);
        assertFalse(text.contains("SEGREDO-CLINICO")); assertTrue(text.contains("prof"));
    }
}
