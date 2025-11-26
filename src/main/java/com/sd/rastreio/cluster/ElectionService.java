package com.sd.rastreio.cluster;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ElectionService {

    private final NodeContext nodeContext;
    private final ClusterClient clusterClient;

    public ElectionService(NodeContext nodeContext, ClusterClient clusterClient) {
        this.nodeContext = nodeContext;
        this.clusterClient = clusterClient;
    }

    /**
     * Tarefa que roda a cada 10 segundos para garantir que o cluster tem um líder
     * vivo.
     */
    @Scheduled(fixedRate = 10000)
    public void verificarLideranca() {
        System.out.println("🔍 [Election] Verificando estado do cluster...");

        // Se EU sou o líder, apenas aviso que estou vivo (log)
        if (nodeContext.isLeader()) {
            System.out.println("👑 [Líder] Eu sou o líder (ID " + nodeContext.getNodeId() + "). Tudo normal.");
            return;
        }

        // Se não sou líder, preciso saber quem é o mais antigo do cluster
        iniciarEleicao();
    }

    public void iniciarEleicao() {
        System.out.println("🗳️ [Eleição] Iniciando processo de eleição por Tempo de Atividade...");

        // 1. Meus dados iniciais (assumo que sou o candidato a líder)
        long melhorStartTime = nodeContext.getStartTime();
        Integer candidatoLiderId = nodeContext.getNodeId();

        // 2. Pergunto para todos os vizinhos
        List<String> peers = nodeContext.getPeers();

        for (String url : peers) {
            // Pulo a mim mesmo (se a lista tiver meu próprio endereço)
            if (url.contains(":" + nodeContext.getNodeId()))
                continue; // Simplificação baseada na porta/id

            // Tenta pegar a saúde do vizinho
            NodeInfoDTO infoVizinho = clusterClient.getHealth(url);

            if (infoVizinho != null) {
                System.out.println("   - Nó vizinho " + infoVizinho.nodeId() + " está vivo. StartTime: "
                        + infoVizinho.startTime());

                // A REGRA DE OURO: Quem tem o MENOR startTime é o mais velho (Líder)
                if (infoVizinho.startTime() < melhorStartTime) {
                    melhorStartTime = infoVizinho.startTime();
                    candidatoLiderId = infoVizinho.nodeId();
                }
            }
        }

        // 3. Resultado da Eleição
        if (candidatoLiderId.equals(nodeContext.getNodeId())) {
            // Se eu continuo sendo o mais velho (menor tempo) entre os vivos:
            tornarSeLider();
        } else {
            // Se achei alguém mais velho
            System.out.println("🛡️ [Seguidor] O Nó " + candidatoLiderId + " é mais antigo. Ele deve ser o líder.");
            nodeContext.setLeader(false);
            nodeContext.setCurrentLeaderId(candidatoLiderId);
        }
    }

    private void tornarSeLider() {
        if (!nodeContext.isLeader()) {
            System.out.println("🚀 [Vitória] Sou o nó mais antigo vivo! Assumindo liderança.");
            nodeContext.setLeader(true);
            nodeContext.setCurrentLeaderId(nodeContext.getNodeId());

            // Avisar os outros (Opcional no modelo passivo, mas bom para garantir)
            for (String url : nodeContext.getPeers()) {
                clusterClient.anunciarLideranca(url, nodeContext.getNodeId());
            }
        }
    }
}