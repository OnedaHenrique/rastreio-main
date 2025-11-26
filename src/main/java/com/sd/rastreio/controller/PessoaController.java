package com.sd.rastreio.controller;

import com.sd.rastreio.model.Pessoa;
import com.sd.rastreio.repository.PessoaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/pessoas") 
public class PessoaController {

    @Autowired
    PessoaRepository pessoaRepository;

    @PostMapping
    public ResponseEntity<Pessoa> cadastrarPessoa(
            @RequestBody DadosCadastroPessoa dados,
            UriComponentsBuilder uriBuilder
    ) {
        
        Pessoa novaPessoa = new Pessoa(dados.nome(), dados.email());
        
        Pessoa pessoaSalva = pessoaRepository.save(novaPessoa);

        URI uri = uriBuilder
                    .path("/api/pessoas/{id}")
                    .buildAndExpand(pessoaSalva.getId())
                    .toUri();

        return ResponseEntity.created(uri).body(pessoaSalva);
    }

    
}