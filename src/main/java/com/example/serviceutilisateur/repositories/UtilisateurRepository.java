package com.example.serviceutilisateur.repositories;

import com.example.serviceutilisateur.models.UtilisateurDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilisateurRepository extends JpaRepository<UtilisateurDAO, Long> {
    @Query("SELECT p FROM UtilisateurDAO p WHERE p.email = :email")
    UtilisateurDAO findByEmail(String email);
}
