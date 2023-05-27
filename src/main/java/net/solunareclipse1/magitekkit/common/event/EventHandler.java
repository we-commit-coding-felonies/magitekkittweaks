package net.solunareclipse1.magitekkit.common.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import moze_intel.projecte.events.PlayerEvents;
import moze_intel.projecte.utils.PlayerHelper;
import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.api.item.IAlchShield;
import net.solunareclipse1.magitekkit.api.item.IDamageReducer;
import net.solunareclipse1.magitekkit.common.entity.projectile.SmartArrow;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.util.ColorsHelper.Color;

import morph.avaritia.entity.GapingVoidEntity;
import morph.avaritia.entity.InfinityArrowEntity;
import morph.avaritia.init.AvaritiaModContent;
import vazkii.botania.common.entity.EntityDoppleganger;
import vazkii.botania.common.entity.EntityManaStorm;

/**
 * does stuff with events that doesnt fit very well into other places
 * @author solunareclipse1
 */
@Mod.EventBusSubscriber(modid = MagiTekkit.MODID)
public class EventHandler {
	
	
	/** what */
	@SubscribeEvent
	public static void livingAttacked(LivingAttackEvent event) {
		// Run the IFireProtector checks from projecte before proceeding
		PlayerEvents.onAttacked(event);
		if (event.isCanceled()) return;
		
		Entity ent = event.getEntity();
		
		// this fixes the server getting stuck in an infinite loop when hit by piercing smart arrows
		// TODO: check if this is actually necessary
		if (ent instanceof EntityDoppleganger gaia && gaia.getInvulTime() > 0) {
			if (event.getSource().getDirectEntity() instanceof SmartArrow arrow) {
				arrow.becomeInert();
				arrow.expire();
				event.setCanceled(true);
				return;
			}
		}
		
		 // order of priority: offhand, curios, armor, inventory
		if (ent instanceof Player player) {
			if (player.getOffhandItem().getItem() instanceof IAlchShield shieldItem) {
				shieldItem.tryShield(event, player.getOffhandItem());
				if (event.isCanceled()) return;
			}
			
			IItemHandler curios = PlayerHelper.getCurios(player);
			if (curios != null) { // does is curios?
				for (int i = 0; i < curios.getSlots(); i++) {
					ItemStack stack = curios.getStackInSlot(i);
					if (stack.getItem() instanceof IAlchShield shieldItem) {
						shieldItem.tryShield(event, stack);
						if (event.isCanceled()) return;
					}
				}
			}
			
			for (ItemStack stack : player.getArmorSlots()) {
				if (stack.getItem() instanceof IAlchShield shieldItem) {
					shieldItem.tryShield(event, stack);
					if (event.isCanceled()) return;
				}
			}
			
			Optional<IItemHandler> itemHandlerCap = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
			if (itemHandlerCap.isPresent()) {
				IItemHandler inv = itemHandlerCap.get();
				for (int i = 0; i < inv.getSlots(); i++) {
					ItemStack stack = inv.getStackInSlot(i);
					if (stack.getItem() instanceof IAlchShield shieldItem) {
						shieldItem.tryShield(event, stack);
						if (event.isCanceled()) return;
					}
				}
			}
			
		}
	}
	
	@SubscribeEvent
	public static void livingHurt(LivingHurtEvent event) {
		float damage = event.getAmount();
		if (damage > 0) {
			LivingEntity entity = event.getEntityLiving();
			DamageSource source = event.getSource();
			Map<ItemStack, Float> absorbList = new HashMap<>();
			float totalDr = 0;
			for (ItemStack stack : entity.getArmorSlots()) {
				if (!stack.isEmpty() && stack.getItem() instanceof IDamageReducer drItem) {
					float dr = drItem.getDr(stack, source);
					totalDr += dr;
					absorbList.put(stack, dr);
				}
			}
			if (totalDr > 0) {
				if (totalDr >= 1) {
					event.setCanceled(true);
				} else {
					event.setAmount(damage * (1f-totalDr));
				}
				entity.level.playSound(null, entity, EffectInit.ARMOR_ABSORB.get(), entity.getSoundSource(), Math.min(1, totalDr), 1);
				for (Entry<ItemStack, Float> absorber : absorbList.entrySet()) {
					ItemStack stack = absorber.getKey();
					float absorbed = absorber.getValue()*event.getAmount();
					stack.hurtAndBreak(Math.round(absorbed), entity, ent -> {
						armorBreak(stack, ent);
					});
				}
			}
		}
	}
	
	private static void armorBreak(ItemStack stack, LivingEntity entity) {
		entity.broadcastBreakEvent(LivingEntity.getEquipmentSlotForItem(stack));
	}
	
	@SubscribeEvent
	public static void entityLeave(EntityLeaveWorldEvent event) {
		// this is related to the Gem Amulet singularity explosion
		// we spawn an avaritia black hole when the mana storm dies because its cool
		// we mark mana storms to do this based on their burst color
		if (event.getEntity() instanceof EntityManaStorm singularity
				&& singularity.getRemovalReason() == Entity.RemovalReason.DISCARDED
				&& singularity.burstColor == Color.COVALENCE_BLUE.I) {
			Level level = event.getWorld();
			GapingVoidEntity blackHole = new GapingVoidEntity(level);
			blackHole.setPos(singularity.getX(), singularity.getY(), singularity.getZ());
			blackHole.setYRot(singularity.getYRot());
			blackHole.setXRot(0);
			level.addFreshEntity(blackHole);
		}
	}
}
