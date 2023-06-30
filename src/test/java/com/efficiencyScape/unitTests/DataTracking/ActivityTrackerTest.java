package com.efficiencyScape.unitTests.DataTracking;

import com.efficiencyScape.DataTracking.ActivityTracker;
import com.efficiencyScape.EfficiencyScapeConfig;
import net.runelite.api.Client;
import net.runelite.client.account.SessionManager;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.any;

public abstract class ActivityTrackerTest
{
	@Mock
	protected Client client;
	@Mock
	protected ClientThread clientThread;
	@Mock
	protected SessionManager sessionManager;
	@Mock
	protected EfficiencyScapeConfig efficiencyScapeConfig;
	@Mock
	protected ItemManager itemManager;
	@Mock
	protected EventBus eventBus;
	@Mock
	protected KeyManager keyManager;

	protected ActivityTracker activityTracker;

	public void before()
	{
		Assertions.assertNotNull(activityTracker);
	}

}
