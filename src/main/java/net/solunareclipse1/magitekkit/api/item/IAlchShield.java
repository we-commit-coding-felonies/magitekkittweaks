package net.solunareclipse1.magitekkit.api.item;

import java.util.HashMap;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.util.EmcHelper;
import net.solunareclipse1.magitekkit.util.LoggerHelper;

/**
 * Block damage with EMC
 * 
 * @author solunareclipse1
 *
 */
public interface IAlchShield {



    /**
     * Checks to make sure we can even shield in the first place
     * If you want to conditionally stop shielding, do it here
     * By default it simply checks to make sure we dont block void damage or creative players
     * 
     * @param player The player being shielded
     * @param damage The amount of damage to shield
     * @param source The DamageSource we are shielding
     * @param stack The ItemStack doing the shielding
     * @return boolean specifying whether or not to shield
     */
    default boolean shieldCondition(Player player, float damage, DamageSource source, ItemStack stack) {
    	if (source.isCreativePlayer() || source == DamageSource.OUT_OF_WORLD) return false;
    	return true;
    }

    /**
     * Attempts to shield a LivingAttackEvent using an ItemStack.
     * This is what gets called by the event subscriber.
     * By default it just does some sanity checks, then offloads the rest to shieldWithEmc.
     * Recommend you dont override this unless you need to for some reason
     * 
     * @param event The LivingAttackEvent being shielded
     * @param stack The ItemStack doing the shielding
     */
	default void tryShield(LivingAttackEvent event, ItemStack stack) {	
        Entity hurt = event.getEntity();
		if (hurt.level.isClientSide || event.isCanceled()) return;
		if (shieldWithEmc((Player)hurt, event.getAmount(), event.getSource(), stack)) {
			event.setCanceled(true);
		}
	}

    /**
     * Handles sounds and EMC consumption, as well as running many of the other functions.
     * Override this if you need to change sounds or how EMC is consumed (for example, taking from the tablet instead of inventory)
     * 
     * @param player The player being shielded
     * @param damage The amount of damage to shield
     * @param source The DamageSource we are shielding
     * @param stack The ItemStack doing the shielding
     * @return If shielding was successful
     */
	default boolean shieldWithEmc(Player player, float damage, DamageSource source, ItemStack stack) {
		boolean doDebug = false;
        if (doDebug) {
        	HashMap<String,String> info = new HashMap<String,String>();
        	info.put("Player Name", player.getName().getString());
        	info.put("Player UUID", player.getStringUUID());
        	info.put("Held EMC", EmcHelper.getAvaliableEmc(player)+"");
        	info.put("Incoming Damage", damage+"");
        	info.put("Damage Source", source.getMsgId());
        	if(source.getDirectEntity() != source.getEntity() && source.getDirectEntity() != null) {
        		info.put("Source Projectile", source.getDirectEntity().getEncodeId());
            	info.put("Projectile UUID", source.getDirectEntity().getStringUUID());
            	info.put("Projectile Position", source.getDirectEntity().position().toString());
        	}
        	if (source.getEntity() != null) {
        		info.put("Source Entity", source.getEntity().getEncodeId());
            	info.put("Entity UUID", source.getEntity().getStringUUID());
            	info.put("Entity Position", source.getEntity().position().toString());
        	}
        	info.put("Will try shield", shieldCondition(player, damage, source, stack)+"");
        	info.put("EMC Cost", calcShieldingCost(player, damage, source, stack)+"");
        	info.put("Affordable damage", calcAffordableDamage(player, damage, source, stack, EmcHelper.getAvaliableEmc(player))+"");
        	
        	LoggerHelper.printDebug("IAlchShield", "ShieldingDebug", "Attempting to shieldWithEmc", info);
        }
        
		if (shieldCondition(player, damage, source, stack)) {
			if (damage <= 0) return true;
			long emcCost = calcShieldingCost(player, damage, source, stack);
			long emcHeld = EmcHelper.getAvaliableEmc(player);
			if (emcCost <= emcHeld && emcHeld > 0) {
				long emcConsumed = EmcHelper.consumeAvaliableEmc(player, emcCost);
				if (emcConsumed > emcCost) {
					player.level.playSound(null, player, EffectInit.EMC_WASTE.get(), SoundSource.PLAYERS, 0.45F, 1.0F);
				}
				
				emcHeld -= emcConsumed;
				if (emcHeld > 0) {
					player.level.playSound(null, player, EffectInit.SHIELD_PROTECT.get(), SoundSource.PLAYERS, 0.45F, 1.0F);
				} else {
					player.level.playSound(null, player, EffectInit.SHIELD_FAIL.get(), SoundSource.PLAYERS, 1.5F, 1.0F);
				}
				return true;
			} else {
				if (emcHeld <= 0) return false;
				float canAfford = calcAffordableDamage(player, damage, source, stack, emcHeld);
				EmcHelper.consumeAvaliableEmc(player, emcHeld);
				player.hurt(source, damage - canAfford);
				player.level.playSound(null, player, EffectInit.SHIELD_FAIL.get(), SoundSource.PLAYERS, 1.5F, 1.0F);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Calculates the EMC cost to shield
	 * Override this to change the cost calculation
	 * Default is Math.max(64, damage^2)
	 * 
	 * @param player Player being shielded
	 * @param damage Amount of incoming damage
	 * @param source DamageSource we are shielding
	 * @param stack ItemStack doing the shielding
     * 
	 * @return EMC cost to shield
	 */
	default long calcShieldingCost(Player player, float damage, DamageSource source, ItemStack stack) {
		return (long) Math.max(64, Math.pow(damage, 2));
		//long calcCost = (long) Math.pow(Math.max(8, damage), 2);
		//return calcCost < 64 ? Long.MAX_VALUE : calcCost;
	}
	
	/**
	 * Calculates how much damage we can afford to shield. <br>
	 * Normally, this is only called if (emcHeld < emcCost) <br>
	 * <br>
	 * Defaults to sqrt(emcHeld)
	 * 
	 * @param player Player being shielded
	 * @param damage Amount of incoming damage
	 * @param source DamageSource we are shielding
	 * @param stack ItemStack doing the shielding
	 * @param emcHeld Total EMC avaliable
	 * 
	 * @return Amount of damage we can afford
	 */
	default float calcAffordableDamage(Player player, float damage, DamageSource source, ItemStack stack, long emcHeld) {
		return (float)Math.sqrt(emcHeld);
	}
}
