package com.efficiencyScape.DataTracking;

import com.efficiencyScape.DataTracking.GenericTracking.GenericsTracker;
import com.efficiencyScape.DataTracking.GenericTracking.MetadataActivityState;
import com.efficiencyScape.DataTracking.ActivityEvent.ActivityEventManager;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.input.KeyListener;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.input.KeyManager;

@Slf4j
@Singleton
public class TrackerManager
{
	private final EventBus eventBus;
	private final KeyManager keyManager;
	private final ClientThread clientThread;
	private final ActivityEventManager activityEventManager;

	private final ArrayList<ActivityTracker> allTrackers;
	private final ArrayList<KeyListener> allKeyListeners;

	@Inject
	public TrackerManager(@NonNull EventBus eventBus,
						  @NonNull KeyManager keyManager,
						  @NonNull ClientThread clientThread,
						  @NonNull ActivityEventManager activityEventManager)
	{
		this.eventBus = eventBus;
		this.keyManager = keyManager;
		this.clientThread = clientThread;
		this.activityEventManager = activityEventManager;
		this.allTrackers = new ArrayList<>();
		this.allKeyListeners = new ArrayList<>();
	}

	public void onStartUp()
	{
		registerTrackers();
		registerKeyListeners();
	}

	public void onShutDown()
	{
		unregisterTrackers();
		unregisterKeyListeners();
	}

	public void flagSendEvent(ActivityState activityState, MetadataActivityState metadataState)
	{
		clientThread.invoke(() -> activityEventManager.queueEvent(UUID.randomUUID().toString(),
			metadataState, activityState));
	}

	public void assignTracker(ActivityTracker activityTracker)
	{
		allTrackers.add(activityTracker);
	}

	public void assignKeyListener(KeyListener keyListener)
	{
		allKeyListeners.add(keyListener);
	}

	private void registerTrackers()
	{
		for (ActivityTracker activityTracker: allTrackers)
		{
			try
			{
				eventBus.register(activityTracker);
			}
			catch (IllegalArgumentException e)
			{
				log.error("Failed to register tracker {}, exception: {}", activityTracker.getClass(), e);
			}
		}
	}

	private void registerKeyListeners()
	{
		for (KeyListener keyListener: allKeyListeners)
		{
			keyManager.registerKeyListener(keyListener);
		}
	}

	private void unregisterTrackers()
	{
		for (ActivityTracker activityTracker: allTrackers)
		{
			eventBus.unregister(activityTracker);
		}
	}

	private void unregisterKeyListeners()
	{
		for (KeyListener keyListener: allKeyListeners)
		{
			keyManager.unregisterKeyListener(keyListener);
		}
	}
}
