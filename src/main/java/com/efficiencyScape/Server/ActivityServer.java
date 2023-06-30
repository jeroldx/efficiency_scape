package com.efficiencyScape.Server;

import java.time.Instant;
import java.util.EnumMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import okhttp3.HttpUrl;

@Data
public class ActivityServer
{
	private HttpUrl baseUrl;
	private EnumMap<EndpointTag, EndpointInfo> allEndpoints;
	@EqualsAndHashCode.Exclude
	private transient int connectionAttempts;
	@EqualsAndHashCode.Exclude
	private boolean available;
	@EqualsAndHashCode.Exclude
	private transient Instant recheckTime;
	private static final String statusEndpoint = "status";

	public ActivityServer()
	{
		this.baseUrl = null;
		this.recheckTime = Instant.EPOCH;
		this.allEndpoints = new EnumMap<>(EndpointTag.class);
		this.connectionAttempts = 0;
		this.available = false;
	}

	private ActivityServer(HttpUrl baseUrl)
	{
		this.baseUrl = baseUrl;
		this.recheckTime = Instant.EPOCH;
		this.allEndpoints = new EnumMap<>(EndpointTag.class);
		this.connectionAttempts = 0;
		this.available = false;
	}

	public static ActivityServer fromUrl(String baseUrl)
	{
		HttpUrl url = HttpUrl.parse(baseUrl);
		if (url != null)
		{
			return fromUrl(url);
		}
		return new ActivityServer();
	}

	public static ActivityServer fromUrl(HttpUrl baseUrl)
	{
		return new ActivityServer(baseUrl);
	}

	public static ActivityServer unavailableServer(HttpUrl baseUrl, int maxRetries)
	{
		ActivityServer activityServer = fromUrl(baseUrl);
		activityServer.alwaysUnavailable(maxRetries);
		return activityServer;
	}

	public boolean alwaysUnavailable(int maxRetries)
	{
		setAvailable(false);
		setRecheckTime(Instant.MAX);
		setConnectionAttempts(maxRetries + 1);
		setAllEndpoints(new EnumMap<>(EndpointTag.class));
		return isAvailable();
	}

	public void resetAttempts()
	{
		connectionAttempts = 0;
	}

	public void incrementAttempts()
	{
		connectionAttempts++;
	}

	public HttpUrl getStatusUrl()
	{
		return baseUrl.newBuilder().addPathSegments(statusEndpoint).build();
	}

	public void putEndpointInfo(EndpointTag serverTag, EndpointInfo endpointInfo)
	{
		this.allEndpoints.put(serverTag, endpointInfo);
	}

	public HttpUrl getFullEndpointUrl(EndpointTag serverTag)
	{
		return baseUrl.newBuilder().addPathSegments(allEndpoints.get(serverTag).getPath()).build();
	}

	public boolean isEndpointAvailable(EndpointTag serverTag)
	{
		return allEndpoints.get(serverTag).isAvailable();
	}

	public boolean setAvailable(boolean available)
	{
		this.available = available;
		if (!available)
		{
			for (EndpointTag serverTag: allEndpoints.keySet())
			{
				allEndpoints.get(serverTag).setAvailable(false);
			}
		}
		return available;
	}

	public void setBaseUrl(String url)
	{
		this.baseUrl = HttpUrl.parse(url);
	}

	/**
	 * Updates this state with data returned from the status endpoint.
	 * Will return true if the state was updated or false if it wasn't.
	 * @param updatedState The latest state object returned by the endpoint.
	 * @return true if the state was updated, false otherwise
	 */
	public boolean updateState(ActivityServer updatedState)
	{
		if (updatedState == null)
		{
			allEndpoints = new EnumMap<>(EndpointTag.class);
			available = false;
			return true;
		}
		baseUrl = updatedState.getBaseUrl();
		available = updatedState.isAvailable();
		allEndpoints = updatedState.getAllEndpoints();
		return true;
	}
}
