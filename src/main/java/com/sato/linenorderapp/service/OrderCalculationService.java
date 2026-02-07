package com.sato.linenorderapp.service;

import org.springframework.stereotype.Service;

@Service
public class OrderCalculationService {

	public int calculateOrderQuantity(
			int currentStock,
			int nextDelivery,
			int baseStock) {

		// X = 在庫数 + 次回納品数 - 定数
		int x = currentStock + nextDelivery - baseStock;

		// Y = (3 / 5 * 定数) - X
		int y = (baseStock * 3 / 5) - x;

		// マイナスは発注しない
		if (y <= 0) {
			return 0;
		}

		// 発注数 = min(Y * 2, 定数)
		return Math.min(y * 2, baseStock);
	}
}