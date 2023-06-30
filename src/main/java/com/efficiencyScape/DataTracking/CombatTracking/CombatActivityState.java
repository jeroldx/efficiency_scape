package com.efficiencyScape.DataTracking.CombatTracking;

import com.efficiencyScape.DataTracking.ActivityState;
import com.efficiencyScape.DataTracking.InteractionTracking.InteractionTargetType;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CombatActivityState implements ActivityState
{
	InteractionTargetType targetType;
	int targetId;
	String targetName;
	int hitsplatType;
	int hitsplatAmount;
}
