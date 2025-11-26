package com.sd.rastreio.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sd.rastreio.service.EmailService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class ServicoDeEmailConsumer {

    private static final String QUEUE = "email_queue";

    @Autowired
    private EmailService emailService;

    @PostConstruct
    public void startConsumer() {
        try {
            // Cria conexão e canal
            Connection connection = RabbitUtils.getConnection();
            Channel channel = connection.createChannel();

            channel.basicQos(1); // processa uma mensagem por vez

            // Callback para processar mensagens
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                try {
                    String rawBody = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> evento = mapper.readValue(rawBody, Map.class);

                    String emailDestino = evento.get("emailDestino");
                    String conteudoEmail = evento.get("conteudoEmail");

                    // Envia o e-mail
                    emailService.enviarEmail(emailDestino, "Novo cadastro de rastreio", conteudoEmail);

                    System.out.println("✅ E-mail enviado para: " + emailDestino);

                    // Confirma recebimento da mensagem
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                } catch (Exception e) {
                    System.err.println("❌ Erro ao processar mensagem: " + e.getMessage());
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
                }
            };

            // Começa a consumir mensagens
            channel.basicConsume(QUEUE, false, deliverCallback, consumerTag -> {});

            System.out.println("📩 ServicoDeEmailConsumer iniciado, aguardando mensagens...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
