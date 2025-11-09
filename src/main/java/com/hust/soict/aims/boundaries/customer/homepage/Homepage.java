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
    
    private JPanel gridPanel;
    private PaginationPanel paginationPanel;
    private ProductSearchPanel searchPanel;
    private JButton cartButton;
    
    // Current search term
    private String currentSearchTerm = "";
    
    public Homepage(ProductController controller, CartController cart,
                   PayByCreditCardController paymentController) {
        super("AIMS - Homepage", null, false);
        
        this.controller = controller;
        this.cart = cart;
        this.paymentController = paymentController;
        
        // Disable navigation for Homepage
        setNavigationEnabled(false);
        
        initializeScreen();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    @Override
    protected void initComponents() {
        // Initialize grid panel for products
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(PRODUCT_GRID_ROWS, PRODUCT_GRID_COLS, 
                                          PRODUCT_GRID_HGAP, PRODUCT_GRID_VGAP));
        gridPanel.setBackground(BACKGROUND_LIGHT);
        
        // Initialize search panel
        searchPanel = new ProductSearchPanel();
        
        // Initialize pagination panel
        paginationPanel = new PaginationPanel();
        
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
        
        // Top Panel (Header + Search)
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(PADDING_MEDIUM);
        headerPanel.setPreferredSize(new Dimension(0, HEADER_HEIGHT));
        
        JLabel titleLabel = new JLabel("AIMS - Product Store");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_ON_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(cartButton, BorderLayout.EAST);
        
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center - Product grid with scroll
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(PADDING_SMALL);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Footer - Pagination
        add(paginationPanel, BorderLayout.SOUTH);
    }
    
    @Override
    protected void bindEvents() {
        // Bind search events
        searchPanel.addSearchListener(searchTerm -> {
            currentSearchTerm = searchTerm;
            paginationPanel.reset();
            refresh();
        });
        
        // Bind pagination events
        paginationPanel.addPaginationListener(newPage -> {
            refresh();
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
        
        // Get current page from pagination panel
        int currentPage = paginationPanel.getCurrentPage();
        
        // Load products (search or all)
        List<Product> products;
        int total;
        
        if (currentSearchTerm.isEmpty()) {
            // Load all products
            products = controller.getPage(currentPage);
            total = controller.countProducts();
        } else {
            // Search products
            products = controller.searchProducts(currentSearchTerm, currentPage);
            total = controller.countSearchResults(currentSearchTerm);
        }
        
        // Show message if no results
        if (products.isEmpty()) {
            JLabel noResultsLabel = new JLabel("No products found");
            noResultsLabel.setFont(FONT_HEADER);
            noResultsLabel.setForeground(TEXT_SECONDARY);
            noResultsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gridPanel.setLayout(new BorderLayout());
            gridPanel.add(noResultsLabel, BorderLayout.CENTER);
        } else {
            // Restore grid layout if needed
            if (!(gridPanel.getLayout() instanceof GridLayout)) {
                gridPanel.setLayout(new GridLayout(PRODUCT_GRID_ROWS, PRODUCT_GRID_COLS, 
                                                  PRODUCT_GRID_HGAP, PRODUCT_GRID_VGAP));
            }
            
            // Create product cards using ProductCardPanel component
            for (Product product : products) {
                ProductCardPanel card = new ProductCardPanel(product, cart, this);
                
                // Set callback for info button
                card.setOnViewInfo(e -> {
                    ProductDetailScreen detailScreen = new ProductDetailScreen(this, product);
                    detailScreen.setVisible(true);
                });
                
                gridPanel.add(card);
            }
        }
        
        // Update pagination
        int totalPages = Math.max(1, (total + controller.getPageSize() - 1) / controller.getPageSize());
        paginationPanel.setCurrentPage(currentPage, totalPages);
        
        // Refresh UI
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    /**
     * Get cart button text with icon and item count
     */
    private String getCartButtonText() {
        return String.format("Cart (%d)", cart.getTotalQuantity());
    }
    
    /**
     * Open cart screen
     */
    private void openCart() {
        CartScreen cartScreen = new CartScreen(cart, paymentController, this);
        navigateTo(cartScreen);
    }
}
