package com.sd.rastreio.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import java.util.Map;

@Service
public class WoncaService {

    private final RestClient restClient;

    public WoncaService() {
        this.restClient = RestClient.create();
    }

    public String consultarRastreioNaApi(String codigoRastreio) {
        // a URL real
        // String url = "https://api-labs.wonca.com.br/wonca.labs.v1.LabsService/Track";
        String url = "http://localhost:8080/mock/track";
        // --------------------

        String apiKey = "Apikey QaFetY71ERZW17StHOYt9fgznZd9iqJ7JzdNfZoU_G4";

        Map<String, String> corpoRequisicao = Map.of("code", codigoRastreio);

        return restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", apiKey)
                .body(corpoRequisicao)
                .retrieve()
                .body(String.class);
    }
}