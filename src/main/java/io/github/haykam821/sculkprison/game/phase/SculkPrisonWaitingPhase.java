package io.github.haykam821.sculkprison.game.phase;

import io.github.haykam821.sculkprison.game.SculkPrisonConfig;
import io.github.haykam821.sculkprison.game.map.SculkPrisonMap;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.plasmid.api.game.GameOpenContext;
import xyz.nucleoid.plasmid.api.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.api.game.GameResult;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.common.GameWaitingLobby;
import xyz.nucleoid.plasmid.api.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.api.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptor;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptorResult;
import xyz.nucleoid.plasmid.api.game.player.JoinOffer;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

public class SculkPrisonWaitingPhase implements GamePlayerEvents.Accept, PlayerDeathEvent, GameActivityEvents.RequestStart {
	private final GameSpace gameSpace;
	private final ServerWorld world;
	private final SculkPrisonMap map;
	private final SculkPrisonConfig config;

	public SculkPrisonWaitingPhase(GameSpace gameSpace, ServerWorld world, SculkPrisonMap map, SculkPrisonConfig config) {
		this.gameSpace = gameSpace;
		this.world = world;
		this.map = map;
		this.config = config;
	}

	public static GameOpenProcedure open(GameOpenContext<SculkPrisonConfig> context) {
		SculkPrisonMap map = new SculkPrisonMap();

		RuntimeWorldConfig worldConfig = new RuntimeWorldConfig()
			.setGenerator(map.createGenerator(context.server()));

		return context.openWithWorld(worldConfig, (activity, world) -> {
			SculkPrisonWaitingPhase phase = new SculkPrisonWaitingPhase(activity.getGameSpace(), world, map, context.config());

			GameWaitingLobby.addTo(activity, context.config().getPlayerConfig());
			SculkPrisonActivePhase.setRules(activity, false);

			// Listeners
			activity.listen(GamePlayerEvents.ACCEPT, phase);
			activity.listen(GamePlayerEvents.OFFER, JoinOffer::accept);
			activity.listen(PlayerDeathEvent.EVENT, phase);
			activity.listen(GameActivityEvents.REQUEST_START, phase);
		});
	}

	// Listeners
	@Override
	public JoinAcceptorResult onAcceptPlayers(JoinAcceptor acceptor) {
		return acceptor.teleport(this.world, SculkPrisonMap.WARDEN_SPAWN).thenRunForEach(player -> {
			player.changeGameMode(GameMode.ADVENTURE);
		});
	}

	@Override
	public EventResult onDeath(ServerPlayerEntity player, DamageSource source) {
		SculkPrisonActivePhase.spawn(this.world, this.map, player, true);
		return EventResult.DENY;
	}

	@Override
	public GameResult onRequestStart() {
		SculkPrisonActivePhase.open(this.gameSpace, this.world, this.map, this.config);
		return GameResult.ok();
	}
}
