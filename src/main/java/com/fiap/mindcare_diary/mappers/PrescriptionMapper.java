package com.fiap.mindcare_diary.mappers;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.Prescription;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.PrescriptionDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionMapper {

    public static PrescriptionDTO convertModelToDTO(Prescription prescription) {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setNumber(prescription.getNumber());
        dto.setIssueDate(prescription.getIssueDate().toString());
        dto.setExpirationDate(prescription.getExpirationDate().toString());
        dto.setMedicines(prescription.getMedicines());
        dto.setDaysRemaining(prescription.getDaysRemaining());
        dto.setValid(prescription.isValid());
        dto.setControlled(prescription.isControlled());
        dto.setDoctorInfo(ProfissionalMapper.convertModelToDTO(prescription.getDoctorInfo()));
        dto.setPrescriptionDocument(PrescriptionDocumentMapper.convertModelToDTO(prescription.getPrescriptionDocument()));
        return dto;
    }

    public static Prescription convertDTOToModel(PrescriptionDTO dto) {
        Prescription model = new Prescription();
        model.setNumber(dto.getNumber());
        model.setIssueDate(LocalDate.parse(dto.getIssueDate()));
        model.setExpirationDate(LocalDate.parse(dto.getExpirationDate()));
        model.setMedicines(dto.getMedicines());
        model.setDaysRemaining(dto.getDaysRemaining());
        model.setValid(dto.isValid());
        model.setControlled(dto.isControlled());
        model.setDoctorInfo(ProfissionalMapper.convertDTOToModel(dto.getDoctorInfo()));
        model.setPrescriptionDocument(PrescriptionDocumentMapper.convertDTOToModel(dto.getPrescriptionDocument()));
        return model;
    }

    public static List<PrescriptionDTO> convertModelListToDTOList(List<Prescription> prescriptions) {
        List<PrescriptionDTO> dtos = new ArrayList<>();
        prescriptions.forEach(p -> {
            dtos.add(convertModelToDTO(p));
        });
        return dtos;
    }

    public static List<Prescription> convertDTOListToModelList(List<PrescriptionDTO> dtos) {
        List<Prescription> prescriptions = new ArrayList<>();
        dtos.forEach(p -> {
            prescriptions.add(convertDTOToModel(p));
        });
        return prescriptions;
    }
}
