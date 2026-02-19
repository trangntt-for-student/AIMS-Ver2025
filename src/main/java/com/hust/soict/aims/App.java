package com.hust.soict.aims;

import javax.swing.*;

import com.hust.soict.aims.utils.PaymentServiceProvider;
import com.hust.soict.aims.boundaries.LandingPage;

public class App {
	public static void main(String[] args) {
		// Disable headless mode for Swing GUI
		System.setProperty("java.awt.headless", "false");
		
		// Start embedded Spring Boot for PayPal callbacks
		EmbeddedTomcat.startAndGetContext(new String[]{});

		// Initialize ServiceProvider (creates all payment controllers internally)
		PaymentServiceProvider.getInstance().initialize();

		SwingUtilities.invokeLater(() -> {
			LandingPage landingPage = new LandingPage();
			landingPage.setVisible(true);
		});
	}
}
