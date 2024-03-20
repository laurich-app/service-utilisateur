package com.example.serviceutilisateur.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPublicKey;

@Service
public class CustomJwtDecoder {
    private final GenerateKey generateKey;

    public CustomJwtDecoder(@Autowired GenerateKey generateKey) {
        this.generateKey = generateKey;
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.generateKey.getRsaPublicKey()).build();
    }
}
