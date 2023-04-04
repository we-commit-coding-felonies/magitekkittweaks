package net.solunareclipse1.magitekkit.common.misc;

import net.minecraft.world.damagesource.DamageSource;

public class MGTKDmgSrc extends DamageSource {
	
	public MGTKDmgSrc(String msgId) {
		super(msgId);
	}

	/** alchemical magic, bypasses armor & enchants & DR */
	public static final MGTKDmgSrc TRANSMUTATION = (MGTKDmgSrc) new MGTKDmgSrc("transmutation")
			.setAlchemy()
			.bypassDr().bypassMagic().bypassArmor()
			.setMagic();

	/** alchemical magic, ignores almost all forms of protection */
	public static final MGTKDmgSrc TRANSMUTATION_2 = (MGTKDmgSrc) new MGTKDmgSrc("transmutation.strong")
			.setAlchemy()
			.bypassNotInvul()
			.setMagic();

	/** alchemical magic status effect damage, ignores almost all forms of protection, doesnt aggro */
	public static final MGTKDmgSrc TRANSMUTATION_POTION = (MGTKDmgSrc) new MGTKDmgSrc("transmutation.potion")
			.setAlchemy()
			.bypassNotInvul()
			.setNoAggro().setMagic();
	
	/** alchemical magic explosion, ignores non-physical protection, doesnt aggro */
	public static final MGTKDmgSrc EMC_NUKE = (MGTKDmgSrc) new MGTKDmgSrc("emcnuke")
			.setAlchemy()
			.bypassDr().bypassMagic()
			.setNoAggro().setMagic().setExplosion();
	
	/** alchemical plasma magic explosion projectile, ignores armor */
	public static final MGTKDmgSrc MUSTANG = (MGTKDmgSrc) new MGTKDmgSrc("mustang")
			.setAlchemy().setPlasma()
			.bypassArmor()
			.setMagic().setExplosion().setProjectile();
	
	/** alchemical magic, ignores armor */
	public static final MGTKDmgSrc MATTER_AOE = (MGTKDmgSrc) new MGTKDmgSrc("aoe")
			.setAlchemy()
			.bypassArmor()
			.setMagic();
	
	/** alchemical magic, ignores enchantments and armor */
	public static final MGTKDmgSrc MATTER_AOE_STRONG = (MGTKDmgSrc) new MGTKDmgSrc("aoe2")
			.setAlchemy()
			.bypassMagic().bypassArmor()
			.setMagic();
	
	/** divine, ignores everything, doesnt aggro */
	public static final MGTKDmgSrc GOD = (MGTKDmgSrc) new MGTKDmgSrc("god")
			.setDivine()
			.bypassEverything()
			.setNoAggro();

	private boolean bypassAlchShield;
	private boolean bypassDr;
	private boolean plasma;
	private boolean alchemy;
	private boolean divine;
	
	/** source should not be blocked by IAlchShield */
	public boolean isBypassAlchShield() {return this.bypassAlchShield;}
	public MGTKDmgSrc bypassAlchShield() {
		this.bypassAlchShield = true;
		return this;
	}
	
	/** source should bypass any 'damage reduction', such as matter armors */
	public boolean isBypassDr() {return this.bypassDr;}
	public MGTKDmgSrc bypassDr() {
		this.bypassDr = true;
		return this;
	}
	
	/** bypasses everything that isnt just invincible */
	public MGTKDmgSrc bypassNotInvul() {
		return (MGTKDmgSrc) this
				.bypassAlchShield()
				.bypassDr()
				.bypassMagic()
				.bypassArmor();
	}
	
	/** immovable object? never heard of it */
	public MGTKDmgSrc bypassEverything() {
		return (MGTKDmgSrc) this
				.bypassNotInvul()
				.bypassInvul();
	}
	
	/** source should behave like fire, but should NOT be blocked by standard fire immunity */
	public boolean isPlasma() {return this.plasma;}
	public MGTKDmgSrc setPlasma() {
		this.plasma = true;
		return this;
	}
	
	/** source is "alchemical", which makes it stronger vs IAlchShield */
	public boolean isAlchemy() {return this.alchemy;}
	public MGTKDmgSrc setAlchemy() {
		this.alchemy = true;
		return this;
	}
	
	/** source that comes from any higher power (deity, god, etc) */
	public boolean isDivine() {return this.divine;}
	public MGTKDmgSrc setDivine() {
		this.divine = true;
		return this;
	}
}
