package com.hust.soict.aims.subsystems.paypal;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

class AccessTokenRequest {
    private final String clientId;
    private final String clientSecret;
    
    AccessTokenRequest(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }
    
    String buildAuthorizationHeader() {
        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(
            credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encodedCredentials;
    }
    
    String getClientId() { return clientId; }
    String getClientSecret() { return clientSecret; }
}
