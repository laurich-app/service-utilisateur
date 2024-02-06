package com.example.serviceutilisateur.controllers;

import com.example.serviceutilisateur.dtos.out.UtilisateurOutDTO;
import com.example.serviceutilisateur.dtos.pagination.Paginate;
import com.example.serviceutilisateur.dtos.pagination.PaginateRequestDTO;
import com.example.serviceutilisateur.exceptions.UtilisateurInconnueException;
import com.example.serviceutilisateur.facades.FacadeUtilisateur;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserController {
    private final FacadeUtilisateur facadeUtilisateur;
    private final Validator validator;

    public UserController(@Autowired FacadeUtilisateur facadeUtilisateur, @Autowired Validator validator) {
        this.facadeUtilisateur = facadeUtilisateur;
        this.validator = validator;
    }

    @GetMapping("/me")
    public ResponseEntity<UtilisateurOutDTO> me(Principal principal) {
        try {
            UtilisateurOutDTO utilisateur = this.facadeUtilisateur.getUserById(Long.valueOf(principal.getName()));
            return ResponseEntity.ok(utilisateur);
        } catch (UtilisateurInconnueException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurOutDTO> getUserById(@RequestParam(name = "id") Long id) {
        try {
            UtilisateurOutDTO utilisateur = this.facadeUtilisateur.getUserById(id);
            return ResponseEntity.ok(utilisateur);
        } catch (UtilisateurInconnueException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Secured("ROLE_ADMIN")
    @GetMapping()
    public ResponseEntity<Paginate<UtilisateurOutDTO>> getUsers(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "sortDirection", required = false) Sort.Direction sortDirection) {

        PaginateRequestDTO paginateRequest = new PaginateRequestDTO(page, limit, sort, sortDirection);
        Set<ConstraintViolation<PaginateRequestDTO>> violations = this.validator.validate(paginateRequest);
        if(!violations.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Paginate<UtilisateurOutDTO> utilisateur = this.facadeUtilisateur.getUsers(paginateRequest);
        return ResponseEntity.ok(utilisateur);
    }
}
