package com.efficiencyScape.DataTracking.ActivityEvent;

import com.google.gson.annotations.SerializedName;

/**
 * This class holds the info for activity event versions and types. The version numbers are broken down at the beginning
 * of the class, and any updates to specific events should be coupled with an update to the relevant version following
 * this convention:
 * 		Major - for any breaking change that will cause issues with event processing, such as removing or renaming a
 * 			field
 * 		Minor - for any extensive change that will allow for backwards compatibility, meaning fields in the event
 * 			remain the same but different logic is used to generate them, e.g. if the location event goes from once
 * 			per tick to once per 10 ticks, or if logic is put in to interaction tracking to not send events when
 * 			clicking on certain widgets
 * 		Patch - for minor bug fixes like typos
 * 	Which version to increment should be up to the maintainer, but general good practice is to lean towards incrementing
 * 	the higher level version.
 */
public class ActivityEventType
{
	private transient static final int EXPERIENCE_MAJOR_VERSION = 1;
	private transient static final int EXPERIENCE_MINOR_VERSION = 0;
	private transient static final int EXPERIENCE_PATCH_VERSION = 0;

	private transient static final int LOCATION_MAJOR_VERSION = 1;
	private transient static final int LOCATION_MINOR_VERSION = 0;
	private transient static final int LOCATION_PATCH_VERSION = 0;

	private transient static final int INTERACTION_MAJOR_VERSION = 1;
	private transient static final int INTERACTION_MINOR_VERSION = 0;
	private transient static final int INTERACTION_PATCH_VERSION = 0;

	private transient static final int INVENTORY_MAJOR_VERSION = 1;
	private transient static final int INVENTORY_MINOR_VERSION = 0;
	private transient static final int INVENTORY_PATCH_VERSION = 0;

	private transient static final int DIALOG_MAJOR_VERSION = 1;
	private transient static final int DIALOG_MINOR_VERSION = 0;
	private transient static final int DIALOG_PATCH_VERSION = 0;

	private transient static final int COMBAT_MAJOR_VERSION = 1;
	private transient static final int COMBAT_MINOR_VERSION = 0;
	private transient static final int COMBAT_PATCH_VERSION = 0;

	public final String eventVersion;
	public final Type type;

	private ActivityEventType(Type eventType)
	{
		this.type = eventType;
		this.eventVersion = getVersion(eventType);
	}

	public static ActivityEventType of(Type eventType)
	{
		return new ActivityEventType(eventType);
	}

	private static String getVersion(Type eventType)
	{
		switch(eventType)
		{
			case EXPERIENCE:
				return concatVersion(EXPERIENCE_MAJOR_VERSION, EXPERIENCE_MINOR_VERSION, EXPERIENCE_PATCH_VERSION);
			case LOCATION_CHANGED:
				return concatVersion(LOCATION_MAJOR_VERSION, LOCATION_MINOR_VERSION, LOCATION_PATCH_VERSION);
			case INTERACTION:
				return concatVersion(INTERACTION_MAJOR_VERSION, INTERACTION_MINOR_VERSION, INTERACTION_PATCH_VERSION);
			case INVENTORY_UPDATED:
				return concatVersion(INVENTORY_MAJOR_VERSION, INVENTORY_MINOR_VERSION, INVENTORY_PATCH_VERSION);
			case DIALOG:
				return concatVersion(DIALOG_MAJOR_VERSION, DIALOG_MINOR_VERSION, DIALOG_PATCH_VERSION);
			case COMBAT:
				return concatVersion(COMBAT_MAJOR_VERSION, COMBAT_MINOR_VERSION, COMBAT_PATCH_VERSION);
			case NONE:
			default:
				return null;
		}
	}

	private static String concatVersion(int major, int minor, int patch)
	{
		return major + "." + minor + "." + patch;
	}

	public enum Type
	{
		@SerializedName("xpGained")
		EXPERIENCE,
		@SerializedName("locationChanged")
		LOCATION_CHANGED,
		@SerializedName("interaction")
		INTERACTION,
		@SerializedName("inventoryUpdated")
		INVENTORY_UPDATED,
		@SerializedName("dialogChanged")
		DIALOG,
		@SerializedName("combat")
		COMBAT,
		NONE
	}
}
