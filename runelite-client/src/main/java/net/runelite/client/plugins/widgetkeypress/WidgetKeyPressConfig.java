package net.runelite.client.plugins.widgetkeypress;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("widgetkeypress")
public interface WidgetKeyPressConfig extends Config {

	@ConfigItem(
			keyName = "customSwaps",
			name = "Custom Swaps",
			description = "Format is as follows: OneClickThis:WithThis" +
					"<br>For example, air battlestaff:space. Separated by a new line. Use // do disable an option."
	)
	default String customSwaps()
	{
		return "";
	}
}
