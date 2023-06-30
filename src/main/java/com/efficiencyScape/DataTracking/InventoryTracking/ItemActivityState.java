package com.efficiencyScape.DataTracking.InventoryTracking;

import com.efficiencyScape.DataTracking.ActivityState;
import lombok.Data;

@Data
public class ItemActivityState implements ActivityState
{
    private int itemId;
	private int quantity;
	private transient int lastQuantity;
	private int totalQuantityDelta;
	private transient boolean flagForDeletion;

	private ItemActivityState(int itemId)
	{
		this.itemId = itemId;
		this.quantity = 0;
		this.lastQuantity = 0;
		this.totalQuantityDelta = 0;
		this.flagForDeletion = false;
	}

	public static ItemActivityState of(int itemId)
	{
		return new ItemActivityState(itemId);
	}

    public void addQuantity(int stackQuantity)
    {
		if (lastQuantity == 0)
		{
			lastQuantity = quantity;
			quantity = 0;
		}
		quantity += stackQuantity;
		totalQuantityDelta = quantity - lastQuantity;
		flagForDeletion = quantity == 0;
    }

	public void itemRemoved()
	{
		addQuantity(0);
	}
}
