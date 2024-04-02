package com.example.serviceutilisateur.controllers;

import com.example.serviceutilisateur.enums.RolesENUM;
import com.example.serviceutilisateur.models.UtilisateurDAO;
import com.example.serviceutilisateur.repositories.UtilisateurRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing
@SpringBootTest(properties = { "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration" })
@AutoConfigureMockMvc
public abstract class TestConfigurationControlleurRest {

    /**
     * Pour le startup.
     */
    @MockBean
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private Function<UtilisateurDAO, String> genererAccessToken;

    private String accessTokenAdmin;

    private String accessTokenNormalUser;

    private String userAgent;

    @BeforeEach
    public void init() throws Exception {
        doReturn(null)
                .when(utilisateurRepository)
                .findByEmail(anyString());

        // Générer token admin
        UtilisateurDAO utilisateurDAO = spy(UtilisateurDAO.class);
        doReturn(1L).when(utilisateurDAO).getId();
        utilisateurDAO.setEmail("email@email.com");
        utilisateurDAO.setRoles(List.of(RolesENUM.USER, RolesENUM.GESTIONNAIRE));
        accessTokenAdmin = this.genererAccessToken.apply(utilisateurDAO);

        // Générer token normal user
        utilisateurDAO.setRoles(List.of(RolesENUM.USER));
        accessTokenNormalUser = this.genererAccessToken.apply(utilisateurDAO);

        userAgent = "[TEST] Chrome";
    }

    public String getAccessTokenAdmin() {
        return accessTokenAdmin;
    }

    public String getAccessTokenNormalUser() {
        return accessTokenNormalUser;
    }

    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Permet de récupérer les base URI, pour les TESTS
     * @return
     */
    protected String getBaseUri(HttpServletRequest request) {
        return ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
    }
}
