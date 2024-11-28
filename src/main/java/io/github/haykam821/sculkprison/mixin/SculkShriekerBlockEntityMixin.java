package io.github.haykam821.sculkprison.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.haykam821.sculkprison.game.event.WardenDataListener;
import io.github.haykam821.sculkprison.game.player.WardenData;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.stimuli.EventInvokers;
import xyz.nucleoid.stimuli.Stimuli;

@Mixin(SculkShriekerBlockEntity.class)
public class SculkShriekerBlockEntityMixin {
	@ModifyReturnValue(method = "findResponsiblePlayerFromEntity", at = @At("RETURN"))
	private static ServerPlayerEntity filterWardenPlayer(ServerPlayerEntity player) {
		if (player != null) {
			try (EventInvokers invokers = Stimuli.select().forEntity(player)) {
				WardenData warden = invokers.get(WardenDataListener.EVENT).getWardenData(player);

				if (warden != null) {
					return null;
				}
			}
		}

		return player;
	}
}
