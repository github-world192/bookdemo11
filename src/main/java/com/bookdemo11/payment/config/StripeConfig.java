package com.bookdemo11.payment.config;

import org.springframework.context.annotation.Configuration;

import com.stripe.Stripe;

import jakarta.annotation.PostConstruct;

@Configuration
public class StripeConfig {

    private final StripeProperties stripeProperties;

    public StripeConfig(StripeProperties stripeProperties) {
        this.stripeProperties = stripeProperties;
    }

    @PostConstruct
    public void init() {
        if (stripeProperties.getSecretKey() != null) {
            Stripe.apiKey = stripeProperties.getSecretKey();
        }
    }
}