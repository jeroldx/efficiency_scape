package com.efficiencyScape.DataTracking.ExperienceTracking;

import com.efficiencyScape.DataTracking.ActivityEvent.ActivityEventType;
import com.efficiencyScape.DataTracking.ActivityTracker;
import com.efficiencyScape.DataTracking.GenericTracking.GenericsTracker;
import com.efficiencyScape.DataTracking.TrackerManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.NonNull;
import net.runelite.api.Client;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.eventbus.Subscribe;

@Singleton
public class ExperienceTracker extends ActivityTracker
{
	private final Client client;
	private final GenericsTracker genericsTracker;

	private boolean xpTrackingInitializing = true;

	@Inject
	public ExperienceTracker(@NonNull Client client,
							 @NonNull GenericsTracker genericsTracker,
							 @NonNull TrackerManager trackerManager)
	{
		super(trackerManager, ActivityEventType.Type.EXPERIENCE);
		this.client = client;
		this.genericsTracker = genericsTracker;
	}

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged)
    {
        switch (client.getGameState())
		{
            case LOGGING_IN:
            case HOPPING:
                xpTrackingInitializing = true;
                break;
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) throws NullPointerException
    {
        if (xpTrackingInitializing ) xpTrackingInitializing = false;
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged)
	{
        if (xpTrackingInitializing) return;
		trackerManager.flagSendEvent(ExperienceActivityState.builder()
			.skill(statChanged.getSkill())
			.xp(statChanged.getXp())
			.level(statChanged.getLevel())
			.boostedLevel(statChanged.getBoostedLevel()).build(), genericsTracker.getMetadata(eventType));

    }
}
