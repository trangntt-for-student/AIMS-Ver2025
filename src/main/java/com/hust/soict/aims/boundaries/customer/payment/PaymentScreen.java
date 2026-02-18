package com.hust.soict.aims.boundaries.customer.payment;

import javax.swing.*;
import java.awt.*;

import com.hust.soict.aims.boundaries.BaseScreenHandler;
import com.hust.soict.aims.controls.PlaceOrderController;
import com.hust.soict.aims.controls.CartController;
import com.hust.soict.aims.entities.Invoice;
import com.hust.soict.aims.entities.payments.QRCode;
import static com.hust.soict.aims.utils.UIConstant.*;

public class PaymentScreen extends BaseScreenHandler {
    private final Invoice invoice;
    private final CartController cartController;
    private final PlaceOrderController placeOrderController;
    
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
        this.placeOrderController = placeOrderController;
        
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
        
        creditCardButton = new JButton("Pay through PayPal");
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
    
    private void loadQRCode() {
        SwingWorker<QRCode, Void> worker = new SwingWorker<>() {
            @Override
            protected QRCode doInBackground() throws Exception {
                return placeOrderController.generatePaymentQR();
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

    private void displayQRCode(QRCode qrCode) {
    	GenerateQRCode generator = new GenerateQRCode(qrImageLabel);
    	
        // Display QR image (if available)
        if (qrCode.getQrCode() != null && !qrCode.getQrCode().isEmpty()) {
            String qrData = qrCode.getQrCode();
            
            // Check format: URL, Base64, or EMV QR string
            if (qrData.startsWith("http://") || qrData.startsWith("https://")) {
                // Load image from URL
                generator.generateQRImageFromURL(qrData);
            } else if (qrData.startsWith("data:image")) {
                // Base64 with data URI prefix
                generator.generateImageFromBase64(qrData);
            } else {
                // Assume it's EMV QR code string, generate QR image
                generator.generateQRImageFromString(qrData);
            }
        } else {
            qrImageLabel.setText("[ QR CODE ]");
            qrImageLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 32));
        }
        
        // Update bank info
        bankInfoLabel.setText(String.format("Bank: %s - Account: %s",
            qrCode.getBankName(), qrCode.getBankAccount()));
        
        // Update amount
        amountLabel.setText(String.format("Amount: %,.0f₫", invoice.getTotal()));
        
        // Update order ID
        orderIdLabel.setText("Order: " + placeOrderController.getCurrentOrder().getId());
        
        qrPanel.revalidate();
        qrPanel.repaint();
    }
    
    @Override
    protected void bindEvents() {
        // Confirm QR payment
        confirmQRButton.addActionListener(e -> handleQRCodePayment());
        
        // Credit card payment
        creditCardButton.addActionListener(e -> handleCreditCardPayment());
        
        // Cancel
        cancelButton.addActionListener(e -> navigateBack());
    }
    
    private void handleQRCodePayment() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Have you completed the payment via QR code?",
            "Confirm Payment",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            processPaymentCompletion();
        }
    }
    
    private void handleCreditCardPayment() {
        // Disable buttons during payment
        confirmQRButton.setEnabled(false);
        creditCardButton.setEnabled(false);
        cancelButton.setEnabled(false);
        
        // Show processing message
        JOptionPane.showMessageDialog(this,
            "A browser window will open for PayPal payment.\nPlease complete the payment and wait...",
            "PayPal Payment",
            JOptionPane.INFORMATION_MESSAGE);
        
        // Process payment in background thread
        SwingWorker<PlaceOrderController.PlaceOrderResult, Void> worker = new SwingWorker<>() {
            @Override
            protected PlaceOrderController.PlaceOrderResult doInBackground() {
                return placeOrderController.payOrderThroughGateway();
            }
            
            @Override
            protected void done() {
                // Re-enable buttons
                confirmQRButton.setEnabled(true);
                creditCardButton.setEnabled(true);
                cancelButton.setEnabled(true);
                
                try {
                    handlePaymentResult(get());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PaymentScreen.this,
                        "Error processing payment: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void processPaymentCompletion() {
        PlaceOrderController.PlaceOrderResult result = placeOrderController.payOrder();
        handlePaymentResult(result);
    }
    
    private void handlePaymentResult(PlaceOrderController.PlaceOrderResult result) {
        if (result.success) {
            paid = true;
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
