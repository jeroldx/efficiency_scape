package com.efficiencyScape.DataTracking.InteractionTracking;

import com.efficiencyScape.DataTracking.ActivityEvent.ActivityEventType;
import com.efficiencyScape.DataTracking.ActivityTracker;
import com.efficiencyScape.DataTracking.GenericTracking.GenericsTracker;
import com.efficiencyScape.DataTracking.TrackerManager;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.Text;

@Slf4j
@Singleton
public class InteractionTracker extends ActivityTracker
{
	private final Client client;
	private final ItemManager itemManager;
	private final GenericsTracker genericsTracker;

	@Inject
	public InteractionTracker(@NonNull Client client,
							  @NonNull ItemManager itemManager,
							  @NonNull TrackerManager trackerManager,
							  @NonNull GenericsTracker genericsTracker)
	{
		super(trackerManager, ActivityEventType.Type.INTERACTION);
		this.client = client;
		this.itemManager = itemManager;
		this.genericsTracker = genericsTracker;
	}

	private InteractionTargetType getInteractionTargetType(MenuOptionClicked menuOptionClicked)
	{
		MenuEntry menuEntry = menuOptionClicked.getMenuEntry();

		if (menuOptionClicked.getWidget() != null)
		{
			Widget clickedWidget = menuOptionClicked.getWidget();

			switch (WidgetInfo.TO_GROUP(clickedWidget.getId()))
			{
				// TODO Seems like this doesn't work like I thought, this should be functional, if very brittle
				case WidgetID.INVENTORY_GROUP_ID:
//				case 9764864:
				{
					return InteractionTargetType.ITEM;
				}
				case WidgetID.SPELLBOOK_GROUP_ID:
//				case 14286851:
				{
					return InteractionTargetType.SPELL;
				}
				case WidgetID.PRAYER_GROUP_ID:
				case 35454979: // Prayer book parent id, it seems
				{
					return InteractionTargetType.PRAYER;
				}
				case WidgetID.DIALOG_NPC_GROUP_ID:
				case WidgetID.DIALOG_OPTION_GROUP_ID:
				case WidgetID.DIALOG_PLAYER_GROUP_ID:
				case WidgetID.DIALOG_SPRITE_GROUP_ID:
				case WidgetID.CHATBOX_GROUP_ID:
					return InteractionTargetType.DIALOG;
				default:
					return InteractionTargetType.OTHER_WIDGET;
			}
		}
		else if (menuEntry.getNpc() != null)
		{
			return InteractionTargetType.NPC;
		}
		else if (menuEntry.getPlayer() != null)
		{
			return InteractionTargetType.PLAYER;
		}
		else if (menuEntry.getActor() != null)
		{
			// Not sure if this will be possible to reach given the above cases, but just in case
			return InteractionTargetType.OTHER_ACTOR;
		}
		else
		{
			return InteractionTargetType.OBJECT;
		}
	}

	@Subscribe
    public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked)
	{
		MenuEntry menuEntry = menuOptionClicked.getMenuEntry();

		InteractionTargetType interactionTargetType = getInteractionTargetType(menuOptionClicked);
		String name = null;
		int id = -1;
		switch (interactionTargetType)
		{
			case ITEM:
				id = Objects.requireNonNull(menuOptionClicked.getWidget()).getItemId();
				name = itemManager.getItemComposition(id).getMembersName();
				break;
			case DIALOG: // Covered under Dialog Tracking, don't need to track interactions here
				return;
			case SPELL:
			case PRAYER:
			case OTHER_WIDGET:
				id = (menuOptionClicked.getWidget() != null) ? menuOptionClicked.getWidget().getId() : -1;
				name = (menuOptionClicked.getWidget() != null) ? Text.removeTags(menuOptionClicked.getWidget().getName()) : null;
				break;
			case NPC:
				id = (menuEntry.getNpc() != null) ? menuEntry.getNpc().getId() : -1;
				name = (menuEntry.getNpc() != null) ? menuEntry.getNpc().getName() : null;
				break;
			case PLAYER:
				id = (menuEntry.getPlayer() != null) ? menuEntry.getPlayer().getId() : -1;
				name = "Player";
				break;
			case OTHER_ACTOR:
				// Not sure if this will be possible to reach given the above cases, but just in case
				id = menuOptionClicked.getId();
				name = (menuEntry.getActor() != null) ? menuEntry.getActor().getName() : null;
				break;
			case OBJECT:
				id = menuOptionClicked.getId();
				String objectName = Text.removeTags(menuEntry.getTarget());
				if (client.getSelectedWidget() != null)
				{
					name = objectName.split("->")[1].trim();
				}
				break;
		}

		trackerManager.flagSendEvent(InteractionActivityState.builder()
			.option(menuOptionClicked.getMenuEntry().getOption())
			.menuAction(menuOptionClicked.getMenuAction())
			.name(name)
			.id(id)
			.selectedWidget(findSelectedWidget())
			.interactionTargetType(interactionTargetType)
			.build(), genericsTracker.getMetadata(eventType));
    }

	private InteractionActivityState findSelectedWidget()
	{
		if (client.getSelectedWidget() != null)
		{
			int id;
			String name;
			InteractionTargetType interactionTargetType;
			int widgetId = client.getSelectedWidget().getId();
			switch (WidgetInfo.TO_GROUP(widgetId))
			{
				case WidgetID.INVENTORY_GROUP_ID:
					id = client.getSelectedWidget().getItemId();
					name = itemManager.getItemComposition(id).getMembersName();
					interactionTargetType = InteractionTargetType.ITEM;
					break;
				case WidgetID.SPELLBOOK_GROUP_ID:
					id = WidgetInfo.TO_CHILD(widgetId);
					name = Text.removeTags(client.getSelectedWidget().getName());
					interactionTargetType = InteractionTargetType.SPELL;
					break;
				default:
					id = WidgetInfo.TO_CHILD(widgetId);
					name = Text.removeTags(client.getSelectedWidget().getName());
					interactionTargetType = InteractionTargetType.OTHER_WIDGET;

			}
			return InteractionActivityState.builder()
				.id(id)
				.name(name)
				.interactionTargetType(interactionTargetType)
				.build();
		}
		return null;
	}
}
