package com.hust.soict.aims.controls;

import com.hust.soict.aims.IPaymentQRCode;
import com.hust.soict.aims.entities.Order;
import com.hust.soict.aims.entities.payments.PaymentTransaction;
import com.hust.soict.aims.entities.payments.QRCode;
import com.hust.soict.aims.entities.payments.QRCodePaymentStatus;
import com.hust.soict.aims.exceptions.PaymentException;

import java.util.UUID;

public class PayOrderController {
    private final IPaymentQRCode qrPaymentController;
    
    public PayOrderController(IPaymentQRCode qrPaymentController) {
        this.qrPaymentController = qrPaymentController;
    }
    
    public QRCode generatePaymentQR(Order order) throws PaymentException {
        return qrPaymentController.generateQRCode(order);
    }
    
    public QRCodePaymentStatus checkPaymentStatus(Order order) throws PaymentException {
        return qrPaymentController.checkPaymentStatus(order);
    }
    
    public PaymentTransaction payOrder(Order order) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setPaymentMethod("QR");
        transaction.setAmount(order.getTotalAmount());
        
        try {
            QRCodePaymentStatus status = checkPaymentStatus(order);
            if (status.isCompleted()) {
                transaction.setStatus("SUCCESS");
            } else if (status.isFailed()) {
                transaction.setStatus("FAILED");
                transaction.setErrorMessage(status.getMessage());
            } else if (status.isCancelled()) {
                transaction.setStatus("CANCELLED");
                transaction.setErrorMessage(status.getMessage());
            } else {
                transaction.setStatus("PENDING");
            }
        } catch (Exception e) {
            transaction.setStatus("FAILED");
            transaction.setErrorMessage(e.getMessage());
        }
        
        return transaction;
    }
}
