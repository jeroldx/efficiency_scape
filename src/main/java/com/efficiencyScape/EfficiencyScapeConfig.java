package com.efficiencyScape;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface EfficiencyScapeConfig extends Config
{
	@ConfigItem(
		keyName = "debug",
		name = "Testing",
		description = "For testing only"
	)

	default boolean debug()
	{
		return true;
	}

	@ConfigItem(
		keyName = "dialogTracking",
		name = "Dialog Tracking",
		description = "For testing.",
		secret = true,
		section = "Tracking debug options"
	)
	default boolean dialogTracking()
	{
		return true;
	}

	@ConfigItem(
		keyName = "combatTracking",
		name = "Combat Tracking",
		description = "For testing.",
		secret = true,
		section = "Tracking debug options"
	)
	default boolean combatTracking()
	{
		return true;
	}

	@ConfigItem(
		keyName = "experienceTracking",
		name = "Experience Tracking",
		description = "For testing.",
		secret = true,
		section = "Tracking debug options"
	)
	default boolean experienceTracking()
	{
		return true;
	}

	@ConfigItem(
		keyName = "interactionTracking",
		name = "Interaction Tracking",
		description = "For testing.",
		secret = true,
		section = "Tracking debug options"
	)
	default boolean interactionTracking()
	{
		return true;
	}

	@ConfigItem(
		keyName = "inventoryTracking",
		name = "Inventory Tracking",
		description = "For testing.",
		secret = true,
		section = "Tracking debug options"
	)
	default boolean inventoryTracking()
	{
		return true;
	}

	@ConfigItem(
		keyName = "locationTracking",
		name = "Location Tracking",
		description = "For testing.",
		secret = true,
		section = "Tracking debug options"
	)
	default boolean locationTracking()
	{
		return true;
	}

	@ConfigItem(
		keyName = "eventSubmitURI",
		name = "Submit Event Location",
		description = "The URI of where to send activity events.",
		warning = "Changing this will stop activity from being recorded properly.",
		secret = true
	)

	default String eventSubmitURI()
	{
		return "http://localhost:8080";
	}
}
