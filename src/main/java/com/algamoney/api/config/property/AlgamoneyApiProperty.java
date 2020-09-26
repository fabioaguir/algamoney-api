package com.algamoney.api.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("algamoney")
public class AlgamoneyApiProperty {

    private String originPermitida = "http://localhost:4200";

    private final Seguranca seguranca = new Seguranca();

    private final Mail mail = new Mail();

    public Mail getMail() {
        return mail;
    }

    public Seguranca getSeguranca() {
        return seguranca;
    }

    public String getOriginPermitida() {
        return originPermitida;
    }

    public void setOriginPermitida(String originPermitida) {
        this.originPermitida = originPermitida;
    }

    @Data
    public static class Seguranca {
        private boolean enableHttps;
    }

    @Data
    public static class Mail {

        private String host;

        private Integer port;

        private String username;

        private String password;
    }
}
