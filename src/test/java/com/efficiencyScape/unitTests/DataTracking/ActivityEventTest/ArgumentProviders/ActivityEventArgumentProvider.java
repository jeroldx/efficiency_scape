package com.efficiencyScape.unitTests.DataTracking.ActivityEventTest.ArgumentProviders;

import com.efficiencyScape.DataTracking.ActivityEvent.ActivityEvent;
import com.efficiencyScape.DataTracking.ActivityEvent.ActivityEventType;
import com.efficiencyScape.unitTests.MocksFactory;
import java.util.ArrayList;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class ActivityEventArgumentProvider implements ArgumentsProvider
{
	MocksFactory mocksFactory = new MocksFactory();

	private ArrayList<ActivityEvent> basicEventProvider()
	{
		ArrayList<ActivityEvent> returnEvents = new ArrayList<>();

		for (ActivityEventType.Type type : ActivityEventType.Type.values())
		{
			returnEvents.add(mocksFactory.filledEvent(null, type));
		}

		return returnEvents;
	}

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception
	{
		return null;
	}
}
