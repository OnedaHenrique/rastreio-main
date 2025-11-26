package com.sd.rastreio.service; // Crie o pacote 'service' se não existir

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

import java.util.Map;

@Service
public class WoncaService {

    private final RestClient restClient;

    public WoncaService() {
        // Inicializa o cliente HTTP
        this.restClient = RestClient.create();
    }

    public String consultarRastreioNaApi(String codigoRastreio) {
        String url = "https://api-labs.wonca.com.br/wonca.labs.v1.LabsService/Track";
        String apiKey = "Apikey QaFetY71ERZW17StHOYt9fgznZd9iqJ7JzdNfZoU_G4";

        // O corpo da requisição que a API espera: {"code": "..."}
        Map<String, String> corpoRequisicao = Map.of("code", codigoRastreio);

        // Faz a chamada POST
        return restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", apiKey)
                .body(corpoRequisicao)
                .retrieve()
                .body(String.class); // Pega a resposta crua como String (JSON)
    }
}