package com.efficiencyScape.DataTracking.DialogTracking;

import com.efficiencyScape.DataTracking.ActivityEvent.ActivityEventType;
import com.efficiencyScape.DataTracking.GenericTracking.GenericsTracker;
import com.efficiencyScape.DataTracking.TrackerManager;
import java.awt.event.KeyEvent;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.VarClientStr;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.input.KeyListener;

@Slf4j
@Singleton
public class InputKeyListener implements KeyListener
{
	private final Client client;
	private final ClientThread clientThread;
	private final TrackerManager trackerManager;
	private final GenericsTracker genericsTracker;
	private final DialogTracker dialogTracker;

	@Inject
	public InputKeyListener(@NonNull Client client,
							@NonNull ClientThread clientThread,
							@NonNull TrackerManager trackerManager,
							@NonNull GenericsTracker genericsTracker,
							@NonNull DialogTracker dialogTracker)
	{
		this.client = client;
		this.clientThread = clientThread;
		this.trackerManager = trackerManager;
		this.genericsTracker = genericsTracker;
		this.dialogTracker = dialogTracker;
		this.trackerManager.assignKeyListener(this);
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		DialogActivityState currentState = dialogTracker.getCurrentState();
		try
		{
			if (currentState != null && currentState.getDialogType() == DialogType.INPUT && e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				clientThread.invoke(() -> {
					String inputValue = client.getVarcStrValue(VarClientStr.INPUT_TEXT);
					currentState.selectOption(inputValue);
					trackerManager.flagSendEvent(currentState, genericsTracker.getMetadata(ActivityEventType.Type.DIALOG));
				});
			}
		}
		catch (RuntimeException runtimeException)
		{
			log.error("{}", runtimeException.toString());
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{

	}
}
