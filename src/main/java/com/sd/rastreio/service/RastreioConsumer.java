package com.sd.rastreio.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sd.rastreio.model.Rastreio;
import com.sd.rastreio.repository.RastreioRepository;
import com.sd.rastreio.rabbitmq.RabbitUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public void processarAtualizacao(String idRastreioString) {
        System.out.println("👷 [Worker] Recebido ID: " + idRastreioString);

        try {
            Integer id = Integer.parseInt(idRastreioString);
            Rastreio rastreio = rastreioRepository.findById(id).orElse(null);

            if (rastreio != null) {
                // 1. Consulta API (Busca o novo estado na Wonca)
                String novoJson = woncaService.consultarRastreioNaApi(rastreio.getCodigoRastreio());

                // 2. Pega o estado que já estava salvo no banco
                String jsonAntigo = rastreio.getHistoricoJson();

                // 3. VERIFICAÇÃO: Só faz algo se houver mudança real
                // (Se novoJson for diferente do Antigo, ou se o Antigo for null/vazio)
                if (novoJson != null && !novoJson.equals(jsonAntigo)) {

                    System.out.println("🔄 Mudança de status detectada! Atualizando...");

                    // Atualiza o banco com a novidade
                    rastreio.setHistoricoJson(novoJson);
                    rastreioRepository.save(rastreio);

                    // Envia Notificação (Pois houve mudança)
                    try {
                        Map<String, String> eventoEmail = Map.of(
                                "emailDestino", rastreio.getPessoa().getEmail(),
                                "conteudoEmail",
                                "O pacote " + rastreio.getCodigoRastreio() + " teve uma atualização de status!");

                        String jsonMensagem = mapper.writeValueAsString(eventoEmail);
                        RabbitUtils.publish("status.email", jsonMensagem);

                        System.out.println("✅ E-mail de notificação enviado.");

                    } catch (Exception e) {
                        System.err.println("⚠️ Erro ao enviar notificação: " + e.getMessage());
                    }

                } else {
                    // Se o JSON for idêntico, não fazemos nada
                    System.out.println("💤 Sem novidades. O status do pacote " + rastreio.getCodigoRastreio()
                            + " continua igual. E-mail não enviado.");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erro fatal no worker: " + e.getMessage());
        }
    }
}