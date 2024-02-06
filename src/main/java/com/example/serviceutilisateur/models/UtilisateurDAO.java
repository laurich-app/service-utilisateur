package com.example.serviceutilisateur.models;

import com.example.serviceutilisateur.enums.RolesENUM;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "UTILISATEURS")
public class UtilisateurDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_UTILISATEUR")
    private Long id;

    @Column(name = "PSEUDO")
    private String pseudo;

    @Column(name = "MOT_DE_PASSE")
    private String motDePasse;

    @Column(name = "EMAIL")
    private String email;

    @ElementCollection
    @CollectionTable(name = "ROLES")
    @Enumerated(EnumType.STRING)
    private List<RolesENUM> roles;

    @OneToMany(mappedBy = "utilisateur")
    private List<TokenDAO> tokens;

    public Long getId() {
        return id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String mot_de_passe) {
        this.motDePasse = mot_de_passe;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<RolesENUM> getRoles() {
        return roles;
    }

    public void setRoles(List<RolesENUM> roles) {
        this.roles = roles;
    }

    public List<TokenDAO> getTokens() {
        return tokens;
    }

    public void setTokens(List<TokenDAO> tokens) {
        this.tokens = tokens;
    }
}
