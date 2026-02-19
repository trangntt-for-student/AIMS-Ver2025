package com.hust.soict.aims.subsystems.paypal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import com.hust.soict.aims.utils.MoneyConstant;

final class CurrencyConverter {
	private static final BigDecimal VND_TO_USD_RATE = new BigDecimal(MoneyConstant.VND_TO_USD_RATE);

	static BigDecimal vndToUsd(BigDecimal vnd) {
		return vnd.multiply(VND_TO_USD_RATE).setScale(2, RoundingMode.HALF_UP);
	}
}
