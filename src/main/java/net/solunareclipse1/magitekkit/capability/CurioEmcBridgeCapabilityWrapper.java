package net.solunareclipse1.magitekkit.capability;

import net.minecraft.world.entity.player.Player;
import top.theillusivec4.curios.api.SlotContext;
import moze_intel.projecte.integration.curios.CurioItemCapability;

import net.solunareclipse1.magitekkit.common.item.curio.CovalenceBracelet.KleinBridgeManaItem;
import net.solunareclipse1.magitekkit.util.EmcHelper;

import vazkii.botania.common.helper.ItemNBTHelper;

/**
 * Designed specifically for use with GemBraceletItem
 * <p>
 * who exactly thought capabilities were a good idea, again?
 * 
 * @author solunareclipse1
 */
public class CurioEmcBridgeCapabilityWrapper extends CurioItemCapability {
	
	// bruh
	protected KleinBridgeManaItem kleinManaItem = null;
	
	// doing this here because we need to mess with the stacks NBT
	// cant figure out a way to do it in the item class itself so /shrug
	@Override
	public void curioTick(SlotContext ctx) {
		if (kleinManaItem != null) {
			int stored = ItemNBTHelper.getInt(getStack(), "emc_reservoir", 0);
			if (ctx.entity() instanceof Player && stored < kleinManaItem.getMaxMana()) {
				Player player = (Player) ctx.entity();
				int amount = (int) EmcHelper.consumeAvaliableEmc(player, kleinManaItem.getMaxMana() - stored); //;
				kleinManaItem.changeMana(amount);
			}
		}
		//getItem().curioTick(ctx);
	}

	//@Override
	//public void onEquip(SlotContext ctx, ItemStack prevStack) {
	//	getItem().onEquip(ctx, prevStack);
	//}
	//
	//@Override
	//public void onUnequip(SlotContext ctx, ItemStack newStack) {
	//	getItem().onUnequip(ctx, newStack);
	//}
	//
	//@Override
	//public boolean canEquip(SlotContext ctx) {
	//	return getItem().canEquip(ctx);
	//}
	//
	//@Override
	//public boolean canUnequip(SlotContext ctx) {
	//	return getItem().canUnequip(ctx);
	//}
	//
	//@Override
	//public List<Component> getSlotsTooltip(List<Component> tooltips) {
	//	return getItem().getSlotsTooltip(tooltips);
	//}
	//
	//@Override
	//public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext ctx, UUID uuid) {
	//	return getItem().getAttributeModifiers(ctx, uuid);
	//}
	//
	//@Override
	//public void onEquipFromUse(SlotContext ctx) {
	//	getItem().onEquipFromUse(ctx);
	//}
	//
	//@Override
	//@Nonnull
	//public SoundInfo getEquipSound(SlotContext ctx) {
	//	return getItem().getEquipSound(ctx);
	//}
	//
	//@Override
	//public boolean canEquipFromUse(SlotContext ctx) {
	//	return getItem().canEquipFromUse(ctx);
	//}
	//
	//@Override
	//public void curioBreak(SlotContext ctx) {
	//	getItem().curioBreak(ctx);
	//}
	//
	//@Override
	//public boolean canSync(SlotContext ctx) {
	//	return getItem().canSync(ctx);
	//}
	//
	//@Override
	//@Nullable
	//public CompoundTag writeSyncData(SlotContext ctx) {
	//	return getItem().writeSyncData(ctx);
	//}
	//
	//@Override
	//public void readSyncData(SlotContext ctx, CompoundTag compound) {
	//	getItem().readSyncData(ctx, compound);
	//}
	//
	//@Override
	//@Nonnull
	//public DropRule getDropRule(SlotContext ctx, DamageSource source, int lootingLevel, boolean recentlyHit) {
	//	return getItem().getDropRule(ctx, source, lootingLevel, recentlyHit);
	//}
	//
	//@Override
	//public List<Component> getAttributesTooltip(List<Component> tooltips) {
	//	return getItem().getAttributesTooltip(tooltips);
	//}
	//
	//@Override
	//public int getFortuneLevel(SlotContext ctx, @Nullable LootContext lootContext) {
	//	return getItem().getFortuneLevel(ctx, lootContext);
	//}
	//
	//@Override
	//public int getLootingLevel(SlotContext ctx, DamageSource source, LivingEntity target, int baseLooting) {
	//	return getItem().getLootingLevel(ctx, source, target, baseLooting);
	//}
	//
	//@Override
	//public boolean makesPiglinsNeutral(SlotContext ctx) {
	//	return getItem().makesPiglinsNeutral(ctx);
	//}
	//
	//@Override
	//public boolean isEnderMask(SlotContext ctx, EnderMan enderMan) {
	//	return getItem().isEnderMask(ctx, enderMan);
	//}
}
