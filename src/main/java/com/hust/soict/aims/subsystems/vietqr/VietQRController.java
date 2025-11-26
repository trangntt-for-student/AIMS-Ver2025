package com.hust.soict.aims.subsystems.vietqr;

import com.hust.soict.aims.controls.IPaymentQRCode;
import com.hust.soict.aims.entities.Order;
import com.hust.soict.aims.entities.QRCode;
import com.hust.soict.aims.exceptions.PaymentException;
import com.hust.soict.aims.exceptions.UnknownException;
import com.hust.soict.aims.entities.PaymentStatus;

/**
 * Controller implementing VietQR payment subsystem
 * Implements IPaymentQRCode interface
 */
public class VietQRController implements IPaymentQRCode {
    private final VietQRBoundary boundary;
    private String accessToken;
    private long tokenExpiryTime;
    
    // VietQR credentials
    private final String vietqrUsername;
    private final String vietqrPassword;
    
    /**
     * Create VietQRController with credentials
     * @param username VietQR username from config
     * @param password VietQR password from config
     */
    public VietQRController(String username, String password) {
        this.boundary = new VietQRBoundary();
        this.vietqrUsername = username;
        this.vietqrPassword = password;
    }
    
    /**
     * Get valid access token (renew if expired)
     * Token expires in 300 seconds (5 minutes)
     * @return Valid access token
     * @throws PaymentException if token generation fails
     */
    private String getValidAccessToken() throws PaymentException {
        long currentTime = System.currentTimeMillis();
        
        // Check if token is still valid (with 30 seconds buffer)
        if (accessToken != null && currentTime < tokenExpiryTime - 30000) {
            return accessToken;
        }
        
        // Get new token
        try {
            QRAccessTokenRequest request = new QRAccessTokenRequest(vietqrUsername, vietqrPassword);
            String authHeader = request.buildAuthorizationHeader();
            
            String responseString = boundary.getAccessToken(authHeader);
            
            QRAccessTokenResponse response = new QRAccessTokenResponse();
            response.parseResponseString(responseString);
            
            if (response.isValid()) {
                this.accessToken = response.getAccessToken();
                this.tokenExpiryTime = currentTime + (response.getExpiresIn() * 1000L);
                return this.accessToken;
            } else {
                throw new UnknownException("Failed to get valid access token");
            }
        } catch (Exception e) {
            throw new UnknownException("Failed to get access token: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate QR code for payment using VietQR service
     * @param order Order to generate QR code for
     * @return QRCode object containing QR information
     * @throws PaymentException if generation fails
     */
    @Override
    public QRCode generateQRCode(Order order) throws PaymentException {
        try {
            // Get valid access token
            String token = getValidAccessToken();
            
            // Create request
            String content = "ORDER " + order.getId();
            
            // Calculate total amount (items + shipping)
            double subtotal = order.getItems().stream()
                .mapToDouble(item -> item.getProduct().getCurrentPrice() * item.getQuantity())
                .sum();
            long amount = (long) (subtotal + order.getShippingFee());
            
            QRGenerateRequest request = new QRGenerateRequest(content, amount, order.getId());
            String requestString = request.buildRequestString();
            
            // Call VietQR API with Bearer token
            String response = boundary.generateQRCode(token, requestString);
            
            // Parse response and create QRCode entity
            QRCode qrCode = parseQRCodeResponse(response);
            
            return qrCode;
            
        } catch (Exception e) {
            throw new UnknownException("Failed to generate QR code: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check payment status for an order
     * @param order Order to check
     * @return PaymentStatus indicating current state
     * @throws PaymentException if check fails
     */
    @Override
    public PaymentStatus checkPaymentStatus(Order order) throws PaymentException {
        try {
            // Get valid access token
            String token = getValidAccessToken();
            
            // Create status check request (simplified)
            String requestString = String.format("{\"orderId\":\"%s\"}", order.getId());
            
            // Call API with Bearer token
            String response = boundary.checkPaymentStatus(token, requestString);
            
            // Parse and return status
            return PaymentStatus.parseResponseString(response);
            
        } catch (Exception e) {
            throw new UnknownException("Failed to check payment status: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parse VietQR API response to QRCode entity
     * @param response JSON response from VietQR
     * @return QRCode object
     */
    private QRCode parseQRCodeResponse(String response) {
        // Simple JSON parsing (in production, use Jackson or Gson)
        QRCode qrCode = new QRCode();
        
        if (response != null) {
            // Extract qrDataURL (base64 image)
            if (response.contains("qrDataURL")) {
                int start = response.indexOf("\"qrDataURL\":\"") + 13;
                int end = response.indexOf("\"", start);
                if (start > 12 && end > start) {
                    qrCode.setQrCode(response.substring(start, end));
                }
            }
            
            // Set default bank info (from VietQR)
            qrCode.setBankCode("VCB");
            qrCode.setBankName("Vietcombank");
            qrCode.setBankAccount("9704198526191432198");
        }
        
        return qrCode;
    }
}

