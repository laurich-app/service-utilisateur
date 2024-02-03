package com.example.serviceutilisateur.models;

import java.util.Random;
import java.util.UUID;

public class Utilisateur {
    private Long id;
    private String pseudo;
    private String mot_de_passe;
    private String email;

    public Long getId() {
        return new Random().nextLong();
//        return id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getMot_de_passe() {
        return mot_de_passe;
    }

    public void setMot_de_passe(String mot_de_passe) {
        this.mot_de_passe = mot_de_passe;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
