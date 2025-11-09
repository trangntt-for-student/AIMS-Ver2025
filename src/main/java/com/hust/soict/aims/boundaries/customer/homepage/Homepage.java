package com.hust.soict.aims.boundaries.customer.homepage;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import com.hust.soict.aims.controls.ProductController;
import com.hust.soict.aims.controls.PayByCreditCardController;
import com.hust.soict.aims.boundaries.BaseScreenHandler;
import com.hust.soict.aims.boundaries.ProductDetailScreen;
import com.hust.soict.aims.boundaries.customer.cart.CartScreen;
import com.hust.soict.aims.controls.CartController;
import com.hust.soict.aims.entities.Product;
import static com.hust.soict.aims.utils.UIConstant.*;

public class Homepage extends BaseScreenHandler {
    private final ProductController controller;
    private final CartController cart;
    private final PayByCreditCardController paymentController;
    
    private int currentPage = 0;
    
    private JPanel gridPanel;
    private JLabel pageLabel;
    private JButton prevButton;
    private JButton nextButton;
    private JButton cartButton;
    
    public Homepage(ProductController controller, CartController cart,
                   PayByCreditCardController paymentController) {
        super("AIMS - Homepage", null, false);
        
        this.controller = controller;
        this.cart = cart;
        this.paymentController = paymentController;
        
        // Initialize screen components and layout
        initializeScreen();
        
        // Override close operation: Exit app when closing Homepage (main window)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    @Override
    protected void initComponents() {
        // Initialize grid panel for products
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(PRODUCT_GRID_ROWS, PRODUCT_GRID_COLS, 
                                          PRODUCT_GRID_HGAP, PRODUCT_GRID_VGAP));
        gridPanel.setBackground(BACKGROUND_LIGHT);
        
        // Initialize navigation components
        pageLabel = new JLabel();
        pageLabel.setFont(FONT_BODY);
        
        prevButton = new JButton(ICON_BACK + " Prev");
        prevButton.setFont(FONT_BUTTON);
        prevButton.setBackground(BACKGROUND_GRAY);
        prevButton.setFocusPainted(false);
        prevButton.setCursor(CURSOR_HAND);
        
        nextButton = new JButton("Next " + ICON_FORWARD);
        nextButton.setFont(FONT_BUTTON);
        nextButton.setBackground(BACKGROUND_GRAY);
        nextButton.setFocusPainted(false);
        nextButton.setCursor(CURSOR_HAND);
        
        // Initialize cart button
        cartButton = new JButton(getCartButtonText());
        cartButton.setFont(FONT_BUTTON);
        cartButton.setBackground(PRIMARY_COLOR);
        cartButton.setForeground(TEXT_ON_PRIMARY);
        cartButton.setFocusPainted(false);
        cartButton.setCursor(CURSOR_HAND);
    }
    
    @Override
    protected void setupLayout() {
        setLayout(new BorderLayout(SPACING_SMALL, SPACING_SMALL));
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(PADDING_MEDIUM);
        
        JLabel titleLabel = new JLabel(ICON_CART + " AIMS - Product Store");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_ON_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(cartButton, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Center - Product grid with scroll
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(PADDING_SMALL);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Footer - Pagination
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_MEDIUM, SPACING_MEDIUM));
        footerPanel.setBackground(BACKGROUND_LIGHT);
        footerPanel.add(prevButton);
        footerPanel.add(pageLabel);
        footerPanel.add(nextButton);
        
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    @Override
    protected void bindEvents() {
        // Bind pagination buttons
        prevButton.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                refresh();
            }
        });
        
        nextButton.addActionListener(e -> {
            int total = controller.countProducts();
            int pages = (total - 1) / controller.getPageSize();
            if (currentPage < pages) {
                currentPage++;
                refresh();
            }
        });
        
        // Bind cart button
        cartButton.addActionListener(e -> {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Cart is empty", 
                    "Cart", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                openCart();
            }
        });
        
        // Subscribe to cart changes to update cart button
        cart.setChangeListener(count -> {
            SwingUtilities.invokeLater(() -> {
                cartButton.setText(getCartButtonText());
            });
        });
    }
    
    @Override
    protected void onBeforeShow() {
        super.onBeforeShow();
        // Refresh product list every time the screen is shown
        refresh();
    }

    /**
     * Refresh the product list and pagination display
     * Override from BaseScreenHandler to implement specific refresh logic
     */
    @Override
    public void refresh() {
        gridPanel.removeAll();
        
        // Load products for current page
        List<Product> products = controller.getPage(currentPage);
        
        // Create product cards with standardized style
        for (Product p : products) {
            JPanel card = new JPanel(new BorderLayout(SPACING_SMALL, SPACING_SMALL));
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT),
                PADDING_MEDIUM
            ));
            card.setBackground(BACKGROUND_WHITE);
            
            // Product title (top)
            JLabel titleLabel = new JLabel(p.getTitle());
            titleLabel.setFont(FONT_PRODUCT_NAME);
            titleLabel.setForeground(TEXT_PRIMARY);
            card.add(titleLabel, BorderLayout.NORTH);
            
            // Product info (center)
            JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, SPACING_XSMALL));
            infoPanel.setOpaque(false);
            
            JLabel priceLabel = new JLabel(String.format("Price: $%.2f", p.getCurrentPrice()));
            priceLabel.setFont(FONT_BODY);
            priceLabel.setForeground(INFO_COLOR);
            
            JLabel weightLabel = new JLabel(String.format("Weight: %.2f kg", p.getWeight()));
            weightLabel.setFont(FONT_SMALL);
            weightLabel.setForeground(TEXT_SECONDARY);
            
            infoPanel.add(priceLabel);
            infoPanel.add(weightLabel);
            card.add(infoPanel, BorderLayout.CENTER);
            
            // Buttons (bottom)
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACING_XSMALL, 0));
            buttonPanel.setOpaque(false);
            
            JButton addButton = new JButton("Add");
            addButton.setFont(FONT_BUTTON);
            addButton.setBackground(PRIMARY_COLOR);
            addButton.setForeground(TEXT_ON_PRIMARY);
            addButton.setFocusPainted(false);
            addButton.setPreferredSize(new Dimension(70, 35));
            addButton.setCursor(CURSOR_HAND);
            addButton.addActionListener(e -> {
                cart.addProduct(p, 1);
                JOptionPane.showMessageDialog(this, 
                    "Added to cart: " + p.getTitle(), 
                    "Cart", 
                    JOptionPane.INFORMATION_MESSAGE);
            });
            
            JButton infoButton = new JButton(ICON_INFO);
            infoButton.setFont(FONT_BUTTON_LARGE);
            infoButton.setBackground(BACKGROUND_GRAY);
            infoButton.setForeground(TEXT_PRIMARY);
            infoButton.setFocusPainted(false);
            infoButton.setPreferredSize(BUTTON_SIZE_ICON);
            infoButton.setCursor(CURSOR_HAND);
            infoButton.addActionListener(e -> {
                ProductDetailScreen detailScreen = new ProductDetailScreen(this, p);
                detailScreen.setVisible(true);
            });
            
            buttonPanel.add(addButton);
            buttonPanel.add(infoButton);
            card.add(buttonPanel, BorderLayout.SOUTH);
            
            gridPanel.add(card);
        }
        
        // Update pagination label
        int total = controller.countProducts();
        int pages = Math.max(0, (total - 1) / controller.getPageSize());
        pageLabel.setText(String.format("Page %d / %d", currentPage + 1, pages + 1));
        
        // Refresh UI
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    /**
     * Get cart button text with icon and item count
     */
    private String getCartButtonText() {
        return String.format(ICON_CART + " (%d)", cart.getTotalQuantity());
    }
    
    /**
     * Open cart screen
     */
    private void openCart() {
        CartScreen cartScreen = new CartScreen(cart, paymentController, this);
        cartScreen.showScreen();
    }
}
