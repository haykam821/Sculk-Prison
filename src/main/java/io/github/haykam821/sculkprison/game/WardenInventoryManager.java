package io.github.haykam821.sculkprison.game;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

public class WardenInventoryManager {
	private static final int ARMOR_COLOR = 0x649683;
	public static final ItemStack HELMET = WardenInventoryManager.createArmorStack(Items.LEATHER_HELMET);
	public static final ItemStack CHESTPLATE = WardenInventoryManager.createArmorStack(Items.LEATHER_CHESTPLATE);
	public static final ItemStack LEGGINGS = WardenInventoryManager.createArmorStack(Items.LEATHER_LEGGINGS);
	public static final ItemStack BOOTS = WardenInventoryManager.createArmorStack(Items.LEATHER_BOOTS);

	private static void updateInventory(ServerPlayerEntity player) {
		player.currentScreenHandler.sendContentUpdates();
		player.playerScreenHandler.onContentChanged(player.inventory);
		player.updateCursorStack();
	}

	public static void applyTo(ServerPlayerEntity player) {
		player.inventory.armor.set(3, HELMET.copy());
		player.inventory.armor.set(2, CHESTPLATE.copy());
		player.inventory.armor.set(1, LEGGINGS.copy());
		player.inventory.armor.set(0, BOOTS.copy());

		WardenInventoryManager.updateInventory(player);
	}

	private static ItemStack createArmorStack(ItemConvertible item) {
		return ItemStackBuilder.of(item)
			.addEnchantment(Enchantments.BINDING_CURSE, 1)
			.setColor(ARMOR_COLOR)
			.setUnbreakable()
			.build();
	}
}