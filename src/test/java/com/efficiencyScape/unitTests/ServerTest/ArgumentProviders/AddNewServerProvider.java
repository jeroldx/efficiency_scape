package com.efficiencyScape.unitTests.ServerTest.ArgumentProviders;

import com.efficiencyScape.Server.ActivityServer;
import com.efficiencyScape.Server.EndpointTag;
import com.efficiencyScape.Server.EndpointInfo;
import java.util.EnumMap;
import java.util.stream.Stream;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class AddNewServerProvider implements ArgumentsProvider
{
	Arguments[] arguments = new Arguments[]{};

	private String buildReturnBody(String status)
	{
		return null;
	}

	String validUrl = "https://some.url:8888";
	String validBody = "{\"status\": \"UP\",\"base-url\": \"https://some.url:8888\"," +
		"\"endpoints\": [{\"type\": \"event-submission\",\"endpoint-status\": \"UP\",\"path\": \"/v1/events\"}," +
		"{\"type\": \"activity-data\",\"endpoint-status\": \"UP\",\"path\": \"/v1/activities\"}]}";
	ActivityServer validServer = ActivityServer.fromUrl(validUrl);
	EnumMap<EndpointTag, EndpointInfo> validEndpointInfo = new EnumMap<>(EndpointTag.class);

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception
	{
		validEndpointInfo.put(EndpointTag.EVENT_SUBMISSION,
			EndpointInfo.builder()
				.path("/v1/events")
				.endpointTag(EndpointTag.EVENT_SUBMISSION)
				.available(true)
				.build());
		validEndpointInfo.put(EndpointTag.ACTIVITY_SUGGESTIONS,
			EndpointInfo.builder()
				.path("/v1/activities")
				.endpointTag(EndpointTag.ACTIVITY_SUGGESTIONS)
				.available(true)
				.build());
		validServer.setAllEndpoints(validEndpointInfo);
		// String name, Boolean[] isAdded, MockResponse[] mockResponses, ActivityServer... expectedResults
		return Stream.of(
			Arguments.of("Valid url, valid returns",
				new Boolean[]{ true },
				new MockResponse[]{ new MockResponse()
					.setBody(validBody)
					.setHeader("Content-Type", "application/json") },
				new ActivityServer[]{ validServer }),
			Arguments.of("Valid url, doesn't return json",
				new Boolean[]{ false },
				new MockResponse[]{ new MockResponse()
					.setBody(validBody) },
				new ActivityServer[]{ ActivityServer.unavailableServer(HttpUrl.parse(validUrl), 10) }),
			Arguments.of("Bad response",
				new Boolean[]{ false },
				new MockResponse[]{ new MockResponse()
					.setResponseCode(500)},
				new ActivityServer[]{ ActivityServer.unavailableServer(HttpUrl.parse(validUrl), 10) }),
			Arguments.of("One bad server, one good one",
				new Boolean[]{ false, true },
				new MockResponse[]{ new MockResponse()
					.setResponseCode(500),
					new MockResponse()
						.setBody(validBody)
						.setHeader("Content-Type", "application/json")},
				new ActivityServer[]{ validServer })
		);
	}
}
