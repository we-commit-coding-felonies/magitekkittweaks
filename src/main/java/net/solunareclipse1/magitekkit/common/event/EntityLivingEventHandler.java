package net.solunareclipse1.magitekkit.common.event;

import java.util.Optional;

import net.minecraft.nbt.CompoundTag;
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
import net.solunareclipse1.magitekkit.common.item.armor.AlchemicalArmorItem;

@Mod.EventBusSubscriber(modid = MagiTekkit.MODID)
public class EntityLivingEventHandler {
	@SubscribeEvent
	public static void livingAttacked(LivingAttackEvent event) {
		if (event.getEntity() instanceof Player player) {
			if (player.getOffhandItem().getItem() instanceof IAlchShield shieldItem) {
				shieldItem.tryShield(event, player.getOffhandItem());
				if (event.isCanceled()) return;
			}
			
			IItemHandler curios = PlayerHelper.getCurios(player);
			if (curios != null) {
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
			LivingEntity ent = event.getEntityLiving();
			DamageSource src = event.getSource();
			float drVal = 0, newDmg = dmg;
			for (ItemStack stack : ent.getArmorSlots()) {
				if (stack.isEmpty()) continue;
				if (stack.getItem() instanceof AlchemicalArmorItem) {
					CompoundTag stackTag = stack.getOrCreateTag();
					float calcDrVal = calcDamageReduction(src, stack);
					stackTag.putInt("pe_burnout", calcBurnOut(src, stack, dmg * calcDrVal));
					drVal += calcDrVal;
					if (drVal >= 1) {
						event.setCanceled(true);
						return;
					}
				}
			}
			newDmg *= 1 - drVal;
			if (newDmg <= 0) event.setCanceled(true); else event.setAmount(newDmg);
			
		}
	}
	private static float calcDamageReduction(DamageSource src, ItemStack stack) {
		int currentBurnOut = stack.getOrCreateTag().getInt("pe_burnout");
		AlchemicalArmorItem item = (AlchemicalArmorItem) stack.getItem();
		if (src.isCreativePlayer() || src.isBypassInvul() || src == DamageSource.STARVE) return 0;
		float mDr = item.getDrAmount();
		if (src.isBypassMagic()) mDr *= 0.3;
		if (src.isBypassArmor()) mDr *= 0.85;
		//if (src.isDamageHelmet()) {
		//	if (item.getSlot() == EquipmentSlot.HEAD) mDr *= 4; else return 0;
		//}
		if (currentBurnOut > 0) {
			mDr *= 1.0f - (float) currentBurnOut / (float) item.getMaxBurnOut();
		}
		return mDr;
	}
	private static int calcBurnOut(DamageSource src, ItemStack stack, float blockedDamage) {
		AlchemicalArmorItem item = (AlchemicalArmorItem) stack.getItem();
		int currentBurnOut = stack.getOrCreateTag().getInt("pe_burnout");
		int toAdd = Math.max(8, Math.round(blockedDamage) + 7);
		
		if (src.isCreativePlayer() || src.isBypassInvul() || src == DamageSource.STARVE) return 0;
		if (src.isBypassMagic()) toAdd *= 2;
		if (src.isBypassArmor()) toAdd *= 16;
		
		if (currentBurnOut + toAdd >= item.getMaxBurnOut()) return item.getMaxBurnOut();
		else if (currentBurnOut < 0) return 0 + toAdd;
		else return currentBurnOut + toAdd;
	}
}
