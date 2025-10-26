package com.hust.soict.aims.boundaries;

import com.hust.soict.aims.controls.ProductController;
import com.hust.soict.aims.controls.PayByCreditCardController;
import com.hust.soict.aims.entities.Product;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainWindow extends JFrame {
    private final ProductController controller;
    private final com.hust.soict.aims.controls.CartController cart;
    private final PayByCreditCardController paymentController;
    private int currentPage = 0;
    private JLabel pageLabel;
    private JPanel gridPanel;
    public MainWindow(ProductController controller, com.hust.soict.aims.controls.CartController cart,
                   PayByCreditCardController paymentController) {
        super("AIMS - Products");
        this.controller = controller;
        this.cart = cart;
        this.paymentController = paymentController;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLayout(new BorderLayout());

        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(5, 4, 10, 10));
        add(new JScrollPane(gridPanel), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        JPanel nav = new JPanel();
        JButton prev = new JButton("Prev");
        JButton next = new JButton("Next");
        pageLabel = new JLabel();
        nav.add(prev); nav.add(pageLabel); nav.add(next);
        bottom.add(nav, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        prev.addActionListener(e -> { if (currentPage>0) { currentPage--; refresh(); } });
        next.addActionListener(e -> { int total = controller.countProducts(); int pages = (total-1)/controller.getPageSize(); if (currentPage<pages) { currentPage++; refresh(); } });

        refresh();
        setLocationRelativeTo(null);

        // create menu bar with cart
        JMenuBar mb = new JMenuBar();
        mb.add(Box.createHorizontalGlue());
        JButton cartBtn = new JButton("\uD83D\uDED2 (" + cart.getTotalQuantity() + ")");
        cartBtn.addActionListener(e -> {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cart is empty", "Cart", JOptionPane.INFORMATION_MESSAGE);
            } else {
                openCart();
            }
        });
        mb.add(cartBtn);
        setJMenuBar(mb);

        // subscribe to cart changes to update count
        cart.setChangeListener(count -> SwingUtilities.invokeLater(() -> cartBtn.setText(String.format("\uD83D\uDED2 (%d)", count))));
    }

    private void refresh() {
        gridPanel.removeAll();
        List<Product> products = controller.getPage(currentPage);
        for (Product p: products) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            JLabel title = new JLabel(p.getTitle());
            title.setFont(title.getFont().deriveFont(Font.BOLD));
            card.add(title, BorderLayout.NORTH);

            JTextArea ta = new JTextArea();
            ta.setEditable(false);
            ta.setText(String.format("Price: %.2f\nWeight: %.2f\n", p.getCurrentPrice(), p.getWeight()));
            card.add(ta, BorderLayout.CENTER);

            JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton info = new JButton("i");
            info.addActionListener(e -> {
                ProductDetailScreen dlg = new ProductDetailScreen(this, p);
                dlg.setVisible(true);
            });
            JButton add = new JButton("Add");
            add.addActionListener(e -> {
                cart.addProduct(p, 1);
                JOptionPane.showMessageDialog(this, "Added to cart: " + p.getTitle());
            });
            bottomRow.add(add);
            bottomRow.add(info);
            card.add(bottomRow, BorderLayout.SOUTH);

            gridPanel.add(card);
        }

        int total = controller.countProducts();
        int pages = Math.max(0, (total-1)/controller.getPageSize());
        pageLabel.setText(String.format("Page %d / %d", currentPage+1, pages+1));
        // cart button is updated via change listener
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    // open cart
    public void openCart() {
        CartScreen cs = new CartScreen(cart, this.paymentController);
        cs.setVisible(true);
    }
}
