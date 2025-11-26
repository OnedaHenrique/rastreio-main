package com.sd.rastreio.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "rastreio")
public class Rastreio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rastreio")
    private Integer id;

    @Column(name = "codigoRastreio", length = 13)
    private String codigoRastreio;

    @Column(name = "historico_json", columnDefinition = "TEXT")
    private String historicoJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id_pessoa")
    @JsonBackReference
    private Pessoa pessoa;

    // --- CONSTRUTORES ---

    // 1. Construtor Vazio (Obrigatório para JPA e new Rastreio())
    public Rastreio() {
    }

    // 2. Construtor usado no Controller
    public Rastreio(String codigoRastreio, Pessoa pessoa) {
        this.codigoRastreio = codigoRastreio;
        this.pessoa = pessoa;
    }

    // --- GETTERS E SETTERS (Manuais) ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigoRastreio() {
        return codigoRastreio;
    }

    public void setCodigoRastreio(String codigoRastreio) {
        this.codigoRastreio = codigoRastreio;
    }

    public String getHistoricoJson() {
        return historicoJson;
    }

    public void setHistoricoJson(String historicoJson) {
        this.historicoJson = historicoJson;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }
}