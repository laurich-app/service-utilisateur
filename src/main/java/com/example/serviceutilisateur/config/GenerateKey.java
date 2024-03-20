package com.example.serviceutilisateur.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Service
public class GenerateKey {

    private RSAPublicKey rsaPublicKey;

    private RSAPrivateKey rsaPrivateKey;

    @PostConstruct
    public void run() throws FileNotFoundException, NoSuchAlgorithmException {
        this.genKey();
    }

    private void genKey() throws NoSuchAlgorithmException {
        // Créer une instance de générateur de clés RSA
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // Longueur de la clé à 2048 bits

        // Générer une paire de clés RSA
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        this.rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
    }

    public RSAPublicKey getRsaPublicKey() {
        return rsaPublicKey;
    }

    public RSAPrivateKey getRsaPrivateKey() {
        return rsaPrivateKey;
    }
}
