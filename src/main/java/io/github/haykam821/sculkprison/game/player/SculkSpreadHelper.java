package io.github.haykam821.sculkprison.game.player;

import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.chunk.Chunk;

public final class SculkSpreadHelper {
	protected static final int SPREAD_CHANCE_PER_TICK = 200;
	protected static final int RECORD_BREADCRUMB_CHANCE_PER_TICK = 100;

	protected static final int MAX_BREADCRUMBS = 10;
	protected static final int BREADCRUMB_BIAS = 3;

	private SculkSpreadHelper() {
		return;
	}

	protected static SculkSpreadManager createSpreadManager() {
		return new SculkSpreadManager(false, BlockTags.SCULK_REPLACEABLE_WORLD_GEN, 10, 4, 10, 5);
	}

	protected static int getCharge(Random random) {
		return random.nextBetween(800, 3000);
	}

	protected static BlockPos findSculkSpreadPos(SculkSpreadManager spreadManager, ServerWorld world, BlockPos origin) {
		Chunk chunk = world.getChunk(origin);
		BlockPos.Mutable pos = origin.mutableCopy();

		while (pos.getY() >= world.getBottomY()) {
			if (chunk.getBlockState(pos).isIn(spreadManager.getReplaceableTag())) {
				pos.move(Direction.UP);

				if (chunk.getBlockState(pos).isSideSolidFullSquare(world, pos, Direction.DOWN)) {
					return null;
				}

				return pos;
			}

			pos.move(Direction.DOWN);
		}

		return null;
	}
}
