package com.hust.soict.aims.subsystems.paypal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CaptureOrderResponse {
	private String id;
	private String status;
	private String captureId;

	public void parseResponseString(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(json);

			this.id = root.get("id").asText();
			this.status = root.get("status").asText();

			JsonNode captures = root.path("purchase_units").get(0).path("payments").path("captures");

			if (captures.isArray() && captures.size() > 0) {
				this.captureId = captures.get(0).get("id").asText();
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getId() {
		return this.id;
	}

	public String getStatus() {
		return this.status;
	}

	public String getCaptureId() {
		return this.captureId;
	}
}
