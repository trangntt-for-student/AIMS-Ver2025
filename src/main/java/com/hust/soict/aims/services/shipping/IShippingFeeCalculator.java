package com.hust.soict.aims.services.shipping;

import com.hust.soict.aims.entities.Order;

public interface IShippingFeeCalculator {
    double calculateShippingFee(Order order);
}
