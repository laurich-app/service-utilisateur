package com.example.serviceutilisateur.facades;

import com.example.serviceutilisateur.dtos.in.ConnexionDTO;
import com.example.serviceutilisateur.dtos.in.InscriptionControllerDTO;
import com.example.serviceutilisateur.dtos.in.InscriptionDTO;
import com.example.serviceutilisateur.dtos.in.RefreshTokenDTO;
import com.example.serviceutilisateur.dtos.out.InscriptionControllerOutDTO;
import com.example.serviceutilisateur.dtos.out.TokenDTO;
import com.example.serviceutilisateur.dtos.out.UtilisateurOutDTO;
import com.example.serviceutilisateur.dtos.pagination.Paginate;
import com.example.serviceutilisateur.dtos.pagination.PaginateRequestDTO;
import com.example.serviceutilisateur.exceptions.*;

public interface FacadeAuthentification {
    /**
     * Permet d'authentifier un utilisateur
     * @param connexionDTO
     * @return
     */
    public TokenDTO connexion(ConnexionDTO connexionDTO, String userAgent) throws UtilisateurInconnueException;

    /**
     * Permet d'inscrire un utilisateur
     * @param inscriptionDTO
     * @return
     */
    public InscriptionControllerOutDTO inscription(InscriptionDTO inscriptionDTO, String userAgent) throws EmailDejaPrisException;

    /**
     * Permet de regénérer une paire de token d'accès.
     * @param refreshTokenDTO
     * @param userAgent
     * @return
     * @throws TokenIncompatibleException
     * @throws RefreshTokenExpirerException
     */
    public TokenDTO genereTokenRaffraichissement(RefreshTokenDTO refreshTokenDTO, String userAgent) throws TokenIncompatibleException, RefreshTokenExpirerException;

    /**
     * Permet de déconnecter l'utilisateur authentifier.
     * @param accessToken
     */
    public void deconnexion(String accessToken) throws TokenInconnueException;
}
