package com.hust.soict.aims.boundaries.customer.cart;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import com.hust.soict.aims.boundaries.BaseScreenHandler;
import com.hust.soict.aims.boundaries.customer.invoice.InvoiceScreen;
import com.hust.soict.aims.boundaries.customer.shipping.DeliveryInfoScreen;
import com.hust.soict.aims.controls.CartController;
import com.hust.soict.aims.controls.PayByCreditCardController;
import com.hust.soict.aims.controls.PlaceOrderController;
import com.hust.soict.aims.entities.CartItem;
import com.hust.soict.aims.entities.DeliveryInfo;

import static com.hust.soict.aims.utils.UIConstant.*;

public class CartScreen extends BaseScreenHandler {
    private final CartController cartController;
    private final PayByCreditCardController paymentController;
    private final PlaceOrderController placeController;
    
    private JPanel itemsPanel;
    private JLabel totalItemsLabel;
    private JLabel subtotalLabel;
    private JButton placeOrderButton;

    public CartScreen(CartController cartController, PayByCreditCardController paymentController, 
                     BaseScreenHandler parentScreen) {
        super("Shopping Cart", parentScreen, false);
        
        this.cartController = cartController;
        this.paymentController = paymentController;
        this.placeController = new PlaceOrderController(cartController);
        
        initializeScreen();
    }

    @Override
    protected void initComponents() {
        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(BACKGROUND_WHITE);
        
        totalItemsLabel = new JLabel("Total Items: 0");
        totalItemsLabel.setFont(FONT_BODY);
        
        subtotalLabel = new JLabel("Subtotal: $0.00");
        subtotalLabel.setFont(FONT_HEADER);
        subtotalLabel.setForeground(INFO_COLOR);

        placeOrderButton = new JButton("Place Order");
        placeOrderButton.setFont(FONT_BUTTON_LARGE);
        placeOrderButton.setBackground(PRIMARY_COLOR);  
        placeOrderButton.setForeground(TEXT_ON_PRIMARY);
        placeOrderButton.setFocusPainted(false);
        placeOrderButton.setPreferredSize(BUTTON_SIZE_LARGE);
        placeOrderButton.setCursor(CURSOR_HAND);
    }

    @Override
    protected void setupLayout() {
        setLayout(new BorderLayout(SPACING_SMALL, SPACING_SMALL));

        // Main Header Panel
        JPanel mainHeaderPanel = new JPanel(new BorderLayout());
        mainHeaderPanel.setBorder(PADDING_SMALL);
        mainHeaderPanel.setBackground(PRIMARY_COLOR);
        mainHeaderPanel.setPreferredSize(new Dimension(0, HEADER_HEIGHT));
        
        // Center: Title
        JLabel titleLabel = new JLabel("Your Shopping Cart");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_ON_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainHeaderPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Combine top navigation bar + main header
        JPanel headerWithNav = createHeaderWithNavigation(mainHeaderPanel);
        add(headerWithNav, BorderLayout.NORTH);
        
        // Center Panel - Cart Items
        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(PADDING_SMALL);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Footer Panel - Summary and Actions
        JPanel footerPanel = new JPanel(new BorderLayout(SPACING_SMALL, SPACING_SMALL));
        footerPanel.setBorder(PADDING_SMALL);
        footerPanel.setBackground(BACKGROUND_LIGHT);
        
        // Summary Panel (left side)
        JPanel summaryPanel = new JPanel(new GridLayout(2, 1, SPACING_XSMALL, SPACING_XSMALL));
        summaryPanel.setOpaque(false);
        summaryPanel.add(totalItemsLabel);
        summaryPanel.add(subtotalLabel);
        
        // Action Panel (right side)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        actionPanel.add(placeOrderButton);
        
        footerPanel.add(summaryPanel, BorderLayout.WEST);
        footerPanel.add(actionPanel, BorderLayout.EAST);
        
        add(footerPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void bindEvents() {
        // Place order button
        placeOrderButton.addActionListener(e -> {
            if (cartController.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Cart is empty! Please add items before placing order.", 
                    "Cart Empty", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Navigate to DeliveryInfoScreen
            DeliveryInfoScreen deliveryDialog = new DeliveryInfoScreen(this, cartController);
            navigateTo(deliveryDialog);
            DeliveryInfo deliveryInfo = deliveryDialog.getDeliveryInfo();
            
            if (deliveryInfo == null) {
                // User cancelled, navigation handled by ScreenNavigator
                return;
            }
            
            PlaceOrderController.PlaceOrderResult result = placeController.placeOrder(deliveryInfo);
            
            if (!result.success) {
                // Order failed, go back to CartScreen
                navigateBack();
                JOptionPane.showMessageDialog(this, 
                    result.message, 
                    "Order Failed", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Note: InvoiceScreen is modal JDialog, so it will block until closed
            InvoiceScreen invoiceScreen = new InvoiceScreen(this, result.invoice, paymentController);
            invoiceScreen.setVisible(true);
            
            if (invoiceScreen.isPaid()) {
                JOptionPane.showMessageDialog(this, 
                    "Payment successful! Thank you for your order.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                cartController.clear();
                
                // Go back to Homepage after successful payment
                // Navigate back until we reach Homepage (currentIndex = 0)
                while (canNavigateBack()) {
                    navigateBack();
                }
                refresh();
            }
            // If payment cancelled, user can use Back button to navigate
        });
    }
    
    @Override
    protected void onBeforeShow() {
        super.onBeforeShow();
        refresh();
    }

    @Override
    public void refresh() {
        itemsPanel.removeAll();
        
        List<CartItem> items = cartController.getItems();
        
        // Add each cart item using CartItemPanel component
        for (CartItem item : items) {
            CartItemPanel itemPanel = new CartItemPanel(item);
            
            // Set callback for quantity change
            itemPanel.setOnQuantityChanged(e -> {
                int newQty = itemPanel.getCurrentQuantity();
                cartController.updateQuantity(item.getProduct().getId(), newQty);
                refresh();
            });
            
            // Set callback for remove button
            itemPanel.setOnRemove(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Remove " + item.getProduct().getTitle() + " from cart?",
                    "Remove Item",
                    JOptionPane.YES_NO_OPTION);
                    
                if (confirm == JOptionPane.YES_OPTION) {
                    cartController.remove(item.getProduct().getId());
                    refresh();
                }
            });
            
            itemsPanel.add(itemPanel);
            itemsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        // Update summary
        int totalItems = cartController.getTotalQuantity();
        double subtotal = cartController.getSubtotal();
        
        totalItemsLabel.setText(String.format("Total Items: %d", totalItems));
        subtotalLabel.setText(String.format("Subtotal: $%.2f", subtotal));
        
        // Enable/disable buttons
        placeOrderButton.setEnabled(!cartController.isEmpty());
        
        // Refresh UI
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }
}
