package net.solunareclipse1.magitekkit.util;

import net.minecraft.world.entity.player.Player;

import net.solunareclipse1.magitekkit.util.Constants.Xp;

public class PlrHelper {
	/**
	 * gets a players current xp, in points
	 * @param player
	 * @return xp points player has
	 */
	public static long getXp(Player player) {
		// levels (as points) + (experience progress * amount for next level)
		return xpLvlToPoints(player.experienceLevel) + (long)(player.experienceProgress * player.getXpNeededForNextLevel());
	}
	
	/**
	 * inserts xp points into the player <br>
	 * will discard xp if the player cant hold any more
	 * @param player
	 * @param amount of xp in points to insert
	 */
	public static void insertXp(Player player, long amount) {
		long newXp = getXp(player) + amount;
		if (newXp >= Xp.VANILLA_MAX_POINTS) {
			player.experienceLevel = Xp.VANILLA_MAX_LVL;
			player.experienceProgress = 0;
			player.totalExperience = Integer.MAX_VALUE;
		} else {
			player.experienceLevel = xpPointsToLvl(newXp);
			long extra = newXp - xpLvlToPoints(player.experienceLevel);
			player.experienceProgress = (float) extra / (float) player.getXpNeededForNextLevel();
			// xp bar seems to not visually update without changing this
			player.totalExperience = (int) newXp;
		}
	}
	
	/**
	 * extracts xp from the given player
	 * @param player
	 * @param amount how much xp points to extract
	 * @return actual extracted amount
	 */
	public static long extractXp(Player player, long amount) {
		long newXp = getXp(player) - amount;
		
		if (newXp <= 0) {
			player.totalExperience = 0;
			player.experienceLevel = 0;
			player.experienceProgress = 0;
			return newXp + amount; // dont need to call getXp twice here
		}
		
		// 
		player.experienceLevel = xpPointsToLvl(newXp);
		long extra = newXp - xpLvlToPoints(player.experienceLevel);
		player.experienceProgress = (float) extra / (float) player.getXpNeededForNextLevel();
		player.totalExperience = (int) newXp;
		return amount;
	}
	
	/**
	 * Extracts a certain amount of levels from a player <br>
	 * Will extract extra points (experienceProgress) first
	 * @param player
	 * @param lvls the amount of levels to extract
	 * @return the extracted amount of points
	 */
	public static int extractLvl(Player player, int lvls) {
		int extracted = 0;
		for (int i = 0; i < lvls; i++) {
			
			if (player.experienceProgress <= 0 && player.experienceLevel <= 0) break;
			
			if (player.experienceProgress > 0) {
				extracted += (int) (player.experienceProgress * player.getXpNeededForNextLevel());
				player.experienceProgress = 0;
			} else if (player.experienceLevel > 0) {
				player.experienceLevel--;
				extracted += player.getXpNeededForNextLevel();
			}
		}
		
		player.totalExperience = Math.max(0, player.totalExperience - extracted);
		return extracted;
	}
	
	/**
	 * Extracts *all* xp from the player
	 * @param player
	 * @return
	 */
	public static long extractAll(Player player) {
		long extracted = 0;
		
		if (player.experienceProgress > 0) {
			extracted += (long) (player.experienceProgress * player.getXpNeededForNextLevel());
			player.experienceProgress = 0;
		}
		
		if (player.experienceLevel > 0) {
			extracted += xpLvlToPoints(player.experienceLevel);
			player.experienceLevel = 0;
		}
		
		player.totalExperience = 0;
		
		return extracted;
	}
	
	/**
	 * Converts XP Level to XP Points <br>
	 * Uses a long so we can work with larger amounts of xp
	 * @param lvl
	 * @return xp
	 */
	public static long xpLvlToPoints(long lvl) {
		if (lvl <= 0) return 0;
		
		if (lvl <= 16) {
			// x^2 + 6x
			return (lvl * lvl + 6 * lvl);
		} else if (lvl <= 31) {
			// 2.5x^2 - 40.5x + 360
			return (long) (2.5 * lvl * lvl - 40.5 * lvl + 360);
		} else {
			// 4.5x^2 - 162.5x + 2220
			return (long) (4.5 * lvl * lvl - 162.5 * lvl + 2220);
		}
	}
	
	/**
	 * Brute-forces the opposite of xpLvlToPoints <br>
	 * Is decently fast, even with very large numbers
	 * <p>
	 * Max return value is 1,431,655,783 (max level when using long)
	 * 
	 * @param xp
	 * @return lvl
	 */
	public static int xpPointsToLvl(long xp) {
		// out of range means we can skip the brute force
		// saves time, also handles negatives
		if (xp < 7) {
			return 0;
		} else if (xp >= Xp.TRANSFER_MAX_POINTS) {
			return Xp.TRANSFER_MAX_LVL;
		}
		
		// we set the initial tick value based on some precalculated values
		// lets us quickly handle big numbers, while not slowing down small ones
		int tick = 1;
		for (int i = 1; i < Xp.LVL_POWS_OF_TEN.length; i++) {
			if (xp < Xp.LVL_POWS_OF_TEN[i]) {
				tick = (int) Math.pow(10, i-1);
			}
		}
		
		int lvl = 0;
		long curPts = 0;
		boolean found = false;
		boolean dir = true; // true means we tick up, false means tick down
		
		while (!found) {
			if (dir) {
				lvl += tick;
			} else {
				lvl -= tick;
			}
			curPts = xpLvlToPoints(lvl);
			// if equal, we can stop here
			if (curPts == xp) {
				// set dir to false so we dont return lvl-1
				dir = false;
				found = true;
			} else {
				// check if we overshot
				if (dir ? curPts > xp : curPts < xp) {
					// if possible, increase precision
					// otherwise, weve found
					if (tick != 1) {
						dir = !dir;
						tick /= 10;
					}
					else found = true;
				}
			}
		}
		// we never want to return a level worth more than the input
		// this uses dir to check if we were > or <= when we found the lvl
		// if we were >, we need to -1 so that the level is worth less than input
		return dir ? lvl-1 : lvl;
	}
	
	/**
	 * Runs the same logic as {@link Player#getXpNeededForNextLevel()} using an arbitrary current level, rather than the players current level
	 * 
	 * @param curLvl the xp level
	 * @return xp needed to curLvl++
	 */
	public static int xpNeededToLevelUpFrom(int curLvl) {
		if (curLvl >= 30) {
			return 112 + (curLvl - 30) * 9;
		} else {
			return curLvl >= 15 ? 37 + (curLvl - 15) * 5 : 7 + curLvl * 2;
		}
	}
}
