package com.efficiencyScape.DataTracking.GenericTracking;

import com.efficiencyScape.DataTracking.ActivityState;
import java.util.EnumSet;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import net.runelite.api.WorldType;

@Value
@Builder
public class WorldActivityState implements ActivityState
{
	int lastWorld;
	EnumSet<WorldType> lastWorldTypes;
	int ping;
}
