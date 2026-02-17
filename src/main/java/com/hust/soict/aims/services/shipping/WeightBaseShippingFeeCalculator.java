package com.hust.soict.aims.services.shipping;

import com.hust.soict.aims.entities.Order;

public class WeightBaseShippingFeeCalculator implements IShippingFeeCalculator {
    
    // Shipping fee constants
    private static final double FREE_SHIPPING_THRESHOLD = 100000.0;
    private static final double MAX_SHIPPING_FEE = 25000.0;
    private static final double EXTRA_WEIGHT_FEE_PER_UNIT = 2500.0;
    private static final double EXTRA_WEIGHT_UNIT = 0.5;
    
    // Major city shipping rates
    private static final double MAJOR_CITY_BASE_WEIGHT = 3.0;
    private static final double MAJOR_CITY_BASE_PRICE = 22000.0;
    
    // Other city shipping rates
    private static final double OTHER_CITY_BASE_WEIGHT = 0.5;
    private static final double OTHER_CITY_BASE_PRICE = 30000.0;
    
    @Override
    public double calculateShippingFee(Order order) {
        if (order == null || order.getItems() == null) {
            return 0.0;
        }
        
        double subtotal = order.getSubtotal();
        double totalWeight = order.getTotalWeight();
        String city = order.getDeliveryInfo().getCity();
        
        // Free shipping for orders over threshold
        if (subtotal > FREE_SHIPPING_THRESHOLD) {
            return 0.0;
        }
        
        boolean isMajorCity = isMajorCity(city);
        double baseWeight = isMajorCity ? MAJOR_CITY_BASE_WEIGHT : OTHER_CITY_BASE_WEIGHT;
        double basePrice = isMajorCity ? MAJOR_CITY_BASE_PRICE : OTHER_CITY_BASE_PRICE;
        
        double fee = basePrice;
        if (totalWeight > baseWeight) {
            double extraWeight = totalWeight - baseWeight;
            int extraUnits = (int) Math.ceil(extraWeight / EXTRA_WEIGHT_UNIT);
            fee += extraUnits * EXTRA_WEIGHT_FEE_PER_UNIT;
        }
        
        return Math.min(fee, MAX_SHIPPING_FEE);
    }
    
    private boolean isMajorCity(String city) {
        if (city == null) return false;
        
        String cityLower = city.toLowerCase();
        return cityLower.contains("hanoi") 
            || cityLower.contains("ha noi")
            || cityLower.contains("ho chi minh") 
            || cityLower.contains("hochiminh") 
            || cityLower.contains("hcm");
    }
}
