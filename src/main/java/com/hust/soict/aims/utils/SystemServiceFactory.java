package com.hust.soict.aims.utils;

import com.hust.soict.aims.services.notification.INotification;
import com.hust.soict.aims.services.shipping.IShippingFeeCalculator;

public class SystemServiceFactory {   
    public static IShippingFeeCalculator createShippingFeeCalculator() {
        String fullClassName = ConfigLoader.getProperty("shippingFeeCalculator.class");
        return createInstance(fullClassName, IShippingFeeCalculator.class);
    }
    
    public static INotification createNotificationService() {
        String fullClassName = ConfigLoader.getProperty("notification.class");
        return createInstance(fullClassName, INotification.class);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T createInstance(String fullClassName, Class<T> expectedType) {
        if (fullClassName == null || fullClassName.isEmpty()) {
            throw new RuntimeException("Class name not configured for " + expectedType.getName());
        }
        
        try {
            Class<?> clazz = Class.forName(fullClassName);
            
            if (!expectedType.isAssignableFrom(clazz)) {
                throw new RuntimeException(String.format(
                    "Class %s does not implement %s", fullClassName, expectedType.getName()));
            }
            
            return (T) clazz.getDeclaredConstructor().newInstance();
            
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + fullClassName, e);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to instantiate: " + fullClassName, e);
        }
    }
}
