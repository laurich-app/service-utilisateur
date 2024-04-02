package com.example.serviceutilisateur.controllers;

import com.example.serviceutilisateur.dtos.in.ConnexionDTO;
import com.example.serviceutilisateur.dtos.in.RefreshTokenDTO;
import com.example.serviceutilisateur.dtos.out.TokenDTO;
import com.example.serviceutilisateur.exceptions.*;
import com.example.serviceutilisateur.facades.FacadeAuthentification;
import com.example.serviceutilisateur.facades.FacadeAuthentificationImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final FacadeAuthentification facadeAuthentification;

    public AuthController(@Autowired FacadeAuthentificationImpl facadeAuthentification) {
        this.facadeAuthentification = facadeAuthentification;
    }

    @PostMapping("/connexion")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody ConnexionDTO loginDTO, @RequestHeader("User-Agent") String userAgent) {
        try {
            String email = loginDTO.email();
            logger.info("[Auth - Login] {} {}", email, userAgent);
            TokenDTO tokenDTO = this.facadeAuthentification.connexion(loginDTO, userAgent);
            return ResponseEntity.ok().header("Authorization", "Bearer " +tokenDTO.accessToken()).body(tokenDTO);
        } catch (UtilisateurInconnueException e) {
            logger.error("[Auth - Login] {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/token")
    public ResponseEntity<TokenDTO> tokenRaffraichissement(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO, @RequestHeader("User-Agent") String userAgent) {
        try {
            logger.info("[Auth - Token Raffraichissement] {} {}", refreshTokenDTO, userAgent);
            TokenDTO tokenDTO = this.facadeAuthentification.genereTokenRaffraichissement(refreshTokenDTO, userAgent);
            return ResponseEntity.ok().header("Authorization", "Bearer " +tokenDTO.accessToken()).body(tokenDTO);
        } catch (TokenIncompatibleException e) {
            logger.error("[Auth - Token Raffraichissement] {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (RefreshTokenExpirerException e) {
            logger.error("[Auth - Token Raffraichissement] {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/connexion")
    public ResponseEntity<Object> deconnexion(@RequestHeader("Authorization") String authorization) {
        try {
            logger.info("[Auth - Deconnexion] {}", authorization);
            String[] bearer = authorization.split(" ");
            this.facadeAuthentification.deconnexion(bearer[1]);
            return ResponseEntity.noContent().build();
        }  catch (TokenInconnueException e) {
            logger.error("[Auth - Deconnexion] {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
