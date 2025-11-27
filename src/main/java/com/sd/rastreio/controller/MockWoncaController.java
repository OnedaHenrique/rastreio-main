package com.sd.rastreio.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mock")
public class MockWoncaController {

    // Banco de dados em memória para o teste (Código -> Status Atual)
    private static final Map<String, String> bancoDeStatus = new HashMap<>();

    /**
     * 1. Endpoint de CONTROLE (Você usa isso para forçar a mudança).
     * Exemplo de uso no Postman/Curl:
     * POST /mock/mudar-status
     * Body: { "codigo": "ND123", "status": "Saiu para Entrega" }
     */
    @PostMapping("/mudar-status")
    public ResponseEntity<String> mudarStatusNaMarra(@RequestBody Map<String, String> payload) {
        String codigo = payload.get("codigo");
        String novoStatus = payload.get("status");

        bancoDeStatus.put(codigo, novoStatus);

        System.out.println("🎲 [MOCK] Status do pacote " + codigo + " alterado para: " + novoStatus);
        return ResponseEntity.ok("Status atualizado no simulador!");
    }

    /**
     * 2. Endpoint que IMITA A WONCA (Seu sistema consome isso).
     * Ele retorna o JSON complexo e feio exatamente como a API original,
     * para que seu front-end não quebre.
     */
    @PostMapping("/track")
    public ResponseEntity<String> consultarRastreioFake(@RequestBody Map<String, String> payload) {
        String codigo = payload.get("code");

        // Se não tiver status definido, retorna um padrão "Postado"
        String statusAtual = bancoDeStatus.getOrDefault(codigo, "Objeto postado");

        System.out.println("🎲 [MOCK] Sistema consultou o pacote: " + codigo + ". Respondendo: " + statusAtual);

        // Monta aquele JSON horrível cheio de escapes que a Wonca retorna
        // para garantir que o seu index.html consiga ler.
        String jsonResposta = """
                {
                    "results": [
                        {
                            "json": "{\\"codObjeto\\":\\"%s\\",\\"eventos\\":[{\\"descricao\\":\\"%s\\",\\"dtHrCriado\\":{\\"date\\":\\"2025-11-27 10:00:00\\"}}]}"
                        }
                    ]
                }
                """
                .formatted(codigo, statusAtual);

        return ResponseEntity.ok(jsonResposta);
    }
}