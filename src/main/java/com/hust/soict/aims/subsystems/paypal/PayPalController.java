package com.hust.soict.aims.subsystems.paypal;

import com.hust.soict.aims.IPaymentGateway;
import com.hust.soict.aims.entities.payments.GatewayPaymentSession;
import com.hust.soict.aims.entities.payments.GatewayPaymentResult;
import com.hust.soict.aims.entities.Order;
import com.hust.soict.aims.exceptions.*;

import java.io.IOException;
import java.math.BigDecimal;

public class PayPalController implements IPaymentGateway {
	private final PayPalBoundary boundary;
	private final String returnUrl;
	private final String cancelUrl;

	// Token caching
	private String accessToken;
	private long tokenExpiryTime;

	// PayPal credentials
	private final String clientId;
	private final String clientSecret;

	public PayPalController(String clientId, String clientSecret, 
			String returnUrl, String cancelUrl, String apiBaseUrl) {
		this.boundary = new PayPalBoundary(apiBaseUrl);
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.returnUrl = returnUrl;
		this.cancelUrl = cancelUrl;
	}

	/**
	 * Get valid access token (renew if expired) Token expires in 300 seconds (5 minutes)
	 * 
	 * @return Valid access token
	 * @throws PaymentException if token generation fails
	 */
	private synchronized String getValidAccessToken() throws PaymentException {
		long currentTime = System.currentTimeMillis();

		// Check if token is still valid (with 30 seconds buffer)
		if (this.accessToken != null && currentTime < tokenExpiryTime - 30000) {
			return this.accessToken;
		}

		try {
			// Get new token
			AccessTokenRequest request = new AccessTokenRequest(clientId, clientSecret);
			String authHeader = request.buildAuthorizationHeader();

			String responseTokenString = boundary.getAccessToken(authHeader);

			AccessTokenResponse tokenResponse = new AccessTokenResponse();
			tokenResponse.parseResponseString(responseTokenString);

			this.accessToken = tokenResponse.getAccessToken();
			this.tokenExpiryTime = System.currentTimeMillis() + (tokenResponse.getExpiresIn() * 1000L);

			return this.accessToken;
		} catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new UnknownException("Thread interrupted", e);
        } catch (IOException e) {
        	throw new UnknownException("Error while fetching access token", e);
        }
	}

	@Override
	public GatewayPaymentSession createPayment(Order order) throws PaymentException {
		try {
			String token = getValidAccessToken();

			BigDecimal amount = BigDecimal.valueOf(order.getTotalAmount());
			BigDecimal amountUsd = CurrencyConverter.vndToUsd(amount);
			CreateOrderRequest request = new CreateOrderRequest(amountUsd, "USD", returnUrl, cancelUrl);

			String response = boundary.createOrder(token, request);
			CreateOrderResponse createOrderResponse = new CreateOrderResponse();
			createOrderResponse.parseResponseString(response);

			return new GatewayPaymentSession(createOrderResponse.getId(), createOrderResponse.getApproveLink());

		} catch (Exception e) {
			throw new UnknownException("Create payment failed", e);
		}
	}

	@Override
	public GatewayPaymentResult completePayment(String paymentId) throws PaymentException {
		try {
			String token = getValidAccessToken();

			String capture = boundary.captureOrder(token, paymentId);
			CaptureOrderResponse captureOrderResponse = new CaptureOrderResponse();
			captureOrderResponse.parseResponseString(capture);

			boolean success = captureOrderResponse.isSuccess();
			String status = captureOrderResponse.getStatus();

			return new GatewayPaymentResult(paymentId, success, status, null);

		} catch (Exception e) {
			return new GatewayPaymentResult(paymentId, false, "ERROR", e.getMessage());
		}
	}
}
