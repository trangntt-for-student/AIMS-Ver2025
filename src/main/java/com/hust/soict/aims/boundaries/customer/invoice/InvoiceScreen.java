package com.hust.soict.aims.boundaries.customer.invoice;

import javax.swing.*;
import java.awt.*;
import com.hust.soict.aims.controls.PayByCreditCardController;
import com.hust.soict.aims.entities.Invoice;

public class InvoiceScreen extends JDialog {
    private boolean paid = false;

    public InvoiceScreen(Frame owner, Invoice invoice, PayByCreditCardController paymentController) {
        super(owner, "Invoice", true);
        setSize(500, 500);
        setLayout(new BorderLayout());

        JTextArea area = new JTextArea();
        area.setEditable(false);

        StringBuilder sb = new StringBuilder();
        sb.append("Invoice\n");
        sb.append("Subtotal: ").append(String.format("%.2f VND\n", invoice.getSubtotal()));
        sb.append("Shipping: ").append(String.format("%.2f VND\n", invoice.getShippingFee()));
        sb.append("Total: ").append(String.format("%.2f VND\n", invoice.getTotal()));
        double totalUSD = Math.ceil(invoice.getTotal() / 25000.0 * 100) / 100;
        sb.append("Total (USD): ").append(String.format("%.2f USD\n", totalUSD));
        sb.append("\nItems:\n");
        invoice.getOrder().getItems().forEach(it -> sb.append(String.format("- %s x%d => %.2f\n", it.getProduct().getTitle(), it.getQuantity(), it.getTotalPrice())));
        area.setText(sb.toString());
        add(new JScrollPane(area), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton pay = new JButton("Pay");
        bottom.add(pay);
        add(bottom, BorderLayout.SOUTH);

        pay.addActionListener(e -> {
            JDialog d = new JDialog(this, "QR Payment", true);
            d.setSize(350, 300);
            d.setLayout(new BorderLayout());
            JLabel qrl = new JLabel("[ QR here ]", SwingConstants.CENTER);
            qrl.setFont(qrl.getFont().deriveFont(Font.BOLD, 24f));
            d.add(qrl, BorderLayout.CENTER);

            JPanel btns = new JPanel();
            JButton paidByQR = new JButton("I have paid by QR");
            JButton payByCard = new JButton("Pay by Credit Card");
            btns.add(paidByQR);
            btns.add(payByCard);
            d.add(btns, BorderLayout.SOUTH);

            paidByQR.addActionListener(ae -> { paid = true; d.setVisible(false); setVisible(false); });

            payByCard.addActionListener(ae -> {
                try {
                    boolean paymentSuccess = paymentController.executePaymentFlow(invoice.getTotal());
                    if (paymentSuccess) {
                        paid = true;
                        d.setVisible(false);
                        setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(d, "Payment not completed or timed out.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(d, "Error starting PayPal flow: " + ex.getMessage());
                }
            });

            d.setLocationRelativeTo(this);
            d.setVisible(true);
        });

        setLocationRelativeTo(owner);
    }

    public boolean isPaid() { return paid; }
}