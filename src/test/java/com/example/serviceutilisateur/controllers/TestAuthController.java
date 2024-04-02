package com.example.serviceutilisateur.controllers;

import com.example.serviceutilisateur.dtos.in.ConnexionDTO;
import com.example.serviceutilisateur.dtos.in.RefreshTokenDTO;
import com.example.serviceutilisateur.dtos.out.TokenDTO;
import com.example.serviceutilisateur.enums.RolesENUM;
import com.example.serviceutilisateur.exceptions.RefreshTokenExpirerException;
import com.example.serviceutilisateur.exceptions.TokenIncompatibleException;
import com.example.serviceutilisateur.exceptions.TokenInconnueException;
import com.example.serviceutilisateur.exceptions.UtilisateurInconnueException;
import com.example.serviceutilisateur.facades.FacadeAuthentification;
import com.example.serviceutilisateur.facades.FacadeAuthentificationImpl;
import com.example.serviceutilisateur.models.UtilisateurDAO;
import com.example.serviceutilisateur.repositories.UtilisateurRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class TestAuthController extends TestConfigurationControlleurRest {
    @MockBean
    private FacadeAuthentificationImpl facadeAuthentification;

    /**
     * Connexion OK, Status OK + tokens dans le Header et dans la réponse.
     * @param mvc
     * @throws Exception
     */
    @Test
    void testConnexion(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        ConnexionDTO connexionDTO = new ConnexionDTO("email@email.com", "password");
        String accessToken = "AccessToken";
        doReturn(new TokenDTO(accessToken, "bearerToken"))
                .when(facadeAuthentification).connexion(connexionDTO, getUserAgent());

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                post("/auth/connexion")
                        .header("User-Agent", getUserAgent())
                        .content(objectMapper.writeValueAsString(connexionDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        Assertions.assertEquals(response.getHeader("Authorization"), "Bearer "+accessToken);
    }

    /**
     * @param mvc
     * @throws Exception
     */
    @Test
    void testRefreshToken(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO("access", "refresh");
        TokenDTO tokenDTO = new TokenDTO("access2", "");
        doReturn(tokenDTO)
                .when(facadeAuthentification).genereTokenRaffraichissement(refreshTokenDTO, getUserAgent());

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                post("/auth/token")
                        .header("User-Agent", getUserAgent())
                        .content(objectMapper.writeValueAsString(refreshTokenDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        Assertions.assertEquals(response.getHeader("Authorization"), "Bearer "+tokenDTO.accessToken());
    }

    /**
     * @param mvc
     * @throws Exception
     */
    @Test
    void testRefreshTokenIncompatible(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO("access", "refresh");
        TokenDTO tokenDTO = new TokenDTO("access2", "");
        doThrow(TokenIncompatibleException.class)
                .when(facadeAuthentification).genereTokenRaffraichissement(refreshTokenDTO, getUserAgent());

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                post("/auth/token")
                        .header("User-Agent", getUserAgent())
                        .content(objectMapper.writeValueAsString(refreshTokenDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        Assertions.assertNotEquals(response.getHeader("Authorization"), "Bearer "+tokenDTO.accessToken());
    }

    /**
     * @param mvc
     * @throws Exception
     */
    @Test
    void testRefreshTokenExpirer(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO("access", "refresh");
        TokenDTO tokenDTO = new TokenDTO("access2", "");
        doThrow(RefreshTokenExpirerException.class)
                .when(facadeAuthentification).genereTokenRaffraichissement(refreshTokenDTO, getUserAgent());

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                post("/auth/token")
                        .header("User-Agent", getUserAgent())
                        .content(objectMapper.writeValueAsString(refreshTokenDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        Assertions.assertNotEquals(response.getHeader("Authorization"), "Bearer "+tokenDTO.accessToken());
    }

    /**
     * Connexion Utilisateur Inconnue
     * @param mvc
     * @throws Exception
     */
    @Test
    void testConnexionNotFound(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        ConnexionDTO connexionDTO = new ConnexionDTO("email@email.com", "password");
        String accessToken = "AccessToken";
        doThrow(UtilisateurInconnueException.class)
                .when(facadeAuthentification).connexion(connexionDTO, getUserAgent());

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                post("/auth/connexion")
                        .header("User-Agent", getUserAgent())
                        .content(objectMapper.writeValueAsString(connexionDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        Assertions.assertNotEquals(response.getHeader("Authorization"), "Bearer "+accessToken);
    }

    /**
     * Connexion OK, Status OK + tokens dans le Header et dans la réponse.
     * @param mvc
     * @throws Exception
     */
    @Test
    void testDeconnexion(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        String accessToken = getAccessTokenNormalUser();
        doNothing()
                .when(facadeAuthentification).deconnexion(accessToken);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                delete("/auth/connexion")
                        .header("User-Agent", getUserAgent())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }



    /**
     * Deconnexion Token Inconnue
     * @param mvc
     * @throws Exception
     */
    @Test
    void testDeonnexionNotFound(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        String accessToken = getAccessTokenNormalUser();
        doThrow(TokenInconnueException.class)
                .when(facadeAuthentification).deconnexion(accessToken);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                delete("/auth/connexion")
                        .header("User-Agent", getUserAgent())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    /**
     * Deconnexion Unauthorized
     * @param mvc
     * @throws Exception
     */
    @Test
    void testDeonnexionUnauthorized(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                delete("/auth/connexion")
                        .header("User-Agent", getUserAgent())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }
}
