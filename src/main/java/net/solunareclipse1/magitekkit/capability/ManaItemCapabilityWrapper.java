package net.solunareclipse1.magitekkit.capability;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraftforge.common.capabilities.Capability;

import moze_intel.projecte.capability.BasicItemCapability;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.mana.IManaItem;

public class ManaItemCapabilityWrapper extends BasicItemCapability<IManaItem> implements IManaItem {

	/**
	 * this is likely probably certainly extremely bad, stupid, dumb, and will likely backfire later
	 */
	protected IManaItem manaItem = null;
	
	@Override
	public Capability<IManaItem> getCapability() {
		return BotaniaForgeCapabilities.MANA_ITEM;
	}

	@Override
	public int getMana() {
		if (manaItem == null) return 0;
		return manaItem.getMana();
	}

	@Override
	public int getMaxMana() {
		if (manaItem == null) return 0;
		return manaItem.getMaxMana();
	}

	@Override
	public void addMana(int mana) {
		if (manaItem == null) return;
		manaItem.addMana(mana);
	}

	@Override
	public boolean canReceiveManaFromPool(BlockEntity pool) {
		if (manaItem == null) return false;
		return manaItem.canReceiveManaFromPool(pool);
	}

	@Override
	public boolean canReceiveManaFromItem(ItemStack otherStack) {
		if (manaItem == null) return false;
		return manaItem.canReceiveManaFromItem(otherStack);
	}

	@Override
	public boolean canExportManaToPool(BlockEntity pool) {
		if (manaItem == null) return false;
		return manaItem.canExportManaToPool(pool);
	}

	@Override
	public boolean canExportManaToItem(ItemStack otherStack) {
		if (manaItem == null) return false;
		return manaItem.canExportManaToItem(otherStack);
	}

	@Override
	public boolean isNoExport() {
		if (manaItem == null) return true;
		return manaItem.isNoExport();
	}
}