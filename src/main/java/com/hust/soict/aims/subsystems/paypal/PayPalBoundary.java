package com.hust.soict.aims.subsystems.paypal;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class PayPalBoundary {
	private final String apiBaseUrl;
	private final HttpClient httpClient;

	PayPalBoundary(String apiBaseUrl) {
		this.apiBaseUrl = apiBaseUrl;
		this.httpClient = HttpClient.newHttpClient();
	}

	String getAccessToken(String authorizationHeader) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(apiBaseUrl + "/v1/oauth2/token"))
				.header("Authorization", authorizationHeader)
				.header("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		return response.body();
	}

	String createOrder(String accessToken, CreateOrderRequest requestDto)
			throws IOException, InterruptedException {

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(apiBaseUrl + "/v2/checkout/orders"))
				.header("Authorization", "Bearer " + accessToken)
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(requestDto.buildRequestString()))
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		return response.body();
	}

	String captureOrder(String accessToken, String orderId)
			throws IOException, InterruptedException {

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(apiBaseUrl + "/v2/checkout/orders/" + orderId + "/capture"))
				.header("Authorization", "Bearer " + accessToken)
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.noBody())
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		return response.body();
	}
}
