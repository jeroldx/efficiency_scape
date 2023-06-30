package com.efficiencyScape.DataTracking.DialogTracking;

import net.runelite.api.widgets.WidgetInfo;

public enum DialogType
{
	PLAYER,
	NPC,
	SPRITE,
	OPTIONS,
	INPUT,
	NONE;

	public static DialogType typeByWidgetInfo(WidgetInfo widgetInfo)
	{
		DialogType dialogType;
		switch(widgetInfo)
		{
			case DIALOG_PLAYER:
			case DIALOG_PLAYER_TEXT:
				dialogType = PLAYER;
				break;
			case DIALOG_SPRITE:
			case DIALOG_SPRITE_SPRITE:
			case DIALOG_SPRITE_TEXT:
				dialogType = SPRITE;
				break;
			case DIALOG_NPC_NAME:
			case DIALOG_NPC_HEAD_MODEL:
			case DIALOG_NPC_TEXT:
				dialogType = NPC;
				break;
			case DIALOG_OPTION:
			case DIALOG_OPTION_OPTIONS:
				dialogType = OPTIONS;
				break;
			case CHATBOX_FULL_INPUT:
			case CHATBOX_INPUT:
				dialogType = INPUT;
				break;
			default:
				dialogType = null;
		}
		return dialogType;
	}
}
