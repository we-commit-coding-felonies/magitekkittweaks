package net.solunareclipse1.magitekkit.common.item.curio;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.solunareclipse1.magitekkit.capability.CurioEmcBridgeCapabilityWrapper;
import net.solunareclipse1.magitekkit.capability.ManaItemCapabilityWrapper;
import net.solunareclipse1.magitekkit.common.item.MGTKItem;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.common.helper.ItemNBTHelper;

public class GemBracelet extends MGTKItem {
	
	/**
	 * This number was chosen because it is just enough to cover the most single-tick mana usage item when wearing full gem<br>
	 * Rod of the Terra Firma can, even with gem's 64% mana usage reduction, consume upwards of 38312 mana per operation
	 * <p>
	 * 40k is enough to handle that, with a bit of room for error.
	 */
	public static final long CAPACITY = 40000;
	
	public GemBracelet(Properties props) {
		super(props);
		addItemCapability(ManaItemCapabilityWrapper::new);
		addItemCapability(CurioEmcBridgeCapabilityWrapper::new);
	}
	


	/**
	 * 1:1 EMC to Mana converter, kinda
	 * <p>
	 * i live in a circus
	 * 
	 * @author solunareclipse1
	 */
	public static class KleinBridgeManaItem implements IManaItem {
		private final ItemStack stack;

		private static final String TAG_MANA = "emc_reservoir";
		
		// this is a horrible idea, im sure of it
		// i was right!
		//private ServerPlayer currentWearer;
		
		public KleinBridgeManaItem(ItemStack itemStack) {
			stack = itemStack;
		}
		
		/**
		 * separate function so that botania doesnt try to add mana itself <br>
		 * should only be used when converting EMC into mana
		 * <p>
		 * Otherwise, it functions identically to addMana()
		 * 
		 * @param amount how much emc/mana/etc to insert, negative to remove mana
		 */
		public void changeMana(int amount) {
			int current = getMana();
			int toSet = Math.min(Math.max(current + amount, 0), (int) CAPACITY);
			ItemNBTHelper.setInt(stack, TAG_MANA, toSet);
		}

		@Override
		public int getMana() {
			return ItemNBTHelper.getInt(stack, TAG_MANA, 0);
		}

		@Override
		public int getMaxMana() {
			return (int) CAPACITY;
		}

		@Override
		public void addMana(int mana) {
			if (mana < 0) { // we cannot accept mana, only provide
				int current = getMana();
				int toSet = Math.min(Math.max(current + mana, 0), (int) CAPACITY);
				ItemNBTHelper.setInt(stack, TAG_MANA, toSet);
			}
		}

		@Override
		public boolean canReceiveManaFromPool(BlockEntity pool) {
			return false; // export only
		}

		@Override
		public boolean canReceiveManaFromItem(ItemStack otherStack) {
			return false; // export only
		}

		@Override
		public boolean canExportManaToPool(BlockEntity pool) {
			return false; // wouldnt make sense if it could
		}

		@Override
		public boolean canExportManaToItem(ItemStack otherStack) {
			return true;
		}

		@Override
		public boolean isNoExport() {
			return false;
		}
	}

}
