package com.sd.rastreio.rabbitmq;

public class SetupRabbit {
    public static void main(String[] args) throws Exception {
        System.out.println("Criando infraestrutura no RabbitMQ...");
        RabbitUtils.setupInfrastructure();
        System.out.println("Infraestrutura criada com sucesso!");
    }
}