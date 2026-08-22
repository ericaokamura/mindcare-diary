package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.PacienteNaoEncontradoException;
import com.fiap.mindcare_diary.exceptions.PrescricaoNaoEncontradaException;
import com.fiap.mindcare_diary.exceptions.ProfissionalNaoEncontradoException;
import com.fiap.mindcare_diary.mappers.PrescriptionMapper;
import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.Prescription;
import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.dtos.PrescriptionDTO;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.PrescriptionRepository;
import com.fiap.mindcare_diary.repositories.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PrescricaoService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    public PrescriptionDTO retornarPrescricaoPorNumber(String pacienteNomeUsuario, String profissionalNomeUsuario, String number) {
        Optional<Profissional> optionalProfissional = profissionalRepository.findByNomeUsuario(profissionalNomeUsuario);
        if(optionalProfissional.isEmpty()) {
            throw new ProfissionalNaoEncontradoException("Profissional não encontrado.");
        }

        Optional<Paciente> optionalPaciente = pacienteRepository.findByNomeUsuario(pacienteNomeUsuario);
        if(optionalPaciente.isEmpty()) {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }

        Optional<Prescription> optionalPrescription = prescriptionRepository.findByPacienteAndProfissionalAndNumber(optionalPaciente.get(), optionalProfissional.get(), number);
        if(optionalPrescription.isEmpty()) {
            throw new PrescricaoNaoEncontradaException("Prescrição não encontrada para esse número, paciente e profissional.");
        }
        return PrescriptionMapper.convertModelToDTO(optionalPrescription.get());
    }
}
