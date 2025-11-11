package com.hust.soict.aims.utils;

import com.hust.soict.aims.controls.PayByCreditCardController;

public class ServiceProvider {
    private static ServiceProvider instance;
    
    private PayByCreditCardController payByCreditCardController;
    // Future: Add more payment controllers
    
    private ServiceProvider() {}
    
    /**
     * Get singleton instance
     */
    public static ServiceProvider getInstance() {
        if (instance == null) {
            instance = new ServiceProvider();
        }
        return instance;
    }
    
    /**
     * Initialize with payment controller from Spring context
     * Should be called once at application startup
     */
    public void initialize(PayByCreditCardController payByCreditCardController) {
        this.payByCreditCardController = payByCreditCardController;
    }
    
    /**
     * Get payment controller for credit card
     */
    public PayByCreditCardController getPaymentController() {
        if (payByCreditCardController == null) {
            throw new IllegalStateException("ServiceProvider not initialized! Call initialize() first.");
        }
        return payByCreditCardController;
    }
    
    /**
     * Future: Get QR code payment controller
     */
}

