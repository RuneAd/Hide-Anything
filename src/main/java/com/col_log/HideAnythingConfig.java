package com.col_log;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("HideAnything")
public interface HideAnythingConfig extends Config
{
	@ConfigItem(
			keyName = "showHideOption",
			name = "Show Hide Option",
			description = "Toggle to show or hide the right-click hide option"
	)
	default boolean showHideOption()
	{
		return true;
	}

	@ConfigItem(
			keyName = "resetHidden",
			name = "Reset Hidden Players",
			description = "Button to reset all hidden players"
	)
	default boolean resetHidden()
	{
		return false;
	}

	@ConfigItem(
			keyName = "hideNPCs",
			name = "Hide NPCs",
			description = "Toggle to allow hiding NPCs"
	)
	default boolean hideNPCs()
	{
		return true;
	}

	@ConfigItem(
			keyName = "hidePlayers",
			name = "Hide Players",
			description = "Toggle to allow hiding players"
	)
	default boolean hidePlayers()
	{
		return true;
	}
}
