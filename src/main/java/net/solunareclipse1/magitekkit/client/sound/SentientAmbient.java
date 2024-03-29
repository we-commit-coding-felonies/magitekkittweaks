package net.solunareclipse1.magitekkit.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import net.solunareclipse1.magitekkit.common.entity.projectile.SentientArrow;
import net.solunareclipse1.magitekkit.init.EffectInit;

/**
 * spooooooky
 * @author solunareclipse1
 */
public class SentientAmbient extends AbstractTickableSoundInstance {
	// sound lasts for 392.4 ticks before looping (at normal speed/pitch)
	private final Entity entity;
	private float step = 0;
	private float nextPitch = 1;
	private int maxPitchChangeTime = 0;

	public SentientAmbient(Entity entity) {
		super(EffectInit.ARCHANGELS_SENTIENT_AMBIENT.get(), SoundSource.NEUTRAL);
		this.entity = entity;
		this.looping = true;
		this.delay = 0;
	}

	public SentientAmbient(Entity entity, int maxPitchChangeTime) {
		super(EffectInit.ARCHANGELS_SENTIENT_AMBIENT.get(), SoundSource.NEUTRAL);
		this.entity = entity;
		this.looping = true;
		this.delay = 0;
		this.maxPitchChangeTime = maxPitchChangeTime;
	}

	public void tick() {
		if (mustCease()) cease();
		else {
			// borked
			//if (entity instanceof SentientArrow arrow) {
			//	if (arrow.hasTarget()) {
			//		this.x = arrow.getTarget().getX();
			//		this.y = arrow.getTarget().getY();
			//		this.z = arrow.getTarget().getZ();
			//	} else {
			//		this.x = entity.getX();
			//		this.y = entity.getY();
			//		this.z = entity.getZ();
			//	}
			//}
			this.x = entity.getX();
			this.y = entity.getY();
			this.z = entity.getZ();
			// cool pitch-changing over time
			if (maxPitchChangeTime > 0) {
				if (step == 0) {
					step = (nextPitch - pitch)/entity.level.random.nextInt(1, maxPitchChangeTime+1);
				}
				pitch += step;
				// checks for overshoots, just in case
				if ( pitch == nextPitch || (step > 0 && pitch > nextPitch) || (step < 0 && pitch < nextPitch) ) {
					nextPitch = entity.level.random.nextFloat(0.1f, 2f);
					step = 0;
				}
			}
		}
	}
	
	protected void cease() {
		this.stop();
	}
	
	protected boolean mustCease() {
		return entity.isRemoved()
				|| (entity instanceof SentientArrow arrow && arrow.isInert());
	}
}