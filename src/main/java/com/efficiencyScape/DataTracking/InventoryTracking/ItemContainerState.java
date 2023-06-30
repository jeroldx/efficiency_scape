package com.efficiencyScape.DataTracking.InventoryTracking;

import com.efficiencyScape.DataTracking.ActivityState;
import com.google.common.collect.Maps;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import net.runelite.api.Item;
import java.util.HashMap;
import java.util.Set;

@Data
public class ItemContainerState implements ActivityState
{
    private final int inventoryId;
    private transient final Map<Integer, ItemActivityState> itemMap;
	private boolean firstState;
	private Map<Integer, ItemActivityState> lastChangedItems;

	private ItemContainerState(int inventoryId)
	{
		this.inventoryId = inventoryId;
		this.itemMap = new ConcurrentHashMap<>();
		this.firstState = true;
	}

	public static ItemContainerState of(int inventoryId)
	{
		return new ItemContainerState(inventoryId);
	}

	private void addQuantity(int itemId, int quantityDelta)
	{
		itemMap.putIfAbsent(itemId, ItemActivityState.of(itemId));
		itemMap.get(itemId).addQuantity(quantityDelta);
	}

    private void resetDeltaAndRemoveEmpty()
    {
		for(Map.Entry<Integer, ItemActivityState> itemActivityStateEntry: itemMap.entrySet())
		{
			itemActivityStateEntry.getValue().setLastQuantity(0);
			if (itemActivityStateEntry.getValue().isFlagForDeletion())
			{
				itemMap.remove(itemActivityStateEntry.getKey());
			}
		}
    }

    public void updateContainer(Item[] newItems)
    {
        resetDeltaAndRemoveEmpty();
		Set<Integer> updatedItems = new HashSet<>();
		for (Item item: newItems)
		{
			addQuantity(item.getId(), item.getQuantity());
			updatedItems.add(item.getId());
		}
		Set<Integer> removedItems = new HashSet<>(itemMap.keySet());
		if (removedItems.removeAll(updatedItems))
		{
			for (int removedItem : removedItems)
			{
				itemMap.get(removedItem).itemRemoved();
			}
		}
		lastChangedItems = Maps.filterEntries(new HashMap<>(itemMap),
			itemState -> itemState.getValue().getTotalQuantityDelta() != 0);
	}
}
