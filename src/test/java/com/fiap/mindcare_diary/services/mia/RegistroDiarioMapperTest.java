package com.fiap.mindcare_diary.services.mia;

import com.fiap.mindcare_diary.mappers.RegistroDiarioMapper;
import com.fiap.mindcare_diary.models.dtos.RegistroDiarioDTO;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.enums.NivelHumor;
import com.fiap.mindcare_diary.models.enums.OrigemRegistro;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegistroDiarioMapperTest {
    @Test void legacyInputDoesNotRequireDateOrTrustClientOwnership() {
        var dto = new RegistroDiarioDTO(); dto.setDataHoraCriacao(""); dto.setPaciente(new PacienteDTO());
        dto.setOrigem("CHAT"); dto.setTextoConfirmado("não deve entrar pelo fluxo tradicional");
        var model = RegistroDiarioMapper.convertDTOToModel(dto);
        assertNull(model.getPaciente()); assertNull(model.getDataHoraCriacao());
        assertNull(model.getTextoConfirmado());
        assertEquals(OrigemRegistro.TRADITIONAL, model.getOrigem());
        assertEquals(NivelHumor.SEM_DEFINICAO, model.getNivelHumor());
    }
}
