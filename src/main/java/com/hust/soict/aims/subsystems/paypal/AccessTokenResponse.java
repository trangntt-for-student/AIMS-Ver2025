package com.hust.soict.aims.subsystems.paypal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class AccessTokenResponse {
	private String accessToken;
	private long expiresIn;

	void parseResponseString(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(json);

			this.accessToken = root.get("access_token").asText();
			this.expiresIn = root.get("expires_in").asLong();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	String getAccessToken() {
		return accessToken;
	}

	long getExpiresIn() {
		return expiresIn;
	}
}
