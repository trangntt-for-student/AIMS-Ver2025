package com.hust.soict.aims.subsystems.vietqr;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class VietQRBoundary {
    private final String apiBaseUrl;
    private final HttpClient httpClient;
    
    VietQRBoundary(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
        this.httpClient = HttpClient.newHttpClient();
    }
    
    /**
     * Get access token from VietQR API
     * Uses Basic Authentication (username:password in Base64)
     * @param authorizationHeader Basic Auth header value (e.g., "Basic Base64String")
     * @return Response string containing access token
     * @throws IOException if HTTP request fails
     * @throws InterruptedException if request is interrupted
     */
    String getAccessToken(String authorizationHeader) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiBaseUrl + "/token_generate"))
                .header("Authorization", authorizationHeader)
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
    
    String generateQRCode(String accessToken, String requestString) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiBaseUrl + "/qr/generate-customer"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(requestString))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
    
    String checkPaymentStatus(String accessToken, String requestString) throws IOException, InterruptedException {
        // Note: Test callback uses different base path
        String testCallbackUrl = apiBaseUrl.replace("/vqr/api", "/vqr/bank/api/test/transaction-callback");
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(testCallbackUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(requestString))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
