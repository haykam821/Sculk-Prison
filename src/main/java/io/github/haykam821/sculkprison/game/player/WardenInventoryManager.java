package io.github.haykam821.sculkprison.game.player;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.plasmid.api.util.ItemStackBuilder;

public class WardenInventoryManager {
	private final ItemStack helmet;
	private final ItemStack chestplate;
	private final ItemStack leggings;
	private final ItemStack boots;

	private final ItemStack activeHelmet;

	public WardenInventoryManager(RegistryWrapper.WrapperLookup registries) {
		this.helmet = WardenInventoryManager.createArmorStack(registries, Items.LEATHER_HELMET, false);
		this.chestplate = WardenInventoryManager.createArmorStack(registries, Items.LEATHER_CHESTPLATE, false);
		this.leggings = WardenInventoryManager.createArmorStack(registries, Items.LEATHER_LEGGINGS, false);
		this.boots = WardenInventoryManager.createArmorStack(registries, Items.LEATHER_BOOTS, false);

		this.activeHelmet = WardenInventoryManager.createArmorStack(registries, Items.LEATHER_HELMET, true);
	}

	private static void updateInventory(ServerPlayerEntity player) {
		player.currentScreenHandler.sendContentUpdates();
		player.playerScreenHandler.onContentChanged(player.getInventory());
	}

	public void applyTo(ServerPlayerEntity player) {
		PlayerInventory inventory = player.getInventory();

		inventory.armor.set(3, this.helmet.copy());
		inventory.armor.set(2, this.chestplate.copy());
		inventory.armor.set(1, this.leggings.copy());
		inventory.armor.set(0, this.boots.copy());

		WardenInventoryManager.updateInventory(player);
	}

	public void applyHelmet(ServerPlayerEntity player) {
		player.getInventory().armor.set(3, this.helmet.copy());
		WardenInventoryManager.updateInventory(player);
	}

	public void applyActiveHelmet(ServerPlayerEntity player) {
		player.getInventory().armor.set(3, this.activeHelmet.copy());
		WardenInventoryManager.updateInventory(player);
	}

	private static ItemStack createArmorStack(RegistryWrapper.WrapperLookup registries, ItemConvertible item, boolean active) {
		return ItemStackBuilder.of(item)
			.addEnchantment(registries, Enchantments.BINDING_CURSE, 1)
			.setDyeColor(active ? WardenColors.ACTIVE_ARMOR : WardenColors.INACTIVE_ARMOR)
			.setUnbreakable()
			.build();
	}
}