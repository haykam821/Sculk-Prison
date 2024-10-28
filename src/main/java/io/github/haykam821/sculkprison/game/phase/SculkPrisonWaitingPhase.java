package io.github.haykam821.sculkprison.game.phase;

import io.github.haykam821.sculkprison.game.SculkPrisonConfig;
import io.github.haykam821.sculkprison.game.map.SculkPrisonMap;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.world.GameMode;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.game.GameResult;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.common.GameWaitingLobby;
import xyz.nucleoid.plasmid.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.game.player.PlayerOffer;
import xyz.nucleoid.plasmid.game.player.PlayerOfferResult;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

public class SculkPrisonWaitingPhase implements GamePlayerEvents.Offer, PlayerDeathEvent, GameActivityEvents.RequestStart {
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
			activity.listen(GamePlayerEvents.OFFER, phase);
			activity.listen(PlayerDeathEvent.EVENT, phase);
			activity.listen(GameActivityEvents.REQUEST_START, phase);
		});
	}

	// Listeners
	@Override
	public PlayerOfferResult onOfferPlayer(PlayerOffer offer) {
		return offer.accept(this.world, SculkPrisonMap.WARDEN_SPAWN).and(() -> {
			offer.player().changeGameMode(GameMode.ADVENTURE);
		});
	}

	@Override
	public ActionResult onDeath(ServerPlayerEntity player, DamageSource source) {
		SculkPrisonActivePhase.spawn(this.world, this.map, player, true);
		return ActionResult.FAIL;
	}

	@Override
	public GameResult onRequestStart() {
		SculkPrisonActivePhase.open(this.gameSpace, this.world, this.map, this.config);
		return GameResult.ok();
	}
}
