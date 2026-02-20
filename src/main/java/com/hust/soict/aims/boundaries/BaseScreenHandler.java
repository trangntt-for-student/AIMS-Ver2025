package com.hust.soict.aims.boundaries;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import static com.hust.soict.aims.utils.UIConstant.*;

public abstract class BaseScreenHandler extends JFrame {
    protected String screenTitle;
    protected BaseScreenHandler parentScreen;
    
    // Navigation buttons
    private JButton backButton;
    private JButton forwardButton;
    private JPanel navigationPanel;
    private boolean navigationEnabled = true;
     
    public BaseScreenHandler(String title) {
        this(title, null);
    }
    
    public BaseScreenHandler(String title, BaseScreenHandler parent) {
        this(title, parent, true);
    }
    
    protected BaseScreenHandler(String title, BaseScreenHandler parent, boolean autoInitialize) {
        this.screenTitle = title;
        this.parentScreen = parent;
        
        if (autoInitialize) {
            initializeScreen();
        }
    }
    
    protected void initializeScreen() {
        setTitle(screenTitle);
        setSize(DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Set taskbar icon
        URL iconURL = getClass().getResource("/icon.png");
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        }
        
        // Lifecycle hook: before initialization
        onBeforeInitialize();
        
        // Initialize navigation buttons first
        if (navigationEnabled) {
            initNavigationComponents();
        }
        
        // Template methods for subclass to implement
        initComponents();
        setupLayout();
        bindEvents();
        
        // Setup navigation listener
        if (navigationEnabled) {
            setupNavigationListener();
        }
        
        // Center the screen
        setLocationRelativeTo(null);
        
        // Lifecycle hook: after initialization
        onAfterInitialize();
    }
    
    // ====================== Navigation Components ======================
    
    private void initNavigationComponents() {
        backButton = new JButton("← Back");
        backButton.setFont(FONT_BUTTON);
        backButton.setBackground(BACKGROUND_GRAY);
        backButton.setFocusPainted(false);
        backButton.setCursor(CURSOR_HAND);
        backButton.setEnabled(false);
        
        forwardButton = new JButton("Forward →");
        forwardButton.setFont(FONT_BUTTON);
        forwardButton.setBackground(BACKGROUND_GRAY);
        forwardButton.setFocusPainted(false);
        forwardButton.setCursor(CURSOR_HAND);
        forwardButton.setEnabled(false);
        
        // Bind navigation events
        backButton.addActionListener(e -> navigateBack());
        forwardButton.addActionListener(e -> navigateForward());
        
        // Create navigation panel
        navigationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_SMALL, 0));
        navigationPanel.setOpaque(false);
        navigationPanel.add(backButton);
        navigationPanel.add(forwardButton);
    }
    
    private void setupNavigationListener() {
        ScreenNavigator.getInstance().addNavigationListener((currentScreen, canGoBack, canGoForward) -> {
            // Only update if this is the current screen
            if (currentScreen == this) {
                SwingUtilities.invokeLater(() -> {
                    backButton.setEnabled(canGoBack);
                    forwardButton.setEnabled(canGoForward);
                });
            }
        });
    }
    
    protected JPanel getNavigationPanel() {
        return navigationPanel;
    }
    
    protected JPanel createTopNavigationBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BACKGROUND_GRAY);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT),
            PADDING_XSMALL
        ));
        topBar.setPreferredSize(new Dimension(0, TOP_NAV_HEIGHT));
        
        topBar.add(navigationPanel, BorderLayout.WEST);
        
        return topBar;
    }
    
    protected JPanel createHeaderWithNavigation(JPanel mainHeaderContent) {
        JPanel headerWrapper = new JPanel(new BorderLayout());
        
        // Top: Navigation bar
        headerWrapper.add(createTopNavigationBar(), BorderLayout.NORTH);
        
        // Bottom: Main header content
        headerWrapper.add(mainHeaderContent, BorderLayout.CENTER);
        
        return headerWrapper;
    }
    
    protected void setNavigationEnabled(boolean enabled) {
        this.navigationEnabled = enabled;
    }
    
    protected boolean isNavigationEnabled() {
        return navigationEnabled;
    }
    
    protected void navigateTo(BaseScreenHandler screen) {
        ScreenNavigator.getInstance().navigateTo(screen);
    }
    
    protected boolean navigateBack() {
        return ScreenNavigator.getInstance().goBack();
    }
    
    protected boolean navigateForward() {
        return ScreenNavigator.getInstance().goForward();
    }
    
    protected boolean canNavigateBack() {
        return ScreenNavigator.getInstance().canGoBack();
    }
    
    protected boolean canNavigateForward() {
        return ScreenNavigator.getInstance().canGoForward();
    }
    
    protected ScreenNavigator getNavigator() {
        return ScreenNavigator.getInstance();
    }
    
    // ====================== Template Methods For SubClass ======================
    
    protected abstract void initComponents();
    
    protected abstract void setupLayout();
    
    protected void bindEvents() {}
    
    // ====================== Show & Hide Screen ======================
    
    public void refresh() {
        revalidate();
        repaint();
    }
    
    public void showScreen() {
        onBeforeShow();
        setVisible(true);
        onAfterShow();
    }
    
    public void hideScreen() {
        setVisible(false);
    }
    
    public void closeAndReturnToParent() {
        onBeforeClose();
        dispose();
        if (parentScreen != null) {
            parentScreen.showScreen();
        }
    }

    // ====================== Life Cycle Hooks ======================
    
    protected void onBeforeInitialize() {}
    
    protected void onAfterInitialize() {}
    
    protected void onBeforeShow() {}
    
    protected void onAfterShow() {}
    
    protected void onBeforeClose() {}
    
    // ====================== Getter & Setter ======================
    
    public String getScreenTitle() {
        return screenTitle;
    }
    
    public void setScreenTitle(String screenTitle) {
        this.screenTitle = screenTitle;
        setTitle(screenTitle);
    }
    
    public BaseScreenHandler getParentScreen() {
        return parentScreen;
    }
    
    public void setParentScreen(BaseScreenHandler parentScreen) {
        this.parentScreen = parentScreen;
    }
}
