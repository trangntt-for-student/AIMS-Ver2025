package com.hust.soict.aims.subsystems.vietqr;

/**
 * Request for generating QR code
 */
public class QRGenerateRequest extends QRRequest {
    private String content;      // Payment content/description
    private long amount;         // Amount in VND
    private String orderId;      // Order ID
    
    public QRGenerateRequest(String content, long amount, String orderId) {
        this.content = content;
        this.amount = amount;
        this.orderId = orderId;
    }
    
    @Override
    public String buildRequestString() {
        // Build JSON request for QR generation
        // Using VietQR standard format
        return String.format(
            "{\"accountNo\":\"9704198526191432198\",\"accountName\":\"NGUYEN VAN A\",\"acqId\":\"970415\"," +
            "\"addInfo\":\"%s\",\"amount\":\"%d\",\"template\":\"compact\"}",
            content, amount
        );
    }
    
    public String getContent() { return content; }
    public long getAmount() { return amount; }
    public String getOrderId() { return orderId; }
}

