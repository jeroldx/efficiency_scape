package com.efficiencyScape.DataTracking.CombatTracking;

import com.efficiencyScape.DataTracking.ActivityEvent.ActivityEventType;
import com.efficiencyScape.DataTracking.ActivityTracker;
import com.efficiencyScape.DataTracking.GenericTracking.GenericsTracker;
import com.efficiencyScape.DataTracking.InteractionTracking.InteractionTargetType;
import com.efficiencyScape.DataTracking.TrackerManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Actor;
import net.runelite.api.Hitsplat;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.eventbus.Subscribe;

@Singleton
public class CombatTracker extends ActivityTracker
{
	private final GenericsTracker genericsTracker;

	@Inject
	public CombatTracker(TrackerManager trackerManager, GenericsTracker genericsTracker)
	{
		super(trackerManager, ActivityEventType.Type.COMBAT);
		this.genericsTracker = genericsTracker; 
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied)
	{
		Hitsplat hitsplat = hitsplatApplied.getHitsplat();
		Actor target = hitsplatApplied.getActor();
		if (hitsplat.isMine())
		{
			String name = target.getName();
			InteractionTargetType targetType = InteractionTargetType.OTHER_ACTOR;
			int id = -1;
			if (target instanceof Player)
			{
				id = ((Player) target).getId();
				targetType = InteractionTargetType.PLAYER;
			}
			else if (target instanceof NPC)
			{
				id = ((NPC) target).getId();
				targetType = InteractionTargetType.NPC;
			}

			trackerManager.flagSendEvent(CombatActivityState.builder()
					.targetId(id)
					.targetName(name)
					.targetType(targetType)
					.hitsplatType(hitsplat.getHitsplatType())
					.hitsplatAmount(hitsplat.getAmount()).build(),
				genericsTracker.getMetadata(eventType)); 
		}
	}
}
