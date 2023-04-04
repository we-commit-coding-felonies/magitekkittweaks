package net.solunareclipse1.magitekkit.util;

import java.util.List;
import java.util.Random;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.api.distmarker.OnlyIn;

import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;

import net.solunareclipse1.magitekkit.common.misc.MGTKDmgSrc;
import net.solunareclipse1.magitekkit.init.EffectInit;

import vazkii.botania.client.fx.SparkleParticleData;

/**
 * Some common functions that don't really fit in anywhere else
 */
public class MiscHelper {
	private static final SoundEvent[] soundsList = {SoundEvents.ZOMBIFIED_PIGLIN_ANGRY, SoundEvents.CREEPER_PRIMED, SoundEvents.ENDERMAN_STARE, SoundEvents.AMBIENT_CAVE, SoundEvents.DROWNED_SHOOT, SoundEvents.ELDER_GUARDIAN_CURSE, SoundEvents.END_PORTAL_SPAWN, SoundEvents.ENDER_DRAGON_DEATH, SoundEvents.ENDER_DRAGON_FLAP, SoundEvents.ENDER_DRAGON_GROWL, SoundEvents.GHAST_AMBIENT, SoundEvents.GHAST_HURT, SoundEvents.PHANTOM_SWOOP, SoundEvents.PORTAL_AMBIENT, SoundEvents.PORTAL_TRIGGER, SoundEvents.WANDERING_TRADER_AMBIENT,
			PESoundEvents.CHARGE.get(), PESoundEvents.DESTRUCT.get(), PESoundEvents.HEAL.get(), PESoundEvents.POWER.get(), PESoundEvents.TRANSMUTE.get(), PESoundEvents.UNCHARGE.get(), PESoundEvents.WATER_MAGIC.get(), PESoundEvents.WIND_MAGIC.get(), EffectInit.EMC_WASTE.get(), EffectInit.ARMOR_BREAK.get(), EffectInit.SHIELD_FAIL.get()};
	
	public static void funnySound(Random rand, Level level, BlockPos pos) {
		level.playSound(null, pos, soundsList[rand.nextInt(soundsList.length)], SoundSource.PLAYERS, 1, 1);
	}
	
	public static void smiteSelf(Level level, ServerPlayer sPlayer) {
		LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
		if (bolt != null) {
			bolt.moveTo(Vec3.atCenterOf(sPlayer.blockPosition()));
			bolt.setCause(sPlayer);
			level.addFreshEntity(bolt);
		}
	}
	
	public static long smiteAllInArea(Level level, AABB area, ServerPlayer culprit, long plrEmc, int costPer) {
		int smitten = 0;
		for (LivingEntity ent : level.getEntitiesOfClass(LivingEntity.class, area)) {
			if (ent.is(culprit)) continue;
			if (plrEmc <= costPer*smitten) break;
			LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
			if (bolt != null) {
				bolt.moveTo(Vec3.atCenterOf(ent.blockPosition()));
				bolt.setCause(culprit);
				level.addFreshEntity(bolt);
			}
			smitten++;
		}
		return costPer*smitten;
	}
	
	public static long slowAllInArea(Level level, AABB area, ServerPlayer culprit, long plrEmc, int costPer) {
		int frozen = 0;
		for (LivingEntity ent : level.getEntitiesOfClass(LivingEntity.class, area)) {
			if (ent.is(culprit) || ent instanceof Stray) continue;
			if (plrEmc <= costPer*frozen) break;
			if (ent instanceof Skeleton skel) {
				skel.convertTo(EntityType.STRAY, true);
				WorldHelper.freezeInBoundingBox(level, ent.getBoundingBox().inflate(1), culprit, false);
			    if (!skel.isSilent()) {
			        skel.level.levelEvent((Player)null, 1048, skel.blockPosition(), 0);
			    }
				continue;
			} else if (ent instanceof Husk husk) {
				husk.convertTo(EntityType.ZOMBIE, true);
				if (!husk.isSilent()) {
					husk.level.levelEvent((Player)null, 1041, husk.blockPosition(), 0);
				}
			};
			ent.clearFire();
			ent.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 100));
			WorldHelper.freezeInBoundingBox(level, ent.getBoundingBox().inflate(1), culprit, false);
			level.playSound(null, ent, PESoundEvents.POWER.get(), SoundSource.PLAYERS, 1, 1);
			if (ent instanceof Blaze) ent.hurt(DamageSource.FREEZE, Float.MAX_VALUE);
			ent.hurt(DamageSource.FREEZE, 1);
			frozen++;
		}
		return costPer*frozen;
	}
	
	public static long burnAllInArea(Level level, AABB area, ServerPlayer culprit, long plrEmc, int costPer) {
		int burnt = 0;
		for (LivingEntity ent : level.getEntitiesOfClass(LivingEntity.class, area)) {
			if (ent.is(culprit) || ent instanceof Blaze) continue;
			if (plrEmc <= costPer*burnt) break;
			if (ent instanceof Stray stray) {
				stray.convertTo(EntityType.SKELETON, true);
				WorldHelper.freezeInBoundingBox(level, ent.getBoundingBox().inflate(1), culprit, false);
			    if (!stray.isSilent()) {
					level.playSound(null, stray, SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE, 1, 1);
			    }
				continue;
			} else if (ent instanceof Zombie zombie) {
				if (!(zombie instanceof Husk)) {
					zombie.convertTo(EntityType.HUSK, true);
					level.playSound(null, zombie, SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE, 1, 1);
				}
			};
			ent.setRemainingFireTicks(costPer);
			burnInBoundingBox(level, ent.getBoundingBox().inflate(1), culprit, false);
			level.playSound(null, ent, PESoundEvents.POWER.get(), SoundSource.PLAYERS, 1, 1);
			ent.hurt(MGTKDmgSrc.MUSTANG, 2);
			burnt++;
		}
		return costPer*burnt;
	}
	
	/**
	 * Like WorldHelper.freezeInBoundingBox(), but with fire/air instead of snow/ice
	 */
	public static void burnInBoundingBox(Level level, AABB box, Player player, boolean random) {
		for (BlockPos pos : WorldHelper.getPositionsFromBox(box)) {
			BlockState state = level.getBlockState(pos);
			Block b = state.getBlock();
			//Ensure we are immutable so that changing blocks doesn't act weird
			pos = pos.immutable();
			if (b == Blocks.WATER) {
				if (player != null) {
					PlayerHelper.checkedReplaceBlock((ServerPlayer) player, pos, Blocks.AIR.defaultBlockState());
				} else {
					level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				}
				level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1, 1);
			} else if (Block.isFaceFull(state.getCollisionShape(level, pos.below()), Direction.UP)) {
				BlockPos up = pos.above();
				BlockState stateUp = level.getBlockState(up);
				BlockState newState = null;
				
				if (stateUp.isAir()) {
					newState = Blocks.FIRE.defaultBlockState();
				}
				if (newState != null) {
					if (player != null) {
						PlayerHelper.checkedReplaceBlock((ServerPlayer) player, up, newState);
					} else {
						level.setBlockAndUpdate(up, newState);
					}
				}
			}
		}
	}
	
	public static long pokeNearby(Level level, Player player, ItemStack stack) {
		List<Entity> toAttack = level.getEntities(player, player.getBoundingBox().inflate((stack.getDamageValue()-38)/10), entity -> !entity.isSpectator() && (entity instanceof Enemy || entity instanceof LivingEntity));
		DamageSource src = DamageSource.playerAttack(player).bypassArmor();
		long consumed = 0;
		
		player.hurt(src, 1);
		for (Entity entity : toAttack) {
			entity.hurt(src, 1);
			consumed++;
		}
		level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.CHARGE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		return consumed;
	}
	
	/**
	 * Draws an AABB with particles
	 * 
	 * @param box the AABB to draw
	 * @param particle the particle to use
	 * @param stepSize lower = more particles
	 * @param level the level to put particles in
	 * @param fill if true, draws a solid box (filled with particles), instead of just an outline
	 */
	public static void drawAABBWithParticles(AABB box, ParticleOptions particle, double stepSize, ClientLevel level, boolean fill) {
		if (fill) {
    		for (double i = box.minX; i < box.maxX; i += stepSize) {
        		for (double j = box.minY; j < box.maxY; j += stepSize) {
            		for (double k = box.minZ; k < box.maxZ; k += stepSize) {
                		level.addAlwaysVisibleParticle(particle, i, j, k, 0, 0, 0);
            		}
        		}
    		}
		}
		
		for (double i = box.minX; i < box.maxX; i += stepSize) {
			level.addParticle(particle, i, box.minY, box.minZ, 0, 0, 0);
			level.addParticle(particle, i, box.minY, box.maxZ, 0, 0, 0);
		}
		for (double i = box.minY; i < box.maxY; i += stepSize) {
			level.addParticle(particle, box.minX, i, box.minZ, 0, 0, 0);
			level.addParticle(particle, box.minX, i, box.maxZ, 0, 0, 0);
		}
		for (double i = box.minZ; i < box.maxZ; i += stepSize) {
			level.addParticle(particle, box.minX, box.minY, i, 0, 0, 0);
			level.addParticle(particle, box.minX, box.maxY, i, 0, 0, 0);
		}
		for (double i = box.maxX; i > box.minX; i -= stepSize) {
			level.addParticle(particle, i, box.maxY, box.maxZ, 0, 0, 0);
			level.addParticle(particle, i, box.maxY, box.minZ, 0, 0, 0);
		}
		for (double i = box.maxY; i > box.minY; i -= stepSize) {
			level.addParticle(particle, box.maxX, i, box.maxZ, 0, 0, 0);
			level.addParticle(particle, box.maxX, i, box.minZ, 0, 0, 0);
		}
		for (double i = box.maxZ; i > box.minZ; i -= stepSize) {
			level.addParticle(particle, box.maxX, box.maxY, i, 0, 0, 0);
			level.addParticle(particle, box.maxX, box.minY, i, 0, 0, 0);
		}
	}
	
	/**
	 * Draws a line between 2 vectors using particles <br>
	 * 
	 * @param start point A
	 * @param end point B
	 * @param particle the particle to use
	 * @param stepSize lower = more particles
	 * @param level world/level particles are in
	 */
	public static void drawVectorWithParticles(Vec3 start, Vec3 end, ParticleOptions particle, double stepSize, ClientLevel level) {
		Vec3 line = end.subtract(start);
		Vec3 step = line.normalize().scale(stepSize);
		int numSteps = (int) (line.length() / step.length());
		
		Vec3 curPos = start;
		for (int i = 0; i < numSteps; i++) {
			level.addParticle(particle, true, curPos.x, curPos.y, curPos.z, 0, 0, 0);
			curPos = curPos.add(step);
		}
	}
}
