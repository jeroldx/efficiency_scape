package com.efficiencyScape.DataTracking.ActivityEvent;

import java.io.IOException;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.annotations.EverythingIsNonNull;

@Slf4j
public class ActivityEventCallback implements Callback
{
	private final ArrayList<ActivityEvent> activityEvents;

	private ActivityEventCallback(ArrayList<ActivityEvent> activityEvents)
	{
		this.activityEvents = activityEvents;
	}

	public static ActivityEventCallback of(ArrayList<ActivityEvent> activityEvents)
	{
		return new ActivityEventCallback(activityEvents);
	}

	private void updateStatuses(ArrayList<ActivityEvent> activityEvents, ActivityEvent.Status status)
	{
		for (ActivityEvent activityEvent: activityEvents)
		{
			activityEvent.setStatus(status);
		}
	}

	@Override
	@EverythingIsNonNull
	public void onFailure(Call call, IOException e)
	{
		log.error("'{}' in ActivityEventCallback: {}\n{}", e.getClass(), e.getMessage(), e.getStackTrace());
		log.error("Connection to server failed while sending event.");
		updateStatuses(activityEvents, ActivityEvent.Status.TO_SAVE);
	}

	@Override
	@EverythingIsNonNull
	public void onResponse(Call call, Response response)
	{
		if (response.isSuccessful())
		{
			updateStatuses(activityEvents, ActivityEvent.Status.PROCESSED);
		}
		else
		{
			log.error("Events failed to reach server with code {}.", response.code());
			updateStatuses(activityEvents, ActivityEvent.Status.TO_SAVE);
		}
	}
}
