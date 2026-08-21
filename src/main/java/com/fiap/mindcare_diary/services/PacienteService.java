package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.PacienteNaoEncontradoException;
import com.fiap.mindcare_diary.exceptions.ProfissionalNaoEncontradoException;
import com.fiap.mindcare_diary.mappers.PacienteMapper;
import com.fiap.mindcare_diary.mappers.PrescriptionMapper;
import com.fiap.mindcare_diary.mappers.ProfissionalMapper;
import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.Prescription;
import com.fiap.mindcare_diary.models.PrescriptionDocument;
import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.PrescriptionDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.models.enums.EstadoPaciente;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.PrescriptionRepository;
import com.fiap.mindcare_diary.repositories.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    private static final String DATE_FORMATTER = "yyyy-MM-dd";

    private Random random = new Random();

    public void salvarCadastroPaciente(PacienteDTO pacienteDTO) {
        Paciente paciente = PacienteMapper.convertDTOToModel(pacienteDTO);
        paciente.setAtivo(true);
        paciente.setDataHoraAtivacao(LocalDateTime.now());
        this.pacienteRepository.save(paciente);
    }

    public PacienteDTO retornarCadastroPaciente(String nomeUsuario) {
        Optional<Paciente> optionalPaciente = pacienteRepository.findByNomeUsuario(nomeUsuario);
        if(optionalPaciente.isPresent()) {
            return PacienteMapper.convertModelToDTO(optionalPaciente.get());
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }
    }

    public PacienteDTO selecionarProfissional(Long idProfissional, Long idPaciente) {
        Optional<Paciente> optionalPaciente = pacienteRepository.findById(idPaciente);
        if(optionalPaciente.isPresent()) {
            Paciente paciente = optionalPaciente.get();
            Optional<Profissional> optionalProfissional = profissionalRepository.findById(idProfissional);
            if(optionalProfissional.isPresent()) {
                Profissional profissional = optionalProfissional.get();
                paciente.getProfissionais().add(profissional);
                pacienteRepository.save(paciente);
                return PacienteMapper.convertModelToDTO(paciente);
            } else {
                throw new ProfissionalNaoEncontradoException("Profissional não encontrado.");
            }
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }
    }

    public PacienteDTO atualizarEstadoPaciente(Long idProfissional, Long idPaciente, String estadoPaciente) {
        Optional<Paciente> optionalPaciente = pacienteRepository.findById(idPaciente);
        if(optionalPaciente.isPresent()) {
            Paciente paciente = optionalPaciente.get();
            Optional<Profissional> optionalProfissional = profissionalRepository.findById(idProfissional);
            if(optionalProfissional.isPresent()) {
                paciente.setEstadoPaciente(EstadoPaciente.valueOf(estadoPaciente));
                pacienteRepository.save(paciente);
                return PacienteMapper.convertModelToDTO(paciente);
            } else {
                throw new ProfissionalNaoEncontradoException("Profissional não encontrado.");
            }
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }
    }

    public List<PrescriptionDTO> retornarPrescricoes(String nomeUsuario) {
        Optional<Paciente> optionalPaciente = pacienteRepository.findByNomeUsuario(nomeUsuario);
        if(optionalPaciente.isPresent()) {
            Paciente paciente = optionalPaciente.get();
            List<Prescription> prescricoes = paciente.getPrescricoes();
            return PrescriptionMapper.convertModelListToDTOList(prescricoes);
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }
    }

    public void salvarPrescricaoDePaciente(String nomeUsuario, String profissionalNomeUsuario, String issueDate, String expirationDate, String medicines, boolean controlled, MultipartFile arquivo) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        Integer numero = 100000 + random.nextInt(900000);
        validarPdf(arquivo);
        Optional<Paciente> optionalPaciente = pacienteRepository.findByNomeUsuario(nomeUsuario);
        if(optionalPaciente.isPresent()) {
            Paciente paciente = optionalPaciente.get();
            Prescription prescription = new Prescription();
            prescription.setPaciente(paciente);
            paciente.getPrescricoes().add(prescription);
            prescription.setIssueDate(LocalDate.parse(issueDate.trim(), formatter));
            prescription.setExpirationDate(LocalDate.parse(expirationDate, formatter));
            prescription.setControlled(controlled);
            prescription.setNumber(numero.toString());
            prescription.setMedicines(Arrays.asList(medicines.split(",")));
            prescription.setValid(LocalDate.now().isBefore(LocalDate.parse(expirationDate)));
            prescription.setDaysRemaining(ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(expirationDate)));
            PrescriptionDocument document = new PrescriptionDocument();
            document.setPrescription(prescription);
            document.setArquivoPdf(arquivo.getBytes());
            document.setNomeArquivo(arquivo.getOriginalFilename());
            document.setContentType(arquivo.getContentType());
            document.setTamanhoBytes(arquivo.getSize());
            document.setCriadoEm(LocalDateTime.now());
            prescription.setPrescriptionDocument(document);
            Optional<Profissional> optionalProfissional = profissionalRepository.findByNomeUsuario(profissionalNomeUsuario);
            if(optionalProfissional.isPresent()) {
                paciente.getProfissionais().add(optionalProfissional.get());
                pacienteRepository.save(paciente);
                prescription.setDoctorInfo(optionalProfissional.get());
                prescriptionRepository.save(prescription);
            } else {
                throw new ProfissionalNaoEncontradoException("Profissional não encontrado.");
            }
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }
    }

    private void validarPdf(MultipartFile arquivo) {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException(
                    "Arquivo PDF não informado"
            );
        }

        if (!"application/pdf".equalsIgnoreCase(
                arquivo.getContentType())) {

            throw new IllegalArgumentException(
                    "O arquivo deve ser um PDF"
            );
        }
    }
}
