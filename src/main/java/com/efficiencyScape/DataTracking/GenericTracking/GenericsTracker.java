package com.efficiencyScape.DataTracking.GenericTracking;

import com.efficiencyScape.DataTracking.ActivityEvent.ActivityEventType;
import com.efficiencyScape.DataTracking.ActivityTracker;
import com.efficiencyScape.DataTracking.TrackerManager;
import java.util.EnumSet;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Varbits;
import net.runelite.api.WorldType;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.worldhopper.ping.Ping;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;

@Singleton
@Slf4j
public class GenericsTracker extends ActivityTracker
{
	private final Client client;
	private final ClientThread clientThread;
	private final WorldService worldService;

	private WorldResult worldResult;
	private World world;
	private UUID sessionId;
	private long accountHash;
	private int accountType;
//	private int world;
	private EnumSet<WorldType> worldTypes;
	private GameState gameState;
	private Player localPlayer;

	@Inject
	public GenericsTracker(@NonNull TrackerManager trackerManager,
						   @NonNull Client client,
						   @NonNull ClientThread clientThread,
						   @NonNull WorldService worldService)
	{
		super(trackerManager, ActivityEventType.Type.NONE);
		this.client = client;
		this.clientThread = clientThread;
		this.worldService = worldService;
	}

	public void onStartUp()
	{
		worldResult = worldService.getWorlds();
		clientThread.invokeLater(this::getDetailsIfLoggedIn);
	}

	private boolean getDetailsIfLoggedIn()
	{
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			world = worldResult.findWorld(client.getWorld());
			accountHash = client.getAccountHash();
			accountType = client.getVarbitValue(Varbits.ACCOUNT_TYPE);
//			world = client.getWorld();
			worldTypes = client.getWorldType();
			localPlayer = client.getLocalPlayer();

			return true;
		}
		return false;
	}

	private int pingCurrentWorld()
	{

		if (worldResult == null || client.getGameState() != GameState.LOGGED_IN)
		{
			return -1;
		}
		final World currentWorld = worldResult.findWorld(client.getWorld());
		if (currentWorld == null)
		{
			log.debug("unable to find current world: {}", client.getWorld());
			return -1;
		}
		return Ping.ping(currentWorld);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		gameState = gameStateChanged.getGameState();
		switch (gameState)
		{
			case LOGGED_IN:
			{
				getDetailsIfLoggedIn();
				break;
			}
			case LOGGING_IN:
			{
				sessionId = UUID.randomUUID();
				break;
			}
		}
	}

	public MetadataActivityState getMetadata(ActivityEventType.Type eventType)
	{
		return MetadataActivityState.builder()
			.eventType(ActivityEventType.of(eventType))
			.sessionId(sessionId)
			.accountState(AccountActivityState.builder()
				.lastAccount(Long.toHexString(accountHash))
				.accountType(accountType).build())
			.gameAndTimeState(GameAndTimeActivityState.builder()
				.tickCount(client.getTickCount())
				.fps(client.getFPS())
				.gameState(gameState).build())
			.worldState(WorldActivityState.builder()
				.lastWorldTypes(worldTypes)
				.ping(Ping.ping(world))
				.lastWorld(world.getId()).build())
			.locationState(LocationState.builder()
				.location(localPlayer.getWorldLocation())
				.localPlayerCount(client.getPlayers().size())
				.build()).build();
	}
}
