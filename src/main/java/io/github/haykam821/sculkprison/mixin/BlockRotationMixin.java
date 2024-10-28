package io.github.haykam821.sculkprison.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.haykam821.sculkprison.game.map.ForcedBlockRotationRandom;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.random.Random;

@Mixin(BlockRotation.class)
public class BlockRotationMixin {
	@Inject(method = "random", at = @At("HEAD"), cancellable = true)
	private static void enforceBlockRotation(Random random, CallbackInfoReturnable<BlockRotation> ci) {
		if (random instanceof ForcedBlockRotationRandom forcedBlockRotationRandom) {
			ci.setReturnValue(forcedBlockRotationRandom.consumeRotation());
		}
	}
}
