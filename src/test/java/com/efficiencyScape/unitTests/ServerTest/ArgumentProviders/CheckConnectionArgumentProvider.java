package com.efficiencyScape.unitTests.ServerTest.ArgumentProviders;

import java.util.stream.Stream;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class CheckConnectionArgumentProvider implements ArgumentsProvider
{
	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception
	{
		return Stream.of(
			Arguments.of("Server responds, accepts POST",
				new MockResponse[]{ new MockResponse().addHeader("Accept", "POST,OPTIONS") }, true),
			Arguments.of("500 response",
				new MockResponse[]{ new MockResponse().setResponseCode(500) }, false),
			Arguments.of("Server responds, doesn't accept POST",
				new MockResponse[]{ new MockResponse().addHeader("Accept", "GET,OPTIONS") }, false)
		);
	}
}
