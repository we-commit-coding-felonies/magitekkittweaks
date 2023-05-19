package net.solunareclipse1.magitekkit.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import moze_intel.projecte.gameObjs.entity.EntityHomingArrow;

import net.solunareclipse1.magitekkit.common.entity.projectile.SentientArrow;
import net.solunareclipse1.magitekkit.common.entity.projectile.SmartArrow;

/**
 * Contains stuff for working with / firing projectiles
 * @author solunareclipse1
 */
public class ProjectileHelper {
	
	public static class ShootContext {
		@NotNull
		final Level level;
		
		@Nullable
		final LivingEntity shooter;
		
		@Nullable
		final Vec3 pos, rot;
		
		public ShootContext(Level level, LivingEntity shooter) {
			this.level = level;
			this.shooter = shooter;
			this.pos = null;
			this.rot = new Vec3(shooter.getXRot(), shooter.getYRot(), 0);
		}
		public ShootContext(Level level, LivingEntity shooter, Vec3 rot) {
			this.level = level;
			this.shooter = shooter;
			this.pos = null;
			this.rot = rot;
		}
		public ShootContext(Level level, Vec3 pos, Vec3 rot) {
			this.level = level;
			this.shooter = null;
			this.pos = pos;
			this.rot = rot;
		}
	}
	
	public record ArrowOptions(float damage, float velocity, float spread, byte pierce, boolean crit, Pickup pickup) {}
	
	public enum ArrowType {
		NORMAL,
		STRAIGHT,
		SMART,
		HOMING,
		SENTIENT;
		
		AbstractArrow make(ShootContext ctx, ArrowOptions opts) {
			AbstractArrow arrow;
			switch (this) {
			case STRAIGHT:
				if (ctx.shooter == null) {
					LoggerHelper.printWarn("ProjectileHelper.ArrowType.make()", "UnsupportedNullShooter", "Attempted to create arrow of type "+this.name()+" with a null shooter, but that type does not support it. Falling back on a normal arrow.");
					arrow = new Arrow(ctx.level, ctx.pos.x, ctx.pos.y, ctx.pos.z);
					arrow.setBaseDamage(opts.damage);
				} else arrow = new SmartArrow(ctx.level, ctx.shooter, opts.damage, 20, (byte) 2);
				arrow.setNoGravity(true);
				break;
			case SMART:
				if (ctx.shooter == null) {
					LoggerHelper.printWarn("ProjectileHelper.ArrowType.make()", "UnsupportedNullShooter", "Attempted to create arrow of type "+this.name()+" with a null shooter, but that type does not support it. Falling back on a normal arrow.");
					arrow = new Arrow(ctx.level, ctx.pos.x, ctx.pos.y, ctx.pos.z);
					arrow.setBaseDamage(opts.damage);
				} else arrow = new SmartArrow(ctx.level, ctx.shooter, opts.damage);
				break;
			case HOMING:
				if (ctx.shooter == null) {
					LoggerHelper.printWarn("ProjectileHelper.ArrowType.make()", "UnsupportedNullShooter", "Attempted to create arrow of type "+this.name()+" with a null shooter, but that type does not support it. Falling back on a normal arrow.");
					arrow = new Arrow(ctx.level, ctx.pos.x, ctx.pos.y, ctx.pos.z);
					arrow.setBaseDamage(opts.damage);
				} else arrow = new EntityHomingArrow(ctx.level, ctx.shooter, opts.damage);
				break;
			case SENTIENT:
				arrow = ctx.shooter == null ?
						new SentientArrow(ctx.level, ctx.pos.x, ctx.pos.y, ctx.pos.z) :
						new SentientArrow(ctx.level, ctx.shooter);
				arrow.setGlowingTag(true);
				arrow.setBaseDamage(opts.damage);
				break;
				
			default:
				arrow = ctx.shooter == null ?
						new Arrow(ctx.level, ctx.pos.x, ctx.pos.y, ctx.pos.z) :
						new Arrow(ctx.level, ctx.shooter);
				arrow.setBaseDamage(opts.damage);
				break;
			}
			if (ctx.shooter != null) {
				arrow.shootFromRotation(ctx.shooter, (float)ctx.rot.x, (float)ctx.rot.y, 0, opts.velocity, opts.spread);
			} else {
				float mx = -Mth.sin((float) (ctx.rot.y * (Math.PI / 180d))) * Mth.cos((float) (ctx.rot.x * (Math.PI / 180d)));
				float my = -Mth.sin((float) ((ctx.rot.x + ctx.rot.z) * (Math.PI / 180d)));
				float mz = Mth.cos((float) (ctx.rot.y * (Math.PI / 180d))) * Mth.cos((float) (ctx.rot.x * (Math.PI / 180d)));
				Random rand = ctx.level.random;
				Vec3 m = new Vec3(mx, my, mz).normalize().add(rand.nextGaussian() * 0.0075d * opts.spread, rand.nextGaussian() * 0.0075d * opts.spread, rand.nextGaussian() * 0.0075d * opts.spread).scale(opts.velocity);
				arrow.setDeltaMovement(m);
			}
			arrow.setPierceLevel(opts.pierce);
			arrow.setCritArrow(opts.crit);
			arrow.pickup = opts.pickup;
			return arrow;
		}
	}
	
	/**
	 * helper for shooting arrows
	 * @param amount number of arrows to shoot
	 * @param type what kind of arows to shoot
	 * @param ctx the context of shooting
	 * @param opts universal arrow settings
	 * 
	 * @return array of all arrows shot
	 */
	public static List<AbstractArrow> shootArrow(int amount, ArrowType type, ShootContext ctx, ArrowOptions opts) {
		List<AbstractArrow> arrows = new ArrayList<>(amount);
		for (int i = 0; i < amount; i++) {
			AbstractArrow a = type.make(ctx, opts);
			ctx.level.addFreshEntity(a);
			ctx.level.playSound(null, a.getX(), a.getY(), a.getZ(),
					SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
					1f, 1f / (ctx.level.getRandom().nextFloat() * 0.4f + 1.2f) + (opts.velocity/3f) * 0.5f);
			arrows.add(a);
		}
		return arrows;
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
		return summoned;
	}
}
