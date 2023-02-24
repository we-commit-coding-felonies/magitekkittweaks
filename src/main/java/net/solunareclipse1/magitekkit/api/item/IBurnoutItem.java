package net.solunareclipse1.magitekkit.api.item;

import net.minecraft.world.item.ItemStack;

/**
 * IBurnoutItem provides an alternative durability system. <br>
 * Default NBT tag is "burnout", stored as int. <br>
 * Minimum burnout is always 0. <br>
 *  <br>
 * TODO: documentation
 */
public interface IBurnoutItem {
	
	/**
	 * Gets the current amount of burnout.
	 * 
	 * @param stack The itemstack to check
	 * @return the amount
	 */
	default int getBurnout(ItemStack stack) {
		return stack.getOrCreateTag().getInt("burnout");
	}
	
	/**
	 * Sets the burnout of an item to a specific amount.
	 * Value is clamped to 0 < amount < max.
	 * 
	 * @param stack the stack to change
	 * @param amount amount of burnout
	 */
	default void setBurnout(ItemStack stack, int amount) {
		stack.getOrCreateTag().putInt("burnout", Math.max(0, Math.min(getBurnoutMax(), amount)));
	}
	
	/**
	 * Gets the current amount of burnout, clamped.
	 * Can optionally update the itemstack with the clamped amount.
	 * 
	 * @param stack The itemstack to check
	 * @param write If true, will also modify the stack with the clamped value
	 * @return burnout amount, clamped to (0 < amount < max)
	 */
	default int checkBurnout(ItemStack stack, boolean write) {
		int amount = Math.max(0, Math.min(getBurnoutMax(), stack.getOrCreateTag().getInt("burnout")));
		if (write) setBurnout(stack, amount);
		return amount;
	}
	
	/**
	 * Define the maximum amount of burnout here.
	 * Should always be > 0 to avoid issues
	 * 
	 * @return The max burnout
	 */
	int getBurnoutMax();
	
	/**
	 * Gets the current amount of burnout, as a percentage of the maximum
	 * 
	 * @param stack The itemstack to check
	 * @return Percentage expressed as a number between 0 and 1
	 */
	default float getBurnoutPercent(ItemStack stack) {
		return Math.max(0.0f, Math.min(1.0f, getBurnout(stack) / getBurnoutMax()));
	}
}
