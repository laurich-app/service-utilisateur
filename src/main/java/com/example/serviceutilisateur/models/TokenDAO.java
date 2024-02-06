package com.example.serviceutilisateur.models;

import com.example.serviceutilisateur.dtos.out.TokenDTO;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TOKENS")
public class TokenDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TOKEN")
    private Long id;

    @Column(name = "DATE_CREATION")
    private LocalDateTime dateCreation;

    @Column(name = "USER_AGENT")
    private String userAgent;

    @Column(name = "ACCESS_TOKEN")
    private String accessToken;

    @Column(name = "REFRESH_TOKEN")
    private String refreshToken;

    @ManyToOne
    private UtilisateurDAO utilisateur;

    public static TokenDTO toDTO(TokenDAO tokenDAO) {
        return new TokenDTO(
                tokenDAO.getAccessToken(),
                tokenDAO.getRefreshToken()
        );
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UtilisateurDAO getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(UtilisateurDAO utilisateurDAO) {
        this.utilisateur = utilisateurDAO;
    }
}
