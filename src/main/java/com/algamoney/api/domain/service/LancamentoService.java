package com.algamoney.api.domain.service;

import com.algamoney.api.domain.model.Lancamento;
import com.algamoney.api.domain.model.Pessoa;
import com.algamoney.api.domain.repository.LancamentoRepository;
import com.algamoney.api.domain.repository.PessoaRepository;
import com.algamoney.api.domain.service.exception.PessoaInexistenteOuInativaException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LancamentoService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private LancamentoRepository lancamentoRepository;

    public Lancamento salvar(Lancamento lancamento) {
        validarPessoa(lancamento);
        return this.lancamentoRepository.save(lancamento);
    }

    public Lancamento atualizar(Long codigo, Lancamento lancamento) {
        Lancamento lancamentoSalvo = buscarLancamentoExistente(codigo);
        if (!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
            validarPessoa(lancamento);
        }

        BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");
        return this.lancamentoRepository.save(lancamentoSalvo);
    }

    private void validarPessoa(Lancamento lancamento) {
        Optional<Pessoa> pessoa = null;
        if (lancamento.getPessoa().getCodigo() != null) {
            pessoa = this.pessoaRepository.findById(lancamento.getPessoa().getCodigo());
        }

        if (pessoa == null || !pessoa.isPresent()) {
            throw new PessoaInexistenteOuInativaException();
        }
    }

    private Lancamento buscarLancamentoExistente(Long codigo) {
        Optional<Lancamento> lancamento = this.lancamentoRepository.findById(codigo);
        return lancamento.orElseThrow(() ->
                new EmptyResultDataAccessException(1)
        );
    }

}
