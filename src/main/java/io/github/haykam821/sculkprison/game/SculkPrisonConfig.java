package io.github.haykam821.sculkprison.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import xyz.nucleoid.plasmid.game.common.config.PlayerConfig;

public class SculkPrisonConfig {
	public static final Codec<SculkPrisonConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			PlayerConfig.CODEC.fieldOf("players").forGetter(SculkPrisonConfig::getPlayerConfig),
			Codec.INT.optionalFieldOf("lock_time", 20 * 30).forGetter(SculkPrisonConfig::getLockTime),
			Codec.INT.optionalFieldOf("survive_time", 20 * 60 * 5).forGetter(SculkPrisonConfig::getSurviveTime)
		).apply(instance, SculkPrisonConfig::new);
	});

	private final PlayerConfig playerConfig;
	private final int lockTime;
	private final int surviveTime;

	public SculkPrisonConfig(PlayerConfig playerConfig, int lockTime, int surviveTime) {
		this.playerConfig = playerConfig;
		this.lockTime = lockTime;
		this.surviveTime = surviveTime;
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
}
