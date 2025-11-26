package com.sd.rastreio.service;

import com.sd.rastreio.rabbitmq.RabbitUtils;
import com.sd.rastreio.cluster.NodeContext;
import com.sd.rastreio.model.Rastreio;
import com.sd.rastreio.repository.RastreioRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RastreioScheduler {

    private final NodeContext nodeContext;
    private final RastreioRepository rastreioRepository;

    public RastreioScheduler(NodeContext nodeContext, RastreioRepository rastreioRepository) {
        this.nodeContext = nodeContext;
        this.rastreioRepository = rastreioRepository;
    }

    // Executa a cada 30 segundos
    @Scheduled(fixedRate = 30000)
    public void distribuirTarefas() {
        // Se não for o líder, não faz nada
        if (!nodeContext.isLeader())
            return;

        System.out.println("👑 [Líder] Buscando tarefas para distribuir...");
        List<Rastreio> todos = rastreioRepository.findAll();

        if (todos.isEmpty()) {
            System.out.println("👑 [Líder] Nada para fazer.");
            return;
        }

        for (Rastreio rastreio : todos) {
            try {
                // Envia o ID para a fila de trabalho via RabbitUtils
                RabbitUtils.enviarTrabalho(rastreio.getId().toString());
            } catch (Exception e) {
                System.err.println("Erro ao enviar ID " + rastreio.getId() + ": " + e.getMessage());
            }
        }
        System.out.println("👑 [Líder] Enviados " + todos.size() + " IDs para processamento.");
    }
}