package com.algamoney.api.domain.service;

import com.algamoney.api.domain.model.Lancamento;
import com.algamoney.api.domain.model.Pessoa;
import com.algamoney.api.domain.model.Usuario;
import com.algamoney.api.domain.repository.LancamentoRepository;
import com.algamoney.api.domain.repository.PessoaRepository;
import com.algamoney.api.domain.repository.UsuarioRepository;
import com.algamoney.api.domain.service.exception.PessoaInexistenteOuInativaException;
import com.algamoney.api.mail.Mailer;

import com.algamoney.api.storage.S3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LancamentoService {

    private static final String DESTINATARIOS = "ROLE_PESQUISAR_LANCAMENTO";

    private static final Logger logger = LoggerFactory.getLogger(LancamentoService.class);

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private Mailer mailer;

    @Autowired
    private S3 s3;

    @Scheduled(cron = "0 0 6 * * *")
    public void avisarSobreLancamentosVencidos() {
        if (logger.isDebugEnabled()) {
            logger.debug("Preparando envio de "
                    + "e-mails de aviso de lançamentos vencidos.");
        }

        List<Lancamento> vencidos = lancamentoRepository
                .findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());

        if (vencidos.isEmpty()) {
            logger.info("Sem lançamentos vencidos para aviso.");

            return;
        }

        logger.info("Exitem {} lançamentos vencidos.", vencidos.size());

        List<Usuario> destinatarios = usuarioRepository
                .findByPermissoesDescricao(DESTINATARIOS);

        if (destinatarios.isEmpty()) {
            logger.warn("Existem lançamentos vencidos, mas o "
                    + "sistema não encontrou destinatários.");

            return;
        }

        mailer.avisarSobreLancamentosVencidos(vencidos, destinatarios);

        logger.info("Envio de e-mail de aviso concluído.");
    }

    public Lancamento salvar(Lancamento lancamento) {
        validarPessoa(lancamento);

        // S3
        if (StringUtils.hasText(lancamento.getAnexo())) {
            s3.salvar(lancamento.getAnexo());
        }

        return this.lancamentoRepository.save(lancamento);
    }

    public Lancamento atualizar(Long codigo, Lancamento lancamento) {
        Lancamento lancamentoSalvo = buscar(codigo);
        if (!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
            validarPessoa(lancamento);
        }

        // S3
        if (StringUtils.isEmpty(lancamento.getAnexo())
                && StringUtils.hasText(lancamentoSalvo.getAnexo())) {
            s3.remover(lancamentoSalvo.getAnexo());
        } else if (StringUtils.hasText(lancamento.getAnexo())
                && !lancamento.getAnexo().equals(lancamentoSalvo.getAnexo())) {
            s3.substituir(lancamentoSalvo.getAnexo(), lancamento.getAnexo());
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

    public Lancamento buscar(Long codigo) {
        Optional<Lancamento> lancamento = this.lancamentoRepository.findById(codigo);
        return lancamento.orElseThrow(() ->
                new EmptyResultDataAccessException(1)
        );
    }

}
