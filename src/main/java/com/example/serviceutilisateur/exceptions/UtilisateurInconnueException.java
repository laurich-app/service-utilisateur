package com.example.serviceutilisateur.exceptions;

public class UtilisateurInconnueException extends Exception {
    public UtilisateurInconnueException() {
    }

    public UtilisateurInconnueException(String message) {
        super(message);
    }
}
