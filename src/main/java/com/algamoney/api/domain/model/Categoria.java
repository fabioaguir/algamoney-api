package com.algamoney.api.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Categoria {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @NotNull
    @Size(min = 3, max = 30)
    @Column(nullable = false)
    private String nome;
}
