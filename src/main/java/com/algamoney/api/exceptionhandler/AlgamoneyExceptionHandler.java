package com.algamoney.api.exceptionhandler;

import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class AlgamoneyExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        String mensagemUsuario = messageSource.getMessage("mensagem.invalida", null,
                LocaleContextHolder.getLocale());
        String mensagemDev = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();

        List<Erro> errors = Arrays.asList(new Erro(mensagemUsuario, mensagemDev));
        return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<Erro> errors = criarListaErro(ex.getBindingResult());
        return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ EmptyResultDataAccessException.class })
    public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex,  WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("recurso.nao-encontrado", null,
                LocaleContextHolder.getLocale());
        String mensagemDev = ex.getMessage();

        List<Erro> errors = Arrays.asList(new Erro(mensagemUsuario, mensagemDev));
        return handleExceptionInternal(ex,errors, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ DataIntegrityViolationException.class })
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex,  WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("recurso.operacao-nao-permitida", null,
                LocaleContextHolder.getLocale());
        String mensagemDev = ExceptionUtils.getRootCauseMessage(ex);

        List<Erro> errors = Arrays.asList(new Erro(mensagemUsuario, mensagemDev));
        return handleExceptionInternal(ex,errors, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    private List<Erro> criarListaErro(BindingResult bindingResult) {

        List<Erro> error = bindingResult.getFieldErrors().stream().map(fieldError -> {
            String mensagemUsuario = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
            String mensagemDev = fieldError.toString();
            return new Erro(mensagemUsuario, mensagemDev);
        }).collect(Collectors.toList());

        return error;
    }

    @Getter
    public static class Erro {
        private String mensagemUsuario;
        private String mensagemDev;

        public Erro(String mensagemUsuario, String mensagemDev) {
            this.mensagemUsuario = mensagemUsuario;
            this.mensagemDev = mensagemDev;
        }
    }

}
