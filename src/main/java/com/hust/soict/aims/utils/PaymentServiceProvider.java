package com.hust.soict.aims.utils;

import com.hust.soict.aims.IPaymentGateway;
import com.hust.soict.aims.IPaymentQRCode;
import com.hust.soict.aims.subsystems.paypal.PayPalController;
import com.hust.soict.aims.subsystems.vietqr.VietQRController;

public class PaymentServiceProvider {
    private static PaymentServiceProvider instance;
    
    private IPaymentQRCode qrPaymentSubsystem;
    private IPaymentGateway gatewayPaymentSubsystem;
    
    private PaymentServiceProvider() {}

    public static PaymentServiceProvider getInstance() {
        if (instance == null) {
            instance = new PaymentServiceProvider();
        }
        return instance;
    }
    
    public void initialize() {
        // Initialize QR payment subsystem
        this.qrPaymentSubsystem = new VietQRController(
            ConfigLoader.getVietQRUsername(),
            ConfigLoader.getVietQRPassword(),
            ConfigLoader.getVietQRBankCode(),
            ConfigLoader.getVietQRBankAccount(),
            ConfigLoader.getVietQRUserBankName(),
            ConfigLoader.getVietQRApiBaseUrl()
        );
        
        // Initialize Gateway payment subsystem
        this.gatewayPaymentSubsystem = PayPalController.create(
            ConfigLoader.getPayPalClientId(),
            ConfigLoader.getPayPalClientSecret(),
            ConfigLoader.getPayPalReturnUrl(),
            ConfigLoader.getPayPalCancelUrl(),
            ConfigLoader.getPayPalApiBaseUrl()
        );
    }
    
    public IPaymentQRCode getQRPaymentSubsystem() {
        if (qrPaymentSubsystem == null) {
            throw new IllegalStateException("ServiceProvider not initialized!");
        }
        return qrPaymentSubsystem;
    }
    
    public IPaymentGateway getGatewayPaymentSubsystem() {
        if (gatewayPaymentSubsystem == null) {
            throw new IllegalStateException("ServiceProvider not initialized!");
        }
        return gatewayPaymentSubsystem;
    }
}
