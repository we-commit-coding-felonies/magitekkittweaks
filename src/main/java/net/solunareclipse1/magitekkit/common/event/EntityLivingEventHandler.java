package net.solunareclipse1.magitekkit.common.event;

import java.util.Optional;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.solunareclipse1.magitekkit.common.entity.projectile.SmartArrow;
import net.solunareclipse1.magitekkit.common.item.armor.VoidArmorBase;
import net.solunareclipse1.magitekkit.common.item.armor.gem.GemJewelryBase;
import net.solunareclipse1.magitekkit.init.EffectInit;

import morph.avaritia.entity.InfinityArrowEntity;
import morph.avaritia.init.AvaritiaModContent;
import vazkii.botania.common.entity.EntityDoppleganger;

@Mod.EventBusSubscriber(modid = MagiTekkit.MODID)
public class EntityLivingEventHandler {
	
	
	/** what */
	@SubscribeEvent
	public static void livingAttacked(LivingAttackEvent event) {
		// Run the IFireProtector checks from projecte before proceeding
		PlayerEvents.onAttacked(event);
		if (event.isCanceled()) return;
		
		Entity ent = event.getEntity();
		
		// this fixes the server getting stuck in an infinite loop when hit by piercing smart arrows
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
		float dmg = event.getAmount();
		if (dmg > 0) {
			LivingEntity entity = event.getEntityLiving();
			DamageSource source = event.getSource();
			float drVal = 0, newDmg = dmg;
			for (ItemStack stack : entity.getArmorSlots()) {
				if (stack.isEmpty()) continue;
				if (stack.getItem() instanceof VoidArmorBase) {
					drVal += calcDr(stack, source, entity);
				}
			}
			newDmg *= 1 - drVal;
			if (newDmg <= 0) event.setCanceled(true); else event.setAmount(newDmg);
			
		}
	}
	
	/**
	 * Checks a DamageSource against a list of sources that should never be blocked.
	 * Specifically, it checks for the following:<br>
	 * <li>isCreativePlayer()
	 * <li>isBypassInvul()
	 * <li>OUT_OF_WORLD
	 * <li>DROWN
	 * <li>FREEZE
	 * <li>IN_WALL
	 * <li>STARVE
	 * 
	 * <br><br>TODO: this should probably be moved to somewhere in magitekkit.util at some point
	 * 
	 * @param source DamageSource to check
	 * @return true if source is unblockable
	 */
	public static boolean isUnblockableSource(DamageSource source) {
		// creative player, bypass invul, void, drown, freeze, suffocate, starve, avaritia
		return source.isCreativePlayer() || source.isBypassInvul()
				|| source == DamageSource.OUT_OF_WORLD
				|| source == DamageSource.DROWN
				|| source == DamageSource.FREEZE
				|| source == DamageSource.IN_WALL
				|| source == DamageSource.STARVE
				|| source.getDirectEntity() instanceof InfinityArrowEntity
				|| (!source.isProjectile()
						&& source.getEntity() instanceof LivingEntity lEnt
						&& lEnt.getMainHandItem().getItem() == AvaritiaModContent.INFINITY_SWORD.get());
	}
	
	/**
	 * calculates damage reduction for a specific DamageSource
	 * 
	 * @param stack the armor
	 * @param source the source
	 * @param entity the wearer
	 * @return percent damage reduction item should provide
	 */
	private static float calcDr(ItemStack stack, DamageSource source, LivingEntity entity) {
		if (isUnblockableSource(source)) return 0;
		VoidArmorBase item = (VoidArmorBase) stack.getItem();
		float drMod = 1; // 100% of the dr
		if (source.isBypassMagic()) drMod = 0.5f;
		else if (source.isBypassArmor()) drMod = 0.9f;
		entity.level.playSound(null, entity.blockPosition(), EffectInit.ARMOR_ABSORB.get(), SoundSource.PLAYERS, 0.1f, 1);
		if (item instanceof GemJewelryBase) {
			// gem jewelry always 100% dr, instead takes more dura damage
			int dmg = 1; // 1, 2 if bypass armor, 3 if bypass magic
			if (drMod < 1f) dmg = drMod < 0.9f ? 3 : 2;
			stack.hurtAndBreak(dmg, entity, ent -> {});
			//item.damageItem(stack, dmg, entity, ent -> {});
			return item.getDr(stack);
		}
		return item.getDr(stack) * drMod;
	}
}
