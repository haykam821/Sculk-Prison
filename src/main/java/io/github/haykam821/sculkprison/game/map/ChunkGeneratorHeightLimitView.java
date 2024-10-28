package io.github.haykam821.sculkprison.game.map;

import net.minecraft.world.HeightLimitView;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ChunkGeneratorHeightLimitView implements HeightLimitView {
	private final ChunkGenerator chunkGenerator;

	public ChunkGeneratorHeightLimitView(ChunkGenerator chunkGenerator) {
		this.chunkGenerator = chunkGenerator;
	}

	@Override
	public int getBottomY() {
		return this.chunkGenerator.getMinimumY();
	}

	@Override
	public int getHeight() {
		return this.chunkGenerator.getWorldHeight();
	}
}
