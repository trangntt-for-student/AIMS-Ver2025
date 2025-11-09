package com.hust.soict.aims.boundaries;

import javax.swing.*;
import static com.hust.soict.aims.utils.UIConstant.*;

public abstract class BaseScreenHandler extends JFrame {
    protected String screenTitle;
    protected BaseScreenHandler parentScreen;
     
    /**
     * Constructor with screen's title
     * @param title screen's title
     */
    public BaseScreenHandler(String title) {
        this(title, null);
    }
    
    /**
     * Constructor with screen's title and parent screen
     * @param title screen's title
     * @param parent parent screen
     */
    public BaseScreenHandler(String title, BaseScreenHandler parent) {
        this(title, parent, true);
    }
    
    /**
     * Constructor with screen's title, parent screen, and auto-initialize flag
     * This constructor allows subclass to delay initialization until after setting dependencies
     * 
     * @param title screen's title
     * @param parent parent screen
     * @param autoInitialize if true, automatically call initializeScreen(); if false, subclass must call it manually
     */
    protected BaseScreenHandler(String title, BaseScreenHandler parent, boolean autoInitialize) {
        this.screenTitle = title;
        this.parentScreen = parent;
        
        if (autoInitialize) {
            initializeScreen();
        }
    }
    
    /**
     * Initialize the basic settings for the screen
     * Automatically called in the constructor
     */
    protected void initializeScreen() {
        setTitle(screenTitle);
        setSize(DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Lifecycle hook: before initialization
        onBeforeInitialize();
        
        // Template methods for subclass to implement
        initComponents();
        setupLayout();
        bindEvents();
        
        // Center the screen on the screen
        setLocationRelativeTo(null);
        
        // Lifecycle hook: after initialization
        onAfterInitialize();
    }
    
    /**
     * Template method: Initialize the components of the screen
     * Subclass must override to create the specific components
     */
    protected abstract void initComponents();
    
    /**
     * Template method: Setup the layout for the screen
     * Subclass must override to setup the specific layout
     */
    protected abstract void setupLayout();
    
    /**
     * Template method: Bind events for the components
     * Subclass can override to bind events (optional)
     */
    protected void bindEvents() {
        // Default: do nothing, subclass can override if needed
    }
    
    /**
     * Refresh/update the interface of the screen
     * Subclass can override to implement the specific refresh logic
     */
    public void refresh() {
        revalidate();
        repaint();
    }
    
    /**
     * Show the screen with lifecycle hooks
     * Note: Do NOT override Component.show() as it causes infinite loop with setVisible()
     */
    public void showScreen() {
        onBeforeShow();
        setVisible(true);
        onAfterShow();
    }
    
    /**
     * Hide the screen
     * Note: Do NOT override Component.hide() as it causes infinite loop with setVisible()
     */
    public void hideScreen() {
        setVisible(false);
    }
    
    /**
     * Close the screen and return to the parent screen (if any)
     */
    public void closeAndReturnToParent() {
        onBeforeClose();
        dispose();
        if (parentScreen != null) {
            parentScreen.showScreen();
        }
    }
    
    /**
     * Navigate to another screen using ScreenNavigator
     * This will automatically manage the navigation history
     * @param screen The screen to navigate to
     */
    protected void navigateTo(BaseScreenHandler screen) {
        ScreenNavigator.getInstance().navigateTo(screen);
    }
    
    /**
     * Go back to previous screen in navigation history
     * @return true if successfully went back, false if no previous screen
     */
    protected boolean navigateBack() {
        return ScreenNavigator.getInstance().goBack();
    }
    
    /**
     * Go forward to next screen in navigation history
     * @return true if successfully went forward, false if no next screen
     */
    protected boolean navigateForward() {
        return ScreenNavigator.getInstance().goForward();
    }
    
    /**
     * Check if can navigate back
     * @return true if there's a previous screen
     */
    protected boolean canNavigateBack() {
        return ScreenNavigator.getInstance().canGoBack();
    }
    
    /**
     * Check if can navigate forward
     * @return true if there's a next screen
     */
    protected boolean canNavigateForward() {
        return ScreenNavigator.getInstance().canGoForward();
    }
    
    /**
     * Get the navigation instance for advanced usage
     * @return ScreenNavigator instance
     */
    protected ScreenNavigator getNavigator() {
        return ScreenNavigator.getInstance();
    }

    /**
     * Lifecycle hook: called before initializing the components
     * Subclass can override to implement the logic before initializing
     */
    protected void onBeforeInitialize() {}
    
    /**
     * Lifecycle hook: called after initializing the components finished
     * Subclass can override to implement the logic after initializing
     */
    protected void onAfterInitialize() {}
    
    /**
     * Lifecycle hook: called before showing the screen
     * Subclass can override to implement the logic before showing
     */
    protected void onBeforeShow() {}
    
    /**
     * Lifecycle hook: called after showing the screen
     * Subclass can override to implement the logic after showing
     */
    protected void onAfterShow() {}
    
    /**
     * Lifecycle hook: called before closing the screen
     * Subclass can override to implement the logic before closing
     */
    protected void onBeforeClose() {}
    
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
