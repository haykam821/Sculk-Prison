package io.github.haykam821.sculkprison.game.player;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
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
		player.playerScreenHandler.onContentChanged(player.getInventory());
	}

	public static void applyTo(ServerPlayerEntity player) {
		PlayerInventory inventory = player.getInventory();

		inventory.armor.set(3, HELMET.copy());
		inventory.armor.set(2, CHESTPLATE.copy());
		inventory.armor.set(1, LEGGINGS.copy());
		inventory.armor.set(0, BOOTS.copy());

		WardenInventoryManager.updateInventory(player);
	}

	private static ItemStack createArmorStack(ItemConvertible item) {
		return ItemStackBuilder.of(item)
			.addEnchantment(Enchantments.BINDING_CURSE, 1)
			.setDyeColor(ARMOR_COLOR)
			.setUnbreakable()
			.build();
	}
}