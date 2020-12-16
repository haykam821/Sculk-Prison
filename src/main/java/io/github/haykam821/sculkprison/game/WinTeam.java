package io.github.haykam821.sculkprison.game;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public enum WinTeam {
	PLAYERS(new TranslatableText("text.sculkprison.win.players").formatted(Formatting.YELLOW)),
	WARDEN(new TranslatableText("text.sculkprison.win.warden").formatted(Formatting.DARK_AQUA));

	private final Text name;

	private WinTeam(Text name) {
		this.name = name;
	}
	
	public Text getWinMessage() {
		return new TranslatableText("text.sculkprison.win", this.name).formatted(Formatting.GOLD);
	}
}
