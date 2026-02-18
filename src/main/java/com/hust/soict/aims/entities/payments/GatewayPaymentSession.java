package com.hust.soict.aims.entities.payments;

public class GatewayPaymentSession {
    private String paymentId;
    private String redirectUrl;

    public GatewayPaymentSession(String paymentId, String redirectUrl) {
        this.paymentId = paymentId;
        this.redirectUrl = redirectUrl;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
