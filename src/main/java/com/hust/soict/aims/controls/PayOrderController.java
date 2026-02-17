package com.hust.soict.aims.controls;

import com.hust.soict.aims.IPaymentQRCode;
import com.hust.soict.aims.entities.Order;
import com.hust.soict.aims.entities.QRCode;
import com.hust.soict.aims.entities.PaymentTransaction;
import com.hust.soict.aims.exceptions.PaymentException;
import com.hust.soict.aims.entities.PaymentStatus;
import java.util.UUID;

public class PayOrderController {
    private final IPaymentQRCode qrPaymentController;
    
    public PayOrderController(IPaymentQRCode qrPaymentController) {
        this.qrPaymentController = qrPaymentController;
    }
    
    public QRCode generatePaymentQR(Order order) throws PaymentException {
        return qrPaymentController.generateQRCode(order);
    }
    
    public PaymentStatus checkPaymentStatus(Order order) throws PaymentException {
        return qrPaymentController.checkPaymentStatus(order);
    }
    
    public PaymentTransaction payOrder(Order order) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setPaymentMethod("QR");
        transaction.setAmount(order.getTotalAmount());
        
        try {
            PaymentStatus status = checkPaymentStatus(order);
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
