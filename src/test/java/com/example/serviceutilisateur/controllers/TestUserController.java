package com.example.serviceutilisateur.controllers;

import com.example.serviceutilisateur.dtos.in.InscriptionControllerDTO;
import com.example.serviceutilisateur.dtos.in.InscriptionDTO;
import com.example.serviceutilisateur.dtos.out.InscriptionControllerOutDTO;
import com.example.serviceutilisateur.dtos.out.TokenDTO;
import com.example.serviceutilisateur.dtos.out.UtilisateurOutDTO;
import com.example.serviceutilisateur.dtos.pagination.Paginate;
import com.example.serviceutilisateur.dtos.pagination.PaginateRequestDTO;
import com.example.serviceutilisateur.dtos.pagination.Pagination;
import com.example.serviceutilisateur.exceptions.EmailDejaPrisException;
import com.example.serviceutilisateur.exceptions.UtilisateurInconnueException;
import com.example.serviceutilisateur.facades.FacadeAuthentificationImpl;
import com.example.serviceutilisateur.facades.FacadeUtilisateurImpl;
import com.example.serviceutilisateur.models.UtilisateurDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class TestUserController extends TestConfigurationControlleurRest {
    @MockBean
    private FacadeUtilisateurImpl facadeUtilisateur;

    @MockBean
    private FacadeAuthentificationImpl facadeAuthentification;

    @MockBean
    private Validator validator;

    @SpyBean
    private PasswordEncoder passwordEncoder;

    /**
     * @param mvc
     * @throws Exception
     */
    @Test
    void testRegisterEmailDejaPris(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        String password = "Password";
        String pseudo = "pseudo";
        String email = "email@email.com";
        InscriptionControllerDTO inscriptionControllerDTO = new InscriptionControllerDTO(pseudo, email, password);

        InscriptionControllerOutDTO i = new InscriptionControllerOutDTO(
                new TokenDTO("access", "bearer"),
                new UtilisateurOutDTO(1L, pseudo, email)
        );
        doThrow(EmailDejaPrisException.class)
                .when(facadeAuthentification)
                .inscription(any(InscriptionDTO.class), eq(getUserAgent()));

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                post("/users")
                        .header("User-Agent", getUserAgent())
                        .content(objectMapper.writeValueAsString(inscriptionControllerDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        verify(this.passwordEncoder).encode(password);
        Assertions.assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    }

    /**
     * @param mvc
     * @throws Exception
     */
    @Test
    void testRegister(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        String password = "Password";
        String pseudo = "pseudo";
        String email = "email@email.com";
        InscriptionControllerDTO inscriptionControllerDTO = new InscriptionControllerDTO(pseudo, email, password);

        InscriptionControllerOutDTO i = new InscriptionControllerOutDTO(
                new TokenDTO("access", "bearer"),
                new UtilisateurOutDTO(1L, pseudo, email)
        );
        doReturn(i)
                .when(facadeAuthentification)
                .inscription(any(InscriptionDTO.class), eq(getUserAgent()));

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                post("/users")
                        .header("User-Agent", getUserAgent())
                        .content(objectMapper.writeValueAsString(inscriptionControllerDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        verify(this.passwordEncoder).encode(password);
        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        Assertions.assertNotNull(response.getHeader("Authorization"));
    }

    /**
     * @param mvc
     * @throws Exception
     */
    @Test
    void testOneUserUnauthorized(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                get("/users/1")
                        .header("User-Agent", getUserAgent())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    /**
     * @param mvc
     * @throws Exception
     */
    @Test
    void testAllUserUnauthorized(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                get("/users")
                        .header("User-Agent", getUserAgent())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    /**
     * @param mvc
     * @throws Exception
     */
    @Test
    void testOneUserNotAdminForbidden(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                get("/users/2")
                        .header("User-Agent", getUserAgent())
                        .header("Authorization", "Bearer "+getAccessTokenNormalUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    /**
     * @param mvc
     * @throws Exception
     */
    @Test
    void testOneUserNotAdminHimSelfNotFoundAnymore(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        UtilisateurOutDTO u = new UtilisateurOutDTO(1L, "pseudo", "email");
        doThrow(UtilisateurInconnueException.class).when(facadeUtilisateur).getUserById(1L);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                get("/users/1")
                        .header("User-Agent", getUserAgent())
                        .header("Authorization", "Bearer "+getAccessTokenNormalUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    /**
     * @param mvc
     * @throws Exception
     */
    @Test
    void testOneUserNotAdminHimSelf(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        UtilisateurOutDTO u = new UtilisateurOutDTO(1L, "pseudo", "email");
        doReturn(u).when(facadeUtilisateur).getUserById(1L);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                get("/users/1")
                        .header("User-Agent", getUserAgent())
                        .header("Authorization", "Bearer "+getAccessTokenNormalUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    /**
     * @param mvc
     * @throws Exception
     */
    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3})
    void testAdmin(Long value, @Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        UtilisateurOutDTO u = new UtilisateurOutDTO(value, "pseudo", "email");
        doReturn(u).when(facadeUtilisateur).getUserById(value);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                get("/users/"+value)
                        .header("User-Agent", getUserAgent())
                        .header("Authorization", "Bearer "+getAccessTokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    /**
     * @param mvc
     * @throws Exception
     */
    @Test
    void testAllUserNotAdminForbidden(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                get("/users")
                        .header("User-Agent", getUserAgent())
                        .header("Authorization", "Bearer "+getAccessTokenNormalUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    /**
     * @param mvc
     * @throws Exception
     */
    @Test
    void testAllUserAdmin(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        Paginate<UtilisateurDAO> paginate = new Paginate<>(List.of(), new Pagination(2, 10, 0));
        doReturn(paginate).when(facadeUtilisateur).getUsers(any());

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                get("/users")
                        .header("User-Agent", getUserAgent())
                        .header("Authorization", "Bearer "+getAccessTokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    /*
     * @param mvc
     * @throws Exception
     */
    @Test
    void testAllUserAdminPaginationNonRespecte(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        Paginate<UtilisateurDAO> paginate = new Paginate<>(List.of(), new Pagination(2, 10, 0));
        Set<ConstraintViolation<PaginateRequestDTO>> mocked = mock(Set.class);
        doReturn(mocked).when(this.validator).validate(any(PaginateRequestDTO.class));
        doReturn(false).when(mocked).isEmpty();

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                get("/users")
                        .header("User-Agent", getUserAgent())
                        .header("Authorization", "Bearer "+getAccessTokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }
}
