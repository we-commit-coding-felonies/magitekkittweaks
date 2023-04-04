package net.solunareclipse1.magitekkit.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import morph.avaritia.handler.ArmorHandler;
import vazkii.botania.common.entity.EntityDoppleganger;

/**
 * generic entity helper functions <br>
 * player specific stuff in PlrHelper
 * @author solunareclipse1
 */
public class EntityHelper {
	
	/**
	 * checks if an entity should never be harmed <br>
	 * stuff like the invulnerable tag, creative mode, and infinity armor <br>
	 * does NOT check for iframes (invulnerableTime), its only for "non-bypassable" invincibility
	 * 
	 * @param entity
	 * @return if entity is invincible
	 */
	public static boolean isInvincible(Entity entity) {
		boolean invincible = entity.isInvulnerable();
		if (!invincible && entity instanceof EntityDoppleganger gaia) {
			invincible = gaia.getInvulTime() > 0;
		}
		if (!invincible && entity instanceof Player plr) {
			invincible = plr.isCreative() || ArmorHandler.isInfinite(plr);
		}
		
		return invincible;
	}
}
