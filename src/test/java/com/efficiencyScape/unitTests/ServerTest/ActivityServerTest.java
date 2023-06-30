package com.efficiencyScape.unitTests.ServerTest;

import com.efficiencyScape.Server.ActivityServer;
import com.efficiencyScape.Server.EndpointTag;
import com.efficiencyScape.Server.EndpointInfo;
import java.util.EnumMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ActivityServerTest
{
	@Test
	public void testUpdateState()
	{
		ActivityServer actualState = ActivityServer.fromUrl("https://some.url");
		actualState.setAvailable(true);
		actualState.updateState(ActivityServer.unavailableServer(actualState.getBaseUrl(), 10));
		Assertions.assertEquals(actualState, ActivityServer.unavailableServer(actualState.getBaseUrl(), 10));
	}

	@Test
	public void testUpdateStateNull()
	{
		ActivityServer actualState = new ActivityServer();
		actualState.setAvailable(true);
		actualState.putEndpointInfo(EndpointTag.EVENT_SUBMISSION, EndpointInfo.builder()
			.available(true)
			.path("/v1/events")
			.endpointTag(EndpointTag.EVENT_SUBMISSION).build());
		actualState.updateState(null);
		Assertions.assertFalse(actualState.isAvailable());
		Assertions.assertEquals(new EnumMap<>(EndpointTag.class), actualState.getAllEndpoints());
	}

	@Test
	public void testSetAvailable()
	{
		ActivityServer actualState = new ActivityServer();
		actualState.setAvailable(true);
		actualState.putEndpointInfo(EndpointTag.EVENT_SUBMISSION, EndpointInfo.builder()
			.available(true)
			.path("/v1/events")
			.endpointTag(EndpointTag.EVENT_SUBMISSION).build());
		actualState.setAvailable(false);
		Assertions.assertFalse(actualState.isEndpointAvailable(EndpointTag.EVENT_SUBMISSION));
	}

	@Test
	public void testGetFullEndpointUrl()
	{
		ActivityServer actualState = ActivityServer.fromUrl("https://base.url");
		actualState.putEndpointInfo(EndpointTag.EVENT_SUBMISSION, EndpointInfo.builder()
			.available(true)
			.path("v1/events")
			.endpointTag(EndpointTag.EVENT_SUBMISSION).build());
		Assertions.assertEquals("https://base.url/v1/events", actualState.getFullEndpointUrl(EndpointTag.EVENT_SUBMISSION).toString());
	}

	@Test
	public void testGetStatusUrl()
	{
		ActivityServer actualState = ActivityServer.fromUrl("https://base.url");
		Assertions.assertEquals("https://base.url/status", actualState.getStatusUrl().toString());
	}

	@Test
	public void testAttempts()
	{
		ActivityServer actualState = ActivityServer.fromUrl("https://base.url");
		actualState.incrementAttempts();
		Assertions.assertEquals(1, actualState.getConnectionAttempts());
		actualState.resetAttempts();
		Assertions.assertEquals(0, actualState.getConnectionAttempts());
	}
}
