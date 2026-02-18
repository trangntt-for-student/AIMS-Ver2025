package com.hust.soict.aims.subsystems.paypal;

import com.hust.soict.aims.IPaymentGateway;
import com.hust.soict.aims.entities.payments.GatewayPaymentSession;
import com.hust.soict.aims.entities.payments.GatewayPaymentResult;
import com.hust.soict.aims.entities.Order;
import java.math.BigDecimal;

public class PayPalController implements IPaymentGateway {
	private final PayPalBoundary boundary;
	private final String returnUrl;
	private final String cancelUrl;

	public PayPalController(PayPalBoundary boundary, String returnUrl, String cancelUrl) {
		this.boundary = boundary;
		this.returnUrl = returnUrl;
		this.cancelUrl = cancelUrl;
	}

	@Override
	public GatewayPaymentSession createPayment(Order order) {
		try {
			AccessTokenResponse token = boundary.getAccessToken();
			
			BigDecimal amount = BigDecimal.valueOf(order.getTotalAmount());
			CreateOrderRequest request = new CreateOrderRequest(amount, "USD", returnUrl, cancelUrl);

			CreateOrderResponse response = boundary.createOrder(token.getAccessToken(), request);

			return new GatewayPaymentSession(response.getId(), response.getApproveLink());

		} catch (Exception e) {
			throw new RuntimeException("Create payment failed", e);
		}
	}

	@Override
	public GatewayPaymentResult completePayment(String paymentId) {
		try {
			AccessTokenResponse token = boundary.getAccessToken();

			CaptureOrderResponse capture = boundary.captureOrder(token.getAccessToken(), paymentId);

			boolean success = "COMPLETED".equalsIgnoreCase(capture.getStatus());

			return new GatewayPaymentResult(paymentId, success, capture.getStatus(), null);

		} catch (Exception e) {
			return new GatewayPaymentResult(paymentId, false, "ERROR", e.getMessage());
		}
	}
}
