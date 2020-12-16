package io.github.haykam821.sculkprison.game.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import io.github.haykam821.sculkprison.Main;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import xyz.nucleoid.plasmid.game.world.generator.GameChunkGenerator;

public final class SculkPrisonChunkGenerator extends GameChunkGenerator {
	private static final BlockPos ORIGIN = new BlockPos(0, 64, 0);
	private static final Random RANDOM = new Random();
	private static final Identifier PRISON_STARTS_ID = new Identifier(Main.MOD_ID, "prison_starts");
	private static final int MAX_DEPTH = 16;

	private final StructureManager structureManager;
	private final DynamicRegistryManager registryManager;
	private final List<PoolStructurePiece> pieces = new ArrayList<>();

	public SculkPrisonChunkGenerator(MinecraftServer server, Structure initialStructure) {
		super(server);

		this.registryManager = server.getRegistryManager();
		this.structureManager = server.getStructureManager();

		StructurePoolBasedGenerator.method_30419(this.registryManager, new StructurePoolFeatureConfig(() -> {
			return this.registryManager.get(Registry.TEMPLATE_POOL_WORLDGEN).get(PRISON_STARTS_ID);
		}, MAX_DEPTH), PoolStructurePiece::new, this, this.structureManager, ORIGIN, this.pieces, RANDOM, false, false);
	}

	private boolean isWithinCenterChunk(BlockBox box, ChunkRegion region) {
		if (box.minX >> 4 != region.getCenterChunkX()) return false;
		if (box.minZ >> 4 != region.getCenterChunkZ()) return false;
		if (box.maxX >> 4 != region.getCenterChunkX()) return false;
		if (box.maxZ >> 4 != region.getCenterChunkZ()) return false;
		return true;
	}

	@Override
	public void generateFeatures(ChunkRegion region, StructureAccessor structures) {
		Iterator<PoolStructurePiece> iterator = this.pieces.iterator();
		while (iterator.hasNext()) {
			PoolStructurePiece piece = iterator.next();
			if (this.isWithinCenterChunk(piece.getBoundingBox(), region)) {
				piece.method_27236(region, structures, this, RANDOM, BlockBox.infinite(), ORIGIN, false);
				iterator.remove();
			}
		}
	}
}
