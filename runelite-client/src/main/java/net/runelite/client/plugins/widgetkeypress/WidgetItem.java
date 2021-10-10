package net.runelite.client.plugins.widgetkeypress;

import java.util.Locale;

public class WidgetItem {

	enum Key {
		SPACE("space"),
		ONE("1"),
		TWO("2"),
		THREE("3"),
		FOUR("4"),
		FIVE("5"),
		SIX("6"),
		SEVEN("7"),
		EIGHT("8"),
		NINE("9");

		private final String name;

		Key(String name) {
			this.name = name;
		}
	}

	public String name;
	public Key key;

	public WidgetItem(String name, Key key) {
		this.name = name;
		this.key = key;
	}
}
