package com.efficiencyScape.unitTests.DataTracking.ActivityEventTest;

import com.efficiencyScape.DataTracking.ActivityEvent.ActivityEvent;
import okhttp3.mockwebserver.MockResponse;

public class EventAndResponse
{
	private final ActivityEvent activityEvent;
	private final MockResponse[] responses;
	private final ActivityEvent.Status finalStatus;

	private EventAndResponse(ActivityEvent activityEvent,
							 ActivityEvent.Status finalStatus,
							 MockResponse... mockResponses)
	{
		this.activityEvent = activityEvent;
		this.responses = mockResponses;
		this.finalStatus = finalStatus;
	}

	public static EventAndResponse of(ActivityEvent activityEvent,
									  ActivityEvent.Status finalStatus,
									  MockResponse... mockResponses)
	{
		return new EventAndResponse(activityEvent, finalStatus, mockResponses);
	}

	public MockResponse[] getResponses()
	{
		return responses;
	}

	public ActivityEvent.Status getFinalStatus()
	{
		return finalStatus;
	}

	public ActivityEvent getActivityEvent()
	{
		return activityEvent;
	}
}
