package io.github.haykam821.sculkprison.game.map;

import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;

/**
 * A {@link ChunkRandom} that forces the first call to {@link BlockRotation#random(Random)}
 * to return a specific {@link BlockRotation}.
 */
public class ForcedBlockRotationRandom extends ChunkRandom {
	private BlockRotation rotation;

	public ForcedBlockRotationRandom(Random baseRandom, BlockRotation rotation) {
		super(baseRandom);

		this.rotation = rotation;
	}

	public BlockRotation consumeRotation() {
		BlockRotation rotation = this.rotation;
		this.rotation = null;

		return rotation;
	}

	public static ForcedBlockRotationRandom of(long seed, BlockRotation rotation) {
		return new ForcedBlockRotationRandom(Random.create(seed), rotation);
	}
}
