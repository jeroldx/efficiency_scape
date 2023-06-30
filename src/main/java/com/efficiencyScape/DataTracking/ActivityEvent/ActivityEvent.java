package com.efficiencyScape.DataTracking.ActivityEvent;

import com.efficiencyScape.DataTracking.ActivityState;
import com.efficiencyScape.DataTracking.GenericTracking.MetadataActivityState;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class ActivityEvent
{
	private final String id;
	private final MetadataActivityState metadata;
	private final ActivityState activity;

	@EqualsAndHashCode.Exclude
	private transient Status status = Status.TO_SEND;

	private ActivityEvent(String id, ActivityState activity, MetadataActivityState metadata)
	{
		this.id = id;
		this.activity = activity;
		this.metadata = metadata;
	}

	public static ActivityEvent of(String id, MetadataActivityState metadataActivityState, ActivityState activityState)
	{
		return new ActivityEvent(id, activityState, metadataActivityState);
	}

	public enum Status
	{
		TO_SEND,
		TO_SAVE,
		AWAITING_RESPONSE,
		PROCESSED,
		FAILED,
		SAVED,
		IGNORED
	}
}
