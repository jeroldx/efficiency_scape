package com.efficiencyScape.unitTests.DataTracking.ActivityEventTest;

import com.efficiencyScape.DataTracking.ActivityEvent.ActivityEvent;
import com.efficiencyScape.DataTracking.ActivityEvent.ActivityEventManager;
import com.efficiencyScape.DataTracking.ActivityEvent.FileManager;
import com.efficiencyScape.Server.ActivityServer;
import com.efficiencyScape.Server.ActivityServerManager;
import com.efficiencyScape.EfficiencyScapeConfig;
import com.efficiencyScape.unitTests.DataTracking.ActivityEventTest.ArgumentProviders.SendEventArgumentProvider;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import net.runelite.client.callback.ClientThread;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class ActivityEventManagerTest
{
	@Mock
	EfficiencyScapeConfig efficiencyScapeConfig;
	@Mock
	FileManager fileManager;
	@Mock
	ClientThread clientThread;
	@Mock
	ActivityServerManager activityServerManager;
	@Spy
	OkHttpClient okHttpClient = new OkHttpClient();

	@InjectMocks
	ActivityEventManager activityEventManager;

	StringWriter eventStringWriter;
	BufferedWriter bufferedWriter;
	BufferedReader bufferedReader;

	MockWebServer mockWebServer;
	HttpUrl baseUrl;

	@BeforeEach
	public void before() throws IOException
	{
		mockWebServer = new MockWebServer();
		mockWebServer.start();
		baseUrl = mockWebServer.url("/test");
		MockitoAnnotations.openMocks(this);
		when(efficiencyScapeConfig.eventSubmitURI()).thenReturn(baseUrl.toString());

		eventStringWriter = new StringWriter();
		bufferedWriter = new BufferedWriter(eventStringWriter);
		when(fileManager.getWriter(true)).thenReturn(bufferedWriter);

		FileReader eventStringReader = new FileReader("src/test/resources/testEvents.esevents");
		bufferedReader = new BufferedReader(eventStringReader);
		when(fileManager.getReader()).thenReturn(bufferedReader);
	}

	@Test
	public void testProcessEvents()
	{
		ActivityServer activityServer = ActivityServer.fromUrl(mockWebServer.url(""));
		when(activityServerManager.getServer(any())).thenReturn(activityServer);
	}

	@Test
	public void testQueueEvent()
	{

	}

	@ParameterizedTest(name = "{0}")
	@ArgumentsSource(SendEventArgumentProvider.class)
	public void testSendEvent(String testName, boolean debugMode,
							  EventAndResponse... eventAndResponses) throws InterruptedException
	{
		when(efficiencyScapeConfig.debug()).thenReturn(debugMode);

		for (EventAndResponse eventAndResponse : eventAndResponses)
		{
			ActivityEvent.Status eventStatus = eventAndResponse.getFinalStatus();
			ActivityEvent activityEvent = eventAndResponse.getActivityEvent();
			for (MockResponse mockResponse: eventAndResponse.getResponses())
			{
				mockWebServer.enqueue(mockResponse);
			}
//			activityServerManager.checkConnection(baseUrl);
//			activityServerManager.sendEvent(baseUrl, activityEvent);
//
//			RecordedRequest request = mockWebServer.takeRequest();
//
//			switch (eventStatus)
//			{
//				case PROCESSED:
//					Assertions.assertEquals(activityEvent.encrypt(debugMode), request.getBody().readUtf8());
//					break;
//				case FAILED:
//
//			}
//			Assertions.assertEquals(eventStatus, serverManager.getEventQueue().getFirst().getStatus());
//			if (eventStatus == ActivityEvent.Status.PROCESSED)
//			{
//				Assertions.assertEquals(expectedResult.trim(), request.getBody().readUtf8());
//			}
		}
	}

	@Test
	public void testSendEventThrowsIllegalState() throws IOException
	{
		mockWebServer.enqueue(new MockResponse().setHeader("Accept", "POST,OPTIONS"));
//		activityServerManager.checkConnection(baseUrl);
//
//		Call call = mock(Call.class);
//		doThrow(new IllegalStateException()).when(call).enqueue(any());
//		doReturn(call).when(okHttpClient).newCall((any()));
//
//		activityEventManager.queueEvent(null,
//			MetadataActivityState.builder()
//				.propertyName("metadata")
//				.activityEventType(ActivityEventType.NONE)
//				.build(),
//			ExperienceActivityState.builder()
//				.propertyName("experience")
//				.activityEventType(ActivityEventType.XP_GAINED)
//				.build());
//
//		Assertions.assertEquals(ActivityEvent.Status.FAILED, activityEventManager.getEventQueue().getFirst().getStatus());
	}

	@AfterEach
	public void after() throws IOException
	{
		mockWebServer.shutdown();
	}
}
