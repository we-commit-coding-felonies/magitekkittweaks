package net.solunareclipse1.magitekkit.util;

import net.minecraft.world.entity.player.Player;

public class PlrHelper {

	/**
	 * gets a players current xp, in points
	 * @param player
	 * @return xp points player has
	 */
	public static int getXp(Player player) {
		return (int) (xpLvlToPoints(player.experienceLevel) + player.experienceProgress * player.getXpNeededForNextLevel());
	}

	/**
	 * inserts xp points into the player
	 * @param player
	 * @param amount of xp in points to insert
	 */
	public static void insertXp(Player player, int amount) {
		long newXp = getXp(player) + amount;
		if (newXp >= Integer.MAX_VALUE) {
			player.totalExperience = Integer.MAX_VALUE;
			player.experienceLevel = (int) xpPointsToLvl(Integer.MAX_VALUE);
			player.experienceProgress = 0;
		} else {
			player.totalExperience = (int) newXp;
			player.experienceLevel = (int) xpPointsToLvl(newXp);
			player.experienceProgress = (float) (newXp - xpLvlToPoints(player.experienceLevel)) / (float) player.getXpNeededForNextLevel();
		}
	}

	/**
	 * extracts xp from the given player
	 * @param player
	 * @param amount how much xp points to extract
	 * @return actual extracted amount
	 */
	public static int extractXp(Player player, int amount) {
		int newXp = getXp(player) - amount;
		if (newXp <= 0) {
			player.totalExperience = 0;
			player.experienceLevel = 0;
			player.experienceProgress = 0;
		} else {
			player.totalExperience = newXp;
			player.experienceLevel = (int) xpPointsToLvl(newXp);
			player.experienceProgress = (float) (newXp - xpLvlToPoints(player.experienceLevel)) / (float) player.getXpNeededForNextLevel();
		}
		return 0;
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
	 * Does the exact opposite of xpLvlToPoints <br>
	 * Current method is a brute-force, but works decently enough <br>
	 * Even using Long.MAX_VALUE, it only takes a couple seconds at most
	 * <p>
	 * Max return value is 1,431,655,783 (max level when using long)
	 * 
	 * @param xp
	 * @return lvl
	 */
	public static int xpPointsToLvl(long xp) {
		
		int lvl = 0;
		int tick = 1000;
		while (xpLvlToPoints(lvl) <= xp) {
			if (lvl >= 1431655783) return 1431655783;
			lvl += tick;
		}
		tick = 100;
		while (xpLvlToPoints(lvl) >= xp) {
			lvl -= tick;
		}
		tick = 10;
		while (xpLvlToPoints(lvl) <= xp) {
			lvl += tick;
		}
		tick = 1;
		while (xpLvlToPoints(lvl) >= xp) {
			lvl -= tick;
		}
		return lvl;
	}
	
	/**
	 * @deprecated unfinished, has floating point jank <br>
	 * TODO: finish this with minimal cringe
	 * 
	 * 
	 * @param xp
	 * @return
	 */
	public static double xpPointsToLvlDouble(long xp) {
		// https://breezewiki.com/minecraft/wiki/Experience#Leveling_up
		return ((325d/18d) + ( Math.sqrt( 18 * (xp - (54215d/72d)) ) / 9d ));
		//return (325d/18d) + Math.sqrt( (2d/9d) * ( xp - ( 54215d/72d ) ) );
	}
	
	/**
	 * Re-formats the given XP points into "dense" format
	 * <p>
	 * Dense format stores XP as 2 long values: Level & 'Remainder' <br>
	 * Remainder is the leftover amount of XP that wasnt quite enough to get to the next level <br>
	 * Doing things this way should allow storage of somewhere between 2^59 and 2^60 levels of experience
	 * 
	 * @param xp measured in points
	 * @return {level, remainder}
	 */
	public long[] xpFormatToDense(long xp) {
		long[] denseXp = {0,0};
		return denseXp;
	}
	
	/**
	 * Will convert dense-format XP back to the standard <br>
	 * NOTE: Any XP beyond Integer.MAX_VALUE will be lost
	 * <p>
	 * Dense format stores XP as 2 long values: Level & 'Remainder' <br>
	 * Remainder is the leftover amount of XP that wasnt quite enough to get to the next level <br>
	 * Doing things this way should allow storage of somewhere between 2^59 and 2^60 levels of experience
	 * 
	 * @param denseXp
	 * @return XP in points
	 */
	public int xpFormatToNormal(long[] denseXp) {
		if (denseXp[1] >= Integer.MAX_VALUE || denseXp[0] == 0) {
			return Integer.MAX_VALUE;
		}
		return 0;
	}
}
