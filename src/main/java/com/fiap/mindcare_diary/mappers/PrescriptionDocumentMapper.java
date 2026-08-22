package com.fiap.mindcare_diary.mappers;

import com.fiap.mindcare_diary.models.Prescription;
import com.fiap.mindcare_diary.models.PrescriptionDocument;
import com.fiap.mindcare_diary.models.dtos.PrescriptionDocumentDTO;

public class PrescriptionDocumentMapper {

    public static PrescriptionDocumentDTO convertModelToDTO(PrescriptionDocument model){
        PrescriptionDocumentDTO dto = new PrescriptionDocumentDTO();
        dto.setArquivoPdf(model.getArquivoPdf());
        dto.setContentType(model.getContentType());
        dto.setCriadoEm(model.getCriadoEm());
        dto.setTamanhoBytes(model.getTamanhoBytes());
        dto.setNomeArquivo(model.getNomeArquivo());
        return dto;
    }

    public static PrescriptionDocument convertDTOToModel(PrescriptionDocumentDTO dto) {
        PrescriptionDocument model = new PrescriptionDocument();
        model.setArquivoPdf(dto.getArquivoPdf());
        model.setContentType(dto.getContentType());
        model.setCriadoEm(dto.getCriadoEm());
        model.setTamanhoBytes(dto.getTamanhoBytes());
        model.setNomeArquivo(dto.getNomeArquivo());
        return model;
    }
}
