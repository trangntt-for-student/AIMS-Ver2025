package com.hust.soict.aims.controls;

import com.hust.soict.aims.entities.*;

import java.util.List;
import java.util.UUID;

public class PlaceOrderController {
    private final CartController cart;

    public PlaceOrderController(CartController cart) {
        this.cart = cart;
    }

    public static class PlaceOrderResult {
        public boolean success;
        public String message;
        public Invoice invoice;
    }

    public PlaceOrderResult placeOrder(DeliveryInfo info) {
        PlaceOrderResult res = new PlaceOrderResult();
        if (cart.isEmpty()) {
            res.success = false;
            res.message = "Cart is empty";
            return res;
        }

        // check stock
        List<CartItem> items = cart.getItems();
        StringBuilder insufficient = new StringBuilder();
        for (CartItem it : items) {
            int stock = Database.getStock(it.getProduct().getId());
            if (stock < it.getQuantity()) {
                insufficient.append(String.format("%s (have %d, need %d)\n", it.getProduct().getTitle(), stock, it.getQuantity()));
            }
        }
        if (insufficient.length() > 0) {
            res.success = false;
            res.message = "Insufficient stock for:\n" + insufficient.toString();
            return res;
        }

        // reduce stock
        for (CartItem it : items) {
            Database.reduceStock(it.getProduct().getId(), it.getQuantity());
        }

        // create order
        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setItems(items);
        order.setDeliveryInfo(info);

        double subtotal = cart.getSubtotal();
        double shipping = calculateShippingFee(subtotal, cart.getTotalWeight(), info.getCity());
        order.setShippingFee(shipping);

        Invoice invoice = new Invoice(order, subtotal, shipping);
        res.success = true;
        res.invoice = invoice;
        res.message = "Order placed";
        return res;
    }

    private double calculateShippingFee(double subtotal, double totalWeight, String city) {
        if (subtotal > 100000.0) return 0.0;

        boolean isHnHcm = city != null && (city.toLowerCase().contains("hanoi") || city.toLowerCase().contains("ho chi minh") || city.toLowerCase().contains("hochiminh") || city.toLowerCase().contains("hcm"));
        double baseWeight = isHnHcm ? 3.0 : 0.5;
        double basePrice = isHnHcm ? 22000.0 : 30000.0;

        double fee = 0.0;
        if (totalWeight <= baseWeight) {
            fee = basePrice;
        } else {
            double extra = totalWeight - baseWeight;
            int extraUnits = (int) Math.ceil(extra / 0.5);
            fee = basePrice + extraUnits * 2500.0;
        }

        if (fee > 25000.0) fee = 25000.0;
        return fee;
    }
}
