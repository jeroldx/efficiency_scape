package com.efficiencyScape.DataTracking.ExperienceTracking;

import com.efficiencyScape.DataTracking.ActivityState;
import lombok.Builder;
import lombok.Value;
import net.runelite.api.Skill;

@Builder
@Value
public class ExperienceActivityState implements ActivityState
{
	Skill skill;
	int xp;
	int level;
	int boostedLevel;
}
