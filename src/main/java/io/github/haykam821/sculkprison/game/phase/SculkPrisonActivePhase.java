package io.github.haykam821.sculkprison.game.phase;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import io.github.haykam821.sculkprison.Main;
import io.github.haykam821.sculkprison.game.SculkPrisonBar;
import io.github.haykam821.sculkprison.game.SculkPrisonConfig;
import io.github.haykam821.sculkprison.game.WardenInventoryManager;
import io.github.haykam821.sculkprison.game.WinTeam;
import io.github.haykam821.sculkprison.game.map.SculkPrisonMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.GameLogic;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.event.AttackEntityListener;
import xyz.nucleoid.plasmid.game.event.GameCloseListener;
import xyz.nucleoid.plasmid.game.event.GameOpenListener;
import xyz.nucleoid.plasmid.game.event.GameTickListener;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.PlayerRemoveListener;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.widget.GlobalWidgets;

public class SculkPrisonActivePhase implements AttackEntityListener, GameCloseListener, GameOpenListener, GameTickListener, PlayerAddListener, PlayerDeathListener, PlayerRemoveListener {
	private final ServerWorld world;
	private final GameSpace gameSpace;
	private final SculkPrisonMap map;
	private final SculkPrisonConfig config;
	private final SculkPrisonBar bar;

	private final List<ServerPlayerEntity> players;
	private final ServerPlayerEntity warden;
	private final boolean singleplayer;

	private int lockTime;
	private int surviveTime;

	public SculkPrisonActivePhase(GameSpace gameSpace, SculkPrisonMap map, SculkPrisonConfig config, List<ServerPlayerEntity> players, GlobalWidgets widgets) {
		this.world = gameSpace.getWorld();
		this.gameSpace = gameSpace;
		this.map = map;
		this.config = config;
		this.bar = new SculkPrisonBar(widgets);

		this.players = players;
		this.warden = this.players.get(this.world.getRandom().nextInt(this.players.size()));
		this.singleplayer = this.players.size() == 1;

		this.lockTime = this.config.getLockTime();
		this.surviveTime = this.config.getSurviveTime();
	}

	public static void setRules(GameLogic game, boolean pvp) {
		game.deny(GameRule.BLOCK_DROPS);
		game.deny(GameRule.BREAK_BLOCKS);
		game.deny(GameRule.CRAFTING);
		game.deny(GameRule.FALL_DAMAGE);
		game.deny(GameRule.HUNGER);
		game.deny(GameRule.INTERACTION);
		game.deny(GameRule.PLACE_BLOCKS);
		game.deny(GameRule.PORTALS);
		game.deny(GameRule.THROW_ITEMS);

		if (pvp) {
			game.allow(GameRule.PVP);
		} else {
			game.deny(GameRule.PVP);
		}
	}

	public static void open(GameSpace gameSpace, SculkPrisonMap map, SculkPrisonConfig config) {
		gameSpace.openGame(game -> {
			GlobalWidgets widgets = new GlobalWidgets(game);

			SculkPrisonActivePhase phase = new SculkPrisonActivePhase(gameSpace, map, config, Lists.newArrayList(gameSpace.getPlayers()), widgets);
			SculkPrisonActivePhase.setRules(game, true);

			// Listeners
			game.listen(AttackEntityListener.EVENT, phase);
			game.listen(GameCloseListener.EVENT, phase);
			game.listen(GameOpenListener.EVENT, phase);
			game.listen(GameTickListener.EVENT, phase);
			game.listen(PlayerAddListener.EVENT, phase);
			game.listen(PlayerDeathListener.EVENT, phase);
			game.listen(PlayerRemoveListener.EVENT, phase);
		});
	}

	// Listeners
	@Override
	public ActionResult onAttackEntity(ServerPlayerEntity attacker, Hand hand, Entity attacked, EntityHitResult hitResult) {
		if (attacker.equals(this.warden) && attacked instanceof ServerPlayerEntity) {
			this.eliminate((ServerPlayerEntity) attacked, new TranslatableText("text.sculkprison.eliminated.warden", attacked.getDisplayName(), attacker.getDisplayName()), true);
		}
		return ActionResult.FAIL;
	}

	@Override
	public void onClose() {
		this.bar.close();
	}

	@Override
	public void onOpen() {
		for (ServerPlayerEntity player : this.players) {
			player.setGameMode(GameMode.ADVENTURE);

			if (player.equals(this.warden)) {
				WardenInventoryManager.applyTo(player);
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, Integer.MAX_VALUE, 1, true, false));
			}
			SculkPrisonActivePhase.spawn(this.world, this.map, player, player.equals(this.warden));
		}
	}

	@Override
	public void onTick() {
		this.lockTime -= 1;
		if (this.lockTime < 0) {
			this.surviveTime -= 1;
		} else if (this.lockTime == 0) {
			this.unlockCage();
			this.bar.changeToSurvive();
		}

		this.bar.tick(this);

		Iterator<ServerPlayerEntity> iterator = this.players.iterator();
		while (iterator.hasNext()) {
			ServerPlayerEntity player = iterator.next();
			if (player.getY() < 64) {
				this.eliminate(player, ".out_of_bounds", false);
				iterator.remove();
			}
		}

		if (!this.singleplayer) this.checkWinners();
		if (this.surviveTime < 0) this.endWithWinner(WinTeam.PLAYERS);
		if (this.players.isEmpty()) this.endWithNoWinners();
	}

	@Override
	public void onAddPlayer(ServerPlayerEntity player) {
		this.setSpectator(player);
	}

	@Override
	public ActionResult onDeath(ServerPlayerEntity player, DamageSource source) {
		SculkPrisonActivePhase.spawn(this.world, this.map, player, player.equals(this.warden));
		return ActionResult.FAIL;
	}

	@Override
	public void onRemovePlayer(ServerPlayerEntity player) {
		this.eliminate(player, true);
		this.players.remove(player);
	}

	// Utilities
	/**
	 * Breaks every block in the {@code sculkprison:warden_cage_break_blocks} block tag within the bounds defined by {@link SculkPrisonMap#WARDEN_CAGE}.
	 */
	private void unlockCage() {
		for (BlockPos pos : SculkPrisonMap.WARDEN_CAGE) {
			if (this.world.getBlockState(pos).isIn(Main.WARDEN_CAGE_BREAK_BLOCKS)) {
				this.world.breakBlock(pos, false);
			}
		}
	}

	private void setSpectator(PlayerEntity player) {
		player.setGameMode(GameMode.SPECTATOR);
	}

	/**
	 * Eliminates a given player and prints a custom message to the chat.
	 * @param remove whether to remove the player from {@link SculkPrisonActivePhase#players}
	 */
	private void eliminate(ServerPlayerEntity player, Text message, boolean remove) {
		this.gameSpace.getPlayers().sendMessage(message);

		if (remove) {
			this.players.remove(player);
		}
		this.setSpectator(player);
	}


	private void eliminate(ServerPlayerEntity player, String suffix, boolean remove) {
		this.eliminate(player, new TranslatableText("text.sculkprison.eliminated" + suffix, player.getDisplayName()).formatted(Formatting.RED), remove);
	}

	/**
	 * Eliminates a given player and prints a default message to the chat.
	 */
	private void eliminate(ServerPlayerEntity player, boolean remove) {
		this.eliminate(player, "", remove);
	}

	/**
	 * Ends the game, printing the win message of a specific team.
	 */
	private void endWithWinner(WinTeam team) {
		this.gameSpace.getPlayers().sendMessage(team.getWinMessage());
		this.gameSpace.close(GameCloseReason.FINISHED);
	}

	private void endWithNoWinners() {
		this.gameSpace.getPlayers().sendMessage(new TranslatableText("text.sculkprison.no_winners").formatted(Formatting.RED));
		this.gameSpace.close(GameCloseReason.FINISHED);
	}

	/**
	 * Checks each team's win conditions, ending the game if any are met.
	 * {@linkplain SculkPrisonActivePhase#surviveTime Survive time} is not checked using this method, and singleplayer never checks win conditions.
	 */
	private void checkWinners() {
		if (!this.players.contains(this.warden)) {
			this.endWithWinner(WinTeam.PLAYERS);
		} else if (this.players.size() == 1) {
			this.endWithWinner(WinTeam.WARDEN);
		}
	}

	public float getBarProgress() {
		if (this.lockTime < 0) {
			return (this.config.getSurviveTime() - this.surviveTime) / (float) this.config.getSurviveTime();
		}
		return (this.config.getLockTime() - this.lockTime) / (float) this.config.getLockTime();
	}

	public int getLockTime() {
		return this.lockTime;
	}

	/**
	 * Spawns a given player within the map.
	 * @param warden whether to use the {@linkplain SculkPrisonMap#WARDEN_SPAWN warden spawn} instead of the {@linkplain SculkPrisonMap#SPAWN default spawn}
	 */
	public static void spawn(ServerWorld world, SculkPrisonMap map, ServerPlayerEntity player, boolean warden) {
		Vec3d pos = warden ? SculkPrisonMap.WARDEN_SPAWN : SculkPrisonMap.SPAWN;
		player.teleport(world, pos.getX(), pos.getY(), pos.getZ(), 0, 0);
	}
}
