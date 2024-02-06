package com.example.serviceutilisateur.facades;

import com.example.serviceutilisateur.dtos.out.UtilisateurOutDTO;
import com.example.serviceutilisateur.dtos.pagination.Paginate;
import com.example.serviceutilisateur.dtos.pagination.PaginateRequestDTO;
import com.example.serviceutilisateur.dtos.pagination.Pagination;
import com.example.serviceutilisateur.exceptions.UtilisateurInconnueException;
import com.example.serviceutilisateur.models.UtilisateurDAO;
import com.example.serviceutilisateur.repositories.TokenRepository;
import com.example.serviceutilisateur.repositories.UtilisateurRepository;
import com.example.serviceutilisateur.services.TokenService;
import com.example.serviceutilisateur.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FacadeUtilisateurImpl implements FacadeUtilisateur {
    private final UtilisateurRepository utilisateurRepository;

    public FacadeUtilisateurImpl(
            @Autowired UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UtilisateurOutDTO getUserById(Long id) throws UtilisateurInconnueException {
        Optional<UtilisateurDAO> utilisateurDAO = this.utilisateurRepository.findById(id);
        if(utilisateurDAO.isEmpty())
            throw new UtilisateurInconnueException();

        return UtilisateurDAO.toDTO(utilisateurDAO.get());
    }

    @Override
    public Paginate<UtilisateurOutDTO> getUsers(PaginateRequestDTO paginateRequestDTO) {
        Pageable pageable = PageableUtils.convert(paginateRequestDTO);
        Page<UtilisateurDAO> paginated = this.utilisateurRepository.findAll(pageable);

        // Convertir les objets BlogDAO en BlogDTO en utilisant la fabrique
        List<UtilisateurOutDTO> dtos = paginated.stream()
                .map(UtilisateurDAO::toDTO)
                .collect(Collectors.toList());

        // Créer un objet Paginate contenant les blogs paginés
        Paginate<UtilisateurOutDTO> paginate = new Paginate<>(dtos, new Pagination(Math.toIntExact(paginated.getTotalElements()),
                paginateRequestDTO.limit(), paginateRequestDTO.page()));

        // Retourner la liste des objets Paginate
        return paginate;
    }
}
