package com.hust.soict.aims.entities;

public class Invoice {
    private double subtotal;
    private double shippingFee;
    private double total;
    private PaymentTransaction paymentTransaction;

    public Invoice(double subtotal, double shippingFee) {
        this.subtotal = subtotal;
        this.shippingFee = shippingFee;
        this.total = subtotal + shippingFee;
    }

    public double getSubtotal() { return subtotal; }
    public double getShippingFee() { return shippingFee; }
    public double getTotal() { return total; }
    
    public PaymentTransaction getPaymentTransaction() { return paymentTransaction; }
    public void setPaymentTransaction(PaymentTransaction paymentTransaction) { 
        this.paymentTransaction = paymentTransaction; 
    }
}
