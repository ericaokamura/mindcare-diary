package com.fiap.mindcare_diary.services;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import com.fiap.mindcare_diary.models.enums.*;
import static org.junit.jupiter.api.Assertions.*;

class HistoricoServiceTest {
    HistoricoService service;
    @BeforeEach void setup() {
        var jdbc = new JdbcTemplate(new DriverManagerDataSource("jdbc:h2:mem:" + java.util.UUID.randomUUID() + ";MODE=Oracle;DB_CLOSE_DELAY=-1", "sa", ""));
        jdbc.execute("CREATE TABLE registro_diario(id BIGINT, paciente_id BIGINT, nivel_humor VARCHAR(30), pontos_positivos VARCHAR(1000), dificuldades_desafios VARCHAR(1000), texto_confirmado CLOB, origem VARCHAR(30), data_hora_criacao TIMESTAMP)");
        var crypto = new com.fiap.mindcare_diary.security.storage.RecordCrypto("AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=");
        for (int i=1;i<=4;i++) jdbc.update("INSERT INTO registro_diario VALUES(?,1,'BOM','Passeio 100%',NULL,NULL,'TRADITIONAL',TIMESTAMP '2026-10-01 23:59:00')",i);
        jdbc.update("INSERT INTO registro_diario VALUES(5,1,'MAL',NULL,NULL,'Texto do Chat','CHAT',TIMESTAMP '2026-10-02 12:00:00')");
        jdbc.update("INSERT INTO registro_diario VALUES(6,2,'BOM','SEGREDO',NULL,NULL,'CHAT',TIMESTAMP '2026-10-03 12:00:00')");
        jdbc.execute("CREATE TABLE relatorio_semanal(id BIGINT, observacoes CLOB, recomendacoes CLOB, relatorio_ia CLOB, resumo CLOB)");
        try (var connection = jdbc.getDataSource().getConnection()) {
            com.fiap.mindcare_diary.security.storage.EncryptionMigration.migrate(connection, crypto, false);
        } catch (java.sql.SQLException ex) { throw new RuntimeException(ex); }
        service = new HistoricoService(jdbc, crypto);
    }
    @Test void paginaOrdenaDesempateENaoVazaOutroPaciente() {
        var p = service.buscar(1L,null,null,null,null,null,0,2);
        assertEquals(java.util.List.of(5L,4L),p.registros().stream().map(HistoricoService.Registro::id).toList());
        assertTrue(p.temMais());
        assertEquals(java.util.List.of(3L,2L),service.buscar(1L,null,null,null,null,null,1,2).registros().stream().map(HistoricoService.Registro::id).toList());
        assertFalse(service.buscar(1L,null,null,null,null,null,2,2).temMais());
        assertTrue(service.buscar(1L,"SEGREDO",null,null,null,null,0,20).registros().isEmpty());
    }
    @Test void combinaFiltrosEIncluiFimDoDia() {
        assertEquals(4,service.buscar(1L,"100%",LocalDate.parse("2026-10-01"),LocalDate.parse("2026-10-01"),NivelHumor.BOM,OrigemRegistro.TRADITIONAL,0,20).registros().size());
        assertEquals(1,service.buscar(1L,"texto",null,null,NivelHumor.MAL,OrigemRegistro.CHAT,0,20).registros().size());
        assertTrue(service.buscar(1L,"%' OR 1=1 --",null,null,null,null,0,20).registros().isEmpty());
    }
    @Test void validaLimites() {
        assertThrows(ResponseStatusException.class,()->service.buscar(1L,null,null,null,null,null,-1,20));
        assertThrows(ResponseStatusException.class,()->service.buscar(1L,null,null,null,null,null,0,51));
        assertThrows(ResponseStatusException.class,()->service.buscar(1L,null,LocalDate.now(),LocalDate.now().minusDays(1),null,null,0,20));
    }
}
