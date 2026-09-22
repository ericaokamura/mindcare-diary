package com.fiap.mindcare_diary.services.mia;

import com.fiap.mindcare_diary.mappers.RegistroDiarioMapper;
import com.fiap.mindcare_diary.models.RegistroDiario;
import com.fiap.mindcare_diary.models.dtos.MiaRegistroRequest;
import com.fiap.mindcare_diary.models.dtos.RegistroDiarioDTO;
import com.fiap.mindcare_diary.models.enums.NivelHumor;
import com.fiap.mindcare_diary.models.enums.OrigemRegistro;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.RegistroDiarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;

@Service
public class MiaRegistroService {
    public static final int MAX_TEXT_LENGTH = 20000;
    private final PacienteRepository patients;
    private final RegistroDiarioRepository records;

    public MiaRegistroService(PacienteRepository patients, RegistroDiarioRepository records) {
        this.patients = patients;
        this.records = records;
    }

    @Transactional
    public RegistroDiarioDTO save(Authentication authentication, MiaRegistroRequest request) {
        var user = MiaAuthentication.requirePatient(authentication);
        if (request == null || request.idRequisicao() == null || request.textoConfirmado() == null
                || request.textoConfirmado().isBlank() || request.textoConfirmado().length() > MAX_TEXT_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Confirme um texto de 1 a 20000 caracteres e um identificador de requisição.");
        }
        NivelHumor mood;
        try {
            mood = request.nivelHumor() == null || request.nivelHumor().isBlank()
                    ? NivelHumor.SEM_DEFINICAO : NivelHumor.valueOf(request.nivelHumor());
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Humor inválido.");
        }
        // Serialize saves for the same patient, including retries arriving concurrently.
        var patient = patients.findForDiaryUpdate(user.getNomeUsuario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Paciente não disponível."));
        if (!patient.isAtivo() || patient.isBloqueado()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Paciente não disponível.");
        }
        String text = request.textoConfirmado().strip();
        var existing = records.findByPacienteAndIdRequisicao(patient, request.idRequisicao());
        if (existing.isPresent()) {
            var record = existing.get();
            if (!text.equals(record.getTextoConfirmado()) || mood != record.getNivelHumor()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta requisição já foi salva com outro conteúdo. Consulte seu histórico.");
            }
            return RegistroDiarioMapper.convertModelToDTO(record);
        }
        var record = new RegistroDiario();
        record.setPaciente(patient);
        record.setTextoConfirmado(text);
        record.setOrigem(OrigemRegistro.CHAT);
        record.setNivelHumor(mood);
        record.setPontosPositivos("");
        record.setDificuldadesDesafios("");
        record.setDataHoraCriacao(LocalDateTime.now());
        record.setIdRequisicao(request.idRequisicao());
        return RegistroDiarioMapper.convertModelToDTO(records.saveAndFlush(record));
    }
}
