package com.hust.soict.aims.boundaries.customer.cart;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import com.hust.soict.aims.boundaries.BaseScreenHandler;
import com.hust.soict.aims.boundaries.customer.shipping.DeliveryInfoScreen;
import com.hust.soict.aims.controls.CartController;
import com.hust.soict.aims.controls.PayByCreditCardController;
import com.hust.soict.aims.controls.PlaceOrderController;
import com.hust.soict.aims.entities.CartItem;
import com.hust.soict.aims.entities.DeliveryInfo;
import com.hust.soict.aims.invoice.InvoiceScreen;
import static com.hust.soict.aims.utils.UIConstant.*;

public class CartScreen extends BaseScreenHandler {
    private final CartController cartController;
    private final PayByCreditCardController paymentController;
    private final PlaceOrderController placeController;
    
    // UI Components
    private JPanel itemsPanel;
    private JLabel totalItemsLabel;
    private JLabel subtotalLabel;
    private JButton placeOrderButton;
    private JButton backButton;
    private JButton clearCartButton;

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
        // Header buttons
        backButton = new JButton(ICON_BACK + " Back");
        backButton.setFont(FONT_BUTTON);
        backButton.setBackground(BACKGROUND_GRAY);
        backButton.setFocusPainted(false);
        backButton.setCursor(CURSOR_HAND);
        
        clearCartButton = new JButton("Clear Cart");
        clearCartButton.setFont(FONT_BUTTON);
        clearCartButton.setBackground(DANGER_COLOR);
        clearCartButton.setForeground(TEXT_ON_PRIMARY);
        clearCartButton.setFocusPainted(false);
        clearCartButton.setCursor(CURSOR_HAND);
        
        // Items panel (will contain cart item rows)
        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(BACKGROUND_WHITE);
        
        // Summary labels
        totalItemsLabel = new JLabel("Total Items: 0");
        totalItemsLabel.setFont(FONT_BODY);
        
        subtotalLabel = new JLabel("Subtotal: $0.00");
        subtotalLabel.setFont(FONT_HEADER);
        subtotalLabel.setForeground(INFO_COLOR);
        
        // Place order button
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
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(PADDING_SMALL);
        headerPanel.setBackground(PRIMARY_COLOR);
        
        JLabel titleLabel = new JLabel(ICON_CART + " Your Shopping Cart");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_ON_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel headerButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACING_SMALL, 0));
        headerButtonsPanel.setOpaque(false);
        headerButtonsPanel.add(clearCartButton);
        headerButtonsPanel.add(backButton);
        headerPanel.add(headerButtonsPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
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
        // Back button
        backButton.addActionListener(e -> {
            dispose();
            if (parentScreen != null) {
                parentScreen.showScreen();
            }
        });
        
        // Clear cart button
        clearCartButton.addActionListener(e -> {
            if (cartController.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Cart is already empty!", 
                    "Cart", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear all items from cart?",
                "Clear Cart",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                cartController.clear();
                refresh();
                JOptionPane.showMessageDialog(this, 
                    "Cart cleared successfully!", 
                    "Cart", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // Place order button
        placeOrderButton.addActionListener(e -> {
            if (cartController.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Cart is empty! Please add items before placing order.", 
                    "Cart Empty", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            DeliveryInfoScreen deliveryDialog = new DeliveryInfoScreen(this);
            deliveryDialog.setVisible(true);
            DeliveryInfo deliveryInfo = deliveryDialog.getDeliveryInfo();
            
            if (deliveryInfo == null) return;
            
            PlaceOrderController.PlaceOrderResult result = placeController.placeOrder(deliveryInfo);
            
            if (!result.success) {
                JOptionPane.showMessageDialog(this, 
                    result.message, 
                    "Order Failed", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            InvoiceScreen invoiceScreen = new InvoiceScreen(this, result.invoice, paymentController);
            invoiceScreen.setVisible(true);
            
            if (invoiceScreen.isPaid()) {
                JOptionPane.showMessageDialog(this, 
                    "Payment successful! Thank you for your order.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                cartController.clear();
                refresh();
                
                dispose();
                if (parentScreen != null) {
                    parentScreen.showScreen();
                }
            }
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
        
        // Add each cart item as a panel
        for (CartItem item : items) {
            JPanel itemRow = createItemPanel(item);
            itemsPanel.add(itemRow);
            itemsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        // Update summary
        int totalItems = cartController.getTotalQuantity();
        double subtotal = cartController.getSubtotal();
        
        totalItemsLabel.setText(String.format("Total Items: %d", totalItems));
        subtotalLabel.setText(String.format("Subtotal: $%.2f", subtotal));
        
        // Enable/disable buttons
        placeOrderButton.setEnabled(!cartController.isEmpty());
        clearCartButton.setEnabled(!cartController.isEmpty());
        
        // Refresh UI
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }
    
    /**
     * Create a clean panel for each cart item with FIXED COLUMN WIDTHS
     */
    private JPanel createItemPanel(CartItem item) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT),
            PADDING_MEDIUM
        ));
        panel.setBackground(BACKGROUND_WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, CART_ITEM_HEIGHT));
        
        // Left: Product info (FIXED WIDTH)
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, SPACING_XSMALL));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(CART_PRODUCT_WIDTH, 50));
        leftPanel.setMinimumSize(new Dimension(CART_PRODUCT_WIDTH, 50));
        leftPanel.setMaximumSize(new Dimension(CART_PRODUCT_WIDTH, 50));
        
        JLabel nameLabel = new JLabel(item.getProduct().getTitle());
        nameLabel.setFont(FONT_PRODUCT_NAME);
        
        JLabel priceLabel = new JLabel(String.format("Price: $%.2f", item.getProduct().getCurrentPrice()));
        priceLabel.setFont(FONT_SMALL);
        priceLabel.setForeground(TEXT_SECONDARY);
        
        leftPanel.add(nameLabel);
        leftPanel.add(priceLabel);
        
        // Center: Quantity control (FIXED WIDTH)
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_XSMALL, SPACING_SMALL));
        centerPanel.setOpaque(false);
        centerPanel.setPreferredSize(new Dimension(CART_QUANTITY_WIDTH, 50));
        centerPanel.setMinimumSize(new Dimension(CART_QUANTITY_WIDTH, 50));
        centerPanel.setMaximumSize(new Dimension(CART_QUANTITY_WIDTH, 50));
        
        JLabel qtyLabel = new JLabel("Qty:");
        qtyLabel.setFont(FONT_SMALL);
        qtyLabel.setPreferredSize(new Dimension(35, 25));
        qtyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(item.getQuantity(), 1, 100, 1));
        qtySpinner.setPreferredSize(INPUT_SIZE_SMALL);
        qtySpinner.setFont(FONT_BODY);
        qtySpinner.addChangeListener(e -> {
            int newQty = (Integer) qtySpinner.getValue();
            cartController.updateQuantity(item.getProduct().getId(), newQty);
            refresh();
        });
        
        centerPanel.add(qtyLabel);
        centerPanel.add(qtySpinner);
        
        // Right: Subtotal and Remove button (FIXED WIDTH)
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 0, SPACING_XSMALL));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(CART_ACTIONS_WIDTH, 50));
        rightPanel.setMinimumSize(new Dimension(CART_ACTIONS_WIDTH, 50));
        rightPanel.setMaximumSize(new Dimension(CART_ACTIONS_WIDTH, 50));
        
        JLabel subtotalLabel = new JLabel(String.format("$%.2f", 
            item.getProduct().getCurrentPrice() * item.getQuantity()));
        subtotalLabel.setFont(FONT_BUTTON_LARGE);
        subtotalLabel.setForeground(INFO_COLOR);
        subtotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JButton removeButton = new JButton("Remove");
        removeButton.setPreferredSize(BUTTON_SIZE_MEDIUM);
        removeButton.setBackground(DANGER_COLOR);
        removeButton.setForeground(TEXT_ON_PRIMARY);
        removeButton.setFont(FONT_BUTTON);
        removeButton.setFocusPainted(false);
        removeButton.setCursor(CURSOR_HAND);
        removeButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Remove " + item.getProduct().getTitle() + " from cart?",
                "Remove Item",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                cartController.remove(item.getProduct().getId());
                refresh();
            }
        });
        
        rightPanel.add(subtotalLabel);
        rightPanel.add(removeButton);
        
        // Add all sections with proper spacing
        panel.add(leftPanel);
        panel.add(Box.createRigidArea(new Dimension(SPACING_MEDIUM, 0)));
        panel.add(centerPanel);
        panel.add(Box.createHorizontalGlue());
        panel.add(rightPanel);
        
        return panel;
    }
}
