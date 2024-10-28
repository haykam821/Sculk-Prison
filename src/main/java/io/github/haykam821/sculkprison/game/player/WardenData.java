package io.github.haykam821.sculkprison.game.player;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angriness;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class WardenData {
	private final ServerPlayerEntity player;

	private int ambientSoundChance;

	private WardenData(ServerPlayerEntity player) {
		this.player = Objects.requireNonNull(player);
	}

	public void initialize() {
		WardenInventoryManager.applyTo(player);
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, StatusEffectInstance.INFINITE, 1, true, false));
}

	public void tick() {
		if (this.player.getRandom().nextInt(1000) < this.ambientSoundChance) {
			this.ambientSoundChance = -80;
			this.playSound(SoundEvents.ENTITY_WARDEN_AMBIENT, 4);
		} else {
			this.ambientSoundChance += 1;
		}

		if (this.player.age % this.getHeartRate() == 0) {
			this.playSound(SoundEvents.ENTITY_WARDEN_HEARTBEAT, 5);
		}
	}

	public boolean isOf(ServerPlayerEntity player) {
		return this.player.equals(player);
	}

	public boolean isIn(Collection<ServerPlayerEntity> players) {
		return players.contains(this.player);
	}

	private void playSound(SoundEvent sound, float volume) {
		this.player.getWorld().playSoundFromEntity(null, this.player, sound, SoundCategory.PLAYERS, volume, this.player.getSoundPitch());
	}

	private int getHeartRate() {
		float angriness = this.getAnger() / (float) Angriness.ANGRY.getThreshold();
		return 40 - MathHelper.floor(MathHelper.clamp(angriness, 0, 1) * 30);
	}

	private int getAnger() {
		return 0;
	}

	public static WardenData choose(List<ServerPlayerEntity> players, Random random) {
		ServerPlayerEntity player = Util.getRandom(players, random);
		return new WardenData(player);
	}
}
