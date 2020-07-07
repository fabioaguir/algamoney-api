package com.algamoney.api.domain.repository.lancamento;

import com.algamoney.api.domain.model.Lancamento;
import com.algamoney.api.domain.repository.filter.LancamentoFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LancamentoRepositoryQuery {

    public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);
}
