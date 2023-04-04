package net.solunareclipse1.magitekkit.api.capability.wrapper;

import net.minecraft.util.Mth;
import moze_intel.projecte.capability.BasicItemCapability;
import vazkii.botania.common.helper.ItemNBTHelper;

/**
 * Covalent items can convert between EMC and other magic systems (such as botanias mana)<br>
 * Does so by having a shared internal pool of EMC (different from the normal StoredEMC) <br>
 * <b>Conversion should be one-way (EMC -> other) only!</b>
 * <p>
 * Extend this to define a new conversion that you can add to items
 * 
 * @author solunareclipse1
 */
public abstract class CovalentCapabilityWrapper<T> extends BasicItemCapability<T> {
	private static final long CAPACITY_EMC = 40000;
	public static final String TAG_POOL = "cov_bridge_pool";
	public static final String TAG_STATE = "cov_bridge_state";
	
	/**
	 * Gets the current state of this item (true or false) <br>
	 * False means the item is disabled, and it should do nothing
	 * @return State of the item
	 */
	protected boolean getState() {
		return ItemNBTHelper.getBoolean(getStack(), TAG_STATE, false);
	}
	
	/**
	 * Use this to enable/disable the item <br>
	 * For example, this can be used to track whether the item is equipped or not
	 * @param state
	 */
	protected void setState(boolean state) {
		ItemNBTHelper.setBoolean(getStack(), TAG_STATE, state);
	}
	
	
	
	/**
	 * Gets the amount of EMC in the pool
	 * @return Pool EMC
	 */
	protected long getPool() {
		return ItemNBTHelper.getLong(getStack(), TAG_POOL, 0);
	}
	
	/**
	 * Gets the maximum capacity of the internal pool, in EMC <br>
	 * @return Pool capacity in emc
	 */
	protected long getPoolMax() {
		return CAPACITY_EMC;
	}
	
	/**
	 * How full the pool is, as a percentage (0.0 - 1.0)
	 * @return percent full
	 */
	protected float getPoolPercent() {
		return ItemNBTHelper.getLong(getStack(), TAG_POOL, 0)/CAPACITY_EMC;
	}
	
	/**
	 * Gets how much more EMC is needed to fill the pool
	 * @return emc needed
	 */
	protected long getPoolNeeded() {
		return CAPACITY_EMC - ItemNBTHelper.getLong(getStack(), TAG_POOL, 0);
	}
	
	/**
	 * Sets the internal EMC pool to a specific amount <br>
	 * This will clamp the value before actually setting it, so dont worry about that
	 * @param amount
	 */
	protected void setPool(long amount) {
		ItemNBTHelper.setLong(getStack(), TAG_POOL, Mth.clamp(amount, 0, CAPACITY_EMC));
	}
}
