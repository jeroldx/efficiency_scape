package com.efficiencyScape.unitTests.ServerTest;

import com.efficiencyScape.Server.ActivityServer;
import com.efficiencyScape.Server.ActivityServerManager;
import com.efficiencyScape.EfficiencyScapeConfig;
import com.efficiencyScape.Server.EndpointTag;
import com.efficiencyScape.unitTests.ServerTest.ArgumentProviders.AddNewServerProvider;
import com.efficiencyScape.unitTests.ServerTest.ArgumentProviders.CheckConnectionArgumentProvider;
import com.efficiencyScape.unitTests.MocksFactory;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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

public class ActivityServerManagerTest
{
	@Mock
	EfficiencyScapeConfig efficiencyScapeConfig;

	@Spy
	OkHttpClient okHttpClient = new OkHttpClient();

	@InjectMocks
	ActivityServerManager activityServerManager;

	MockWebServer mockWebServer;
	HttpUrl baseUrl;
	MocksFactory mocksFactory = new MocksFactory();

	@BeforeEach
	public void before() throws IOException
	{
		mockWebServer = new MockWebServer();
		mockWebServer.start();
		baseUrl = mockWebServer.url("/test");
		MockitoAnnotations.openMocks(this);
		when(efficiencyScapeConfig.eventSubmitURI()).thenReturn(baseUrl.toString());
	}

	@AfterEach
	public void after() throws IOException
	{
		mockWebServer.shutdown();
	}

	@Test
	public void testActivityServerAdapterRead()
	{}

	private void enqueueMocks(MockResponse... mockResponses)
	{
		for (MockResponse response: mockResponses) mockWebServer.enqueue(response);
	}

	@ParameterizedTest(name = "{0}")
	@ArgumentsSource(AddNewServerProvider.class)
	public void testAddNewValidServer(String name, Boolean[] isAdded, MockResponse[] mockResponses, ActivityServer[] expectedResults)
	{
		activityServerManager.removeAllServers();

		enqueueMocks(mockResponses);
		for (boolean added: isAdded)
		{
			Assertions.assertEquals(added, activityServerManager.addNewServer(mockWebServer.url("/test")));
		}
		for (ActivityServer activityServer: expectedResults)
		{
			for (EndpointTag serverTag: activityServer.getAllEndpoints().keySet())
			{
				ActivityServer actualServer = activityServerManager.getServer(serverTag);
				System.out.println(actualServer);
				Assertions.assertEquals(activityServer.getFullEndpointUrl(serverTag),
					actualServer.getFullEndpointUrl(serverTag));
				Assertions.assertEquals(activityServer.isEndpointAvailable(serverTag),
					actualServer.isEndpointAvailable(serverTag));
			}
		}
	}

	@Test
	public void testGetServerNoServers()
	{
		Assertions.assertNull(activityServerManager.getServer(EndpointTag.ACTIVITY_SUGGESTIONS));
	}

	@ParameterizedTest(name = "{0}")
	@ArgumentsSource(AddNewServerProvider.class)
	public void testGetServerNoGoodServer(String name, Boolean[] isAdded, MockResponse[] mockResponses, ActivityServer[] expectedResults)
	{
		activityServerManager.removeAllServers();
		enqueueMocks(mockResponses);
		for (boolean added: isAdded)
		{
			Assertions.assertEquals(added, activityServerManager.addNewServer(mockWebServer.url("/test")));
			if (!activityServerManager.addNewServer(mockWebServer.url("/test")))
			{

			}
		}
		mockWebServer.enqueue(new MockResponse().setHeader("Content-Type", "application/json"));
	}

	@ParameterizedTest(name = "{0}")
	@ArgumentsSource(CheckConnectionArgumentProvider.class)
	public void testPollAvailable(String name, MockResponse[] mockResponses, boolean expectedResponse)
	{
		for (MockResponse response: mockResponses)
		{
			mockWebServer.enqueue(response);
		}
//		activityServerManager.checkConnection(baseUrl);
//		Assertions.assertEquals(expectedResponse, activityServerManager.isConnectionEstablished());
	}

	@Test
	public void testCheckConnectionOnThrowIOException() throws IOException
	{
		Call call = mock(Call.class);
		doThrow(new IOException()).when(call).execute();
		doReturn(call).when(okHttpClient).newCall((any()));
//
//		activityServerManager.checkConnection(baseUrl);
//
//		Assertions.assertFalse(activityServerManager.isConnectionEstablished());
	}

	@Test
	public void testCheckConnectionOnThrowIllegalArgument()
	{
		when(efficiencyScapeConfig.eventSubmitURI()).thenReturn("BADURI");

//		activityServerManager.checkConnection(baseUrl);
//
//		Assertions.assertFalse(activityServerManager.isConnectionEstablished());
	}


}
