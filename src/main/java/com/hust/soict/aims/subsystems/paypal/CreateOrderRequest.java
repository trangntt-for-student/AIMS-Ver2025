package com.hust.soict.aims.subsystems.paypal;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Map;

class CreateOrderRequest {
	private final Map<String, Object> raw;

	CreateOrderRequest(BigDecimal amount, String currency, String returnUrl, String cancelUrl) {
		this.raw = Map.of("intent", "CAPTURE", "purchase_units",
				new Object[] { Map.of("amount", Map.of("currency_code", currency, "value", amount.toString())) },
				"application_context", Map.of("return_url", returnUrl, "cancel_url", cancelUrl));
	}

	String buildRequestString() {
		try {
			return new ObjectMapper().writeValueAsString(raw);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
