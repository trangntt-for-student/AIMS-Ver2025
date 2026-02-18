package com.hust.soict.aims.subsystems.paypal;

import java.math.BigDecimal;
import java.math.RoundingMode;

final class CurrencyConverter {
	private static final BigDecimal VND_TO_USD_RATE = new BigDecimal("0.000041");

	static BigDecimal vndToUsd(BigDecimal vnd) {
		return vnd.multiply(VND_TO_USD_RATE).setScale(2, RoundingMode.HALF_UP);
	}
}
