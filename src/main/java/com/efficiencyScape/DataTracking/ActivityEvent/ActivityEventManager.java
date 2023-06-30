package com.efficiencyScape.DataTracking.ActivityEvent;

import com.efficiencyScape.DataTracking.ActivityState;
import com.efficiencyScape.DataTracking.GenericTracking.MetadataActivityState;
import com.efficiencyScape.EfficiencyScapeConfig;
import com.efficiencyScape.Server.ActivityServer;
import com.efficiencyScape.Server.ActivityServerManager;
import com.efficiencyScape.Server.EndpointTag;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.callback.ClientThread;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@Slf4j
@Singleton
public class ActivityEventManager
{
	private final EfficiencyScapeConfig efficiencyScapeConfig;
	private final FileManager fileManager;
	private final ClientThread clientThread;
	private final ActivityServerManager activityServerManager;
	private final OkHttpClient okHttpClient;
	private final Gson gson;

	@Getter
	private final BlockingDeque<ActivityEvent> eventQueue;
	private ActivityServer activityServer;
	private final EnumMap<ActivityEventType.Type, Boolean> debugState;

	@Inject
	public ActivityEventManager(@NonNull EfficiencyScapeConfig efficiencyScapeConfig,
								@NonNull FileManager fileManager,
								@NonNull ClientThread clientThread,
								@NonNull ActivityServerManager activityServerManager,
								@NonNull OkHttpClient okHttpClient)
	{
		this.efficiencyScapeConfig = efficiencyScapeConfig;
		this.fileManager = fileManager;
		this.clientThread = clientThread;
		this.activityServerManager = activityServerManager;
		this.okHttpClient = okHttpClient;
		this.eventQueue = new LinkedBlockingDeque<>();
		this.gson = new GsonBuilder().create();
		this.debugState = new EnumMap<>(ActivityEventType.Type.class);
	}

	public void processEvents()
	{
		int awaiting = 0;
		int processed = 0;
		int failed = 0;
		int saved = 0;
		int ignored = 0;

		ArrayList<ActivityEvent> toSaveEvents = new ArrayList<>();
		ArrayList<ActivityEvent> toSendEvents = new ArrayList<>();

		for (ActivityEvent activityEvent: eventQueue)
		{
			switch(activityEvent.getStatus())
			{
				case AWAITING_RESPONSE:
					awaiting++;
					break;
				case PROCESSED:
					processed++;
					if (!eventQueue.remove(activityEvent)) log.error("Tried to delete a missing event.");
					break;
				case FAILED:
					failed++;
					if (!eventQueue.remove(activityEvent)) log.error("Tried to delete a missing event.");
					break;
				case SAVED:
					saved++;
					if (!eventQueue.remove(activityEvent)) log.error("Tried to delete a missing event.");
					break;
				case TO_SEND:
					if (validateServer())
					{
						toSendEvents.add(activityEvent);
						break;
					}
				case TO_SAVE:
					toSaveEvents.add(activityEvent);
					break;
				case IGNORED:
				default:
					ignored++;
					if (!eventQueue.remove(activityEvent)) log.error("Tried to delete a missing event.");
					break;
			}
		}

		if (eventQueue.size() > 0)
		{
			log.debug("Events on deck: " +
					"\n\tEvents to send: {}" +
					"\n\tSend events awaiting response: {}" +
					"\n\tSubmitted to save locally: {}" +
					"\n\tEvents accepted by server: {}" +
					"\n\tEvents saved locally: {}" +
					"\n\tFailed to process or save, deleting: {}" +
					"\n\tIgnored events: {}",
				toSendEvents.size(), awaiting, toSaveEvents.size(), processed, saved, failed, ignored);
		}
		saveEvent(toSaveEvents, fileManager.getWriter(true));
		sendEvent(toSendEvents, activityServer);
	}

	private void updateDebugState()
	{
		debugState.put(ActivityEventType.Type.DIALOG, efficiencyScapeConfig.dialogTracking());
		debugState.put(ActivityEventType.Type.EXPERIENCE, efficiencyScapeConfig.experienceTracking());
		debugState.put(ActivityEventType.Type.INTERACTION, efficiencyScapeConfig.interactionTracking());
		debugState.put(ActivityEventType.Type.INVENTORY_UPDATED, efficiencyScapeConfig.inventoryTracking());
		debugState.put(ActivityEventType.Type.LOCATION_CHANGED, efficiencyScapeConfig.locationTracking());
		debugState.put(ActivityEventType.Type.COMBAT, efficiencyScapeConfig.combatTracking());
	}

	private boolean validateServer()
	{
		if (activityServer == null)
		{
			activityServer = activityServerManager.getServer(EndpointTag.EVENT_SUBMISSION);
		}
		return activityServer != null;
	}

	public void queueEvent(String eventId,
						   @NonNull MetadataActivityState metadataState,
						   @NonNull ActivityState activityState)
	{
		ActivityEvent activityEvent = ActivityEvent.of(eventId, metadataState, activityState);
		queueEvent(activityEvent);
	}

	private void queueEvent(ActivityEvent activityEvent)
	{
		updateDebugState();
		if (!debugState.get(activityEvent.getMetadata().getEventType().type))
		{
			activityEvent.setStatus(ActivityEvent.Status.IGNORED);
		}
		else if (efficiencyScapeConfig.debug())
		{
			activityEvent.setStatus(ActivityEvent.Status.TO_SAVE);
		}
		else
		{
			activityEvent.setStatus(ActivityEvent.Status.TO_SEND);
		}
		eventQueue.add(activityEvent);
	}

	private void saveEvent(ArrayList<ActivityEvent> activityEvents, BufferedWriter bufferedWriter)
	{
		if (activityEvents.size() == 0) return;
		try(bufferedWriter)
		{
			while (activityEvents.size() > 0)
			{
				ActivityEvent activityEvent = activityEvents.remove(0);
				String eventJson = encrypt(gson.toJson(activityEvent), efficiencyScapeConfig.debug());
				bufferedWriter.append(eventJson);
				bufferedWriter.newLine();
				activityEvent.setStatus(ActivityEvent.Status.SAVED);
			}
		}
		catch(IOException e)
		{
			for (ActivityEvent activityEvent: activityEvents)
			{
				activityEvent.setStatus(ActivityEvent.Status.FAILED);
			}
			log.error("'{}' in saveEventToLocalFile: {}\n{}", e.getClass(), e.getMessage(), e.getStackTrace());
		}
	}

	public void sendEvent(ArrayList<ActivityEvent> activityEvents, ActivityServer activityServer)
	{
		if (activityEvents.size() == 0) return;
		if (activityServer.isAvailable())
		{
			HttpUrl eventSubmissionUrl = activityServer.getFullEndpointUrl(EndpointTag.EVENT_SUBMISSION);
			try
			{
				RequestBody eventBody = RequestBody.create(
					MediaType.parse("application/octet-stream"),
					gson.toJson(activityEvents));
				Request request = new Request.Builder()
					.url(eventSubmissionUrl)
					.method("POST", eventBody)
					.build();
				okHttpClient.newCall(request).enqueue(ActivityEventCallback.of(activityEvents));
				activityEvents.forEach(activityEvent -> activityEvent.setStatus(ActivityEvent.Status.AWAITING_RESPONSE));
			}
			catch (IllegalStateException e)
			{
				log.error("Tried to send the event twice somehow.");
			}
		}
		else
		{
			activityEvents.forEach(activityEvent -> activityEvent.setStatus(ActivityEvent.Status.TO_SAVE));
		}
	}

	// TODO actually do encryption to mitigate tampering
	private String encrypt(String unencrypted, boolean debug)
	{
		return unencrypted;
	}

	private String decrypt(String encrypted)
	{
		// TODO, check if unencrypted, then decrypt if so, return string if not
		return encrypted;
	}

	public void queueLocalSavedEvents()
	{
		if (!fileManager.checkFile()) return;
		if (activityServer != null)
		{
			try (BufferedReader reader = fileManager.getReader())
			{
				String line;
				while ((line = reader.readLine()) != null)
				{
					if (line.length() > 0)
					{
						gson.fromJson(decrypt(line), ActivityEvent.class);
					}
				}
			}
			catch (IOException e)
			{
				log.error("'{}' in sendLocalSavedEvents: {}\n{}", e.getClass(), e.getMessage(), e.getStackTrace());
			}
			fileManager.setFlaggedDelete(true);
			clientThread.invoke(fileManager::deleteEventFile);
		}
	}
}
