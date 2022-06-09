package io.github.haykam821.sculkprison.game.map;

import io.github.haykam821.sculkprison.Main;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.Structure;
import net.minecraft.util.Identifier;

public class SculkPrisonMapBuilder {
	private static final Identifier START_ID = new Identifier(Main.MOD_ID, "start");

	public SculkPrisonMap create(MinecraftServer server) {
		Structure structure = server.getStructureManager().getStructure(START_ID).orElseThrow();
		return new SculkPrisonMap(structure);
	}
}
