package net.solunareclipse1.magitekkit.util;

import net.minecraft.util.Mth;


/**
 * This class contains some functions to help with custom durability bars
 * 
 * @author solunareclipse1
 *
 */
public class DuraBarHelper {

	/**
	 * Function to help with {@link net.minecraft.world.item.Item#getBarWidth}
	 * 
	 * cur / max = percentage for the bar
	 * 
	 * @param cur Current value
	 * @param max Maximum value
	 * @return Value for use in getBarWidth
	 */
	public static int barLevelFromCurMax(float cur, float max) {
		return (int) Math.round(13 - 13 * cur / max);
	}
	/**
	 * Function to help with {@link net.minecraft.world.item.Item#getBarWidth}
	 * 
	 * cur / max = percentage for the bar
	 * 
	 * @param cur Current value
	 * @param max Maximum value
	 * @return Value for use in getBarWidth
	 */
	public static int barLevelFromCurMax(double cur, double max) {
		return (int) Math.round(13 - 13 * cur / max);
	}
	/**
	 * Function to help with {@link net.minecraft.world.item.Item#getBarWidth}
	 * 
	 * cur / max = percentage for the bar
	 * 
	 * @param cur Current value
	 * @param max Maximum value
	 * @return Value for use in getBarWidth
	 */
	public static int barLevelFromCurMax(int cur, int max) {
		return (int) Math.round(13 - 13 * cur / max);
	}
	/**
	 * Function to help with {@link net.minecraft.world.item.Item#getBarWidth}
	 * 
	 * cur / max = percentage for the bar
	 * 
	 * @param cur Numerator
	 * @param max Denominator
	 * @return Value for use in getBarWidth
	 */
	public static int barLevelFromCurMax(long cur, long max) {
		return (int) Math.round(13 - 13 * cur / max);
	}
	
	/**
	 * Function for help with {@link net.minecraft.world.item.Item#getBarWidth}
	 * 
	 * @param fill Percentage that the bar is filled (0.5 is 50%)
	 * @return
	 */
	public static int barLevelFromPercent(float fill) {
		return (int) Math.round(13 - 13 * fill);
	}
	/**
	 * Function for help with {@link net.minecraft.world.item.Item#getBarWidth}
	 * 
	 * @param fill Percentage that the bar is filled (0.5 is 50%)
	 * @return
	 */
	public static int barLevelFromPercent(double fill) {
		return (int) Math.round(13 - 13 * fill);
	}
}
