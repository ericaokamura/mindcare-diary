package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.PacienteNaoEncontradoException;
import com.fiap.mindcare_diary.exceptions.PrescricaoNaoEncontradaException;
import com.fiap.mindcare_diary.mappers.PrescriptionMapper;
import com.fiap.mindcare_diary.models.Paciente;
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

    @Autowired
    private PacienteRepository pacienteRepository;

    public PrescriptionDTO retornarPrescricaoPorNumber(String pacienteNomeUsuario, String number) {
        Optional<Paciente> optionalPaciente = pacienteRepository.findByNomeUsuario(pacienteNomeUsuario);
        if(optionalPaciente.isEmpty()) {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }
        Optional<Prescription> optionalPrescription = prescriptionRepository.findByPacienteAndNumber(optionalPaciente.get(), number);
        if(optionalPrescription.isEmpty()) {
            throw new PrescricaoNaoEncontradaException("Prescrição não encontrada para esse número e paciente.");
        }
        return PrescriptionMapper.convertModelToDTO(optionalPrescription.get());
    }
}
