package com.algamoney.api.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

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

    @JsonIgnoreProperties("pessoa")
    @Valid
    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contato> contatos;

    @JsonIgnore
    @Transient
    public Boolean isInativo() {
        return this.ativo;
    }
}
