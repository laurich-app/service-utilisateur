package com.example.serviceutilisateur.dtos.in;

import jakarta.validation.constraints.NotNull;

public record ConnexionDTO(@NotNull String email, @NotNull String motDePasse) {
}
