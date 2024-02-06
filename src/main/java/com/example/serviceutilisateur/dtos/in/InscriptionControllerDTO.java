package com.example.serviceutilisateur.dtos.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InscriptionControllerDTO(@NotNull @Min(2) @Max(18) String pseudo,
                                       @NotNull @Email String email,
                                       @NotNull @Min(3) @Max(18) CharSequence motDePasse) {
}
