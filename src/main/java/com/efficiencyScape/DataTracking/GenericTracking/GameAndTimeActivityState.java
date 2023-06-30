package com.efficiencyScape.DataTracking.GenericTracking;

import com.efficiencyScape.DataTracking.ActivityState;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import net.runelite.api.GameState;

@Value
@Builder
public class GameAndTimeActivityState implements ActivityState
{
	GameState gameState;
	int tickCount;
	@Builder.Default
	long timestamp = Instant.now().toEpochMilli();
	int fps;
}
