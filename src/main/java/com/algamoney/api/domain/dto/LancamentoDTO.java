package com.algamoney.api.domain.dto;

import com.algamoney.api.domain.model.Categoria;
import com.algamoney.api.domain.model.Pessoa;
import com.algamoney.api.domain.model.TipoLancamento;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LancamentoDTO {

    private Long codigo;
    private String descricao;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private BigDecimal valor;
    private String observacao;
    private TipoLancamento tipo;
    private String categoria;
    private String pessoa;

    public LancamentoDTO(Long codigo, String descricao, LocalDate dataVencimento, LocalDate dataPagamento,
                         BigDecimal valor, TipoLancamento tipo, String categoria, String pessoa) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.dataVencimento = dataVencimento;
        this.dataPagamento = dataPagamento;
        this.valor = valor;
        this.tipo = tipo;
        this.categoria = categoria;
        this.pessoa = pessoa;
    }
}
