package com.algamoney.api.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Pessoa {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @NotNull
    @Column(nullable = false)
    private String nome;

    @Embedded
    private Endereco endereco;

    @NotNull
    @Column(nullable = false)
    private Boolean ativo;

    @JsonIgnore
    @Transient
    public Boolean isInativo() {
        return this.ativo;
    }
}
