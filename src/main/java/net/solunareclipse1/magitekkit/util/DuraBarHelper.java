package net.solunareclipse1.magitekkit.util;

import net.minecraft.util.Mth;


/**
 * This class contains some functions to help with custom durability bars
 * 
 * @author solunareclipse1
 *
 */
public class DuraBarHelper {
	public int covBarColor(float fillPercent) {
		float f = Math.max(0.3911f, fillPercent / 1.65125495376f);
		return Mth.hsvToRgb(f, 1.0f, 0.824f);
	}
	
	/**
	 * @deprecated non functional / unfinished.
	 * TODO: make this work, would be useful.
	 * A function that gets a color from a gradient based on a 3rd value
	 * Designed for use with {@link net.minecraft.world.item.Item#getBarColor}
	 * Behaves similarly to the vanilla durability bar colors
	 * 
	 * @param percent Determines what color in the gradient to use, 0.5 is halfway
	 * @param h1 Hue for Color 1
	 * @param s1 Saturation for Color 1
	 * @param v1 Value for Color 1
	 * @param h2 Hue for Color 2
	 * @param s2 Saturation for Color 2
	 * @param v2 Value for Color 2
	 * @return RGB value as an integer
	 */
	public int gradientBarColor(float percent, float h1, float s1, float v1, float h2, float s2, float v2) {
		boolean hInv, sInv, vInv;
		if (Math.max(h1, h2) == h1) hInv = true; else hInv = false;
		if (Math.max(s1, s2) == s1) sInv = true; else sInv = false;
		if (Math.max(v1, v2) == v1) vInv = true; else vInv = false;
		return Mth.hsvToRgb(Math.max(0.3911F, (float) (1.0F - percent) / 1.65125495376F), 1.0f, 0.824f);
	}
	
	/**
	 * A very fancy function that will fade between 2 different HSV colors
	 * Designed for use with {@link net.minecraft.world.item.Item#getBarColor}
	 * Fades from color 1 to color 2, then back to color 1
	 * 
	 * @param timer An incrementing value that the cycle uses to fade, such as the world time
	 * @param cycle How many timer ticks a full cycle takes, lower = faster fade
	 * @param offset The amount of timer ticks to offset this fading by.
	 * @param h1 Hue for Color 1
	 * @param s1 Saturation for Color 1
	 * @param v1 Value for Color 1
	 * @param h2 Hue for Color 2
	 * @param s2 Saturation for Color 2
	 * @param v2 Value for Color 2
	 * @return RGB value as an integer
	 */
	public int fadingBarColor(long timer, int cycle, int offset, float h1, float s1, float v1, float h2, float s2, float v2) {
		boolean hInv, sInv, vInv;
		if (Math.max(h1, h2) == h1) hInv = true; else hInv = false;
		if (Math.max(s1, s2) == s1) sInv = true; else sInv = false;
		if (Math.max(v1, v2) == v1) vInv = true; else vInv = false;
		float hDiff, sDiff, vDiff, hVal, sVal, vVal;
		hDiff = Math.max(h1, h2) - Math.min(h1, h2);
		sDiff = Math.max(s1, s2) - Math.min(s1, s2);
		vDiff = Math.max(v1, v2) - Math.min(v1, v2);
		
		int swapPoint = cycle / 2;
		float fade = ((timer % cycle) + offset) % cycle;
		if (fade < swapPoint) {
			if (hInv) hVal = h1 - ((hDiff * (fade - swapPoint)) / swapPoint); else hVal = h1 + ((hDiff * fade) / swapPoint);
			if (sInv) sVal = s1 - ((sDiff * (fade - swapPoint)) / swapPoint); else sVal = s1 + ((sDiff * fade) / swapPoint);
			if (vInv) vVal = v1 - ((vDiff * (fade - swapPoint)) / swapPoint); else vVal = v1 + ((vDiff * fade) / swapPoint);
			return Mth.hsvToRgb(hVal, sVal, vVal);
		} else {
			if (hInv) hVal = h2 + ((hDiff * fade) / swapPoint); else hVal = h2 - ((hDiff * (fade - swapPoint)) / swapPoint);
			if (sInv) sVal = s2 + ((sDiff * fade) / swapPoint); else sVal = s2 - ((sDiff * (fade - swapPoint)) / swapPoint);
			if (vInv) vVal = v2 + ((vDiff * fade) / swapPoint); else vVal = v2 - ((vDiff * (fade - swapPoint)) / swapPoint);
			return Mth.hsvToRgb(hVal, sVal, vVal);
		}
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
	public int barLevelFromCurMax(float cur, float max) {
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
	public int barLevelFromCurMax(double cur, double max) {
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
	public int barLevelFromCurMax(int cur, int max) {
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
	public int barLevelFromCurMax(long cur, long max) {
		return (int) Math.round(13 - 13 * cur / max);
	}
	
	/**
	 * Function for help with {@link net.minecraft.world.item.Item#getBarWidth}
	 * 
	 * @param fill Percentage that the bar is filled (0.5 is 50%)
	 * @return
	 */
	public int barLevelFromPercent(float fill) {
		return (int) Math.round(13 - 13 * fill);
	}
	/**
	 * Function for help with {@link net.minecraft.world.item.Item#getBarWidth}
	 * 
	 * @param fill Percentage that the bar is filled (0.5 is 50%)
	 * @return
	 */
	public int barLevelFromPercent(double fill) {
		return (int) Math.round(13 - 13 * fill);
	}
}
