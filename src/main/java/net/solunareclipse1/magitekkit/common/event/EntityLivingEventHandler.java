package net.solunareclipse1.magitekkit.common.event;

import java.util.Optional;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import moze_intel.projecte.utils.PlayerHelper;
import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.api.item.IAlchShield;
import net.solunareclipse1.magitekkit.common.item.armor.VoidArmorItem;
import net.solunareclipse1.magitekkit.common.item.armor.gem.GemJewelryItemBase;

@Mod.EventBusSubscriber(modid = MagiTekkit.MODID)
public class EntityLivingEventHandler {
	
	
	@SubscribeEvent
	public static void livingAttacked(LivingAttackEvent event) {
		 // order of priority: offhand, curios, armor, inventory
		if (event.getEntity() instanceof Player player) {
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
				if (stack.getItem() instanceof VoidArmorItem) {
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
		// creative player, bypass invul, void, drown, freeze, suffocate, starve
		return (source.isCreativePlayer() || source.isBypassInvul() || source == DamageSource.OUT_OF_WORLD || source == DamageSource.DROWN || source == DamageSource.FREEZE || source == DamageSource.IN_WALL || source == DamageSource.STARVE);
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
		VoidArmorItem item = (VoidArmorItem) stack.getItem();
		float drMod = 1; // 100% of the dr
		if (source.isBypassMagic()) drMod = 0.5f;
		else if (source.isBypassArmor()) drMod = 0.9f;
		if (item instanceof GemJewelryItemBase) {
			// gem jewelry always 100% dr, instead takes more dura damage
			int dmg = 1; // 1, 2 if bypass armor, 3 if bypass magic
			if (drMod < 1) dmg = drMod == 0.5 ? 3 : 2;
			item.damageItem(stack, dmg, entity, ent -> {});
			return item.getDr(stack);
		}
		return item.getDr(stack) * drMod;
	}
	
	//        OLD
	//private static float calcDamageReduction(DamageSource src, ItemStack stack) {
	//	int currentBurnOut = stack.getOrCreateTag().getInt("pe_burnout");
	//	VoidArmorItem item = (VoidArmorItem) stack.getItem();
	//	if (src.isCreativePlayer() || src.isBypassInvul() || src == DamageSource.STARVE) return 0;
	//	float mDr = item.getDrMax();
	//	if (src.isBypassMagic()) mDr *= 0.3;
	//	if (src.isBypassArmor()) mDr *= 0.85;
	//	//if (src.isDamageHelmet()) {
	//	//	if (item.getSlot() == EquipmentSlot.HEAD) mDr *= 4; else return 0;
	//	//}
	//	if (currentBurnOut > 0) {
	//		//mDr *= 1.0f - (float) currentBurnOut / (float) item.getMaxBurnOut();
	//	}
	//	return mDr;
	//}
	//private static int calcBurnOut(DamageSource src, ItemStack stack, float blockedDamage) {
	//	VoidArmorItem item = (VoidArmorItem) stack.getItem();
	//	int currentBurnOut = stack.getOrCreateTag().getInt("pe_burnout");
	//	int toAdd = Math.max(8, Math.round(blockedDamage) + 7);
	//	
	//	if (src.isCreativePlayer() || src.isBypassInvul() || src == DamageSource.STARVE) return 0;
	//	if (src.isBypassMagic()) toAdd *= 2;
	//	if (src.isBypassArmor()) toAdd *= 16;
	//	
	//	//if (currentBurnOut + toAdd >= item.getMaxBurnOut()) return item.getMaxBurnOut();
	//	/**else**/ if (currentBurnOut < 0) return 0 + toAdd;
	//	else return currentBurnOut + toAdd;
	//}
}
