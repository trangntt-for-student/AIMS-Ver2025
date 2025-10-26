package com.hust.soict.aims;

import com.hust.soict.aims.boundaries.MainWindow;
import com.hust.soict.aims.controls.PayByCreditCardController;
import com.hust.soict.aims.controls.ProductController;
import com.hust.soict.aims.controls.CartController;
import javax.swing.*;

public class App {
	public static void main(String[] args) {
		// start embedded Spring Boot for PayPal callbacks and get controllers
		var ctx = EmbeddedTomcat.startAndGetContext(new String[]{});
		var paymentController = ctx.getBean(PayByCreditCardController.class);

		SwingUtilities.invokeLater(() -> {
			ProductController productController = new ProductController();
			CartController cartController = new CartController();

			MainWindow w = new MainWindow(productController, cartController, paymentController);
			w.setVisible(true);
		});
	}
}
