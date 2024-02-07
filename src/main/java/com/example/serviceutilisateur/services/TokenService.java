package com.example.serviceutilisateur.services;

import com.example.serviceutilisateur.models.TokenDAO;
import com.example.serviceutilisateur.models.UtilisateurDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.function.Function;

@Service
public class TokenService {

    private final Function<UtilisateurDAO, String> genererAccessToken;
    private final Function<UtilisateurDAO, String> genererRefreshToken;
    private final Function<String, Boolean> verifyRToken;

    public TokenService(
            @Autowired Function<UtilisateurDAO, String> genererAccessToken,
            @Autowired Function<UtilisateurDAO, String> genererRefreshToken,
            @Autowired Function<String, Boolean> vrt
    ) {
        this.genererAccessToken = genererAccessToken;
        this.genererRefreshToken = genererRefreshToken;
        this.verifyRToken = vrt;
    }

    public TokenDAO genereToken(UtilisateurDAO utilisateurDAO, String userAgent) {
        TokenDAO tokenDAO = new TokenDAO();
        tokenDAO.setAccessToken(this.genererAccessToken.apply(utilisateurDAO));
        tokenDAO.setRefreshToken(this.genererRefreshToken.apply(utilisateurDAO));
        tokenDAO.setUserAgent(userAgent);
        tokenDAO.setDateCreation(LocalDateTime.now());
        tokenDAO.setUtilisateur(utilisateurDAO);
        return tokenDAO;
    }

    public Boolean verifRefreshToken(String refreshToken) {
        return this.verifyRToken.apply(refreshToken);
    }
}
