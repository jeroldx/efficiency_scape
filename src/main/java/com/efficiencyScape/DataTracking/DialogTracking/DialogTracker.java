package com.efficiencyScape.DataTracking.DialogTracking;

import com.efficiencyScape.DataTracking.ActivityEvent.ActivityEventType;
import com.efficiencyScape.DataTracking.ActivityTracker;
import com.efficiencyScape.DataTracking.GenericTracking.GenericsTracker;
import com.efficiencyScape.DataTracking.TrackerManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

@Slf4j
@Singleton
public class DialogTracker extends ActivityTracker
{
	private final Client client;
	private final GenericsTracker genericsTracker;

	@Setter
	@Getter
	DialogActivityState currentState;
	boolean optionSelected;

	private final ArrayList<WidgetInfo> WIDGET_TYPE_CHECK = new ArrayList<>(Arrays.asList(
		WidgetInfo.DIALOG_PLAYER,
		WidgetInfo.DIALOG_SPRITE,
		WidgetInfo.DIALOG_NPC_NAME, // Annoying that this one is the only one without a DIALOG_XXX, but this should work
		WidgetInfo.DIALOG_OPTION,
		WidgetInfo.CHATBOX_FULL_INPUT
	));

	@Inject
	public DialogTracker(@NonNull Client client,
						 @NonNull GenericsTracker genericsTracker,
						 @NonNull TrackerManager trackerManager)
	{
		super(trackerManager, ActivityEventType.Type.DIALOG);
		this.client = client;
		this.genericsTracker = genericsTracker;
		this.optionSelected = false;
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired scriptPostFired)
	{
		int scriptId = scriptPostFired.getScriptId();
		// Script ids for selecting an option I presume? Copied from other plugins TODO Verify
		if (scriptId == 2153 || scriptId == 2869)
		{
			Widget optionsWidget = client.getWidget(WidgetInfo.DIALOG_OPTION_OPTIONS);
			if (optionsWidget != null && !optionsWidget.isHidden())
			{
				Widget[] dynamicChildren = optionsWidget.getDynamicChildren();
				for (int i = 0; i < dynamicChildren.length; i++)
				{
					Widget optionOptionWidget = dynamicChildren[i];
					String optionOptionString = Text.removeTags(optionOptionWidget.getText());
					if ("Please wait...".equals(optionOptionString) && !optionSelected)
					{
						currentState.selectOption(i);
						optionSelected = true;
						trackerManager.flagSendEvent(currentState, genericsTracker.getMetadata(eventType));
					}
				}
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		optionSelected = false;
		updateCurrentState();
	}

	private void updateCurrentState()
	{
		DialogActivityState checkState = getCurrentDialogState();
		if (!Objects.equals(checkState, currentState))
		{
			if (checkState == null && currentState.getDialogType() == DialogType.OPTIONS)
			{
				trackerManager.flagSendEvent(currentState, genericsTracker.getMetadata(eventType));
			}
			currentState = checkState;
			if (currentState != null && currentState.getDialogType() != DialogType.OPTIONS)
			{
				trackerManager.flagSendEvent(currentState, genericsTracker.getMetadata(eventType));
			}
		}
	}

	@NonNull
	private String getText(WidgetInfo widgetInfo)
	{
		Widget widget = client.getWidget(widgetInfo);
		return (widget != null) ? widget.getText() : "";
	}

	@Nullable
	private DialogActivityState getCurrentDialogState()
	{
		DialogType dialogType = getCurrentDialogType();
		switch (dialogType)
		{
			case NPC:
			{
				return DialogActivityState.builder()
					.dialogType(dialogType)
					.name(getText(WidgetInfo.DIALOG_NPC_NAME))
					.text(getText(WidgetInfo.DIALOG_NPC_TEXT))
					.build();
			}
			case PLAYER:
			{
				return DialogActivityState.builder()
					.dialogType(dialogType)
					.name("Player")
					.text(getText(WidgetInfo.DIALOG_PLAYER_TEXT))
					.build();
			}
			case SPRITE:
			{
				return DialogActivityState.builder()
					.dialogType(dialogType)
					.name(getText(WidgetInfo.DIALOG_SPRITE_SPRITE))
					.text(getText(WidgetInfo.DIALOG_SPRITE_TEXT))
					.build();
			}
			case OPTIONS:
			{
				Widget optionsWidget = client.getWidget(WidgetInfo.DIALOG_OPTION_OPTIONS);
				ArrayList<String> options = null;
				if (optionsWidget != null)
				{
					options = new ArrayList<>();
					for (Widget child : optionsWidget.getDynamicChildren())
					{
						if (child.getText() != null && !child.getText().isEmpty())
						{
							options.add(child.getText());
						}
					}
				}
				return DialogActivityState.builder()
					.dialogType(dialogType)
					.name("Options")
					.text((options != null) ? options.remove(0) : null)
					.options(options)
					.build();
			}
			case INPUT:
			{
				return DialogActivityState.builder()
					.dialogType(dialogType)
					.name("Input")
					.build();
			}
			default:
				return null;
		}
	}

	@NonNull
	private DialogType getCurrentDialogType()
	{
		for (WidgetInfo widgetInfo: WIDGET_TYPE_CHECK)
		{
			Widget widget = client.getWidget(widgetInfo);
			if (widget != null && !widget.isHidden())
			{
				return DialogType.typeByWidgetInfo(widgetInfo);
			}
		}
		return DialogType.NONE;
	}
}
