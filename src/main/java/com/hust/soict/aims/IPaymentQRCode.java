package com.hust.soict.aims;

import com.hust.soict.aims.entities.Order;
import com.hust.soict.aims.entities.payments.QRCode;
import com.hust.soict.aims.entities.payments.QRCodePaymentStatus;
import com.hust.soict.aims.exceptions.PaymentException;

public interface IPaymentQRCode {
    
    /**
     * Generate QR code for payment
     * @param order Order to generate QR code for
     * @return QRCode object containing QR image and bank information
     * @throws PaymentException if QR generation fails
     */
    QRCode generateQRCode(Order order) throws PaymentException;
    
    /**
     * Check payment status for an order
     * @param order Order to check payment status
     * @return PaymentStatus indicating current payment state
     * @throws PaymentException if status check fails
     */
    QRCodePaymentStatus checkPaymentStatus(Order order) throws PaymentException;
}
