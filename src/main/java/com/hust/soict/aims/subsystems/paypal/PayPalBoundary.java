package com.hust.soict.aims.subsystems.paypal;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

class PayPalBoundary {
	private static final String BASE_URL = "https://api-m.sandbox.paypal.com";

	// PayPal credentials
	private final String clientId;
	private final String clientSecret;
	
	private final HttpClient httpClient;

	PayPalBoundary(String clientId, String clientSecret) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.httpClient = HttpClient.newHttpClient();
	}

	String getAccessToken() throws IOException, InterruptedException {
		String credentials = clientId + ":" + clientSecret;
		String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/v1/oauth2/token"))
				.header("Authorization", "Basic " + encoded).header("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials")).build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		return response.body();
	}

	String createOrder(String accessToken, CreateOrderRequest requestDto)
			throws IOException, InterruptedException {

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/v2/checkout/orders"))
				.header("Authorization", "Bearer " + accessToken).header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(requestDto.buildRequestString())).build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		return response.body();
	}

	String captureOrder(String accessToken, String orderId)
			throws IOException, InterruptedException {

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(BASE_URL + "/v2/checkout/orders/" + orderId + "/capture"))
				.header("Authorization", "Bearer " + accessToken).header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.noBody()).build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		return response.body();
	}
}
