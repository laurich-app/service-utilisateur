package com.example.serviceutilisateur.controllers;

import com.example.serviceutilisateur.dtos.ConnexionDTO;
import com.example.serviceutilisateur.dtos.InscriptionDTO;
import com.example.serviceutilisateur.facades.FacadeAuthentification;
import com.example.serviceutilisateur.facades.FacadeAuthentificationImpl;
import com.example.serviceutilisateur.models.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.function.Function;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final FacadeAuthentification facadeAuthentification;
    private final PasswordEncoder passwordEncoder;
    private final Function<Utilisateur, String> genererToken;

    public AuthController(@Autowired FacadeAuthentificationImpl facadeAuthentification, @Autowired PasswordEncoder passwordEncoder, @Autowired Function<Utilisateur, String> genererToken ) {
        this.facadeAuthentification = facadeAuthentification;
        this.passwordEncoder = passwordEncoder;
        this.genererToken = genererToken;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody ConnexionDTO loginDTO) {
        String token = this.genererToken.apply(new Utilisateur());
        return ResponseEntity.ok().header("Authorization", "Bearer " +token).build();
//        Joueur joueur;
//        try {
//            joueur = this.facadeJoueur.getJoueurByPseudo(loginDTO.pseudo());
//        } catch (JoueurInexistantException e) {
//            return ResponseEntity.notFound().build();
//        }
//        if(passwordEncoder.matches(loginDTO.mdp(), joueur.getMdpJoueur())){
//            String token = this.genererToken.apply(joueur);
//            return ResponseEntity.ok().header("Authorization", "Bearer " +token).build();
//        }
//        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody InscriptionDTO registerDTO) {
        return ResponseEntity.ok().build();
//        Joueur joueur;
//        try {
//            joueur = this.facadeJoueur.inscription(registerDTO.nouveauJoueur(), this.passwordEncoder.encode(
//                    registerDTO.mdp()));
//        } catch(PseudoDejaPrisException p) {
//            return ResponseEntity.badRequest().build();
//        }
//        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{pseudo}")
//                .buildAndExpand(joueur.getNomJoueur()).toUri();
//        return ResponseEntity.created(location).build();
    }
}
