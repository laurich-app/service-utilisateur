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
import com.example.serviceutilisateur.dtos.pagination.Pagination;
import com.example.serviceutilisateur.enums.RolesENUM;
import com.example.serviceutilisateur.exceptions.*;
import com.example.serviceutilisateur.models.TokenDAO;
import com.example.serviceutilisateur.models.UtilisateurDAO;
import com.example.serviceutilisateur.repositories.TokenRepository;
import com.example.serviceutilisateur.repositories.UtilisateurRepository;
import com.example.serviceutilisateur.services.ServiceRabbitMQSender;
import com.example.serviceutilisateur.utils.PageableUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.serviceutilisateur.services.TokenService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FacadeAuthentificationImpl implements FacadeAuthentification {
    private final TokenRepository tokenRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final ServiceRabbitMQSender serviceRabbitMQSender;

    public FacadeAuthentificationImpl(
            @Autowired TokenRepository tokenRepository,
            @Autowired UtilisateurRepository utilisateurRepository,
            @Autowired PasswordEncoder passwordEncoder,
            @Autowired TokenService tokenService,
            @Autowired ServiceRabbitMQSender serviceRabbitMQSender) {
        this.tokenRepository = tokenRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.serviceRabbitMQSender = serviceRabbitMQSender;
    }

    @Override
    public TokenDTO connexion(ConnexionDTO connexionDTO, String userAgent) throws UtilisateurInconnueException {
        UtilisateurDAO utilisateurDAO = this.utilisateurRepository.findByEmail(connexionDTO.email());
        if(utilisateurDAO == null)
            throw new UtilisateurInconnueException("Utilisateur non trouvé");

        if(passwordEncoder.matches(connexionDTO.motDePasse(), utilisateurDAO.getMotDePasse())){
            TokenDAO tokenDAO = this.tokenRepository.save(this.tokenService.genereToken(utilisateurDAO, userAgent));
            return TokenDAO.toDTO(tokenDAO);
        }
        throw new UtilisateurInconnueException("Pair mot de passe / utilisateur invalide");
    }

    @Override
    @Transactional
    public InscriptionControllerOutDTO inscription(InscriptionDTO inscriptionDTO, String userAgent) throws EmailDejaPrisException {
        UtilisateurDAO utilisateurDAO = UtilisateurDAO.fromDTO(inscriptionDTO);

        utilisateurDAO.setRoles(List.of(RolesENUM.USER));

        if(this.utilisateurRepository.findByEmail(utilisateurDAO.getEmail()) != null)
            throw new EmailDejaPrisException();

        utilisateurDAO = this.utilisateurRepository.save(utilisateurDAO);
        // Génère des clés
        TokenDAO tokenDAO = this.tokenRepository.save(this.tokenService.genereToken(utilisateurDAO, userAgent));

        this.serviceRabbitMQSender.inscriptionBienvenue(
                UtilisateurDAO.toInscriptionBienvenueDTO(utilisateurDAO)
        );
        return new InscriptionControllerOutDTO(
                TokenDAO.toDTO(tokenDAO),
                UtilisateurDAO.toDTO(utilisateurDAO)
        );
    }

    @Override
    public TokenDTO genereTokenRaffraichissement(RefreshTokenDTO refreshTokenDTO, String userAgent) throws TokenIncompatibleException, RefreshTokenExpirerException {
        TokenDAO tokenDAO = this.tokenRepository.findByTokens(refreshTokenDTO.accessToken(), refreshTokenDTO.refreshToken());
        if(Objects.isNull(tokenDAO))
            throw new TokenIncompatibleException();

        if(Boolean.FALSE.equals(this.tokenService.verifRefreshToken(refreshTokenDTO.refreshToken())))
            throw new RefreshTokenExpirerException();

        // Suppression des anciens tokens
        this.tokenRepository.delete(tokenDAO);

        // Création des nouveaux
        TokenDAO token = this.tokenRepository.save(this.tokenService.genereToken(tokenDAO.getUtilisateur(), userAgent));
        return TokenDAO.toDTO(token);
    }

    @Override
    public void deconnexion(String accessToken) throws TokenInconnueException {
        TokenDAO tokenDAO = this.tokenRepository.findByAccessToken(accessToken);
        if(Objects.isNull(tokenDAO))
            throw new TokenInconnueException();

        this.tokenRepository.delete(tokenDAO);
    }
}
