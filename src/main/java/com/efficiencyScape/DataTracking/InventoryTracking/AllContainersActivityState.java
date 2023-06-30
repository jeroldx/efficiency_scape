package com.efficiencyScape.DataTracking.InventoryTracking;

import com.efficiencyScape.DataTracking.ActivityState;
import java.util.HashMap;
import lombok.Value;
import net.runelite.api.Item;

@Value
public class AllContainersActivityState implements ActivityState
{
	HashMap<Integer, ItemContainerState> inventoryStates = new HashMap<>();

	public void updateItemContainerState(int containerId, Item[] items)
	{
		boolean isFirstState = inventoryStates.putIfAbsent(containerId, ItemContainerState.of(containerId)) == null;
		inventoryStates.get(containerId).setFirstState(isFirstState);
		inventoryStates.get(containerId).updateContainer(items);
	}

	public ItemContainerState getItemContainerState(int containerId)
	{
		return inventoryStates.get(containerId);
	}
}
