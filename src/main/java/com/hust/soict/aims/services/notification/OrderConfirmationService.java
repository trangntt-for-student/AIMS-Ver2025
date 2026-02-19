package com.hust.soict.aims.services.notification;

import com.hust.soict.aims.entities.CartItem;
import com.hust.soict.aims.entities.Invoice;
import com.hust.soict.aims.entities.Order;
import com.hust.soict.aims.entities.payments.PaymentTransaction;
import com.hust.soict.aims.utils.SystemServiceFactory;

public class OrderConfirmationService {   
    private final INotification notificationService;
    
    public OrderConfirmationService() {
        this.notificationService = SystemServiceFactory.createNotificationService();
    }
    
    public OrderConfirmationService(INotification notificationService) {
        this.notificationService = notificationService;
    }
    
    /**
     * Send order confirmation email asynchronously.
     * Runs in background thread to not block UI.
     */
    public void sendOrderConfirmation(Order order, Invoice invoice, PaymentTransaction transaction) {
        if (order == null || order.getDeliveryInfo() == null) {
            return;
        }
        
        String recipientEmail = order.getDeliveryInfo().getEmail();
        if (recipientEmail == null || recipientEmail.isEmpty()) {
            return;
        }
        
        String subject = "AIMS - Order Confirmation #" + order.getId();
        String content = buildOrderConfirmationContent(order, invoice, transaction);
        
        NotificationMessage message = new NotificationMessage(recipientEmail, subject, content);
        
        // Run notification in background to not block UI
        new Thread(() -> notificationService.notify(message)).start();
    }
    
    private String buildOrderConfirmationContent(Order order, Invoice invoice, PaymentTransaction transaction) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>");
        html.append("<html><head><meta charset='UTF-8'></head>");
        html.append("<body style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f5f5f5;'>");
        
        // Header
        html.append("<div style='background: linear-gradient(135deg, #1976D2, #42A5F5); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0;'>");
        html.append("<h1 style='margin: 0; font-size: 28px;'>🎉 Order Confirmed! 🎉</h1>");
        html.append("<p style='margin: 10px 0 0 0; opacity: 0.9;'>AIMS - An Internet Media Store</p>");
        html.append("</div>");
        
        // Main content
        html.append("<div style='background: white; padding: 30px; border-radius: 0 0 10px 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>");
        
        // Greeting
        html.append("<p style='font-size: 16px; color: #333;'>Dear <strong>")
            .append(order.getDeliveryInfo().getReceiverName())
            .append("</strong>,</p>");
        html.append("<p style='color: #666;'>Thank you for your order! Here's your order summary:</p>");
        
        // Order Info Box
        html.append("<div style='background: #E3F2FD; padding: 15px; border-radius: 8px; margin: 20px 0;'>");
        html.append("<p style='margin: 0; color: #1976D2;'><strong>Order ID:</strong> ").append(order.getId()).append("</p>");
        html.append("<p style='margin: 5px 0 0 0; color: #1976D2;'><strong>Status:</strong> <span style='background: #4CAF50; color: white; padding: 2px 8px; border-radius: 4px; font-size: 12px;'>").append(order.getStatus().toUpperCase()).append("</span></p>");
        html.append("</div>");
        
        // Items Table
        html.append("<h3 style='color: #1976D2; border-bottom: 2px solid #1976D2; padding-bottom: 10px;'>Order Items</h3>");
        html.append("<table style='width: 100%; border-collapse: collapse;'>");
        html.append("<tr style='background: #f8f9fa;'>");
        html.append("<th style='padding: 12px; text-align: left; border-bottom: 1px solid #ddd;'>Product</th>");
        html.append("<th style='padding: 12px; text-align: center; border-bottom: 1px solid #ddd;'>Qty</th>");
        html.append("<th style='padding: 12px; text-align: right; border-bottom: 1px solid #ddd;'>Price</th>");
        html.append("</tr>");
        
        for (CartItem item : order.getItems()) {
            html.append("<tr>");
            html.append("<td style='padding: 12px; border-bottom: 1px solid #eee;'>").append(item.getProduct().getTitle()).append("</td>");
            html.append("<td style='padding: 12px; text-align: center; border-bottom: 1px solid #eee;'>").append(item.getQuantity()).append("</td>");
            html.append(String.format("<td style='padding: 12px; text-align: right; border-bottom: 1px solid #eee;'>%,.0f₫</td>", item.getTotalPrice()));
            html.append("</tr>");
        }
        html.append("</table>");
        
        // Payment Summary
        html.append("<div style='background: #fafafa; padding: 15px; border-radius: 8px; margin-top: 20px;'>");
        html.append("<table style='width: 100%;'>");
        html.append(String.format("<tr><td style='padding: 5px 0; color: #666;'>Subtotal:</td><td style='text-align: right;'>%,.0f₫</td></tr>", invoice.getSubtotal()));
        html.append(String.format("<tr><td style='padding: 5px 0; color: #666;'>Shipping:</td><td style='text-align: right;'>%,.0f₫</td></tr>", invoice.getShippingFee()));
        html.append(String.format("<tr><td style='padding: 5px 0; color: #666;'>Tax:</td><td style='text-align: right;'>%,.0f₫</td></tr>", invoice.getTax()));
        html.append(String.format("<tr style='font-size: 18px; font-weight: bold; color: #1976D2;'><td style='padding: 10px 0; border-top: 2px solid #1976D2;'>Total:</td><td style='text-align: right; border-top: 2px solid #1976D2;'>%,.0f₫</td></tr>", invoice.getTotal()));
        html.append("</table>");
        html.append("</div>");
        
        // Payment Info
        html.append("<h3 style='color: #1976D2; border-bottom: 2px solid #1976D2; padding-bottom: 10px; margin-top: 25px;'>Payment Details</h3>");
        html.append("<p style='color: #666; margin: 5px 0;'><strong>Method:</strong> ").append(transaction.getPaymentMethod()).append("</p>");
        html.append("<p style='color: #666; margin: 5px 0;'><strong>Transaction ID:</strong> <code style='background: #f5f5f5; padding: 2px 6px; border-radius: 4px;'>").append(transaction.getTransactionId()).append("</code></p>");
        
        // Delivery Info
        html.append("<h3 style='color: #1976D2; border-bottom: 2px solid #1976D2; padding-bottom: 10px; margin-top: 25px;'>Delivery Address</h3>");
        html.append("<p style='color: #666; margin: 5px 0;'>").append(order.getDeliveryInfo().getAddressLine() + ", " + order.getDeliveryInfo().getDistrict() + ", " + order.getDeliveryInfo().getCity()).append("</p>");
        
        // Footer message
        html.append("<div style='margin-top: 30px; padding: 20px; background: #E8F5E9; border-radius: 8px; text-align: center;'>");
        html.append("<p style='margin: 0; color: #2E7D32;'>We will notify you when your order is shipped!</p>");
        html.append("</div>");
        
        html.append("<p style='margin-top: 30px; color: #999; font-size: 14px;'>Best regards,<br><strong>AIMS Team</strong></p>");
        
        html.append("</div></body></html>");
        
        return html.toString();
    }
}
