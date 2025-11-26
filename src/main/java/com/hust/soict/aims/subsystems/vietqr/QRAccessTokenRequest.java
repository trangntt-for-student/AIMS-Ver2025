package com.hust.soict.aims.subsystems.vietqr;

import java.util.Base64;

/**
 * Request for getting VietQR access token
 * Uses Basic Authentication (username:password encoded in Base64)
 */
public class QRAccessTokenRequest extends QRRequest {
    private String username;
    private String password;
    
    public QRAccessTokenRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    /**
     * Build Basic Authentication header value
     * Format: "Basic {Base64(username:password)}"
     * @return Authorization header value
     */
    public String buildAuthorizationHeader() {
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encodedCredentials;
    }
    
    @Override
    public String buildRequestString() {
        // VietQR API doesn't need request body for token generation
        // Only Basic Auth header is required
        return "";
    }
    
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}

