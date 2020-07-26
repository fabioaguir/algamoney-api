package com.algamoney.api.controller;

import com.algamoney.api.domain.model.Lancamento;
import com.algamoney.api.domain.repository.LancamentoRepository;
import com.algamoney.api.domain.repository.filter.LancamentoFilter;
import com.algamoney.api.domain.service.LancamentoService;
import com.algamoney.api.domain.service.exception.PessoaInexistenteOuInativaException;
import com.algamoney.api.event.RecursoCriadoEvent;
import com.algamoney.api.exceptionhandler.AlgamoneyExceptionHandler;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoController {

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LancamentoService lancamentoService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') ans #oauth2.hasScope('read')")
    public Page<Lancamento> pesquisar(LancamentoFilter lancamentoFilter, Pageable pageable) {
        return this.lancamentoRepository.filtrar(lancamentoFilter, pageable);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') ans #oauth2.hasScope('write')")
    public ResponseEntity<Lancamento> criar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
        Lancamento lancamentoSalvo = this.lancamentoService.salvar(lancamento);
        this.eventPublisher.publishEvent(new RecursoCriadoEvent(this, response ,lancamentoSalvo.getCodigo()));
        return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
    }

    @GetMapping("/{codigo}")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') ans #oauth2.hasScope('read')")
    public ResponseEntity<Lancamento> buscar(@PathVariable Long codigo) {
        Optional<Lancamento> lancamento = this.lancamentoRepository.findById(codigo);
        return !lancamento.isEmpty() ? ResponseEntity.ok(lancamento.get()) : ResponseEntity.notFound().build();
    }

    @ExceptionHandler({ PessoaInexistenteOuInativaException.class })
    public ResponseEntity<Object> handlePessoaInexistenteOuInativaException(PessoaInexistenteOuInativaException ex) {
        String mensagemUsuario = messageSource.getMessage("pessoa.inexistente-ou-inativa", null,
                LocaleContextHolder.getLocale());
        String mensagemDev = ex.toString();

        var errors = Arrays.asList(new AlgamoneyExceptionHandler.Erro(mensagemUsuario, mensagemDev));
        return ResponseEntity.badRequest().body(errors);
    }

    @DeleteMapping("/{codigo}")
    @PreAuthorize("hasAuthority('ROLE_REMOVER_LANCAMENTO') ans #oauth2.hasScope('write')")
    public void remover(@PathVariable Long codigo){
        this.lancamentoRepository.deleteById(codigo);
    }
}
