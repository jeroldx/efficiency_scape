package com.efficiencyScape.DataTracking.GenericTracking;

import com.efficiencyScape.DataTracking.ActivityEvent.ActivityEventType;
import com.efficiencyScape.DataTracking.ActivityState;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MetadataActivityState implements ActivityState
{
	UUID sessionId;
	ActivityEventType eventType;
	AccountActivityState accountState;
	GameAndTimeActivityState gameAndTimeState;
	WorldActivityState worldState;
	LocationState locationState;
}
