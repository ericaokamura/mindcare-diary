package com.fiap.mindcare_diary.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.mindcare_diary.models.*;
import com.fiap.mindcare_diary.models.enums.UserRole;
import com.fiap.mindcare_diary.repositories.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.*;
import java.util.*;
import java.util.zip.*;

@Service
public class PrivacidadeService {

    public static final String VERSAO = "2026-09-21.1";
    private final EntityManager em;
    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwords;
    private final ObjectMapper json;
    private final String hashDocumentos;

    public PrivacidadeService(EntityManager em, UsuarioRepository usuarios, PasswordEncoder passwords, ObjectMapper json) {
        this.em = em; this.usuarios = usuarios; this.passwords = passwords; this.json = json;
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            for (String file : List.of("termos.txt", "privacidade.txt")) {
                try (var in = new ClassPathResource("legal/" + file).getInputStream()) { digest.update(in.readAllBytes()); }
            }
            hashDocumentos = HexFormat.of().formatHex(digest.digest());
        } catch (Exception e) { throw new IllegalStateException("Documentos legais indisponíveis.", e); }
    }

    @Transactional
    public void registrarAceite(Usuario usuario, String versao) {
        if (!VERSAO.equals(versao)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Atualize os termos no aplicativo.");
        }

        List<AceiteTermos> aceites = em.createQuery(
                        "SELECT a FROM AceiteTermos a WHERE a.versao = :versao",
                        AceiteTermos.class
                )
                .setParameter("versao", versao)
                .getResultList();

        if(aceites != null && aceites.size() > 0) {
            return;
        }

        var aceite = new AceiteTermos();
        aceite.setUsuario(usuario);
        aceite.setVersao(VERSAO);
        aceite.setHashDocumentos(hashDocumentos);
        aceite.setAceitoEm(Instant.now());
        em.persist(aceite);
    }

    private Usuario autenticar(Long id, String senha, boolean bloquear) {
        var user = bloquear ? em.find(Usuario.class, id, LockModeType.PESSIMISTIC_WRITE) : usuarios.findById(id).orElse(null);
        if (user == null || senha == null || senha.length() > 200 || !passwords.matches(senha, user.getSenha()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não foi possível confirmar sua identidade.");
        return user;
    }

    @Transactional
    public Map<String, Object> encerrar(Long id, String senha) {
        var user = autenticar(id, senha, true);
        if (user.getEncerradaEm() == null) {
            user.setEncerradaEm(LocalDateTime.now());
            user.setProtocoloEliminacao(UUID.randomUUID().toString());
            user.setAtivo(false); user.setBloqueado(true); user.setToken(null);
            usuarios.save(user);
        }
        return campos("protocolo", user.getProtocoloEliminacao(), "status", "CONTA_ENCERRADA_ELIMINACAO_EM_ANALISE",
                "mensagem", "Acesso encerrado. A eliminação dos dados exige análise de conservação. Guarde o protocolo e contate mindcare.diary@gmail.com.");
    }

    @Transactional(readOnly = true)
    public byte[] exportar(Long id, String senha) {
        var user = autenticar(id, senha, false);
        if (user.getEncerradaEm() != null) throw new ResponseStatusException(HttpStatus.GONE, "Conta encerrada.");
        var dados = new LinkedHashMap<String, Object>();
        dados.put("formato", "mindcare-exportacao-1"); dados.put("geradoEm", Instant.now());
        dados.put("cadastro", campos("nomeUsuario", user.getNomeUsuario(), "nomeCompleto", user.getNomeCompleto(),
                "dataNascimento", user.getDataNascimento(), "genero", user.getGenero(), "perfil", user.getUserRole()));
        dados.put("aceites", em.createQuery("select a from AceiteTermos a where a.usuario.id = :id order by a.aceitoEm", AceiteTermos.class)
                .setParameter("id", id).getResultList().stream().map(a -> campos("versao", a.getVersao(), "hashDocumentos", a.getHashDocumentos(), "aceitoEm", a.getAceitoEm())).toList());
        List<PrescriptionDocument> anexos = new ArrayList<>();
        if (user.getUserRole() == UserRole.PACIENTE) {
            dados.put("diario", em.createQuery("select r from RegistroDiario r where r.paciente.id = :id order by r.dataHoraCriacao", RegistroDiario.class)
                    .setParameter("id", id).getResultList().stream().map(r -> campos("id", r.getId(), "data", r.getDataHoraCriacao(), "origem", r.getOrigem(),
                            "humor", r.getNivelHumor(), "pontosPositivos", r.getPontosPositivos(), "dificuldades", r.getDificuldadesDesafios(), "textoConfirmado", r.getTextoConfirmado())).toList());
            dados.put("relatorios", em.createQuery("select r from RelatorioSemanal r where r.paciente.id = :id order by r.dataHoraCriacao", RelatorioSemanal.class)
                    .setParameter("id", id).getResultList().stream().map(r -> campos("id", r.getId(), "data", r.getDataHoraCriacao(), "periodo", r.getFaixaDeDatas(),
                            "resumoIA", r.getRelatorioIA(), "resumo", r.getResumo(), "observacoes", r.getObservacoes(), "recomendacoes", r.getRecomendacoes())).toList());
            dados.put("consultas", em.createQuery("select c from Consulta c where c.paciente.id = :id order by c.dataHoraConsulta", Consulta.class)
                    .setParameter("id", id).getResultList().stream().map(c -> campos("id", c.getId(), "numero", c.getNumero(), "data", c.getDataHoraConsulta(),
                            "modalidade", c.getConsultaModalidade(), "valor", c.getValorConsulta(), "atendida", c.isAtendida(), "cancelada", c.isCancelada())).toList());
            dados.put("prescricoes", em.createQuery("select p from Prescription p where p.paciente.id = :id order by p.issueDate", Prescription.class)
                    .setParameter("id", id).getResultList().stream().map(p -> {
                        var doc = p.getPrescriptionDocument();
                        if (doc != null && doc.getArquivoPdf() != null) anexos.add(doc);
                        return campos("id", p.getId(), "numero", p.getNumero(), "emissao", p.getIssueDate(), "validade", p.getExpirationDate(),
                                "medicamentos", new ArrayList<>(p.getMedicines()), "controlada", p.isControlled(), "valida", p.isValid(),
                                "arquivo", doc != null && doc.getArquivoPdf() != null ? "prescricoes/" + doc.getId() + ".pdf" : null);
                    }).toList());
        } else {
            dados.put("escopo", "Cadastro próprio e aceites. Dados de pacientes não integram a exportação da conta profissional/administrativa.");
        }
        try (var bytes = new ByteArrayOutputStream(); var zip = new ZipOutputStream(bytes, StandardCharsets.UTF_8)) {
            adicionar(zip, "dados.json", json.writerWithDefaultPrettyPrinter().writeValueAsBytes(dados));
            adicionar(zip, "LEIA-ME.txt", ("Cópia dos dados disponíveis da sua conta. Diário e Chat confirmado estão em dados.json. "
                    + "PDFs existentes de prescrições estão na pasta prescricoes. Áudio e conversas não salvas não são armazenados. "
                    + "Este arquivo contém dados pessoais: guarde-o em local privado. Dúvidas: mindcare.diary@gmail.com.").getBytes(StandardCharsets.UTF_8));
            for (var doc : anexos) adicionar(zip, "prescricoes/" + doc.getId() + ".pdf", doc.getArquivoPdf());
            zip.finish(); return bytes.toByteArray();
        } catch (IOException e) { throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível gerar a cópia."); }
    }

    private static void adicionar(ZipOutputStream zip, String nome, byte[] dados) throws IOException {
        zip.putNextEntry(new ZipEntry(nome)); zip.write(dados); zip.closeEntry();
    }
    private static Map<String, Object> campos(Object... pares) {
        var result = new LinkedHashMap<String, Object>();
        for (int i = 0; i < pares.length; i += 2) result.put((String) pares[i], pares[i + 1]);
        return result;
    }
}
