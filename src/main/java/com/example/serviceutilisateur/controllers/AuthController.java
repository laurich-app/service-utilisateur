package com.example.serviceutilisateur.controllers;

import com.example.serviceutilisateur.dtos.in.ConnexionDTO;
import com.example.serviceutilisateur.dtos.in.InscriptionControllerDTO;
import com.example.serviceutilisateur.dtos.in.InscriptionDTO;
import com.example.serviceutilisateur.dtos.in.RefreshTokenDTO;
import com.example.serviceutilisateur.dtos.out.InscriptionControllerOutDTO;
import com.example.serviceutilisateur.dtos.out.TokenDTO;
import com.example.serviceutilisateur.exceptions.*;
import com.example.serviceutilisateur.facades.FacadeAuthentification;
import com.example.serviceutilisateur.facades.FacadeAuthentificationImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final FacadeAuthentification facadeAuthentification;
    private final PasswordEncoder passwordEncoder;

    public AuthController(@Autowired FacadeAuthentificationImpl facadeAuthentification, @Autowired PasswordEncoder passwordEncoder ) {
        this.facadeAuthentification = facadeAuthentification;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/connexion")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody ConnexionDTO loginDTO, @RequestHeader("User-Agent") String userAgent) {
        try {
            TokenDTO tokenDTO = this.facadeAuthentification.connexion(loginDTO, userAgent);
            return ResponseEntity.ok().header("Authorization", "Bearer " +tokenDTO.accessToken()).build();
        } catch (UtilisateurInconnueException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/inscription")
    public ResponseEntity<TokenDTO> register(@Valid @RequestBody InscriptionControllerDTO registerDTO, @RequestHeader("User-Agent") String userAgent) {
        InscriptionDTO inscriptionDTO = new InscriptionDTO(
                registerDTO.pseudo(),
                registerDTO.email(),
                this.passwordEncoder.encode(registerDTO.motDePasse()));
        try {
            InscriptionControllerOutDTO inscription = this.facadeAuthentification.inscription(inscriptionDTO, userAgent);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{pseudo}")
                .buildAndExpand(inscription.utilisateur().id()).toUri();
            return ResponseEntity.created(location).header("Authorization", "Bearer " +inscription.tokenDTO().accessToken()).build();
        } catch (EmailDejaPrisException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/token_raffraichissement")
    public ResponseEntity<TokenDTO> tokenRaffraichissement(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO, @RequestHeader("User-Agent") String userAgent, Principal principal) {
        try {
            TokenDTO tokenDTO = this.facadeAuthentification.genereTokenRaffraichissement(refreshTokenDTO, Long.valueOf(principal.getName()), userAgent);
            return ResponseEntity.ok(tokenDTO);
        } catch (TokenIncompatibleException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (RefreshTokenExpirerException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (UtilisateurInconnueException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/deconnexion")
    public ResponseEntity deconnexion(@RequestHeader("Authorization") String authorization) {
        try {
            String[] bearer = authorization.split(" ");
            this.facadeAuthentification.deconnexion(bearer[1]);
            return ResponseEntity.noContent().build();
        }  catch (TokenInconnueException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
