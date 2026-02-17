package com.hust.soict.aims.entities;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private String id;
    private List<CartItem> items;
    private DeliveryInfo deliveryInfo;
    private double shippingFee;
    private LocalDateTime createdAt;
    private String status;
    private Invoice invoice;
    private static final double TAX = 0.1;

    public Order() { this.createdAt = LocalDateTime.now(); }

    public String getId() { return id; }
    
    public void setId(String id) { this.id = id; }
    
    public List<CartItem> getItems() { return items; }
    
    public void setItems(List<CartItem> items) { this.items = items; }
    
    public DeliveryInfo getDeliveryInfo() { return deliveryInfo; }
    
    public void setDeliveryInfo(DeliveryInfo deliveryInfo) { this.deliveryInfo = deliveryInfo; }
    
    public double getShippingFee() { return shippingFee; }
    
    public void setShippingFee(double shippingFee) { this.shippingFee = shippingFee; }

    public String getStatus() { return status; }
    
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    public Invoice getInvoice() { return invoice; }
    
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }
    
    public double getSubtotal() {
        return items.stream()
                .mapToDouble(item -> item.getProduct().getCurrentPrice() * item.getQuantity())
                .sum();
    }

    public double getTotalWeight() {
        return items.stream()
                .mapToDouble(item -> item.getProduct().getWeight() * item.getQuantity())
                .sum();
    }
    
    public double getTotalAmount() {
    	long amount = (long) (getSubtotal() + getShippingFee());
        return amount + amount * TAX;
    }
}
