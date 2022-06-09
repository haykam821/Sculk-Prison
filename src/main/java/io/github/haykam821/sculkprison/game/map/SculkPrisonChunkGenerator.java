package io.github.haykam821.sculkprison.game.map;

import java.util.ArrayList;
import java.util.List;

import io.github.haykam821.sculkprison.Main;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import xyz.nucleoid.plasmid.game.world.generator.GameChunkGenerator;

public final class SculkPrisonChunkGenerator extends GameChunkGenerator {
	private static final BlockPos ORIGIN = new BlockPos(0, 64, 0);
	private static final OneDirectionRandom RANDOM = new OneDirectionRandom();
	private static final Identifier PRISON_STARTS_ID = new Identifier(Main.MOD_ID, "prison_starts");
	private static final int MAX_DEPTH = 16;

	private final StructureManager structureManager;
	private final DynamicRegistryManager registryManager;

	private final Long2ObjectMap<List<StructurePiece>> piecesByChunk = new Long2ObjectOpenHashMap<>();

	public SculkPrisonChunkGenerator(MinecraftServer server, Structure initialStructure) {
		super(server);

		this.registryManager = server.getRegistryManager();
		this.structureManager = server.getStructureManager();

		StructurePoolFeatureConfig config = new StructurePoolFeatureConfig(() -> {
			return this.registryManager.get(Registry.STRUCTURE_POOL_KEY).get(PRISON_STARTS_ID);
		}, MAX_DEPTH);

		List<StructurePiece> pieces = new ArrayList<>();
		StructurePiecesHolder holder = new StructurePiecesHolder() {
			@Override
			public void addPiece(StructurePiece piece) {
				pieces.add(piece);
			}

			@Override
			public StructurePiece getIntersecting(BlockBox box) {
				return StructureStart.getIntersecting(pieces, box);
			}
		};

		RANDOM.enforceOneDirectionNext();
		StructurePoolBasedGenerator.generate(this.registryManager, config, PoolStructurePiece::new, this, this.structureManager, ORIGIN, holder, RANDOM, false, false, new HeightLimitView() {
			@Override
			public int getBottomY() {
				return this.getBottomY();
			}

			@Override
			public int getHeight() {
				return this.getHeight();
			}
		});

		this.addStructurePieces(pieces);
	}

	private void addStructurePieces(List<StructurePiece> pieces) {
		for (StructurePiece piece : pieces) {
			BlockBox box = piece.getBoundingBox();
			int minChunkX = box.getMinX() >> 4;
			int minChunkZ = box.getMinZ() >> 4;
			int maxChunkX = box.getMaxX() >> 4;
			int maxChunkZ = box.getMaxZ() >> 4;

			for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
				for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
					long chunkPos = ChunkPos.toLong(chunkX, chunkZ);
					List<StructurePiece> piecesByChunk = this.piecesByChunk.computeIfAbsent(chunkPos, p -> new ArrayList<>());
					piecesByChunk.add(piece);
				}
			}
		}
	}

	@Override
	public void generateFeatures(ChunkRegion region, StructureAccessor structures) {
		if (this.piecesByChunk.isEmpty()) {
			return;
		}

		ChunkPos chunkPos = region.getCenterPos();
		List<StructurePiece> pieces = this.piecesByChunk.remove(chunkPos.toLong());

		if (pieces != null) {
			BlockBox chunkBox = new BlockBox(chunkPos.getStartX(), 0, chunkPos.getStartZ(), chunkPos.getEndX(), 255, chunkPos.getEndZ());
			for (StructurePiece piece : pieces) {
				piece.generate(region, structures, this, RANDOM, chunkBox, chunkPos, ORIGIN);
			}
		}
	}
}
