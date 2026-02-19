package com.hust.soict.aims.boundaries;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import com.hust.soict.aims.boundaries.customer.homepage.Homepage;
import com.hust.soict.aims.controls.CartController;
import com.hust.soict.aims.controls.ProductController;

import static com.hust.soict.aims.utils.UIConstant.*;

public class LandingPage extends JFrame {
    
    public LandingPage() {
        setTitle("AIMS - An Internet Media Store");
        setSize(DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel with light background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // White to light gray gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, BACKGROUND_WHITE,
                    0, getHeight(), BACKGROUND_LIGHT
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Decorative blue circles
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                g2d.setColor(PRIMARY_COLOR);
                g2d.fillOval(-150, -150, 400, 400);
                g2d.fillOval(getWidth() - 200, getHeight() - 250, 450, 450);
                
                g2d.dispose();
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Header label
        JLabel headerLabel = new JLabel("MEDIA STORE");
        headerLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_SMALL));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title
        JLabel titleLabel = new JLabel("AIMS");
        titleLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 72));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("An Internet Media Store");
        subtitleLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE_TITLE));
        subtitleLabel.setForeground(TEXT_PRIMARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Tagline
        JLabel taglineLabel = new JLabel("Books • CDs • DVDs • Newspapers");
        taglineLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE_BODY));
        taglineLabel.setForeground(TEXT_SECONDARY);
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, SPACING_XLARGE, 0));
        
        // Customer button - Success theme (green)
        JButton customerBtn = createStyledButton(
            "Browse as Customer", 
            SUCCESS_COLOR, 
            SUCCESS_DARK,
            260, 50
        );
        customerBtn.addActionListener(e -> openCustomerHomepage());
        
        // Admin button - Primary theme (blue)
        JButton adminBtn = createStyledButton(
            "Admin / Manager Login", 
            PRIMARY_COLOR, 
            PRIMARY_DARK,
            260, 50
        );
        adminBtn.addActionListener(e -> showLoginNotImplemented());
        
        buttonsPanel.add(customerBtn);
        buttonsPanel.add(adminBtn);
        
        // Footer
        JLabel footerLabel = new JLabel("© 2025 AIMS - SOICT HUST");
        footerLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE_CAPTION));
        footerLabel.setForeground(TEXT_SECONDARY);
        footerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Assemble content
        contentPanel.add(headerLabel);
        contentPanel.add(Box.createVerticalStrut(SPACING_MEDIUM));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        contentPanel.add(taglineLabel);
        contentPanel.add(Box.createVerticalStrut(50));
        contentPanel.add(buttonsPanel);
        contentPanel.add(Box.createVerticalStrut(60));
        contentPanel.add(footerLabel);
        
        mainPanel.add(contentPanel);
        setContentPane(mainPanel);
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color hoverColor, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Draw rounded background
                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 25, 25));
                
                // Draw text with correct font
                g2d.setFont(getFont());
                g2d.setColor(getForeground());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), textX, textY);
                
                g2d.dispose();
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                // No border
            }
        };
        
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setBackground(bgColor);
        button.setForeground(TEXT_ON_PRIMARY);
        button.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_BUTTON_LARGE));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(CURSOR_HAND);
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void openCustomerHomepage() {
        // Create controllers
        ProductController productController = new ProductController();
        CartController cartController = new CartController();
        
        // Create and navigate to Homepage
        Homepage homepage = new Homepage(productController, cartController);
        ScreenNavigator.getInstance().navigateTo(homepage);
        
        // Close landing page
        this.dispose();
    }
    
    private void showLoginNotImplemented() {
        JOptionPane.showMessageDialog(
            this,
            "Login feature will be implemented in the future.\nPlease check back later!",
            "Coming Soon",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}
