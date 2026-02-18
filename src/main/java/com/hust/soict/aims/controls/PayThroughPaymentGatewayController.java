package com.hust.soict.aims.controls;

import com.hust.soict.aims.IPaymentGateway;
import com.hust.soict.aims.entities.Order;
import com.hust.soict.aims.entities.payments.GatewayPaymentResult;
import com.hust.soict.aims.entities.payments.GatewayPaymentSession;
import com.hust.soict.aims.entities.payments.PaymentTransaction;

import java.awt.Desktop;
import java.net.URI;
import java.util.UUID;

public class PayThroughPaymentGatewayController {
    private final IPaymentGateway paymentGateway;
    
    
    private static final long PAYMENT_TIMEOUT_MS = 120_000;
    private static final long POLL_INTERVAL_MS = 2000;

    public PayThroughPaymentGatewayController(IPaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    /**
     * Process payment for an order through the payment gateway.
     * Flow:
     * 1. Create payment session via gateway
     * 2. Open browser for user to approve payment
     * 3. Poll for payment completion
     * 4. Return PaymentTransaction with result
     *
     * @param order The order to pay for
     * @return PaymentTransaction with payment result
     */
    public PaymentTransaction payOrder(Order order) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setPaymentMethod("GATEWAY");
        transaction.setAmount(order.getTotalAmount());

        try {
            // Step 1: Create payment session
            GatewayPaymentSession session = paymentGateway.createPayment(order);
            
            if (session == null || session.getRedirectUrl() == null) {
                transaction.setStatus("FAILED");
                transaction.setErrorMessage("Failed to create payment session");
                return transaction;
            }

            String paymentId = session.getPaymentId();
            String redirectUrl = session.getRedirectUrl();

            // Step 2: Open browser for payment approval
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(redirectUrl));
            } else {
                transaction.setStatus("FAILED");
                transaction.setErrorMessage("Desktop not supported - cannot open browser");
                return transaction;
            }

            // Step 3: Poll for payment completion
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < PAYMENT_TIMEOUT_MS) {
                Thread.sleep(POLL_INTERVAL_MS);
                
                GatewayPaymentResult result = paymentGateway.completePayment(paymentId);
                
                if (result.isSuccess()) {
                    transaction.setStatus("SUCCESS");
                    transaction.setTransactionId(paymentId);
                    return transaction;
                } else if ("ERROR".equals(result.getStatus())) {
                    // Only fail on explicit error, continue polling for other statuses
                    transaction.setStatus("FAILED");
                    transaction.setErrorMessage(result.getMessage());
                    return transaction;
                }
            }

            // Timeout reached
            transaction.setStatus("FAILED");
            transaction.setErrorMessage("Payment timeout - please try again");

        } catch (Exception e) {
            transaction.setStatus("FAILED");
            transaction.setErrorMessage(e.getMessage());
        }

        return transaction;
    }
}