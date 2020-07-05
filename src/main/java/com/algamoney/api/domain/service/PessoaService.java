package com.algamoney.api.domain.service;

import com.algamoney.api.domain.model.Pessoa;
import com.algamoney.api.domain.repository.PessoaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public Pessoa atualizar(Long codigo, Pessoa pessoa) {
        Pessoa pessoaSalva = buscarPessoa(codigo);
        BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo");
        return this.pessoaRepository.save(pessoaSalva);
    }

    public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
        Pessoa pessoa = buscarPessoa(codigo);
        pessoa.setAtivo(ativo);
        this.pessoaRepository.save(pessoa);
    }

    private Pessoa buscarPessoa(Long codigo) {
        Optional<Pessoa> pessoa = this.pessoaRepository.findById(codigo);
        return pessoa.orElseThrow(() -> {
            throw new EmptyResultDataAccessException(1);
        });
    }
}
