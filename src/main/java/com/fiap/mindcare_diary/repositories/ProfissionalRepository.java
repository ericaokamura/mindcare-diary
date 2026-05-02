package com.fiap.mindcare_diary.repositories;

import com.fiap.mindcare_diary.models.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {

    Optional<Profissional> findByNomeUsuario(String nomeUsuario);
}
