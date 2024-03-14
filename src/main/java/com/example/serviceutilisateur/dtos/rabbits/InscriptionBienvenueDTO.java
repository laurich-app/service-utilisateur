package com.example.serviceutilisateur.dtos.rabbits;

import java.io.Serializable;

public record InscriptionBienvenueDTO(String email, String pseudo) implements Serializable {
}
