package com.bookdemo11.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "stripe")
@Getter
@Setter
public class StripeProperties {

    private String secretKey;
    private String publishableKey;
    private String webhookSecret;

    public boolean isConfigured() {
        return secretKey != null && !secretKey.isBlank()
                && !secretKey.contains("placeholder")
                && !secretKey.contains("your_secret_key");
    }
}