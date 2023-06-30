package com.efficiencyScape.Server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
@Singleton
public class ActivityServerManager
{
	private final OkHttpClient okHttpClient;
	private final Gson gson;

	@Getter
	private final EnumMap<EndpointTag, Set<ActivityServer>> activityServers;

	// TODO Not a fan of having these hard-coded, investigate the best place to have them as variables (config?)
	final private static int MAX_RETRIES = 10;
	final private static TemporalAmount RECHECK_TIME = Duration.of(5, ChronoUnit.MINUTES);

	@Inject
	public ActivityServerManager(@NonNull OkHttpClient okHttpClient)
	{
		this.okHttpClient = okHttpClient;
		this.activityServers = new EnumMap<>(EndpointTag.class);
		this.gson = new GsonBuilder().registerTypeAdapter(HttpUrl.class, new HttpUrlAdapter()).create();
	}

	public boolean addNewServer(HttpUrl baseUrl)
	{
		ActivityServer activityServer = ActivityServer.fromUrl(baseUrl);
		if(pollAvailable(activityServer))
		{
			for (EndpointTag serverTag: activityServer.getAllEndpoints().keySet())
			{
				activityServers.putIfAbsent(serverTag, new HashSet<>());
				activityServers.get(serverTag).add(activityServer);
			}
			return true;
		}
		return false;
	}

	public void removeAllServers(EndpointTag... serverTags)
	{
		if (serverTags.length == 0) activityServers.clear();
		else for (EndpointTag serverTag: serverTags) activityServers.remove(serverTag);
	}

	public ActivityServer getServer(EndpointTag serverTag)
	{
		if (activityServers.values().size() == 0)
		{
			log.error("No servers have been added.");
			return null;
		}
		for (ActivityServer activityServer: activityServers.get(serverTag))
		{
			if (activityServer.isAvailable() && activityServer.isEndpointAvailable(serverTag))
			{
				return activityServer;
			}
		}
		log.error("Couldn't find a server for {}.", serverTag);
		return null;
	}

	private boolean shouldRecheckIfAvailable(ActivityServer activityServer)
	{
		boolean belowRetryLimit = activityServer.getConnectionAttempts() < MAX_RETRIES;
		boolean pastRecheckTime = Instant.now().isAfter(activityServer.getRecheckTime());
		return belowRetryLimit || pastRecheckTime;
	}

	public boolean pollAvailable(ActivityServer activityServer, EndpointTag... endpointTags)
	{
		if (shouldRecheckIfAvailable(activityServer))
		{
			// Increment the number of attempts to hit the server and pre-set the next recheck time
			activityServer.incrementAttempts();
			activityServer.setRecheckTime(Instant.now().plus(RECHECK_TIME));

			Request request = buildStatusRequest(activityServer.getStatusUrl());
			// Set the server to always unavailable if the base url is malformed
			if (request == null) return activityServer.alwaysUnavailable(MAX_RETRIES);

			Response response = getStatusResponse(request);
			if (response == null) return activityServer.setAvailable(false);

			ActivityServer returnedState = serverDataFromResponse(response);
			if (returnedState == null) return activityServer.setAvailable(false);

			if (activityServer.updateState(returnedState)) log.debug("Server state updated: {}", gson.toJson(activityServer));

			boolean selectedEndpointsAvailable = activityServer.isAvailable();
			for (EndpointTag endpointTag : endpointTags)
			{
				boolean endpointAvailable = activityServer.isEndpointAvailable(endpointTag);
				selectedEndpointsAvailable = selectedEndpointsAvailable && endpointAvailable;
			}
			activityServer.resetAttempts();
			return selectedEndpointsAvailable;
		}
		activityServer.setAvailable(false);
		return false;
	}

	private ActivityServer serverDataFromResponse(Response response)
	{
		try (response)
		{
			if (response.isSuccessful())
			{
				if (!Objects.equals(response.headers().get("Content-Type"), "application/json"))
				{
					log.error("Status endpoint for '{}' did not return JSON.", response.request().url());
					return ActivityServer.unavailableServer(response.request().url(), MAX_RETRIES);
				}
				String responseBody = (response.body() != null) ? response.body().string() : "{}";
				return gson.fromJson(responseBody, ActivityServer.class); // ActivityServer.fromJson(responseBody);
			}
			else
			{
				log.error("checkConnection - Connection not established, endpoint returned code: {}", response.code());
			}
		}
		catch (IOException e)
		{
			log.error("Error connecting to server.");
		}
		return null;
	}

	private Response getStatusResponse(Request request)
	{
		try
		{
			return okHttpClient.newCall(request).execute();
		}
		catch (IOException e)
		{
			log.error("Error connecting to server.");
			return null;
		}
	}

	private Request buildStatusRequest(HttpUrl url)
	{
		try
		{
			return new Request.Builder()
				.url(url)
				.method("GET", null)
				.build();
		}
		catch (IllegalArgumentException e)
		{
			log.error(e.getMessage());
			return null;
		}
	}
}
