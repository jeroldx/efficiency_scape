package com.efficiencyScape.DataTracking;

import com.efficiencyScape.DataTracking.ActivityEvent.ActivityEventType;

public abstract class ActivityTracker
{
	protected final TrackerManager trackerManager;
	protected final ActivityEventType.Type eventType;

	public ActivityTracker(TrackerManager trackerManager, ActivityEventType.Type eventType)
	{
		this.trackerManager = trackerManager;
		this.trackerManager.assignTracker(this);
		this.eventType = eventType;
	}
}
