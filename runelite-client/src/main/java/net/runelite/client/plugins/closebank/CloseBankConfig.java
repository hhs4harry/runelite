package net.runelite.client.plugins.closebank;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("closebank")
public interface CloseBankConfig extends Config {

	@ConfigItem(
			keyName = "customBank",
			name = "Bank",
			description = "Format is as follows: itemid:count&itemid:count use & to denote multiple items" +
					"<br>For example, 1987:14&1937:14. Separated by a new line. Use // do disable an option."
	)
	default String customBank()
	{
		return "";
	}
}