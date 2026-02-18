package com.hust.soict.aims;

import javax.swing.*;

import com.hust.soict.aims.utils.PaymentServiceProvider;
import com.hust.soict.aims.controls.ProductController;
import com.hust.soict.aims.controls.CartController;
import com.hust.soict.aims.boundaries.customer.homepage.Homepage;
import com.hust.soict.aims.boundaries.ScreenNavigator;

public class App {
	public static void main(String[] args) {
		// Start embedded Spring Boot for PayPal callbacks
		EmbeddedTomcat.startAndGetContext(new String[]{});

		// Initialize ServiceProvider (creates all payment controllers internally)
		PaymentServiceProvider.getInstance().initialize();

		SwingUtilities.invokeLater(() -> {
			ProductController productController = new ProductController();
			CartController cartController = new CartController();

			Homepage homepage = new Homepage(productController, cartController);
		
			ScreenNavigator.getInstance().navigateTo(homepage);
		});
	}
}
