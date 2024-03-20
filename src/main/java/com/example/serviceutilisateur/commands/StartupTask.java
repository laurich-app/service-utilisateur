package com.example.serviceutilisateur.commands;

import com.example.serviceutilisateur.enums.RolesENUM;
import com.example.serviceutilisateur.models.UtilisateurDAO;
import com.example.serviceutilisateur.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StartupTask implements CommandLineRunner {
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public StartupTask(@Autowired UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Code à exécuter au démarrage de l'application
        UtilisateurDAO utilisateurDAO = this.utilisateurRepository.findByEmail("root@root.com");
        if(utilisateurDAO == null)
            this.addRoot();
    }

    private void addRoot() {
        // Création d'un gestionnaire par défaut, s'il n'existe pas.
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        utilisateurDAO.setPseudo("root");
        utilisateurDAO.setEmail("root@root.com");
        utilisateurDAO.setMotDePasse(this.passwordEncoder.encode("root"));
        utilisateurDAO.setRoles(List.of(RolesENUM.USER, RolesENUM.GESTIONNAIRE));

        this.utilisateurRepository.save(utilisateurDAO);
    }
}
