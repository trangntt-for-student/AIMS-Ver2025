package com.hust.soict.aims.subsystems.paypal;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

class CreateOrderRequest {
	private String intent = "CAPTURE";
	private final List<Map<String, Object>> purchaseUnits = new ArrayList<>();
	private Map<String, Object> applicationContext;

	CreateOrderRequest(BigDecimal amount, String currency, String returnUrl, String cancelUrl) {
		validateAmount(amount);
		validateCurrency(currency);

		addPurchaseUnit(amount, currency);

		this.applicationContext = Map.of("return_url", returnUrl, "cancel_url", cancelUrl);
	}

	private void addPurchaseUnit(BigDecimal amount, String currency) {

		Map<String, Object> unit = Map.of("amount", Map.of("currency_code", currency, "value", amount.toPlainString()));

		purchaseUnits.add(unit);
	}

	private void validateAmount(BigDecimal amount) {
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
			throw new IllegalArgumentException("Amount must be positive");
	}

	private void validateCurrency(String currency) {
		if (currency == null || currency.isBlank())
			throw new IllegalArgumentException("Currency is required");
	}

	String buildRequestString() {
		try {
			Map<String, Object> root = new HashMap<>();
			root.put("intent", intent);
			root.put("purchase_units", purchaseUnits);
			root.put("application_context", applicationContext);

			return new ObjectMapper().writeValueAsString(root);
		} catch (Exception e) {
			throw new RuntimeException("Cannot build PayPal request", e);
		}
	}
}
