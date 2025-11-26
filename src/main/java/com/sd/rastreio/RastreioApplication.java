package com.sd.rastreio;

import com.sd.rastreio.rabbitmq.RabbitUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RastreioApplication {

	public static void main(String[] args) {
		// 1. Cria a infraestrutura do RabbitMQ ANTES de tudo
		try {
			RabbitUtils.setupInfrastructure();
			System.out.println("🐰 Infraestrutura RabbitMQ criada com sucesso!");
		} catch (Exception e) {
			System.err.println("❌ Erro crítico ao configurar RabbitMQ: " + e.getMessage());
			// Podemos optar por continuar ou fechar, vamos continuar tentando
		}

		// 2. Inicia a aplicação Spring
		SpringApplication.run(RastreioApplication.class, args);
	}
}