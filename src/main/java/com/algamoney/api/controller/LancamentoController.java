package com.algamoney.api.controller;

import com.algamoney.api.domain.dto.Anexo;
import com.algamoney.api.domain.dto.LancamentoDTO;
import com.algamoney.api.domain.dto.LancamentoEstatisticaCategoria;
import com.algamoney.api.domain.dto.LancamentoEstatisticaDia;
import com.algamoney.api.domain.model.Lancamento;
import com.algamoney.api.domain.repository.LancamentoRepository;
import com.algamoney.api.domain.repository.filter.LancamentoFilter;
import com.algamoney.api.domain.service.LancamentoService;
import com.algamoney.api.domain.service.exception.PessoaInexistenteOuInativaException;
import com.algamoney.api.event.RecursoCriadoEvent;
import com.algamoney.api.exceptionhandler.AlgamoneyExceptionHandler;
import com.algamoney.api.storage.S3;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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

    @Autowired
    private S3 s3;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
    public Page<Lancamento> pesquisar(LancamentoFilter lancamentoFilter, Pageable pageable) {
        return this.lancamentoRepository.filtrar(lancamentoFilter, pageable);
    }

    @GetMapping("/resumir")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
    public Page<LancamentoDTO> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
        return this.lancamentoRepository.resumir(lancamentoFilter, pageable);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
    public ResponseEntity<Lancamento> criar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
        Lancamento lancamentoSalvo = this.lancamentoService.salvar(lancamento);
        this.eventPublisher.publishEvent(new RecursoCriadoEvent(this, response ,lancamentoSalvo.getCodigo()));
        return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
    }

    @GetMapping("/{codigo}")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
    public Lancamento buscar(@PathVariable Long codigo) {
        return this.lancamentoService.buscar(codigo);
    }

    @DeleteMapping("/{codigo}")
    @PreAuthorize("hasAuthority('ROLE_REMOVER_LANCAMENTO') and #oauth2.hasScope('write')")
    public void remover(@PathVariable Long codigo){
        this.lancamentoRepository.deleteById(codigo);
    }

    @PutMapping("/{codigo}")
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
    public ResponseEntity<Lancamento> atualizar(@PathVariable Long codigo, @RequestBody Lancamento lancamento) {
        try {
            Lancamento lancamentoSalvo = lancamentoService.atualizar(codigo, lancamento);
            return ResponseEntity.ok(lancamentoSalvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler({ PessoaInexistenteOuInativaException.class })
    public ResponseEntity<Object> handlePessoaInexistenteOuInativaException(PessoaInexistenteOuInativaException ex) {
        String mensagemUsuario = messageSource.getMessage("pessoa.inexistente-ou-inativa", null,
                LocaleContextHolder.getLocale());
        String mensagemDev = ex.toString();

        List<AlgamoneyExceptionHandler.Erro> errors = Arrays.asList(new AlgamoneyExceptionHandler.Erro(mensagemUsuario, mensagemDev));
        return ResponseEntity.badRequest().body(errors);
    }

    @GetMapping("/estatisticas/por-categoria")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
    public List<LancamentoEstatisticaCategoria> porCategoria() {
        return this.lancamentoRepository.porCategoria(LocalDate.now());
    }

    @GetMapping("/estatisticas/por-dia")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
    public List<LancamentoEstatisticaDia> porDia() {
        return this.lancamentoRepository.porDia(LocalDate.now());
    }

//    @PostMapping("/anexo")
//    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
//    public Anexo uploadAnexo(@RequestParam MultipartFile anexo) throws IOException {
//        String nome = s3.salvarTemporariamente(anexo);
//        return new Anexo(nome, s3.configurarUrl(nome));
//    }
}
