package com.algamoney.api.domain.service;

import com.algamoney.api.domain.model.Lancamento;
import com.algamoney.api.domain.model.Pessoa;
import com.algamoney.api.domain.repository.LancamentoRepository;
import com.algamoney.api.domain.repository.PessoaRepository;
import com.algamoney.api.domain.service.exception.PessoaInexistenteOuInativaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LancamentoService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private LancamentoRepository lancamentoRepository;

    public Lancamento salvar(Lancamento lancamento) {
        Optional<Pessoa> pessoa = this.pessoaRepository.findById(lancamento.getPessoa().getCodigo());
        if(pessoa.isEmpty() || pessoa.get().isInativo()) {
            throw new PessoaInexistenteOuInativaException();
        }

        return this.lancamentoRepository.save(lancamento);
    }
}
