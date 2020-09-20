package com.algamoney.api.domain.repository.lancamento;

import com.algamoney.api.domain.dto.LancamentoDTO;
import com.algamoney.api.domain.dto.LancamentoEstatisticaCategoria;
import com.algamoney.api.domain.dto.LancamentoEstatisticaDia;
import com.algamoney.api.domain.model.Lancamento;
import com.algamoney.api.domain.repository.filter.LancamentoFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface LancamentoRepositoryQuery {

    public List<LancamentoEstatisticaCategoria> porCategoria(LocalDate mesReferencia);
    public List<LancamentoEstatisticaDia> porDia(LocalDate mesReferencia);

    public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);
    public Page<LancamentoDTO> resumir(LancamentoFilter lancamentoFilter, Pageable pageable);
}
