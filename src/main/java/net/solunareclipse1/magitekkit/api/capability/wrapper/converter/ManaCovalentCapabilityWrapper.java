package net.solunareclipse1.magitekkit.api.capability.wrapper.converter;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraftforge.common.capabilities.Capability;

import net.solunareclipse1.magitekkit.api.capability.wrapper.CovalentCapabilityWrapper;

import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.mana.IManaItem;

/**
 * Works with CovalentBridgeCapabilityWrapper to provide Botania mana
 * @author solunareclipse1
 */
public class ManaCovalentCapabilityWrapper extends CovalentCapabilityWrapper<IManaItem> implements IManaItem {
	private static final int CAPACITY_MANA = 40000;
	
	@Override
	public Capability<IManaItem> getCapability() {
		return BotaniaForgeCapabilities.MANA_ITEM;
	}

	@Override
	public int getMana() {
		if (!getState()) return 0;
		return (int) getPool();
	}

	@Override
	public int getMaxMana() {
		if (!getState()) return 0;
		return (int) getPoolMax();
	}

	@Override
	public void addMana(int mana) {
		if (mana >= 0) return;
		setPool(getPool() + mana);
	}

	@Override public boolean canReceiveManaFromPool(BlockEntity pool) {return false;}
	@Override public boolean canReceiveManaFromItem(ItemStack otherStack) {return false;}
	@Override public boolean canExportManaToPool(BlockEntity pool) {return false;}

	@Override
	public boolean canExportManaToItem(ItemStack otherStack) {
		return getState();
	}

	@Override
	public boolean isNoExport() {
		return !getState();
	}

	//@Override
	//public int getMana() {
	//	if (isActive(getStack())) {
	//		return ItemNBTHelper.getInt(getStack(), TAG_MANA, 0);
	//	}
	//	return 0;
	//}
	//
	//@Override
	//public int getMaxMana() {
	//	if (isActive(getStack())) {
	//		return CAPACITY;
	//	}
	//	return 0;
	//}
	//
	///**
	// * we should never recieve mana
	// */
	//@Override
	//public void addMana(int mana) {
	//	if (mana < 0 && isActive(getStack())) {
	//		int current = getMana();
	//		int toSet = Math.min(Math.max(current + mana, 0), getMaxMana());
	//		ItemNBTHelper.setInt(getStack(), TAG_MANA, toSet);
	//	}
	//}
	//@Override
	//public boolean canReceiveManaFromPool(BlockEntity pool) {return false;}
	//@Override
	//public boolean canReceiveManaFromItem(ItemStack otherStack) {return false;}
	//
	///**
	// * exporting to pool is silly
	// */
	//@Override
	//public boolean canExportManaToPool(BlockEntity pool) {return false;}
	//
	//@Override
	//public boolean canExportManaToItem(ItemStack otherStack) {
	//	return isActive(getStack());
	//}
	//
	//@Override
	//public boolean isNoExport() {
	//	return !isActive(getStack());
	//}
}
