package com.bookdemo11.payment.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.bookdemo11.payment.config.AppProperties;
import com.bookdemo11.payment.config.StripeProperties;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class StripePaymentService {

    private final StripeProperties stripeProperties;
    private final AppProperties appProperties;

    public StripePaymentService(StripeProperties stripeProperties, AppProperties appProperties) {
        this.stripeProperties = stripeProperties;
        this.appProperties = appProperties;
    }

    public String getPublishableKey() {
        return stripeProperties.getPublishableKey();
    }

    public boolean isConfigured() {
        return stripeProperties.isConfigured();
    }

    public Session createCheckoutSession(String orderType,
                                         String successPath,
                                         String cancelPath,
                                         String customerEmail,
                                         List<LineItem> lineItems,
                                         Map<String, String> metadata) throws StripeException {
        ensureConfigured();

        List<SessionCreateParams.LineItem> stripeItems = new ArrayList<>();
        for (LineItem item : lineItems) {
            stripeItems.add(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(item.quantity())
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("twd")
                                            .setUnitAmount(item.unitAmount())
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(item.name())
                                                            .setDescription(item.description())
                                                            .build())
                                            .build())
                            .build());
        }

        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(appProperties.getBaseUrl() + successPath + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(appProperties.getBaseUrl() + cancelPath)
                .addAllLineItem(stripeItems)
                .putMetadata("order_type", orderType);

        if (customerEmail != null && !customerEmail.isBlank()) {
            builder.setCustomerEmail(customerEmail);
        }
        if (metadata != null) {
            metadata.forEach(builder::putMetadata);
        }

        return Session.create(builder.build());
    }

    public Session retrieveSession(String sessionId) throws StripeException {
        ensureConfigured();
        return Session.retrieve(sessionId);
    }

    public boolean isSessionPaid(Session session) {
        return session != null && "paid".equals(session.getPaymentStatus());
    }

    private void ensureConfigured() {
        if (!stripeProperties.isConfigured()) {
            throw new IllegalStateException(
                    "Stripe 尚未設定，請在 .env 填入 STRIPE_SECRET_KEY 與 STRIPE_PUBLISHABLE_KEY");
        }
    }

    public record LineItem(String name, String description, long unitAmount, long quantity) {}
}