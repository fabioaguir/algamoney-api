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
        Optional<Pessoa> pessoaExistente = this.pessoaRepository.findById(codigo);

        pessoaExistente.orElseThrow(() -> {
            throw new EmptyResultDataAccessException(1);
        });

        Pessoa pessoaSalva = pessoaExistente.get();
        BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo");

        return this.pessoaRepository.save(pessoaSalva);
    }
}
