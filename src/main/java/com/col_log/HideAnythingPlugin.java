package com.col_log;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.Text;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@PluginDescriptor(
		name = "Hide Anything",
		description = "Adds a menu option to hide players and NPCs",
		tags = {"hide", "player", "npc"}
)
public class HideAnythingPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private HideAnythingConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private HideAnythingOverlay hideAnythingOverlay;

	private final Set<String> hiddenPlayers = new HashSet<>();
	private final Set<Integer> hiddenNPCs = new HashSet<>();

	@Provides
	HideAnythingConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HideAnythingConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		log.info("Hide Anything started!");
		overlayManager.add(hideAnythingOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Hide Anything stopped!");
		hiddenPlayers.clear();
		hiddenNPCs.clear();
		overlayManager.remove(hideAnythingOverlay);
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (!config.showHideOption())
		{
			return;
		}

		String option = Text.removeTags(event.getMenuOption()).toLowerCase();
		String target = Text.removeTags(event.getMenuTarget());

		if (option.equals("hide"))
		{
			clientThread.invoke(() -> {
				if (event.getMenuTarget().contains("<col=ffff00>"))
				{
					// It's a player
					if (config.hidePlayers())
					{
						hiddenPlayers.add(target);
					}
				}
				else
				{
					// It's an NPC
					if (config.hideNPCs())
					{
						int npcIndex = event.getId();
						hiddenNPCs.add(npcIndex);
					}
				}
			});
		}
	}

	private class HideAnythingOverlay extends Overlay
	{
		@Inject
		private HideAnythingOverlay()
		{
			setPosition(OverlayPosition.DYNAMIC);
		}

		@Override
		public Dimension render(Graphics2D graphics)
		{
			if (client.getGameState() == null)
			{
				return null;
			}

			// Hide Players
			for (Player player : client.getPlayers())
			{
				if (player != null && hiddenPlayers.contains(player.getName()))
				{
					graphics.setComposite(AlphaComposite.SrcOver.derive(0.0f));
					graphics.fill(player.getConvexHull());
				}
			}

			// Hide NPCs
			for (NPC npc : client.getNpcs())
			{
				if (npc != null && hiddenNPCs.contains(npc.getIndex()))
				{
					graphics.setComposite(AlphaComposite.SrcOver.derive(0.0f));
					graphics.fill(npc.getConvexHull());
				}
			}

			return null;
		}
	}
}
