package com.algamoney.api.controller;

import com.algamoney.api.domain.model.Pessoa;
import com.algamoney.api.domain.repository.PessoaRepository;
import com.algamoney.api.domain.service.PessoaService;
import com.algamoney.api.event.RecursoCriadoEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @GetMapping
    public ResponseEntity<?> listar() {
        List<Pessoa> pessoas = this.pessoaRepository.findAll();
        return ResponseEntity.ok(pessoas);
    }

    @PostMapping
    public ResponseEntity<Pessoa> criar(@Valid  @RequestBody Pessoa pessoa, HttpServletResponse response) {
        Pessoa pessoaSalva = this.pessoaRepository.save(pessoa);
        this.eventPublisher.publishEvent(new RecursoCriadoEvent(this, response, pessoaSalva.getCodigo()));
        return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Pessoa> buscar(@PathVariable Long codigo) {
        Optional<Pessoa> pessoa = this.pessoaRepository.findById(codigo);
        return !pessoa.isEmpty() ? ResponseEntity.ok(pessoa.get()) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{codigo}")
    public void remover(@PathVariable Long codigo){
        this.pessoaRepository.deleteById(codigo);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Pessoa> atualizar(@PathVariable Long codigo, @RequestBody Pessoa pessoa) {
        Pessoa pessoaUpdated = this.pessoaService.atualizar(codigo, pessoa);
        return ResponseEntity.ok(pessoaUpdated);
    }

    @PutMapping("/{codigo}/ativo")
    public void atualizar(@PathVariable Long codigo, @RequestBody Boolean ativo) {
        this.pessoaService.atualizarPropriedadeAtivo(codigo, ativo);
    }
}
