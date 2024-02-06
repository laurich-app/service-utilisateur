package com.example.serviceutilisateur.repositories;

import com.example.serviceutilisateur.models.UtilisateurDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilisateurRepository extends JpaRepository<UtilisateurDAO, Long> {
    UtilisateurDAO findByEmail(String email);
}
