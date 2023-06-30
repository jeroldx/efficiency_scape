package com.efficiencyScape.DataTracking.GenericTracking;

import com.efficiencyScape.DataTracking.ActivityState;
import lombok.Builder;
import lombok.Value;
import net.runelite.api.coords.WorldPoint;

@Value
@Builder
public class LocationState implements ActivityState
{
	WorldPoint location;
	int localPlayerCount;
}
