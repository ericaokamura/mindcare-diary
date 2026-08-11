package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.PrescricaoNaoEncontrada;
import com.fiap.mindcare_diary.mappers.PrescriptionMapper;
import com.fiap.mindcare_diary.models.Prescription;
import com.fiap.mindcare_diary.models.dtos.PrescriptionDTO;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PrescricaoService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    public PrescriptionDTO retornarPrescricaoPorNumber(String number) {
        Optional<Prescription> optionalPrescription = prescriptionRepository.findByNumber(number);
        if(optionalPrescription.isEmpty()) {
            throw new PrescricaoNaoEncontrada("Prescrição não encontrada para esse número.");
        }
        return PrescriptionMapper.convertModelToDTO(optionalPrescription.get());
    }
}
