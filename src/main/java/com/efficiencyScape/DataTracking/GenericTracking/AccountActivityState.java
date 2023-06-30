package com.efficiencyScape.DataTracking.GenericTracking;

import com.efficiencyScape.DataTracking.ActivityState;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AccountActivityState implements ActivityState
{
	String lastAccount;
	int accountType;
}
