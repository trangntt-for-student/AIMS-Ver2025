package com.hust.soict.aims;

import com.hust.soict.aims.entities.Order;
import com.hust.soict.aims.entities.payments.QRCode;
import com.hust.soict.aims.entities.payments.QRCodePaymentStatus;
import com.hust.soict.aims.exceptions.PaymentException;

public interface IPaymentQRCode {
    QRCode generateQRCode(Order order) throws PaymentException;
    QRCodePaymentStatus checkPaymentStatus(Order order) throws PaymentException;
}
