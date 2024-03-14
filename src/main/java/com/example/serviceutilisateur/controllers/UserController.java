package com.example.serviceutilisateur.controllers;

import com.example.serviceutilisateur.dtos.out.UtilisateurOutDTO;
import com.example.serviceutilisateur.dtos.pagination.Paginate;
import com.example.serviceutilisateur.dtos.pagination.PaginateRequestDTO;
import com.example.serviceutilisateur.enums.RolesENUM;
import com.example.serviceutilisateur.exceptions.UtilisateurInconnueException;
import com.example.serviceutilisateur.facades.FacadeUtilisateur;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final FacadeUtilisateur facadeUtilisateur;
    private final Validator validator;

    public UserController(@Autowired FacadeUtilisateur facadeUtilisateur, @Autowired Validator validator) {
        this.facadeUtilisateur = facadeUtilisateur;
        this.validator = validator;
    }

    @GetMapping("/me")
    public ResponseEntity<UtilisateurOutDTO> me(Principal principal) {
        try {
            logger.info("[Users - Me] {}", principal.getName());
            UtilisateurOutDTO utilisateur = this.facadeUtilisateur.getUserById(Long.valueOf(principal.getName()));
            return ResponseEntity.ok(utilisateur);
        } catch (UtilisateurInconnueException e) {
            logger.error("[Users - Me] {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurOutDTO> getUserById(@PathVariable(name = "id") Long id) {
        try {
            logger.info("[Users - getUserById] {}", id);
            UtilisateurOutDTO utilisateur = this.facadeUtilisateur.getUserById(id);
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
