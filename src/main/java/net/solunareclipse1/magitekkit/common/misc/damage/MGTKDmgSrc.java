package net.solunareclipse1.magitekkit.common.misc.damage;

import net.minecraft.world.entity.Entity;

/**
 * contains a bunch of stuff for custom damage sources
 * @author solunareclipse1
 */
public class MGTKDmgSrc {

	/** alchemical magic status effect damage, ignores almost all forms of protection, doesnt aggro */
	public static final MGTKSimpleDamageSource TRANSMUTATION_POTION = (MGTKSimpleDamageSource) new MGTKSimpleDamageSource("transmutation.potion")
			.setAlchemy()
			.bypassNotInvul()
			.setNoAggro().setMagic();
	
	/** divine, ignores everything, doesnt aggro */
	public static final MGTKSimpleDamageSource GOD = (MGTKSimpleDamageSource) new MGTKSimpleDamageSource("god")
			.setDivine()
			.bypassEverything()
			.setNoAggro();
	
	/** alchemical magic, bypasses armor, enchants & DR */
	public static MGTKEntityDamageSource transmutation(Entity culprit) {
		return (MGTKEntityDamageSource) new MGTKEntityDamageSource("transmutation", culprit)
			.setAlchemy()
			.bypassDr().bypassMagic().bypassArmor()
			.setMagic();
	}

	/** alchemical magic, ignores almost all forms of protection */
	public static MGTKEntityDamageSource strongTransmutation(Entity culprit) {
		return (MGTKEntityDamageSource) new MGTKEntityDamageSource("transmutation.strong", culprit)
			.setAlchemy()
			.bypassNotInvul()
			.setMagic();
	}
	
	/** alchemical magic explosion, ignores non-physical protection, doesnt aggro */
	public static MGTKEntityDamageSource emcNuke(Entity culprit) {
		return (MGTKEntityDamageSource) new MGTKEntityDamageSource("emcnuke", culprit)
			.setAlchemy()
			.bypassDr().bypassMagic()
			.setNoAggro().setMagic().setExplosion();
	}
	
	/** alchemical plasma magic explosion projectile, ignores armor */
	public static MGTKEntityDamageSource mustang(Entity culprit) {
		return (MGTKEntityDamageSource) new MGTKEntityDamageSource("mustang", culprit)
			.setAlchemy().setPlasma()
			.bypassArmor()
			.setMagic().setExplosion().setProjectile();
	}
	
	/** alchemical magic, ignores armor */
	public static MGTKEntityDamageSource matterAoe(Entity culprit) {
		return (MGTKEntityDamageSource) new MGTKEntityDamageSource("matter_aoe", culprit)
			.setAlchemy()
			.bypassArmor()
			.setMagic();
	}
	
	/**
	 * contains shared functions for custom damage sources <br>
	 * you should test for instanceof this when checking for any magitekkit damage source types
	 * @author solunareclipse1
	 */
	public interface IMGTKDamageSource {
		public boolean isBypassAlchShield();
		public <T extends IMGTKDamageSource> T bypassAlchShield();
		
		/** source should bypass any 'damage reduction', such as matter armors */
		public boolean isBypassDr();
		public <T extends IMGTKDamageSource> T bypassDr();
		
		/** bypasses everything that isnt just invincible */
		public <T extends IMGTKDamageSource> T bypassNotInvul();
		
		/** immovable object? never heard of it */
		public <T extends IMGTKDamageSource> T bypassEverything();
		
		/** source should behave like fire, but should NOT be blocked by standard fire immunity */
		public boolean isPlasma();
		public <T extends IMGTKDamageSource> T setPlasma();
		
		/** source is "alchemical", which makes it stronger vs IAlchShield */
		public boolean isAlchemy();
		public <T extends IMGTKDamageSource> T setAlchemy();
		
		/** source that comes from any higher power (deity, god, etc) */
		public boolean isDivine();
		public <T extends IMGTKDamageSource> T setDivine();
	}
}
