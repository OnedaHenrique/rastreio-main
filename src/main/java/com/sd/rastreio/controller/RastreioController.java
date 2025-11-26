package com.sd.rastreio.controller;

import com.sd.rastreio.model.Pessoa;
import com.sd.rastreio.model.Rastreio;
import com.sd.rastreio.rabbitmq.ServicoDeCadastroProducer;
import com.sd.rastreio.repository.PessoaRepository;
import com.sd.rastreio.repository.RastreioRepository;
import com.sd.rastreio.service.WoncaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RastreioController {

    @Autowired
    private RastreioRepository rastreioRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private WoncaService woncaService;

    // ✅ Injetando o Producer
    @Autowired
    private ServicoDeCadastroProducer cadastroProducer;

    @PostMapping("/rastrear")
    public ResponseEntity<RespostaApi> receberRastreio(@RequestBody DadosRastreio dados) {

        Optional<Pessoa> pessoaOptional = pessoaRepository.findById(dados.idPessoa());
        if (pessoaOptional.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(new RespostaApi("erro", "Pessoa não encontrada"));
        }
        Pessoa pessoa = pessoaOptional.get();

        Rastreio rastreio = rastreioRepository.findByCodigoRastreio(dados.codigo())
                .orElse(new Rastreio());
        rastreio.setCodigoRastreio(dados.codigo());
        rastreio.setPessoa(pessoa);

        // 🔹 Envia evento para RabbitMQ
        try {
            cadastroProducer.enviarCadastro(
                    dados.codigo(),
                    dados.idPessoa(),
                    pessoa.getEmail()
            );
            System.out.println("✅ Evento enviado para RabbitMQ para pessoa: " + pessoa.getNome());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new RespostaApi("erro", "Falha ao enviar evento para RabbitMQ: " + e.getMessage()));
        }

        // 🔹 Consulta API externa
        try {
            System.out.println("🔎 Consultando API da Wonca para: " + dados.codigo());
            String jsonResposta = woncaService.consultarRastreioNaApi(dados.codigo());
            rastreio.setHistoricoJson(jsonResposta);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new RespostaApi("erro", "Erro ao consultar API externa: " + e.getMessage()));
        }

        rastreioRepository.save(rastreio);

        return ResponseEntity.ok(
                new RespostaApi("sucesso", "Rastreio atualizado e evento enviado para RabbitMQ!")
        );
    }

    @GetMapping("/rastreios")
    public ResponseEntity<List<Rastreio>> getRastreiosPorPessoa(@RequestParam("pessoaId") Integer id) {
        List<Rastreio> rastreios = rastreioRepository.findByPessoa_Id(id);
        return ResponseEntity.ok(rastreios);
    }
}
