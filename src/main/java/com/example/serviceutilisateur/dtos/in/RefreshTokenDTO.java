package com.example.serviceutilisateur.dtos.in;

import jakarta.validation.constraints.NotNull;

public record RefreshTokenDTO(@NotNull String accessToken, @NotNull String refreshToken) {
}
