package com.algamoney.api.controller;

import com.algamoney.api.domain.model.Lancamento;
import com.algamoney.api.domain.repository.LancamentoRepository;
import com.algamoney.api.event.RecursoCriadoEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoController {

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @GetMapping
    public ResponseEntity<?> listar() {
        List<Lancamento> lancamentos = this.lancamentoRepository.findAll();
        return ResponseEntity.ok(lancamentos);
    }

    @PostMapping
    public ResponseEntity<Lancamento> criar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
        Lancamento lancamentoSalvo = this.lancamentoRepository.save(lancamento);
        this.eventPublisher.publishEvent(new RecursoCriadoEvent(this, response ,lancamentoSalvo.getCodigo()));
        return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Lancamento> buscar(@PathVariable Long codigo) {
        Optional<Lancamento> lancamento = this.lancamentoRepository.findById(codigo);
        return !lancamento.isEmpty() ? ResponseEntity.ok(lancamento.get()) : ResponseEntity.notFound().build();
    }
}
