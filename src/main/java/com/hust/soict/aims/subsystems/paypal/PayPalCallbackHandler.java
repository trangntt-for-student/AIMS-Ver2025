package com.hust.soict.aims.subsystems.paypal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PayPalCallbackHandler {

    @GetMapping(path = "/paypal/return")
    public String paypalReturn(@RequestParam(name = "token", required = false) String token) {
        return "<html>" +
            "<head>" +
            "<style>" +
            "body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }" +
            ".success { color: #28a745; }" +
            "h3 { margin-bottom: 20px; }" +
            "</style>" +
            "</head>" +
            "<body>" +
            "<h3 class=\"success\">✓ Payment Approved</h3>" +
            "<p>You can close this window and return to the application.</p>" +
            "<p>The application will automatically process your payment.</p>" +
            "</body>" +
            "</html>";
    }

    @GetMapping(path = "/paypal/cancel")
    public String paypalCancel() {
        return "<html>" +
            "<head>" +
            "<style>" +
            "body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }" +
            ".cancelled { color: #dc3545; }" +
            "h3 { margin-bottom: 20px; }" +
            "</style>" +
            "</head>" +
            "<body>" +
            "<h3 class=\"cancelled\">✗ Payment Cancelled</h3>" +
            "<p>You can close this window and return to the application.</p>" +
            "</body>" +
            "</html>";
    }
}
