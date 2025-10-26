package com.hust.soict.aims.boundaries;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import com.hust.soict.aims.controls.CartController;
import com.hust.soict.aims.controls.PayByCreditCardController;
import com.hust.soict.aims.controls.PlaceOrderController;
import com.hust.soict.aims.entities.CartItem;
import com.hust.soict.aims.entities.DeliveryInfo;


public class CartScreen extends JFrame {
    private final CartController cart;
    private final PlaceOrderController placeController;
    private JPanel itemsPanel;
    private JLabel subtotalLabel;

    public CartScreen(CartController cart, PayByCreditCardController paymentController) {
        super("Cart");
        this.cart = cart;
        this.placeController = new PlaceOrderController(cart);
        setSize(600, 600);
        setLayout(new BorderLayout());

        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(itemsPanel), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        subtotalLabel = new JLabel("Subtotal: 0.0");
        JButton place = new JButton("Place Order");
        bottom.add(subtotalLabel);
        bottom.add(place);
        add(bottom, BorderLayout.SOUTH);

        place.addActionListener(e -> {
            if (cart.isEmpty()) { JOptionPane.showMessageDialog(this, "Cart is empty"); return; }
            DeliveryInfoScreen dlg = new DeliveryInfoScreen(this);
            dlg.setVisible(true);
            DeliveryInfo info = dlg.getDeliveryInfo();
            if (info == null) return; // cancelled or invalid

            PlaceOrderController.PlaceOrderResult r = placeController.placeOrder(info);
            if (!r.success) {
                JOptionPane.showMessageDialog(this, r.message, "Order failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            InvoiceScreen invDlg = new InvoiceScreen(this, r.invoice, paymentController);
            invDlg.setVisible(true);
            if (invDlg.isPaid()) {
                JOptionPane.showMessageDialog(this, "Payment successful. Thank you!");
                cart.clear();
                refresh();
            }
        });

        refresh();
        setLocationRelativeTo(null);
    }

    public void refresh() {
        itemsPanel.removeAll();
        List<CartItem> items = cart.getItems();
        for (CartItem it : items) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.add(new JLabel(it.getProduct().getTitle()));
            row.add(new JLabel(String.format("Price: %.2f", it.getProduct().getCurrentPrice())));
            JSpinner sp = new JSpinner(new SpinnerNumberModel(it.getQuantity(), 1, 100, 1));
            sp.addChangeListener(e -> cart.updateQuantity(it.getProduct().getId(), (Integer) sp.getValue()));
            row.add(new JLabel("Qty:")); row.add(sp);
            JButton remove = new JButton("Remove");
            remove.addActionListener(e -> { cart.remove(it.getProduct().getId()); refresh(); });
            row.add(remove);
            itemsPanel.add(row);
        }
        subtotalLabel.setText(String.format("Subtotal: %.2f", cart.getSubtotal()));
        itemsPanel.revalidate(); itemsPanel.repaint();
    }
}
