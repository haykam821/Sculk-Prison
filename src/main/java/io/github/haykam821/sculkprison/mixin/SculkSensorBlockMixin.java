package io.github.haykam821.sculkprison.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.haykam821.sculkprison.game.event.WardenDataListener;
import io.github.haykam821.sculkprison.game.player.WardenData;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.stimuli.EventInvokers;
import xyz.nucleoid.stimuli.Stimuli;

@Mixin(SculkSensorBlock.class)
public class SculkSensorBlockMixin {
	@WrapOperation(method = "onSteppedOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getType()Lnet/minecraft/entity/EntityType;"))
	private EntityType<?> useWardenSculkSensorBehaviorForPlayer(Entity entity, Operation<EntityType<?>> operation) {
		if (entity instanceof ServerPlayerEntity player) {
			try (EventInvokers invokers = Stimuli.select().forEntity(player)) {
				WardenData warden = invokers.get(WardenDataListener.EVENT).getWardenData(player);

				if (warden != null) {
					return EntityType.WARDEN;
				}
			}
		}

		return operation.call(entity);
	}
}
