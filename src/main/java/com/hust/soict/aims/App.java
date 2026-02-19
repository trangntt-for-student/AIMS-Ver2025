package com.hust.soict.aims;

import javax.swing.*;

import com.hust.soict.aims.utils.PaymentServiceProvider;
import com.hust.soict.aims.boundaries.LandingPage;

public class App {
	public static void main(String[] args) {
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
