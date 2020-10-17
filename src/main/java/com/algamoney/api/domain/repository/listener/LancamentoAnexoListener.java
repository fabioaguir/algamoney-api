package com.algamoney.api.domain.repository.listener;

import javax.persistence.PostLoad;

import com.algamoney.api.AlgamoneyApiApplication;
import com.algamoney.api.domain.model.Lancamento;
import com.algamoney.api.storage.S3;
import org.springframework.util.StringUtils;

public class LancamentoAnexoListener {

    @PostLoad
    public void postLoad(Lancamento lancamento) {
        if (StringUtils.hasText(lancamento.getAnexo())) {
            S3 s3 = AlgamoneyApiApplication.getBean(S3.class);
            lancamento.setUrlAnexo(s3.configurarUrl(lancamento.getAnexo()));
        }
    }

}
