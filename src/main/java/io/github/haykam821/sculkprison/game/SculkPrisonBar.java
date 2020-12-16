package io.github.haykam821.sculkprison.game;

import io.github.haykam821.sculkprison.game.phase.SculkPrisonActivePhase;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xyz.nucleoid.plasmid.widget.BossBarWidget;
import xyz.nucleoid.plasmid.widget.GlobalWidgets;

public class SculkPrisonBar implements AutoCloseable {
	private static final Text CAGE_LOCKED_TEXT = new TranslatableText("text.sculkprison.cage_locked");
	private static final Text CAGE_UNLOCKED_TEXT = new TranslatableText("text.sculkprison.cage_unlocked");

	private final BossBarWidget bar;

	public SculkPrisonBar(GlobalWidgets widgets) {
		this.bar = widgets.addBossBar(CAGE_LOCKED_TEXT, BossBar.Color.BLUE, BossBar.Style.PROGRESS);
	}

	public void tick(SculkPrisonActivePhase phase) {
		this.bar.setProgress(phase.getBarProgress());
	}

	public void changeToSurvive() {
		this.bar.setTitle(CAGE_UNLOCKED_TEXT);
		this.bar.setStyle(BossBar.Color.GREEN, BossBar.Style.PROGRESS);
	}

	@Override
	public void close() {
		this.bar.close();
	}
}
