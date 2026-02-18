package com.hust.soict.aims.entities.payments;

public class GatewayPaymentResult {
    private String paymentId;
    private boolean success;
    private String status;
    private String message;

    public GatewayPaymentResult(String paymentId, boolean success, String status, String message) {
        this.paymentId = paymentId;
        this.success = success;
        this.status = status;
        this.message = message;
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
    
    public String getMessage() {
    	return this.message;
    }
}
