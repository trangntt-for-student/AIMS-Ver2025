package com.hust.soict.aims;

import com.hust.soict.aims.entities.payments.GatewayPaymentResult;
import com.hust.soict.aims.entities.payments.GatewayPaymentSession;
import com.hust.soict.aims.entities.Order;
import com.hust.soict.aims.exceptions.*;

public interface IPaymentGateway {
	public GatewayPaymentSession createPayment(Order order) throws PaymentException;
	public GatewayPaymentResult completePayment(String paymentId) throws PaymentException;
}
