package com.example.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {

    private final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Async
    public void sendEmail(String to, String subject, String body) {
        logger.info("Iniciando envio de email assíncrono para '{}', assunto='{}'.", to, subject);

        try {
            Thread.sleep(3000L); // simula latência
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Envio de email interrompido", e);
        }

        String timestamp = LocalDateTime.now().format(formatter);

        // Personaliza corpo do email
        String fullBody = """
                Olá %s,
                
                %s
                
                Data da operação: %s
                
                Obrigado por utilizar a aplicação!
                """.formatted(to, body, timestamp);

        logger.info("Email simulado enviado para '{}'. Assunto='{}'. Corpo:\n{}", to, subject, fullBody);

        CompletableFuture.completedFuture(null);
    }
}
