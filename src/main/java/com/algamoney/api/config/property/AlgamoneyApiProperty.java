package com.algamoney.api.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("algamoney")
public class AlgamoneyApiProperty {

    private String originPermitida = "http://localhost:4200";

    private final Seguranca seguranca = new Seguranca();

    private final Mail mail = new Mail();

    private final S3 s3 = new S3();

    public Seguranca getSeguranca() {
        return seguranca;
    }

    public String getOriginPermitida() {
        return originPermitida;
    }

    public Mail getMail() {
        return mail;
    }

    public S3 getS3() {
        return s3;
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

    @Data
    public static class S3 {

        private String accessKeyId;

        private String secretAccessKey;

        private String bucket = "aw-algamoney-faroagba-arquivos";
    }
}
