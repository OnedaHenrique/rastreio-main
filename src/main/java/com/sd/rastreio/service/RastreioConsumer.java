package com.sd.rastreio.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sd.rastreio.model.Rastreio;
import com.sd.rastreio.repository.RastreioRepository;
import com.sd.rastreio.rabbitmq.RabbitUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // <--- IMPORT NOVO

import java.util.Map;

@Component
public class RastreioConsumer {

    private final RastreioRepository rastreioRepository;
    private final WoncaService woncaService;
    private final ObjectMapper mapper = new ObjectMapper();

    public RastreioConsumer(RastreioRepository rastreioRepository, WoncaService woncaService) {
        this.rastreioRepository = rastreioRepository;
        this.woncaService = woncaService;
    }

    @RabbitListener(queues = "fila_rastreio_trabalho")
    @Transactional // <--- ADICIONE ISSO AQUI
    public void processarAtualizacao(String idRastreioString) {
        System.out.println("👷 [Worker] Recebido ID: " + idRastreioString);

        try {
            Integer id = Integer.parseInt(idRastreioString);
            Rastreio rastreio = rastreioRepository.findById(id).orElse(null);

            if (rastreio != null) {
                // 1. CONSULTA API E ATUALIZA BANCO
                String novoJson = woncaService.consultarRastreioNaApi(rastreio.getCodigoRastreio());
                rastreio.setHistoricoJson(novoJson);
                rastreioRepository.save(rastreio);

                // 2. NOTIFICAÇÃO
                try {
                    // Aqui ele acessa rastreio.getPessoa().getEmail()
                    // Com @Transactional, a conexão fica aberta e isso funciona!
                    Map<String, String> eventoEmail = Map.of(
                            "emailDestino", rastreio.getPessoa().getEmail(),
                            "conteudoEmail",
                            "O pacote " + rastreio.getCodigoRastreio() + " teve uma atualização de status!");

                    String jsonMensagem = mapper.writeValueAsString(eventoEmail);
                    RabbitUtils.publish("status.email", jsonMensagem);

                    System.out.println("✅ Atualizado e notificado (JSON enviado com sucesso).");

                } catch (Exception e) {
                    System.err.println("⚠️ Erro ao enviar notificação: " + e.getMessage());
                    e.printStackTrace(); // Ajuda a ver mais detalhes se der erro
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erro fatal no worker: " + e.getMessage());
        }
    }
}