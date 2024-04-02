package com.example.serviceutilisateur.services;

import com.example.serviceutilisateur.dtos.rabbits.InscriptionBienvenueDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ServiceRabbitMQSender {
    @Value("${spring.rabbitmq.exchange.utilisateur.inscription.bienvenue}")
    private String exchangeUtilisateurInscriptionBienvenue;

    @Value("${spring.rabbitmq.routingkey.utilisateur.inscription.bienvenue}")
    private String routingkeyUtilisateurInscriptionBienvenue;

    private final RabbitTemplate rabbitTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ServiceRabbitMQSender.class);

    @Autowired
    public ServiceRabbitMQSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void inscriptionBienvenue(InscriptionBienvenueDTO inscriptionBienvenueDTO){
        logger.info("[RabbitMQ] Inscription bienvenue : {}", inscriptionBienvenueDTO);
        rabbitTemplate.convertAndSend(exchangeUtilisateurInscriptionBienvenue,routingkeyUtilisateurInscriptionBienvenue, inscriptionBienvenueDTO);
    }
}
