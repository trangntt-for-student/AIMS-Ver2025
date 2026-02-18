package com.hust.soict.aims.entities.payments;

import java.time.LocalDateTime;

public class PaymentTransaction {
    private String transactionId;
    private String paymentMethod;  // "QR", "CREDIT_CARD"
    private double amount;
    private String status;  // "SUCCESS", "FAILED", "PENDING"
    private LocalDateTime transactionTime;
    private String errorMessage;

    public PaymentTransaction() {
        this.transactionTime = LocalDateTime.now();
    }

    public PaymentTransaction(String transactionId, String paymentMethod, double amount, String status) {
        this.transactionId = transactionId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.status = status;
        this.transactionTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTransactionTime() { return transactionTime; }
    public void setTransactionTime(LocalDateTime transactionTime) { this.transactionTime = transactionTime; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public boolean isSuccess() {
        return "SUCCESS".equalsIgnoreCase(status);
    }
}
