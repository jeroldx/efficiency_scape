package com.efficiencyScape.unitTests;

import com.efficiencyScape.DataTracking.ActivityEvent.ActivityEvent;
import com.efficiencyScape.DataTracking.ActivityEvent.ActivityEventType;
import com.efficiencyScape.DataTracking.ActivityState;
import com.efficiencyScape.DataTracking.DialogTracking.DialogActivityState;
import com.efficiencyScape.DataTracking.DialogTracking.DialogType;
import com.efficiencyScape.DataTracking.ExperienceTracking.ExperienceActivityState;
import com.efficiencyScape.DataTracking.GenericTracking.MetadataActivityState;
import com.efficiencyScape.DataTracking.InteractionTracking.InteractionActivityState;
import com.efficiencyScape.DataTracking.InteractionTracking.InteractionTargetType;
import com.efficiencyScape.EfficiencyScapeConfig;
import java.awt.event.KeyEvent;
import java.io.IOException;
import net.runelite.api.MenuAction;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import okhttp3.Call;
import okhttp3.Response;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MocksFactory
{
	public Widget createMockWidget(boolean isHidden, String text, Widget... dynamicChildren)
	{
		Widget createdWidget = mock(Widget.class);
		when(createdWidget.isHidden()).thenReturn(isHidden);
		when(createdWidget.getDynamicChildren()).thenReturn(dynamicChildren);
		when(createdWidget.getText()).thenReturn(text);
		return createdWidget;
	}

	public Call createMockCall(Response response, Response... responses) throws IOException
	{
		Call call = mock(Call.class);
		when(call.execute()).thenReturn(response, responses);
		return call;
	}

	public Response createMockResponse(String[] stringHeaders, boolean isSuccessful, Boolean... isSuccessfulMulti)
	{
		Response response = mock(Response.class);
		int lastIndex = stringHeaders.length - 1;
		for (int i = 0; i <= lastIndex; i += 2 )
		{
			String headerName = stringHeaders[i];
			String headerValue = (i+1 <= lastIndex) ? stringHeaders[i+1] : null;
			when(response.header(headerName)).thenReturn(headerValue);
		}
		when(response.isSuccessful()).thenReturn(isSuccessful, isSuccessfulMulti);
		return response;
	}

	public ActivityEvent simpleEvent()
	{
		ExperienceActivityState xpState = ExperienceActivityState.builder()
			.build();
		MetadataActivityState metadataActivityState = MetadataActivityState.builder()
			.build();
		return ActivityEvent.of("ba00b4e1-1bf7-4cc9-8604-3d0c117a3d7c",
			metadataActivityState,
			xpState);
	}

	public ActivityEvent filledEvent(String id, ActivityEventType.Type type)
	{
		MetadataActivityState metadataActivityState = null;
		ActivityState activityState = null;
		switch(type)
		{
			case DIALOG:
				InteractionActivityState interactionState = InteractionActivityState.builder()
					.menuAction(MenuAction.NPC_FIRST_OPTION)
					.name("An NPC")
					.id(256)
					.interactionTargetType(InteractionTargetType.NPC)
					.build();
				activityState = DialogActivityState.builder()
					.dialogType(DialogType.NPC)
					.text("This is a test")
					.name("An NPC")
					.build();
				break;
			case INTERACTION:
				activityState = InteractionActivityState.builder()
					.menuAction(MenuAction.NPC_FIRST_OPTION)
					.name("An NPC")
					.id(256)
					.interactionTargetType(InteractionTargetType.NPC)
					.build();
				break;
			case INVENTORY_UPDATED:
			case LOCATION_CHANGED:
			case EXPERIENCE:
			default:
				activityState = ExperienceActivityState.builder()
					.skill(Skill.AGILITY)
					.xp(8_888)
					.level(26)
					.boostedLevel(30)
					.build();
				break;
		}
		return ActivityEvent.of(id, metadataActivityState, activityState);
	}

	public EfficiencyScapeConfig createMockConfig(boolean debug, String uri)
	{
		EfficiencyScapeConfig efficiencyScapeConfig = mock(EfficiencyScapeConfig.class);
		when(efficiencyScapeConfig.debug()).thenReturn(debug);
		when(efficiencyScapeConfig.eventSubmitURI()).thenReturn(uri);
		return efficiencyScapeConfig;
	}

	public KeyEvent createMockKeyEvent(int keyCode)
	{
		KeyEvent createdKeyEvent = mock(KeyEvent.class);
		when(createdKeyEvent.getKeyCode()).thenReturn(keyCode);
		return createdKeyEvent;
	}
}
