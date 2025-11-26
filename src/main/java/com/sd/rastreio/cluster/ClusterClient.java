package com.sd.rastreio.cluster;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

@Component
public class ClusterClient {

    private final RestClient restClient;

    public ClusterClient() {
        this.restClient = RestClient.create();
    }

    /**
     * Tenta conectar no endpoint /internal/health de um nó vizinho.
     * Se o nó estiver offline, retorna null (ou lança exceção tratada).
     */
    public NodeInfoDTO getHealth(String baseUrl) {
        try {
            return restClient.get()
                    .uri(baseUrl + "/internal/health")
                    .retrieve()
                    .body(NodeInfoDTO.class);
        } catch (Exception e) {
            // Se der erro (timeout, connection refused), assumimos que o nó está morto
            System.out.println("⚠️ Falha ao conectar no nó " + baseUrl + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Envia um POST avisando que EU sou o novo líder.
     */
    public void anunciarLideranca(String baseUrl, Integer meuId) {
        try {
            restClient.post()
                    .uri(baseUrl + "/internal/coordinator")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(meuId) // Envia apenas o ID no corpo
                    .retrieve()
                    .toBodilessEntity(); // Não esperamos resposta, apenas status 200 OK
        } catch (Exception e) {
            System.out.println("⚠️ Erro ao anunciar liderança para " + baseUrl);
        }
    }
}