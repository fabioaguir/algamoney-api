package com.algamoney.api.domain.service;

import com.algamoney.api.domain.model.Lancamento;
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

    public Pessoa salvar(Pessoa pessoa) {
        pessoa.getContatos().forEach(c -> c.setPessoa(pessoa));
        return pessoaRepository.save(pessoa);
    }

    public Pessoa atualizar(Long codigo, Pessoa pessoa) {
        Pessoa pessoaSalva = buscar(codigo);

        pessoaSalva.getContatos().clear();
        pessoaSalva.getContatos().addAll(pessoa.getContatos());
        pessoaSalva.getContatos().forEach(c -> c.setPessoa(pessoaSalva));

        BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo", "contatos");
        return this.pessoaRepository.save(pessoaSalva);
    }

    public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
        Pessoa pessoa = buscar(codigo);
        pessoa.setAtivo(ativo);
        this.pessoaRepository.save(pessoa);
    }

    public Pessoa buscar(Long codigo) {
        Optional<Pessoa> pessoa = this.pessoaRepository.findById(codigo);
        return pessoa.orElseThrow(() ->
             new EmptyResultDataAccessException(1)
        );
    }
}
