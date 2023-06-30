package com.efficiencyScape.DataTracking.InteractionTracking;

import com.efficiencyScape.DataTracking.ActivityState;
import lombok.Builder;
import lombok.Value;
import net.runelite.api.MenuAction;

@Builder
@Value
public class InteractionActivityState implements ActivityState
{
	InteractionTargetType interactionTargetType;
	String name;
	int id;
	String option;
	MenuAction menuAction;
	InteractionActivityState selectedWidget;
}
