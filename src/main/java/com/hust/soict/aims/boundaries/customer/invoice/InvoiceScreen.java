package com.hust.soict.aims.boundaries.customer.invoice;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

import com.hust.soict.aims.boundaries.BaseScreenHandler;
import com.hust.soict.aims.controls.PayByCreditCardController;
import com.hust.soict.aims.controls.CartController;
import com.hust.soict.aims.entities.Invoice;
import com.hust.soict.aims.entities.CartItem;

import static com.hust.soict.aims.utils.UIConstant.*;

public class InvoiceScreen extends BaseScreenHandler {
    private boolean paid = false;
    private final Invoice invoice;
    private final PayByCreditCardController paymentController;
    private final CartController cartController;
    
    private JPanel invoiceDetailsPanel;
    private JButton payButton;

    public InvoiceScreen(BaseScreenHandler parent, Invoice invoice, 
                        PayByCreditCardController paymentController, CartController cartController) {
        super("Invoice & Payment", parent, false);
        
        this.invoice = invoice;
        this.paymentController = paymentController;
        this.cartController = cartController;
        
        initializeScreen();
    }
    
    @Override
    protected void initComponents() {
        // Invoice details panel
        invoiceDetailsPanel = new JPanel();
        invoiceDetailsPanel.setLayout(new BoxLayout(invoiceDetailsPanel, BoxLayout.Y_AXIS));
        invoiceDetailsPanel.setBackground(BACKGROUND_WHITE);
        
        // Add invoice items using InvoiceItemPanel component
        JPanel itemsSection = createItemsSection();
        invoiceDetailsPanel.add(itemsSection);
        invoiceDetailsPanel.add(Box.createRigidArea(new Dimension(0, SPACING_LARGE)));
        
        // Add payment summary using PaymentSummaryPanel component
        PaymentSummaryPanel paymentSummaryPanel = new PaymentSummaryPanel(invoice);
        paymentSummaryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));
        invoiceDetailsPanel.add(paymentSummaryPanel);

        // Pay button
        payButton = new JButton("Proceed to Payment");
        payButton.setFont(FONT_BUTTON_LARGE);
        payButton.setBackground(PRIMARY_COLOR);
        payButton.setForeground(TEXT_ON_PRIMARY);
        payButton.setFocusPainted(false);
        payButton.setPreferredSize(BUTTON_SIZE_LARGE);
        payButton.setCursor(CURSOR_HAND);
    }
    
    private JPanel createItemsSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(BACKGROUND_WHITE);
        
        // Use TitledBorder to match Payment Summary style
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_MEDIUM, 2),
            "Order Items"
        );
        border.setTitleFont(FONT_HEADER);
        border.setTitleColor(PRIMARY_COLOR);
        section.setBorder(BorderFactory.createCompoundBorder(border, PADDING_LARGE));
        
        // Items container
        JPanel itemsContainer = new JPanel();
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));
        itemsContainer.setBackground(BACKGROUND_WHITE);
        
        // Add each item using InvoiceItemPanel
        List<CartItem> items = invoice.getOrder().getItems();
        for (CartItem item : items) {
            InvoiceItemPanel itemPanel = new InvoiceItemPanel(item);
            itemsContainer.add(itemPanel);
        }
        
        section.add(itemsContainer, BorderLayout.CENTER);
        
        return section;
    }
    
    @Override
    protected void setupLayout() {
        setLayout(new BorderLayout(SPACING_MEDIUM, SPACING_MEDIUM));
        
        // Main Header Panel
        JPanel mainHeaderPanel = new JPanel(new BorderLayout());
        mainHeaderPanel.setBackground(PRIMARY_COLOR);
        mainHeaderPanel.setBorder(PADDING_SMALL);
        mainHeaderPanel.setPreferredSize(new Dimension(0, HEADER_HEIGHT));
        
        JLabel titleLabel = new JLabel("Invoice & Payment");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_ON_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainHeaderPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Combine top navigation bar + main header
        JPanel headerWithNav = createHeaderWithNavigation(mainHeaderPanel);
        add(headerWithNav, BorderLayout.NORTH);
        
        // Center - Invoice Details
        JScrollPane scrollPane = new JScrollPane(invoiceDetailsPanel);
        scrollPane.setBorder(PADDING_SMALL);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Footer - Pay Button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACING_MEDIUM, SPACING_MEDIUM));
        footerPanel.setBackground(BACKGROUND_LIGHT);
        footerPanel.add(payButton);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    @Override
    protected void bindEvents() {
        payButton.addActionListener(e -> showPaymentDialog());
    }
    
    private void showPaymentDialog() {
        JDialog paymentDialog = new JDialog(this, "Select Payment Method", true);
        paymentDialog.setSize(400, 250);
        paymentDialog.setLayout(new BorderLayout(SPACING_MEDIUM, SPACING_MEDIUM));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(PADDING_MEDIUM);
        
        JLabel headerLabel = new JLabel("Choose Payment Method");
        headerLabel.setFont(FONT_HEADER);
        headerLabel.setForeground(TEXT_ON_PRIMARY);
        headerPanel.add(headerLabel);
        
        paymentDialog.add(headerPanel, BorderLayout.NORTH);
        
        // Center - QR Code Display
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(PADDING_LARGE);
        centerPanel.setBackground(BACKGROUND_WHITE);
        
        JLabel qrLabel = new JLabel("[ QR CODE ]", SwingConstants.CENTER);
        qrLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 28));
        qrLabel.setForeground(TEXT_SECONDARY);
        qrLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_MEDIUM, 2),
            PADDING_LARGE
        ));
        centerPanel.add(qrLabel, BorderLayout.CENTER);
        
        paymentDialog.add(centerPanel, BorderLayout.CENTER);
        
        // Footer - Payment Buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_MEDIUM, SPACING_MEDIUM));
        footerPanel.setBackground(BACKGROUND_LIGHT);
        
        JButton paidByQRButton = new JButton("I Paid by QR");
        paidByQRButton.setFont(FONT_BUTTON);
        paidByQRButton.setBackground(SUCCESS_COLOR);
        paidByQRButton.setForeground(TEXT_ON_PRIMARY);
        paidByQRButton.setFocusPainted(false);
        paidByQRButton.setCursor(CURSOR_HAND);
        
        JButton payByCardButton = new JButton("Pay by Credit Card");
        payByCardButton.setFont(FONT_BUTTON);
        payByCardButton.setBackground(INFO_COLOR);
        payByCardButton.setForeground(TEXT_ON_PRIMARY);
        payByCardButton.setFocusPainted(false);
        payByCardButton.setCursor(CURSOR_HAND);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(FONT_BUTTON);
        cancelButton.setBackground(BACKGROUND_GRAY);
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(CURSOR_HAND);
        
        footerPanel.add(paidByQRButton);
        footerPanel.add(payByCardButton);
        footerPanel.add(cancelButton);
        
        paymentDialog.add(footerPanel, BorderLayout.SOUTH);
        
        // Event handlers
        paidByQRButton.addActionListener(e -> {
            paid = true;
            paymentDialog.dispose();
            handlePaymentSuccess();
        });
        
        payByCardButton.addActionListener(e -> {
                try {
                    boolean paymentSuccess = paymentController.executePaymentFlow(invoice.getTotal());
                    if (paymentSuccess) {
                        paid = true;
                    paymentDialog.dispose();
                    handlePaymentSuccess();
                    } else {
                    JOptionPane.showMessageDialog(paymentDialog, 
                        "Payment not completed or timed out.", 
                        "Payment Failed", 
                        JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                JOptionPane.showMessageDialog(paymentDialog, 
                    "Error starting PayPal flow: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> paymentDialog.dispose());

        paymentDialog.setLocationRelativeTo(this);
        paymentDialog.setVisible(true);
    }
    
    private void handlePaymentSuccess() {
        // Clear cart after successful payment
        cartController.clear();
        
        JOptionPane.showMessageDialog(this, 
            "Payment successful! Thank you for your order.", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
        
        // Navigate back to Homepage
        while (canNavigateBack()) {
            navigateBack();
        }
    }

    public boolean isPaid() { 
        return paid; 
    }
}
