package net.solunareclipse1.magitekkit.util;

import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import moze_intel.projecte.gameObjs.entity.EntityHomingArrow;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;

import net.solunareclipse1.magitekkit.init.EffectInit;

/**
 * Contains an assortment of functions that do assorted things
 */
public class ShenanigansHelper {
	private static final SoundEvent[] soundsList = {SoundEvents.ZOMBIFIED_PIGLIN_ANGRY, SoundEvents.CREEPER_PRIMED, SoundEvents.ENDERMAN_STARE, SoundEvents.AMBIENT_CAVE, SoundEvents.DROWNED_SHOOT, SoundEvents.ELDER_GUARDIAN_CURSE, SoundEvents.END_PORTAL_SPAWN, SoundEvents.ENDER_DRAGON_DEATH, SoundEvents.ENDER_DRAGON_FLAP, SoundEvents.ENDER_DRAGON_GROWL, SoundEvents.GHAST_AMBIENT, SoundEvents.GHAST_HURT, SoundEvents.PHANTOM_SWOOP, SoundEvents.PORTAL_AMBIENT, SoundEvents.PORTAL_TRIGGER, SoundEvents.WANDERING_TRADER_AMBIENT,
			PESoundEvents.CHARGE.get(), PESoundEvents.DESTRUCT.get(), PESoundEvents.HEAL.get(), PESoundEvents.POWER.get(), PESoundEvents.TRANSMUTE.get(), PESoundEvents.UNCHARGE.get(), PESoundEvents.WATER_MAGIC.get(), PESoundEvents.WIND_MAGIC.get(), EffectInit.EMC_WASTE.get(), EffectInit.ARMOR_BREAK.get(), EffectInit.SHIELD_FAIL.get()};
	
	public static void funnySound(Random rand, Level level, BlockPos pos) {
		level.playSound(null, pos, soundsList[rand.nextInt(soundsList.length)], SoundSource.PLAYERS, 1, 1);
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
	 * 
	 * @return how many arrows were spawned
	 */
	public static byte fiftyTwoCardPickup(Random rand, Level level, Player player) {
		byte cost = 0;
		for (int i = rand.nextInt(52); i < 52; i++) {
			cost++;
			EntityHomingArrow arrow = new EntityHomingArrow(level, player, 2.0F);
			arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, rand.nextFloat(3), 100);
			level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.random.nextFloat() * 0.4F + 1.2F));
			level.addFreshEntity(arrow);
		}
		return cost;
	}
	
	public static void smitePlayer(Level level, ServerPlayer sPlayer) {
		LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
		if (bolt != null) {
			bolt.moveTo(Vec3.atCenterOf(sPlayer.blockPosition()));
			bolt.setCause(sPlayer);
			level.addFreshEntity(bolt);
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
}
