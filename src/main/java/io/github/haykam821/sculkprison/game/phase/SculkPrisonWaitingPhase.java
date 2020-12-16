package io.github.haykam821.sculkprison.game.phase;

import io.github.haykam821.sculkprison.game.SculkPrisonConfig;
import io.github.haykam821.sculkprison.game.map.SculkPrisonMap;
import io.github.haykam821.sculkprison.game.map.SculkPrisonMapBuilder;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.GameMode;
import xyz.nucleoid.fantasy.BubbleWorldConfig;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.GameWaitingLobby;
import xyz.nucleoid.plasmid.game.StartResult;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.RequestStartListener;
import xyz.nucleoid.plasmid.game.rule.RuleResult;

public class SculkPrisonWaitingPhase implements PlayerAddListener, PlayerDeathListener, RequestStartListener {
	private final GameSpace gameSpace;
	private final SculkPrisonMap map;
	private final SculkPrisonConfig config;

	public SculkPrisonWaitingPhase(GameSpace gameSpace, SculkPrisonMap map, SculkPrisonConfig config) {
		this.gameSpace = gameSpace;
		this.map = map;
		this.config = config;
	}

	public static GameOpenProcedure open(GameOpenContext<SculkPrisonConfig> context) {
		SculkPrisonMapBuilder mapBuilder = new SculkPrisonMapBuilder();
		SculkPrisonMap map = mapBuilder.create(context.getServer());

		BubbleWorldConfig worldConfig = new BubbleWorldConfig()
			.setGenerator(map.createGenerator(context.getServer()))
			.setDefaultGameMode(GameMode.ADVENTURE);

		return context.createOpenProcedure(worldConfig, game -> {
			SculkPrisonWaitingPhase phase = new SculkPrisonWaitingPhase(game.getSpace(), map, context.getConfig());

			GameWaitingLobby.applyTo(game, context.getConfig().getPlayerConfig());
			SculkPrisonActivePhase.setRules(game, RuleResult.DENY);

			// Listeners
			game.on(PlayerAddListener.EVENT, phase);
			game.on(PlayerDeathListener.EVENT, phase);
			game.on(RequestStartListener.EVENT, phase);
		});
	}

	// Listeners
	@Override
	public void onAddPlayer(ServerPlayerEntity player) {
		SculkPrisonActivePhase.spawn(this.gameSpace.getWorld(), this.map, player, true);
	}

	@Override
	public ActionResult onDeath(ServerPlayerEntity player, DamageSource source) {
		SculkPrisonActivePhase.spawn(this.gameSpace.getWorld(), this.map, player, true);
		return ActionResult.FAIL;
	}

	@Override
	public StartResult requestStart() {
		SculkPrisonActivePhase.open(this.gameSpace, this.map, this.config);
		return StartResult.OK;
	}
}
