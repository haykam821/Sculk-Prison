package io.github.haykam821.sculkprison.game.map;

import java.util.Random;

/**
 * @see https://youtu.be/W-TE_Ys4iwM
 */
public class OneDirectionRandom extends Random {
	private static final long serialVersionUID = -5727974465544360780L;

	private boolean enforceOneDirection = false;

	@Override
	public int nextInt(int bound) {
		if (this.enforceOneDirection) {
			this.enforceOneDirection = false;
			return 0;
		}
		return super.nextInt(bound);
	}

	public void enforceOneDirectionNext() {
		this.enforceOneDirection = true;
	}
}
