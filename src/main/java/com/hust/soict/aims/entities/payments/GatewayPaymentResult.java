package com.hust.soict.aims.entities.payments;

public class GatewayPaymentResult {
    private String paymentId;
    private boolean success;
    private String status;

    public GatewayPaymentResult(String paymentId, boolean success, String status) {
        this.paymentId = paymentId;
        this.success = success;
        this.status = status;
    }
    
    public String getPaymentId() {
    	return this.paymentId;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getStatus() {
        return this.status;
    }
}
