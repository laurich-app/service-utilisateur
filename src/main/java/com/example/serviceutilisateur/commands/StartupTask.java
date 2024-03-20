package com.example.serviceutilisateur.commands;

import com.example.serviceutilisateur.config.GenerateKey;
import com.example.serviceutilisateur.enums.RolesENUM;
import com.example.serviceutilisateur.models.UtilisateurDAO;
import com.example.serviceutilisateur.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;

@Component
public class StartupTask implements CommandLineRunner {
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final GenerateKey generateKey;

    @Value("${spring.cloud.consul.host}:${spring.cloud.consul.port}")
    private String discoveryUrl;

    public StartupTask(@Autowired GenerateKey generateKey, @Autowired UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.generateKey = generateKey;
    }

    @Override
    public void run(String... args) throws Exception {
        // Code à exécuter au démarrage de l'application
        UtilisateurDAO utilisateurDAO = this.utilisateurRepository.findByEmail("root@root.com");
        if(utilisateurDAO == null)
            this.addRoot();

        this.pushKey();
    }

    private void pushKey() throws IOException, InterruptedException {
        // Créez une instance de HttpClient
        HttpClient httpClient = HttpClient.newHttpClient();

        // URL de l'endpoint de l'API REST
        String url = "http://" + discoveryUrl + "/v1/kv/config/application/publicKey";

        // Créez le contenu de la requête à partir du fichier
        byte[] publicKeyData = this.generateKey.getRsaPublicKey().getEncoded();
        String base64Encoded = Base64.getEncoder().encodeToString(publicKeyData);

        // Créez la requête HTTP PUT
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "text/plain")
                .PUT(HttpRequest.BodyPublishers.ofString(base64Encoded))
                .build();

        // Envoyez la requête HTTP
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Vérifiez la réponse
        if (response.statusCode() == 200) {
            System.out.println("La clé publique a été mise à jour avec succès !");
        } else {
            System.out.println("Une erreur s'est produite lors de la mise à jour de la clé publique : " + response.statusCode());
        }
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
