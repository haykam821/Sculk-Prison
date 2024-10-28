package io.github.haykam821.sculkprison.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.SharedConstants;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import xyz.nucleoid.plasmid.game.common.config.PlayerConfig;

public class SculkPrisonConfig {
	public static final Codec<SculkPrisonConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			PlayerConfig.CODEC.fieldOf("players").forGetter(SculkPrisonConfig::getPlayerConfig),
			Codec.INT.optionalFieldOf("lock_time", 20 * 30).forGetter(SculkPrisonConfig::getLockTime),
			Codec.INT.optionalFieldOf("survive_time", 20 * 60 * 5).forGetter(SculkPrisonConfig::getSurviveTime),
			IntProvider.NON_NEGATIVE_CODEC.optionalFieldOf("ticks_until_close", ConstantIntProvider.create(SharedConstants.TICKS_PER_SECOND * 5)).forGetter(SculkPrisonConfig::getTicksUntilClose)
		).apply(instance, SculkPrisonConfig::new);
	});

	private final PlayerConfig playerConfig;
	private final int lockTime;
	private final int surviveTime;
	private final IntProvider ticksUntilClose;

	public SculkPrisonConfig(PlayerConfig playerConfig, int lockTime, int surviveTime, IntProvider ticksUntilClose) {
		this.playerConfig = playerConfig;
		this.lockTime = lockTime;
		this.surviveTime = surviveTime;
		this.ticksUntilClose = ticksUntilClose;
	}

	public PlayerConfig getPlayerConfig() {
		return this.playerConfig;
	}

	public int getLockTime() {
		return this.lockTime;
	}

	public int getSurviveTime() {
		return this.surviveTime;
	}

	public IntProvider getTicksUntilClose() {
		return this.ticksUntilClose;
	}
}
