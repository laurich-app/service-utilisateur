package com.example.serviceutilisateur.controllers;

import com.example.serviceutilisateur.dtos.in.InscriptionControllerDTO;
import com.example.serviceutilisateur.dtos.in.InscriptionDTO;
import com.example.serviceutilisateur.dtos.out.InscriptionControllerOutDTO;
import com.example.serviceutilisateur.dtos.out.TokenDTO;
import com.example.serviceutilisateur.dtos.out.UtilisateurOutDTO;
import com.example.serviceutilisateur.dtos.pagination.Paginate;
import com.example.serviceutilisateur.dtos.pagination.PaginateRequestDTO;
import com.example.serviceutilisateur.exceptions.EmailDejaPrisException;
import com.example.serviceutilisateur.exceptions.UtilisateurInconnueException;
import com.example.serviceutilisateur.facades.FacadeAuthentification;
import com.example.serviceutilisateur.facades.FacadeAuthentificationImpl;
import com.example.serviceutilisateur.facades.FacadeUtilisateur;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final FacadeUtilisateur facadeUtilisateur;
    private final Validator validator;
    private final FacadeAuthentification facadeAuthentification;
    private final PasswordEncoder passwordEncoder;

    public UserController(@Autowired FacadeAuthentificationImpl facadeAuthentification, @Autowired PasswordEncoder passwordEncoder, @Autowired FacadeUtilisateur facadeUtilisateur, @Autowired Validator validator) {
        this.facadeUtilisateur = facadeUtilisateur;
        this.validator = validator;
        this.facadeAuthentification = facadeAuthentification;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping()
    public ResponseEntity<TokenDTO> register(@Valid @RequestBody InscriptionControllerDTO registerDTO, @RequestHeader("User-Agent") String userAgent) {
        InscriptionDTO inscriptionDTO = new InscriptionDTO(
                registerDTO.pseudo(),
                registerDTO.email(),
                this.passwordEncoder.encode(registerDTO.motDePasse()));
        try {
            String email = registerDTO.email();
            logger.info("[Auth - Inscription] {} {}", email, userAgent);
            InscriptionControllerOutDTO inscription = this.facadeAuthentification.inscription(inscriptionDTO, userAgent);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{pseudo}")
                    .buildAndExpand(inscription.utilisateur().id()).toUri();
            return ResponseEntity.created(location).header("Authorization", "Bearer " +inscription.tokenDTO().accessToken()).body(inscription.tokenDTO());
        } catch (EmailDejaPrisException e) {
            logger.error("[Auth - Inscription] {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.username or hasRole('GESTIONNAIRE')")
    public ResponseEntity<UtilisateurOutDTO> getUserById(@PathVariable(name = "id") String id) {
        try {
            logger.info("[Users - getUserById] {}", id);
            UtilisateurOutDTO utilisateur = this.facadeUtilisateur.getUserById(Long.valueOf(id));
            return ResponseEntity.ok(utilisateur);
        } catch (UtilisateurInconnueException e) {
            logger.error("[Users - getUserById] {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping()
    @PreAuthorize("hasRole('GESTIONNAIRE')")
    public ResponseEntity<Paginate<UtilisateurOutDTO>> getUsers(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "sortDirection", required = false) Sort.Direction sortDirection) {

        PaginateRequestDTO paginateRequest = new PaginateRequestDTO(page, limit, sort, sortDirection);
        logger.info("[Users - getUsers] {}", paginateRequest);
        Set<ConstraintViolation<PaginateRequestDTO>> violations = this.validator.validate(paginateRequest);
        if(!violations.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Paginate<UtilisateurOutDTO> utilisateur = this.facadeUtilisateur.getUsers(paginateRequest);
        return ResponseEntity.ok(utilisateur);
    }
}
