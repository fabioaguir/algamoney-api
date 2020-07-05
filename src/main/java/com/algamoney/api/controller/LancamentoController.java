package com.algamoney.api.controller;

import com.algamoney.api.domain.model.Lancamento;
import com.algamoney.api.domain.repository.LancamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/{codigo}")
    public ResponseEntity<Lancamento> buscar(@PathVariable Long codigo) {
        Optional<Lancamento> lancamento = this.lancamentoRepository.findById(codigo);
        return !lancamento.isEmpty() ? ResponseEntity.ok(lancamento.get()) : ResponseEntity.notFound().build();
    }
}
