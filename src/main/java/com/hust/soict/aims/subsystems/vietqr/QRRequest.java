package com.hust.soict.aims.subsystems.vietqr;

/**
 * Base class for VietQR API requests
 */
public abstract class QRRequest {
    protected static final String CONTENT_TYPE = "application/json";
    protected static final String AUTHORIZATION = "Bearer ";
    
    /**
     * Build request string (typically JSON) for API call
     * @return Request body string
     */
    public abstract String buildRequestString();
}

