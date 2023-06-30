package com.efficiencyScape;

import com.efficiencyScape.DataTracking.ActivityEvent.ActivityEventManager;
import com.efficiencyScape.DataTracking.CombatTracking.CombatTracker;
import com.efficiencyScape.DataTracking.DialogTracking.InputKeyListener;
import com.efficiencyScape.Server.ActivityServerManager;
import com.efficiencyScape.DataTracking.DialogTracking.DialogTracker;
import com.efficiencyScape.DataTracking.ExperienceTracking.ExperienceTracker;
import com.efficiencyScape.DataTracking.GenericTracking.GenericsTracker;
import com.efficiencyScape.DataTracking.InteractionTracking.InteractionTracker;
import com.efficiencyScape.DataTracking.InventoryTracking.InventoryTracker;
import com.efficiencyScape.DataTracking.TrackerManager;
import com.efficiencyScape.DataTracking.ActivityEvent.FileManager;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.worldhopper.WorldHopperPlugin;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import okhttp3.HttpUrl;

@Slf4j
@PluginDescriptor(
	name = "EfficiencyScape"
)
@PluginDependency(WorldHopperPlugin.class)
public class EfficiencyScapePlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private EfficiencyScapeConfig config;
	@Inject
	private ClientToolbar clientToolbar;
	@Inject
	private ClientThread clientThread;
	@Inject
	private EfficiencyScapeConfig efficiencyScapeConfig;

	private NavigationButton navButton;
	private EfficiencyScapePanel escapePanel;

	// Managers
	@Inject
	private TrackerManager trackerManager;
	@Inject
	private FileManager fileManager;
	@Inject
	private ActivityEventManager activityEventManager;
	@Inject
	private ActivityServerManager activityServerManager;

	// Trackers
	@Inject
	private GenericsTracker genericsTracker;
	@Inject
	private ExperienceTracker experienceTracker;
	@Inject
	private InteractionTracker interactionTracker;
	@Inject
	private InventoryTracker inventoryTracker;
	@Inject
	private DialogTracker dialogTracker;
	@Inject
	private CombatTracker combatTracker;
	@Inject
	private InputKeyListener inputKeyListener;

	@Provides
	EfficiencyScapeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(EfficiencyScapeConfig.class);
	}

	@Override
	protected void startUp()
	{
		// Panel creation
		escapePanel = new EfficiencyScapePanel(this, config, client);
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/tab_icon.png");
		navButton = NavigationButton.builder()
			.tooltip("EScape")
			.icon(icon)
			.priority(99)
			.panel(escapePanel)
			.build();
		clientToolbar.addNavigation(navButton);

		fileManager.onStartUp();
		trackerManager.onStartUp();
		genericsTracker.onStartUp();
		// TODO Will need to add in some new servers
		activityEventManager.queueLocalSavedEvents();
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);
		trackerManager.onShutDown();
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		activityEventManager.processEvents();
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		// TODO Should be able to access the activity guide here somewhere/how,
		//  hopefully I can edit/add to it from here as well
	}
}
