package com.hust.soict.aims.controls;

import com.hust.soict.aims.entities.*;
import com.hust.soict.aims.services.shipping.IShippingFeeCalculator;
import com.hust.soict.aims.services.shipping.WeightBaseShippingFeeCalculator;
import java.util.List;
import java.util.UUID;

public class PlaceOrderController {
    private final IShippingFeeCalculator shippingFeeCalculator;
    
    private Order currentOrder;
    private List<CartItem> orderItems;
    private double subtotal;
    
    public PlaceOrderController() {
        this.shippingFeeCalculator = new WeightBaseShippingFeeCalculator();
    }
    
    public PlaceOrderController(IShippingFeeCalculator shippingFeeCalculator) {
        this.shippingFeeCalculator = shippingFeeCalculator;
    }
    
    /**
     * Result class for place order operations
     */
    public static class PlaceOrderResult {
        public final boolean success;
        public final String message;
        public Invoice invoice;
        
        public PlaceOrderResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public static PlaceOrderResult success(String message) {
            return new PlaceOrderResult(true, message);
        }
        
        public static PlaceOrderResult failure(String message) {
            return new PlaceOrderResult(false, message);
        }
    }
    
    // ==================== Public API ====================
    
    /**
     * Place order from cart with delivery info
     * Validates stock, calculates shipping, creates invoice preview
     * Note: Does NOT reduce stock - call payOrder() after payment confirmation
     */
    public PlaceOrderResult placeOrder(CartController cart, DeliveryInfo deliveryInfo) {
        if (!initializeOrder(cart)) {
            return PlaceOrderResult.failure("Cart is empty");
        }
        
        PlaceOrderResult stockCheck = validateStock();
        if (!stockCheck.success) {
            return stockCheck;
        }
        
        if (!applyDeliveryInfo(deliveryInfo)) {
            return PlaceOrderResult.failure("Failed to set delivery information");
        }
        
        PlaceOrderResult result = PlaceOrderResult.success("Order created successfully");
        result.invoice = buildInvoice();
        return result;
    }
    
    /**
     * Finalize order after payment confirmation
     * Re-validates stock and reduces inventory
     */
    public PlaceOrderResult payOrder() {
        if (currentOrder == null || orderItems == null) {
            return PlaceOrderResult.failure("No order to pay");
        }
        
        PlaceOrderResult stockCheck = validateStock();
        if (!stockCheck.success) {
            return stockCheck;
        }
        
        return reduceStockAndComplete();
    }
    
    /**
     * Get current order being processed
     */
    public Order getCurrentOrder() {
        return currentOrder;
    }
    
    // ==================== Order Initialization ====================
    
    private boolean initializeOrder(CartController cart) {
        if (cart == null || cart.isEmpty()) {
            return false;
        }
        
        currentOrder = new Order();
        currentOrder.setId(UUID.randomUUID().toString());
        
        orderItems = cart.getItems();
        currentOrder.setItems(orderItems);
        
        subtotal = cart.getSubtotal();
        
        return true;
    }
    
    // ==================== Stock Validation ====================
    
    private PlaceOrderResult validateStock() {
        if (currentOrder == null || orderItems == null) {
            return PlaceOrderResult.failure("No order to validate");
        }
        
        StringBuilder insufficient = new StringBuilder();
        for (CartItem item : orderItems) {
            int stock = Database.getStock(item.getProduct().getId());
            if (stock < item.getQuantity()) {
                insufficient.append(String.format("%s (available: %d, needed: %d)%n",
                    item.getProduct().getTitle(), stock, item.getQuantity()));
            }
        }
        
        if (insufficient.length() > 0) {
            return PlaceOrderResult.failure("Insufficient stock for:\n" + insufficient);
        }
        
        return PlaceOrderResult.success("Stock validated");
    }
    
    private PlaceOrderResult reduceStockAndComplete() {
        try {
            for (CartItem item : orderItems) {
                Database.reduceStock(item.getProduct().getId(), item.getQuantity());
            }
            
            PlaceOrderResult result = PlaceOrderResult.success("Order completed successfully");
            result.invoice = buildInvoice();
            return result;
            
        } catch (Exception e) {
            return PlaceOrderResult.failure("Failed to process order: " + e.getMessage());
        }
    }
    
    // ==================== Delivery & Shipping ====================
    
    private boolean applyDeliveryInfo(DeliveryInfo deliveryInfo) {
        if (currentOrder == null) {
            return false;
        }
        
        currentOrder.setDeliveryInfo(deliveryInfo);
        currentOrder.setShippingFee(shippingFeeCalculator.calculateShippingFee(currentOrder));
        return true;
    }
    
    // ==================== Invoice ====================
    
    private Invoice buildInvoice() {
        if (currentOrder == null || currentOrder.getDeliveryInfo() == null) {
            return null;
        }
        return new Invoice(currentOrder, subtotal, currentOrder.getShippingFee());
    }
}
