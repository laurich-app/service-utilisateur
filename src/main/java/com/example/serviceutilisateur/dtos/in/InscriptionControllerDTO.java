package com.example.serviceutilisateur.dtos.in;

import jakarta.validation.constraints.*;

public record InscriptionControllerDTO(@NotNull @Size(min = 2, max = 18) String pseudo,
                                       @NotNull @Email String email,
                                       @NotNull @Size(min = 4, max = 18) CharSequence motDePasse) {
}
