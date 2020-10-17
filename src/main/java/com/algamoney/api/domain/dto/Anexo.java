package com.algamoney.api.domain.dto;

import lombok.Data;

@Data
public class Anexo {

    private String nome;

    private String url;

    public Anexo(String nome, String url) {
        this.nome = nome;
        this.url = url;
    }
}
