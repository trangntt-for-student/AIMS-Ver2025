package com.hust.soict.aims;

import com.hust.soict.aims.entities.payments.GatewayPaymentResult;
import com.hust.soict.aims.entities.payments.GatewayPaymentSession;
import com.hust.soict.aims.entities.Order;

public interface IPaymentGateway {
	public GatewayPaymentSession createPayment(Order order);
	public GatewayPaymentResult completePayment(String paymentId);
}
