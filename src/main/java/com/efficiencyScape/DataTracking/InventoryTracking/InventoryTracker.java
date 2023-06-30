package com.efficiencyScape.DataTracking.InventoryTracking;

import com.efficiencyScape.DataTracking.ActivityEvent.ActivityEventType;
import com.efficiencyScape.DataTracking.ActivityTracker;
import com.efficiencyScape.DataTracking.GenericTracking.GenericsTracker;
import com.efficiencyScape.DataTracking.TrackerManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.eventbus.Subscribe;

@Singleton
public class InventoryTracker extends ActivityTracker
{
	private final GenericsTracker genericsTracker;

	private final AllContainersActivityState allInventoriesState;

	@Inject
	public InventoryTracker(TrackerManager trackerManager,
							GenericsTracker genericsTracker)
	{
		super(trackerManager, ActivityEventType.Type.INVENTORY_UPDATED);
		this.genericsTracker = genericsTracker;
		this.allInventoriesState = new AllContainersActivityState();
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged itemContainerChanged)
	{
		ItemContainer changedContainer = itemContainerChanged.getItemContainer();
		allInventoriesState.updateItemContainerState(changedContainer.getId(), changedContainer.getItems());
		ItemContainerState changedContainerState = allInventoriesState.getItemContainerState(changedContainer.getId());
		trackerManager.flagSendEvent(changedContainerState, genericsTracker.getMetadata(eventType));
	}
}
