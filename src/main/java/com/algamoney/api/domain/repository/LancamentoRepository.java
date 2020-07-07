package com.algamoney.api.domain.repository;

import com.algamoney.api.domain.model.Lancamento;
import com.algamoney.api.domain.repository.lancamento.LancamentoRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryQuery {
}
