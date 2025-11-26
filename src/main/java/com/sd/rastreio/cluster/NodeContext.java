package com.sd.rastreio.cluster;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Component
public class NodeContext {

    // Identidade do Nó
    private final Integer nodeId;
    private final List<String> peers;

    // O "Tempo de Atividade" é definido por este timestamp.
    // Quanto MENOR o número, MAIS VELHO é o nó (iniciou antes).
    private final long startTime;

    // Estado da Eleição
    private boolean isLeader = false;
    private Integer currentLeaderId = null;

    // Construtor: Captura os dados do application.properties
    public NodeContext(
            @Value("${node.id}") Integer nodeId,
            @Value("${node.peers}") String peersString) {
        this.nodeId = nodeId;
        // Divide a string "localhost:8080,localhost:8081" em uma lista
        this.peers = Arrays.asList(peersString.split(","));

        // Define o momento exato do nascimento deste nó
        this.startTime = System.currentTimeMillis();

        System.out.println("--- NÓ INICIADO ---");
        System.out.println("ID: " + nodeId);
        System.out.println("Start Time: " + startTime);
        System.out.println("Vizinhos: " + peers);
        System.out.println("-------------------");
    }

    // --- Getters e Setters ---

    public Integer getNodeId() {
        return nodeId;
    }

    public long getStartTime() {
        return startTime;
    }

    public List<String> getPeers() {
        return peers;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }

    public Integer getCurrentLeaderId() {
        return currentLeaderId;
    }

    public void setCurrentLeaderId(Integer currentLeaderId) {
        this.currentLeaderId = currentLeaderId;
    }
}