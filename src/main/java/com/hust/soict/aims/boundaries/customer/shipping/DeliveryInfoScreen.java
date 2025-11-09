package com.hust.soict.aims.boundaries.customer.shipping;

import javax.swing.*;
import java.awt.*;
import com.hust.soict.aims.entities.DeliveryInfo;

public class DeliveryInfoScreen extends JDialog {
    private DeliveryInfo info;

    public DeliveryInfoScreen(Frame owner) {
        super(owner, "Delivery Information", true);
        setSize(400, 350);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(5,2));
        JTextField name = new JTextField();
        JTextField phone = new JTextField();
        JTextField city = new JTextField();
        JTextField district = new JTextField();
        JTextField address = new JTextField();

        form.add(new JLabel("Receiver name:")); form.add(name);
        form.add(new JLabel("Phone:")); form.add(phone);
        form.add(new JLabel("City:")); form.add(city);
        form.add(new JLabel("District:")); form.add(district);
        form.add(new JLabel("Address line:")); form.add(address);

        add(form, BorderLayout.CENTER);
        JPanel bottom = new JPanel();
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        bottom.add(ok); bottom.add(cancel);
        add(bottom, BorderLayout.SOUTH);

        ok.addActionListener(e -> {
            if (name.getText().trim().isEmpty() || phone.getText().trim().isEmpty() || city.getText().trim().isEmpty() || address.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill required fields");
                return;
            }
            // basic phone validation
            String ph = phone.getText().trim();
            if (!ph.matches("[0-9+\\- ]{7,15}")) {
                JOptionPane.showMessageDialog(this, "Invalid phone number");
                return;
            }

            info = new DeliveryInfo();
            info.setReceiverName(name.getText().trim());
            info.setPhone(ph);
            info.setCity(city.getText().trim());
            info.setDistrict(district.getText().trim());
            info.setAddressLine(address.getText().trim());
            setVisible(false);
        });

        cancel.addActionListener(e -> { info = null; setVisible(false); });
        setLocationRelativeTo(owner);
    }

    public DeliveryInfo getDeliveryInfo() { return info; }
}
