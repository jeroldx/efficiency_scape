package com.efficiencyScape.unitTests.DataTracking.ActivityEventTest.ArgumentProviders;

import com.efficiencyScape.DataTracking.ActivityEvent.ActivityEvent;
import com.efficiencyScape.DataTracking.ActivityEvent.ActivityEventType;
import com.efficiencyScape.unitTests.DataTracking.ActivityEventTest.EventAndResponse;
import com.efficiencyScape.unitTests.MocksFactory;
import java.util.stream.Stream;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class SendEventArgumentProvider implements ArgumentsProvider
{

	MocksFactory mocksFactory = new MocksFactory();

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception
	{
		// String testName,
		// String eventId, boolean debugMode, Exception exception,
		//		MockResponse[] responses,
		//		MetadataActivityState metadataActivityState, ActivityState activityState,
		//		String expectedResult
		String TEST_UUID = "ba00b4e1-1bf7-4cc9-8604-3d0c117a3d7c";
		return Stream.of(
			Arguments.of("Valid States, events send", false,
				EventAndResponse.of(mocksFactory.filledEvent(TEST_UUID, ActivityEventType.Type.EXPERIENCE),
					ActivityEvent.Status.PROCESSED,
					new MockResponse().addHeader("Accept", "POST"),
					new MockResponse())),
			Arguments.of("Valid States, debug mode enabled", true,
				EventAndResponse.of(mocksFactory.filledEvent(TEST_UUID, ActivityEventType.Type.EXPERIENCE),
					ActivityEvent.Status.PROCESSED,
					new MockResponse().addHeader("Accept", "POST"),
					new MockResponse()))
//				"ba00b4e1-1bf7-4cc9-8604-3d0c117a3d7c", true,
//				new MockResponse[]{
//					new MockResponse().addHeader("Accept", "POST"),
//					new MockResponse() },
//				ActivityEvent.Status.FAILED,
//				mocksFactory.createMockMetadataState("7670abb8-69b5-4d73-b95e-a186e5735128",
//					10L, 0,
//					GameState.LOGGED_IN, 5, 800L,
//					301, EnumSet.of(WorldType.MEMBERS)),
//					ExperienceActivityState.builder()
//						.activityEventType(ActivityEventType.XP_GAINED)
//						.skill(Skill.AGILITY)
//						.xp(8_888)
//						.level(26)
//						.boostedLevel(30)
//						.propertyName("xp-state")
//						.build(),
//				"{\"_id\":\"ba00b4e1-1bf7-4cc9-8604-3d0c117a3d7c\"," +
//					"\"metadata\":{\"account-state\":{\"account-hash\":10,\"account-type-id\":0}," +
//					"\"game-and-time-state\":{\"game-state\":\"LOGGED_IN\",\"tick-count\":5,\"timestamp\":800}," +
//					"\"world-state\":{\"world\":301,\"world-types\":[\"MEMBERS\"]}," +
//					"\"session-id\":\"7670abb8-69b5-4d73-b95e-a186e5735128\"}," +
//					"\"xp-state\":{\"skill\":\"AGILITY\",\"xp-total\":8888,\"level-total\":26,\"boosted-level\":30}}\n"),
		);
	}
}
