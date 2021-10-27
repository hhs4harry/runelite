package net.runelite.client.plugins.closebank;

import com.google.common.base.Splitter;
import com.google.inject.Provides;
import com.sun.jna.platform.win32.WinDef;
import net.runelite.api.Client;
import net.runelite.api.KeyCode;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.externals.utils.ExtUtils;
import net.runelite.client.plugins.widgetkeypress.WidgetItem;
import net.runelite.client.plugins.widgetkeypress.WidgetKeyPressConfig;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@PluginDescriptor(
		name = "Close Bank",
		description = "Closes the bank when items are withdrawn",
		tags = {"close", "bank"},
		enabledByDefault = false
)
@PluginDependency(ExtUtils.class)
public class CloseBankPlugin extends Plugin {

	private final List<BankItem> items = new ArrayList<>();
	private static final Splitter NEWLINE_SPLITTER = Splitter
			.on("\n")
			.omitEmptyStrings()
			.trimResults();
	private static final Splitter AND_SPLITTER = Splitter.on("&").omitEmptyStrings().trimResults();

	@Inject
	private CloseBankConfig config;

	@Inject
	private Client client;

	@Inject
	private ExtUtils extUtils;

	@Provides
	CloseBankConfig getConfig(ConfigManager configManager) {
		return configManager.getConfig(CloseBankConfig.class);
	}

	@Override
	protected void startUp() {
		updateMap(config.customBank());
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		if (items.isEmpty()) {
			return;
		}

		var bank = client.getWidget(WidgetInfo.BANK_CONTAINER);

		if (bank == null) {
			return;
		}

		var inventory = client.getWidget(WidgetInfo.INVENTORY);
		var items = inventory.getWidgetItems();

		if (items.size() == 0) {
			return;
		}

		HashMap<Integer, Integer> inventoryItems = new HashMap<Integer, Integer>();

		items.forEach( item -> {
			var id = item.getId();
			var count = inventoryItems.getOrDefault(id, 0) + 1;

			inventoryItems.put(id, count);
		});

		var inventoryItemsCount = inventoryItems.values().stream().count();

		for (BankItem bankItem : this.items) {
			if (bankItem.items.stream().count() != inventoryItemsCount) {
				continue;
			}

			var matched = bankItem.items.stream().allMatch( x -> inventoryItems.get(x.id) == x.count);

			if (matched) {
				try {
					Robot r = new Robot();
					r.keyPress(KeyEvent.VK_ESCAPE);
				} catch (AWTException e) {
					e.printStackTrace();
				}

				return;
			}
		}
	}

	private void updateMap(String swaps)
	{
		items.clear();
		final Iterable<String> tmp = NEWLINE_SPLITTER.split(swaps);

		for (String s : tmp) {
			if (s.startsWith("//")) {
				continue;
			}

			List<BankItem.Item> items = new ArrayList<>();
			final Iterable<String> splitItems = AND_SPLITTER.split(s);

			for (String item : splitItems) {
				String[] split = item.split(":");

				if (split.length != 2) {
					continue;
				}

				try {
					items.add(new BankItem.Item(Integer.valueOf(split[0]), Integer.valueOf(split[1])));
				}
				catch (Exception e) {
					continue;
				}
			}

			if (!items.isEmpty()) {
				this.items.add(new BankItem(items));
			}
		}
	}
}