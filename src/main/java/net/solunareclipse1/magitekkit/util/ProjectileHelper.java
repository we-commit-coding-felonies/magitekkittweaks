package net.solunareclipse1.magitekkit.util;

import java.util.Random;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;

import moze_intel.projecte.gameObjs.entity.EntityHomingArrow;

/**
 * Contains stuff for working with / firing projectiles
 * @author solunareclipse1
 *
 */
public class ProjectileHelper {
	
	/**
	 * Helper for shooting arrows <br>
	 * contains way too goddamn many options
	 * 
	 * @param level The level to shoot in
	 * @param shooter who is shooting the arrow
	 * @param damage the base damage of the arrow
	 * @param vel the velocity of the arrow
	 * @param spread inaccuracy
	 * @param pierce level of pierce enchant
	 * @param crit if the arrow has crit particles
	 * @param homing if true, arrow will be a ProjectE homing arrow
	 * @param pickup pickupability of arrow
	 */
	public static void shootArrow(Level level, LivingEntity shooter, float damage, float vel, float spread, byte pierce, boolean crit, boolean homing, Pickup pickup) {
		Arrow arrow = null;
		if (homing) {
			arrow = new EntityHomingArrow(level, shooter, damage);
		}
		else {
			arrow = new Arrow(level, shooter);
			arrow.setBaseDamage(damage);
		}
		arrow.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0, vel, spread);
		arrow.setPierceLevel(pierce);
		arrow.setCritArrow(crit);
		arrow.pickup = pickup;
		
		level.addFreshEntity(arrow);
		level.playSound(null, shooter, SoundEvents.ARROW_SHOOT, shooter.getSoundSource(), 1.0F, Math.min(2, 1.0F / (level.random.nextFloat() * 0.4F + 1.2F) + (vel / 3) * 0.5F));
	}

	/**
	 * 
	 * 
	 * @param level The level to shoot in
	 * @param shooter who is shooting the arrow
	 * @param damage the base damage of the arrow
	 * @param vel the velocity of the arrow
	 * @param spread inaccuracy
	 * @param pierce level of pierce enchant
	 * @param crit if the arrow has crit particles
	 * @param homing if true, arrow will be a ProjectE homing arrow
	 * @param pickup pickupability of arrow <br><br>
	 * @param effects MobEffectInstance(s) to apply to this arrow
	 */
	public static void shootArrowTipped(Level level, LivingEntity shooter, float damage, float vel, float spread, byte pierce, boolean crit, boolean homing, Pickup pickup, MobEffectInstance... effects) {
		Arrow arrow = null;
		if (homing) {
			arrow = new EntityHomingArrow(level, shooter, damage);
		}
		else {
			arrow = new Arrow(level, shooter);
			arrow.setBaseDamage(damage);
		}
		
		arrow.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0, vel, spread);
		arrow.setPierceLevel(pierce);
		arrow.setCritArrow(crit);
		arrow.pickup = pickup;
		
		for (MobEffectInstance effect : effects) {
			arrow.addEffect(effect);
		}
		
		level.addFreshEntity(arrow);
		level.playSound(null, shooter, SoundEvents.ARROW_SHOOT, shooter.getSoundSource(), 1.0F, Math.min(2, 1.0F / (level.random.nextFloat() * 0.4F + 1.2F) + (vel / 3) * 0.5F));
	}
	
	/**
	 * The classic card game to play with your friends.
	 * Now with heat-seeking arrows of death!
	 * <p>
	 * Shoots a random amount of homing arrows in all directions
	 * 
	 * @param rand Random to use
	 * @param level The level to shoot in
	 * @param player The player shooting
	 * @param homing If false, "normal" arrows will be spawned instead
	 * 
	 * @return how many arrows were spawned
	 */
	public static byte fiftyTwoCardPickup(Random rand, Level level, Player player, boolean homing) {
		byte summoned = 0;
		for (int i = rand.nextInt(52); i < 52; i++) {
			summoned++;
			if (homing) {
				EntityHomingArrow arrow = new EntityHomingArrow(level, player, 2.0F);
				arrow.shootFromRotation(player, 0, 0, 0, rand.nextFloat(3), 400);
				level.addFreshEntity(arrow);
				level.playSound(null, player, SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 1.2F));
			} else {
				if (summoned == 1) i = 0;
				Arrow arrow = new Arrow(level, player);
				arrow.setBaseDamage(20);
				arrow.pickup = Pickup.CREATIVE_ONLY;
				arrow.setCritArrow(true);
				arrow.shootFromRotation(player, 0, 0, 0, rand.nextFloat(25), 400);
				level.addFreshEntity(arrow);
				level.playSound(null, player, SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 2.5F / (rand.nextFloat() * 0.4F + 1.2F));
			}
		}
		//System.out.println(summoned);
		return summoned;
	}
}
