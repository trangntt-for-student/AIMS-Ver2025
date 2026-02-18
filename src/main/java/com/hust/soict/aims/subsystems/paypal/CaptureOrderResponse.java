package com.hust.soict.aims.subsystems.paypal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class CaptureOrderResponse {
	// SUCCESS
	private String id;
	private String status;
	private String captureId;

	// FAIL
	private String name;
	private String message;
	private String debugId;

	void parseResponseString(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(json);

			// Error handling
			if (root.has("name")) {
				this.name = root.path("name").asText(null);
				this.message = root.path("message").asText(null);
				this.debugId = root.path("debug_id").asText(null);
				return;
			}

			// Success fields
			this.id = root.path("id").asText(null);
			this.status = root.path("status").asText(null);

			JsonNode purchaseUnits = root.path("purchase_units");

			if (purchaseUnits.isArray() && purchaseUnits.size() > 0) {

				JsonNode captures = purchaseUnits.get(0).path("payments").path("captures");

				if (captures.isArray() && captures.size() > 0) {
					this.captureId = captures.get(0).path("id").asText(null);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("Cannot parse PayPal capture response", e);
		}
	}

	boolean isSuccess() {
		return "COMPLETED".equalsIgnoreCase(status);
	}

	boolean isError() {
		return name != null;
	}
	
	String getId() {
		return this.id;
	}
	
	String getStatus() {
		return this.status;
	}
	
	String getCaptureId() {
		return this.captureId;
	}

	String getErrorMessage() {
		if (!isError())
			return null;
		return name + " - " + message + " (debug_id=" + debugId + ")";
	}
}
