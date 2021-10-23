package net.runelite.client.plugins.widgetkeypress;

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

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@PluginDescriptor(
		name = "Widget KeyPress",
		description = "Presses a key when a widget is shown.",
		tags = {"widget", "key", "press", "keyboard"},
		enabledByDefault = false
)
@PluginDependency(ExtUtils.class)
public class WidgetKeyPressPlugin extends Plugin {

	private final List<WidgetItem> items = new ArrayList<>();
	private static final Splitter NEWLINE_SPLITTER = Splitter
			.on("\n")
			.omitEmptyStrings()
			.trimResults();
	private Boolean closeBank = false;
	private Integer closeBankTick = 0;

	@Inject
	private WidgetKeyPressConfig config;

	@Inject
	private Client client;

	@Inject
	private ExtUtils extUtils;

	@Provides
	WidgetKeyPressConfig getConfig(ConfigManager configManager) {
		return configManager.getConfig(WidgetKeyPressConfig.class);
	}

	@Override
	protected void startUp() {
		updateMap(config.customSwaps());
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		var widget = client.getWidget(WidgetID.MULTISKILL_MENU_GROUP_ID, 14);
		var bank = client.getWidget(WidgetInfo.BANK_CONTAINER);

		if (widget != null && !items.isEmpty()) {
			var widgetName = widget.getName().toLowerCase();

			items.forEach(item -> {
				if (!widgetName.contains(item.name)) {
					return;
				}

				var canvas = client.getCanvas();

				if (canvas == null) {
					return;
				}

				var keyPressed = new KeyEvent(canvas, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, 54, KeyEvent.CHAR_UNDEFINED);
				KeyEvent keyTyped = null;
				var keyReleased = new KeyEvent(canvas, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, 54, KeyEvent.CHAR_UNDEFINED);

				switch (item.key) {
					case SPACE:
						try {
							Robot r = new Robot();
							r.keyPress(KeyEvent.VK_SPACE);
						} catch (AWTException e) {
							e.printStackTrace();
						}
						return;
					case ONE:
						keyTyped = new KeyEvent(canvas, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, (char)KeyEvent.VK_1);
						break;
					case TWO:
						keyTyped = new KeyEvent(canvas, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, (char)KeyEvent.VK_2);
						break;
					case THREE:
						keyTyped = new KeyEvent(canvas, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, (char)KeyEvent.VK_3);
						break;
					case FOUR:
						keyTyped = new KeyEvent(canvas, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, (char)KeyEvent.VK_4);
						break;
					case FIVE:
						keyTyped = new KeyEvent(canvas, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, (char)KeyEvent.VK_5);
						break;
					case SIX:
						keyTyped = new KeyEvent(canvas, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, (char)KeyEvent.VK_6);
						break;
					case SEVEN:
						keyTyped = new KeyEvent(canvas, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, (char)KeyEvent.VK_7);
						break;
					case EIGHT:
						keyTyped = new KeyEvent(canvas, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, (char)KeyEvent.VK_8);
						break;
					case NINE:
						keyTyped = new KeyEvent(canvas, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, (char)KeyEvent.VK_9);
						break;
				}

				canvas.dispatchEvent(keyPressed);
				canvas.dispatchEvent(keyTyped);
				canvas.dispatchEvent(keyReleased);
			});
		} else if (bank != null && !closeBank) {
			var inventory = client.getWidget(WidgetInfo.INVENTORY);
			var items = inventory.getWidgetItems();

			if (items.size() != 28) {
				return;
			}

			var bows = (int) items.stream().filter(item -> item.getId() == 1777).count();
			var bowStrings = (int) items.stream().filter(item -> item.getId() == 66).count();

			if (bows == 14 && bowStrings == 14) {
				try {
					Robot r = new Robot();
					r.keyPress(KeyEvent.VK_ESCAPE);
				} catch (AWTException e) {
					e.printStackTrace();
				}
			}
		} else if (closeBank) {
			if (closeBankTick != 1) {
				closeBankTick++;
			} else {

				// Press space

				closeBank = false;
				closeBankTick = 0;
			}
		}
	}

	public void updateMap(String swaps)
	{
		items.clear();
		final Iterable<String> tmp = NEWLINE_SPLITTER.split(swaps);

		for (String s : tmp) {
			if (s.startsWith("//")) {
				continue;
			}

			String[] split = s.split(":");

			try {
				items.add(new WidgetItem(split[0].toLowerCase(), WidgetItem.Key.valueOf(split[1].toUpperCase())));
			}
			catch (Exception e) {
				continue;
			}
		}
	}
}
