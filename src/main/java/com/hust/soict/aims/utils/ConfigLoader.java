package com.hust.soict.aims.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();
    
    static {
        loadProperties();
    }
    
    /**
     * Load properties from application.properties file
     */
    private static void loadProperties() {
        try (InputStream input = ConfigLoader.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            
            if (input == null) {
                System.err.println("Unable to find application.properties");
                return;
            }
            
            properties.load(input);
            
        } catch (IOException e) {
            System.err.println("Error loading application.properties: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public static String getVietQRUsername() {
        return getProperty("vietqr.username", "customer-vietqrtest-user2468");
    }
    
    public static String getVietQRPassword() {
        return getProperty("vietqr.password", "customer-vietqrtest-user2468=");
    }
    
    public static String getVietQRBankAccount() {
        return getProperty("vietqr.accountNo", "9704198526191432198");
    }
    
    public static String getVietQRUserBankName() {
        return getProperty("vietqr.accountName", "NGUYEN VAN A");
    }
    
    public static String getVietQRBankCode() {
        return getProperty("vietqr.bankCode", "970415");
    }
   
    public static String getPayPalClientId() {
        return getProperty("paypal.clientId", "");
    }
    
    public static String getPayPalClientSecret() {
        return getProperty("paypal.clientSecret", "");
    }
    
    public static String getPayPalReturnUrl() {
        return getProperty("paypal.returnUrl", "http://localhost:8080/paypal/return");
    }
    
    public static String getPayPalCancelUrl() {
        return getProperty("paypal.cancelUrl", "http://localhost:8080/paypal/cancel");
    }
}

