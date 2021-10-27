package net.runelite.client.plugins.closebank;

import java.util.List;

public class BankItem {

	static final class Item {
		Integer id;
		Integer count;

		public Item(Integer id, Integer count) {
			this.id = id;
			this.count = count;
		}
	}

	List<Item> items;

	public BankItem(List<Item> items) {
		this.items = items;
	}
}
