package com.efficiencyScape.DataTracking.DialogTracking;

import com.efficiencyScape.DataTracking.ActivityState;
import lombok.Builder;
import java.util.ArrayList;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Builder
@Data
@Slf4j
public class DialogActivityState implements ActivityState
{
    ArrayList<String> options;
	DialogType dialogType;
	private String text;
	private String name;
	int spriteItemId;
    @Builder.Default
	private String chosenOption = null;

	//Pretty wacky that this results in an int input for strings and a String input for numbers,
	// but logically that's just how it is.
	/**
	 * Sets the option that's chosen by the player by the index of the one they selected.
	 * @param index index of the chosen option
	 */
	public void selectOption(int index)
	{
		if (options.size() >= index)
		{
			chosenOption = options.get(index);
		}
		else
		{
			log.error("Trying to select dialog option index {} out of options '{}'", index, options);
		}
	}

	/**
	 * Sets the value that the user input into a field.
	 * @param value the entered value
	 */
	public void selectOption(String value)
	{
		chosenOption = value;
	}
}
