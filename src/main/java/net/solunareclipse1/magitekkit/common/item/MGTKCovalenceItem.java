package net.solunareclipse1.magitekkit.common.item;

import net.minecraft.world.item.ItemStack;

import net.solunareclipse1.magitekkit.api.capability.wrapper.CovalentCapabilityWrapper;
import net.solunareclipse1.magitekkit.api.capability.wrapper.converter.ManaCovalentCapabilityWrapper;

import wayoftime.bloodmagic.api.compat.EnumDemonWillType;
import wayoftime.bloodmagic.api.compat.IDemonWill;
import wayoftime.bloodmagic.api.compat.IDemonWillGem;

/**
 * this entire system is a mess
 * @author solunareclipse1
 */
public class MGTKCovalenceItem extends MGTKItem implements IDemonWillGem {
	public MGTKCovalenceItem(Properties props) {
		super(props);
		addItemCapability(ManaCovalentCapabilityWrapper::new);
	}
	
	protected boolean covalenceActive(ItemStack stack) {
		return CovalentCapabilityWrapper.getState(stack);
	}

	@Override
	public ItemStack fillDemonWillGem(ItemStack gemStack, ItemStack soulStack) {
		// no inserting!
		//if (covalenceActive(gemStack) && soulStack != null && soulStack.getItem() instanceof IDemonWill soul) {
		//	EnumDemonWillType type = soul.getType(soulStack);
		//	double held = getWill(type, gemStack);
		//	double toConsume = Math.min(getMaxWill(type, gemStack)-held, soul.getWill(type, soulStack));
		//	if (toConsume > 0) {
		//		double consumed = soul.drainWill(type, soulStack, toConsume);
		//		setWill(type, gemStack, held + consumed);
		//		
		//		if (soul.getWill(type, soulStack) <= 0) {
		//			return ItemStack.EMPTY;
		//		}
		//	}
		//}
		return soulStack;
	}

	@Override
	public double getWill(EnumDemonWillType type, ItemStack stack) {
		return covalenceActive(stack) ? CovalentCapabilityWrapper.getPool(stack) : 0;
	}
	
	@Override
	public void setWill(EnumDemonWillType type, ItemStack stack, double amount) {
		if (amount > getWill(type, stack)) return;
		if (covalenceActive(stack)) {
			CovalentCapabilityWrapper.setPool(stack, (long)amount);
		}
	}

	@Override
	public int getMaxWill(EnumDemonWillType type, ItemStack stack) {
		return covalenceActive(stack) ? (int)CovalentCapabilityWrapper.getPoolMax(stack) : 0;
	}

	@Override
	public double drainWill(EnumDemonWillType type, ItemStack stack, double requested, boolean exec) {
		if (covalenceActive(stack)) {
			double held = getWill(type, stack);
			double toConsume = Math.min(requested, held);

			if (exec) {
				setWill(type, stack, held - toConsume);
			}

			return toConsume;
		}
		return 0;
	}

	@Override
	public double fillWill(EnumDemonWillType type, ItemStack stack, double requested, boolean exec) {
		// insertion is not allowed
		//if (covalenceActive(stack)) {
		//	double current = getWill(type, stack);
		//	double toInsert = Math.min(requested, getMaxWill(type, stack)-current);
		//	if (exec) {
		//		setWill(type, stack, current+toInsert);
		//	}
		//	return toInsert;
		//}
		return 0;
	}

}
