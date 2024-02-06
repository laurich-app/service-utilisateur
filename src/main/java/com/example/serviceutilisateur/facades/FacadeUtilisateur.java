package com.example.serviceutilisateur.facades;

import com.example.serviceutilisateur.dtos.out.UtilisateurOutDTO;
import com.example.serviceutilisateur.dtos.pagination.Paginate;
import com.example.serviceutilisateur.dtos.pagination.PaginateRequestDTO;
import com.example.serviceutilisateur.exceptions.UtilisateurInconnueException;

public interface FacadeUtilisateur {
    /**
     * Renvoie l'utilisateur connecté.
     * @param id
     * @return
     */
    public UtilisateurOutDTO getUserById(Long id) throws UtilisateurInconnueException;

    /**
     * Permet de récupérer les utilisateurs de la plateforme pour l'ADMIN.
     * @param paginateRequestDTO
     * @return
     */
    public Paginate<UtilisateurOutDTO> getUsers(PaginateRequestDTO paginateRequestDTO);
}
