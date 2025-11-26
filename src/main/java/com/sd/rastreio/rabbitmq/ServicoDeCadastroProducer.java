package com.sd.rastreio.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ServicoDeCadastroProducer {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Envia uma mensagem de cadastro para a fila RabbitMQ.
     */
    public void enviarCadastro(String codigoRastreio, Integer idPessoa, String emailDestino) {
        try {
            Map<String, String> evento = Map.of(
                    "tipo", "cadastro_rastreio",
                    "codigoRastreio", codigoRastreio,
                    "idPessoa", idPessoa.toString(),
                    "emailDestino", emailDestino,
                    "conteudoEmail", "Seu pacote foi cadastrado com sucesso! Código de rastreio: " + codigoRastreio
            );

            String body = mapper.writeValueAsString(evento);

            // Publica na fila
            RabbitUtils.publish("cadastro.email", body);

            System.out.println("✅ Evento enviado para RabbitMQ → fila.email.cadastro");

        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar evento para RabbitMQ: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
