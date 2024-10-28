package io.github.haykam821.sculkprison.game.event;

import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public interface CheckWardenListener {
	public StimulusEvent<CheckWardenListener> EVENT = StimulusEvent.create(CheckWardenListener.class, context -> {
		return entity -> {
			try {
				for (CheckWardenListener listener : context.getListeners()) {
					if (listener.isWarden(entity)) {
						return true;
					}
				}
			} catch (Throwable throwable) {
				context.handleException(throwable);
			}

			return false;
		};
	});

	public boolean isWarden(ServerPlayerEntity entity);
}
