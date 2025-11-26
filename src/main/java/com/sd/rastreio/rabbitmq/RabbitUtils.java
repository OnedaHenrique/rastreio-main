package com.sd.rastreio.rabbitmq;

import com.rabbitmq.client.*;
import java.nio.charset.StandardCharsets;

public class RabbitUtils {

    public static final String RABBIT_URI = System.getenv().getOrDefault("RABBITMQ_URL",
            "amqp://guest:guest@localhost:5672/%2F");
    public static final String EXCHANGE = "eventos_rede_social_exchange";
    public static final String DLX_EXCHANGE = "dlx_exchange";

    // Nome da sua fila de trabalho
    public static final String QUEUE_TRABALHO = "fila_rastreio_trabalho";

    public static Connection getConnection() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(RABBIT_URI);
        return factory.newConnection();
    }

    public static void setupInfrastructure() throws Exception {
        try (Connection conn = getConnection(); Channel ch = conn.createChannel()) {

            // --- Configuração do Colega ---
            ch.exchangeDeclare(EXCHANGE, BuiltinExchangeType.TOPIC, true);
            ch.exchangeDeclare(DLX_EXCHANGE, BuiltinExchangeType.FANOUT, true);

            ch.queueDeclare("dead_letter_queue", true, false, false, null);
            ch.queueBind("dead_letter_queue", DLX_EXCHANGE, "");

            var dlxArgs = new java.util.HashMap<String, Object>();
            dlxArgs.put("x-dead-letter-exchange", DLX_EXCHANGE);

            ch.queueDeclare("email_queue", true, false, false, dlxArgs);
            ch.queueBind("email_queue", EXCHANGE, "cadastro.email");
            ch.queueBind("email_queue", EXCHANGE, "status.email");

            // --- SUA Configuração (Fila de Trabalho) ---
            // Cria a fila que o Scheduler usa e o RastreioConsumer escuta
            ch.queueDeclare(QUEUE_TRABALHO, true, false, false, null);
        }
    }

    // Publica no Exchange (Notificações)
    public static void publish(String routingKey, String message) throws Exception {
        try (Connection conn = getConnection(); Channel ch = conn.createChannel()) {
            ch.basicPublish(EXCHANGE, routingKey,
                    new AMQP.BasicProperties.Builder().deliveryMode(2).build(),
                    message.getBytes(StandardCharsets.UTF_8));
        }
    }

    // Envia direto para a Fila (Scheduler -> Worker)
    public static void enviarTrabalho(String mensagemId) throws Exception {
        try (Connection conn = getConnection(); Channel ch = conn.createChannel()) {
            ch.basicPublish("", QUEUE_TRABALHO,
                    new AMQP.BasicProperties.Builder().deliveryMode(2).build(),
                    mensagemId.getBytes(StandardCharsets.UTF_8));
        }
    }
}