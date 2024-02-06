package com.example.serviceutilisateur.repositories;

import com.example.serviceutilisateur.models.TokenDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<TokenDAO, Long> {
    @Query("SELECT p FROM TokenDAO p WHERE p.accessToken = :accessToken AND p.refreshToken = :refreshToken")
    TokenDAO findByTokens(String accessToken, String refreshToken);

    TokenDAO findByAccessToken(String accessToken);
}
