package com.hust.soict.aims.boundaries.customer.payment;

import javax.swing.*;
import java.awt.*;

import com.hust.soict.aims.boundaries.BaseScreenHandler;
import com.hust.soict.aims.controls.PayOrderController;
import com.hust.soict.aims.controls.PlaceOrderController;
import com.hust.soict.aims.controls.CartController;
import com.hust.soict.aims.entities.Invoice;
import com.hust.soict.aims.entities.QRCode;
import com.hust.soict.aims.entities.Order;
import com.hust.soict.aims.utils.ServiceProvider;

import static com.hust.soict.aims.utils.UIConstant.*;

/**
 * Screen for processing payment via QR code or credit card
 * Displays QR code and handles payment confirmation
 */
public class PaymentScreen extends BaseScreenHandler {
    private final Invoice invoice;
    private final CartController cartController;
    private PayOrderController payOrderController;
    
    private JPanel qrPanel;
    private JLabel qrImageLabel;
    private JLabel bankInfoLabel;
    private JLabel amountLabel;
    private JLabel orderIdLabel;
    
    private JButton confirmQRButton;
    private JButton creditCardButton;
    private JButton cancelButton;
    
    private boolean paid = false;
    
    public PaymentScreen(BaseScreenHandler parent, Invoice invoice, 
                        PlaceOrderController placeOrderController, CartController cartController) {
        super("Payment", parent, false);
        
        this.invoice = invoice;
        this.cartController = cartController;
        
        // Initialize PayOrderController
        this.payOrderController = new PayOrderController(
            ServiceProvider.getInstance().getQRPaymentController(),
            placeOrderController
        );
        
        initializeScreen();
    }
    
    @Override
    protected void initComponents() {
        // QR Panel
        qrPanel = new JPanel();
        qrPanel.setLayout(new BoxLayout(qrPanel, BoxLayout.Y_AXIS));
        qrPanel.setBackground(BACKGROUND_WHITE);
        qrPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_MEDIUM, 2),
            PADDING_LARGE
        ));
        
        // QR Image placeholder
        qrImageLabel = new JLabel("Generating QR Code...", SwingConstants.CENTER);
        qrImageLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 24));
        qrImageLabel.setForeground(TEXT_SECONDARY);
        qrImageLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_MEDIUM, 2),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        qrImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Bank info
        bankInfoLabel = new JLabel();
        bankInfoLabel.setFont(FONT_BODY);
        bankInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Amount
        amountLabel = new JLabel();
        amountLabel.setFont(FONT_HEADER);
        amountLabel.setForeground(INFO_COLOR);
        amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Order ID
        orderIdLabel = new JLabel();
        orderIdLabel.setFont(FONT_SMALL);
        orderIdLabel.setForeground(TEXT_SECONDARY);
        orderIdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Buttons
        confirmQRButton = new JButton("I Paid by QR");
        confirmQRButton.setFont(FONT_BUTTON);
        confirmQRButton.setBackground(SUCCESS_COLOR);
        confirmQRButton.setForeground(TEXT_ON_PRIMARY);
        confirmQRButton.setFocusPainted(false);
        confirmQRButton.setCursor(CURSOR_HAND);
        confirmQRButton.setPreferredSize(new Dimension(160, 40));
        
        creditCardButton = new JButton("Pay by Credit Card");
        creditCardButton.setFont(FONT_BUTTON);
        creditCardButton.setBackground(INFO_COLOR);
        creditCardButton.setForeground(TEXT_ON_PRIMARY);
        creditCardButton.setFocusPainted(false);
        creditCardButton.setCursor(CURSOR_HAND);
        creditCardButton.setPreferredSize(new Dimension(160, 40));
        
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(FONT_BUTTON);
        cancelButton.setBackground(BACKGROUND_GRAY);
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(CURSOR_HAND);
        cancelButton.setPreferredSize(new Dimension(100, 40));
    }
    
    @Override
    protected void setupLayout() {
        setLayout(new BorderLayout(SPACING_MEDIUM, SPACING_MEDIUM));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(PADDING_MEDIUM);
        headerPanel.setPreferredSize(new Dimension(0, HEADER_HEIGHT));
        
        JLabel titleLabel = new JLabel("Payment Method");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_ON_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JPanel headerWithNav = createHeaderWithNavigation(headerPanel);
        add(headerWithNav, BorderLayout.NORTH);
        
        // Center - QR Panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(PADDING_LARGE);
        centerPanel.setBackground(BACKGROUND_LIGHT);
        
        // Build QR display panel
        buildQRDisplayPanel();
        
        JScrollPane scrollPane = new JScrollPane(qrPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Footer - Buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_MEDIUM, SPACING_MEDIUM));
        footerPanel.setBackground(BACKGROUND_LIGHT);
        footerPanel.add(confirmQRButton);
        footerPanel.add(creditCardButton);
        footerPanel.add(cancelButton);
        
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Build QR display panel with QR code and payment info
     */
    private void buildQRDisplayPanel() {
        qrPanel.removeAll();
        
        // Title
        JLabel titleLabel = new JLabel("Scan QR Code to Pay");
        titleLabel.setFont(FONT_HEADER);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        qrPanel.add(titleLabel);
        qrPanel.add(Box.createRigidArea(new Dimension(0, SPACING_LARGE)));
        
        // QR Image
        qrPanel.add(qrImageLabel);
        qrPanel.add(Box.createRigidArea(new Dimension(0, SPACING_LARGE)));
        
        // Payment info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT),
            PADDING_MEDIUM
        ));
        
        infoPanel.add(bankInfoLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, SPACING_SMALL)));
        infoPanel.add(amountLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, SPACING_SMALL)));
        infoPanel.add(orderIdLabel);
        
        qrPanel.add(infoPanel);
        qrPanel.add(Box.createVerticalGlue());
        
        // Generate QR code in background
        loadQRCode();
    }
    
    /**
     * Load QR code from payment controller
     */
    private void loadQRCode() {
        SwingWorker<QRCode, Void> worker = new SwingWorker<>() {
            @Override
            protected QRCode doInBackground() throws Exception {
                Order order = invoice.getOrder();
                return payOrderController.generatePaymentQR(order);
            }
            
            @Override
            protected void done() {
                try {
                    QRCode qrCode = get();
                    displayQRCode(qrCode);
                } catch (Exception e) {
                    e.printStackTrace();
                    qrImageLabel.setText("<html><center>Failed to generate QR<br/>" + 
                        e.getMessage() + "</center></html>");
                    qrImageLabel.setForeground(DANGER_COLOR);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Display QR code on screen
     */
    private void displayQRCode(QRCode qrCode) {
        // Display QR image (if available)
        if (qrCode.getQrCode() != null && !qrCode.getQrCode().isEmpty()) {
            try {
                // If it's base64, decode and display
                // For now, just show placeholder
                qrImageLabel.setText("[ QR CODE ]");
                qrImageLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 32));
            } catch (Exception e) {
                qrImageLabel.setText("[ QR CODE ]");
            }
        } else {
            qrImageLabel.setText("[ QR CODE ]");
            qrImageLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 32));
        }
        
        // Update bank info
        bankInfoLabel.setText(String.format("<html><center>Bank: %s<br/>Account: %s</center></html>",
            qrCode.getBankName(), qrCode.getBankAccount()));
        
        // Update amount
        amountLabel.setText(String.format("Amount: $%.2f", invoice.getTotal()));
        
        // Update order ID
        orderIdLabel.setText("Order: " + invoice.getOrder().getId());
        
        qrPanel.revalidate();
        qrPanel.repaint();
    }
    
    @Override
    protected void bindEvents() {
        // Confirm QR payment
        confirmQRButton.addActionListener(e -> handleQRPayment());
        
        // Credit card payment
        creditCardButton.addActionListener(e -> handleCreditCardPayment());
        
        // Cancel
        cancelButton.addActionListener(e -> navigateBack());
    }
    
    /**
     * Handle QR payment confirmation
     */
    private void handleQRPayment() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Have you completed the payment via QR code?",
            "Confirm Payment",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            processPaymentCompletion();
        }
    }
    
    /**
     * Handle credit card payment
     */
    private void handleCreditCardPayment() {
        try {
            boolean success = ServiceProvider.getInstance()
                .getCreditCardController()
                .executePaymentFlow(invoice.getTotal());
            
            if (success) {
                processPaymentCompletion();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Payment not completed or timed out.",
                    "Payment Failed",
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error processing payment: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Complete order after successful payment
     */
    private void processPaymentCompletion() {
        // Call payOrder to reduce stock
        PlaceOrderController.PlaceOrderResult result = payOrderController.completeOrder();
        
        if (result.success) {
            paid = true;
            
            // Clear cart after successful payment
            cartController.clear();
            
            JOptionPane.showMessageDialog(this,
                "Payment successful! Thank you for your order.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Navigate back to homepage
            while (canNavigateBack()) {
                navigateBack();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to complete order: " + result.message,
                "Order Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isPaid() {
        return paid;
    }
}
