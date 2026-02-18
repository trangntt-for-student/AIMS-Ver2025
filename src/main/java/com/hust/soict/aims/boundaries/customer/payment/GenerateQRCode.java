package com.hust.soict.aims.boundaries.customer.payment;

import static com.hust.soict.aims.utils.UIConstant.FONT_FAMILY;

import java.awt.Font;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

public class GenerateQRCode {
    private JLabel qrImageLabel;
    
    public GenerateQRCode(JLabel qrImageLabel) {
    	this.qrImageLabel = qrImageLabel;
    }
    
	public void generateQRImageFromURL(String imageUrl) {
		SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
			@Override
			protected ImageIcon doInBackground() throws Exception {
				try {
					// Try to read image directly
					java.net.URI uri = new java.net.URI(imageUrl);
					Image image = javax.imageio.ImageIO.read(uri.toURL());

					if (image != null) {
						// Scale to 300x300
						Image scaledImage = image.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
						return new ImageIcon(scaledImage);
					}
				} catch (Exception e) {
					throw e;
				}

				return null;
			}

			@Override
			protected void done() {
				try {
					ImageIcon icon = get();
					if (icon != null) {
						qrImageLabel.setIcon(icon);
						qrImageLabel.setText(""); // Clear text
						System.out.println("[PaymentScreen] ✅ QR image loaded successfully");
					} else {
						// Fallback: Show URL as clickable link
						System.err.println("[PaymentScreen] Cannot load image, showing link instead");
						qrImageLabel.setText("<html><center>Cannot display QR image<br/><br/>" + "<a href='" + imageUrl
								+ "'>Click to view QR Code</a></center></html>");
						qrImageLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
					}
				} catch (Exception e) {
					System.err.println("[PaymentScreen] ❌ Failed to load QR from URL: " + e.getMessage());
					e.printStackTrace();
					qrImageLabel.setText("<html><center>QR Code available at:<br/>" + "<a href='" + imageUrl + "'>"
							+ imageUrl + "</a></center></html>");
					qrImageLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
				}
			}
		};
		worker.execute();
	}

	public void generateImageFromBase64(String base64Image) {
		try {
			System.out.println("[PaymentScreen] Loading QR from Base64");

			// Remove data URI prefix if present
			if (base64Image.contains(",")) {
				base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
			}

			// Decode Base64 to image
			byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Image);
			ImageIcon qrIcon = new ImageIcon(imageBytes);

			// Scale image to fit (300x300)
			Image scaledImage = qrIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
			qrImageLabel.setIcon(new ImageIcon(scaledImage));
			qrImageLabel.setText(""); // Clear text

			System.out.println("[PaymentScreen] ✅ QR image loaded from Base64");

		} catch (Exception e) {
			System.err.println("[PaymentScreen] ❌ Failed to decode Base64 QR: " + e.getMessage());
			e.printStackTrace();
			qrImageLabel.setText("[ QR CODE ]");
			qrImageLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 32));
		}
	}

	public void generateQRImageFromString(String qrCodeString) {
		SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
			@Override
			protected ImageIcon doInBackground() throws Exception {
				System.out.println("[PaymentScreen] Generating QR from string (length=" + qrCodeString.length() + ")");

				// Use ZXing to generate QR code
				com.google.zxing.qrcode.QRCodeWriter qrCodeWriter = new com.google.zxing.qrcode.QRCodeWriter();
				com.google.zxing.common.BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeString,
						com.google.zxing.BarcodeFormat.QR_CODE, 300, 300);

				// Convert BitMatrix to BufferedImage
				int width = bitMatrix.getWidth();
				int height = bitMatrix.getHeight();
				java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(width, height,
						java.awt.image.BufferedImage.TYPE_INT_RGB);

				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
					}
				}

				return new ImageIcon(image);
			}

			@Override
			protected void done() {
				try {
					ImageIcon icon = get();
					if (icon != null) {
						qrImageLabel.setIcon(icon);
						qrImageLabel.setText(""); // Clear text
						System.out.println("[PaymentScreen] ✅ QR code generated successfully");
					} else {
						throw new Exception("Failed to generate QR code");
					}
				} catch (Exception e) {
					System.err.println("[PaymentScreen] ❌ Failed to generate QR: " + e.getMessage());
					e.printStackTrace();
					qrImageLabel.setText("[ QR CODE ]");
					qrImageLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 32));
				}
			}
		};
		worker.execute();
	}
}
