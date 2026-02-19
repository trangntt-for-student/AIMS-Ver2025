package com.hust.soict.aims.entities;

import com.hust.soict.aims.entities.payments.PaymentTransaction;
import com.hust.soict.aims.utils.MoneyConstant;

public class Invoice {
    private double subtotal;
    private double shippingFee;
    private double total;
    private PaymentTransaction paymentTransaction;

    public Invoice(double subtotal, double shippingFee, double total) {
        this.subtotal = subtotal;
        this.shippingFee = shippingFee;
        this.total = total;
    }

    public double getSubtotal() { return subtotal; }
    public double getShippingFee() { return shippingFee; }
    public double getTax() { return (subtotal + shippingFee) * MoneyConstant.TAX; }
    public double getTotal() { return total; }
    
    public PaymentTransaction getPaymentTransaction() { return paymentTransaction; }
    public void setPaymentTransaction(PaymentTransaction paymentTransaction) { 
        this.paymentTransaction = paymentTransaction; 
    }
}
