package com.efficiencyScape.unitTests.DataTracking;

import com.efficiencyScape.DataTracking.DialogTracking.DialogTracker;
import com.efficiencyScape.DataTracking.ExperienceTracking.ExperienceTracker;
import com.efficiencyScape.DataTracking.GenericTracking.GenericsTracker;
import com.efficiencyScape.DataTracking.InteractionTracking.InteractionTracker;
import com.efficiencyScape.DataTracking.InventoryTracking.InventoryTracker;
import com.efficiencyScape.DataTracking.TrackerManager;
import javax.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TrackerManagerTest
{
	@Inject
	private ExperienceTracker experienceTracker;
	@Inject
	private InteractionTracker interactionTracker;
	@Inject
	private InventoryTracker inventoryTracker;
	@Inject
	private DialogTracker dialogTracker;
	@Inject
	private GenericsTracker genericsTracker;

	@Inject
	private TrackerManager trackerManager;

	@Test
	public void testActivityTrackerInjects()
	{
//		Assertions.assertTrue(trackerManager.getAllTrackers().length > 0);
	}
}
