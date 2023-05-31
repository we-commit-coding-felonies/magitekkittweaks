package net.solunareclipse1.magitekkit.common.misc.damage;

import net.minecraft.world.damagesource.DamageSource;

import net.solunareclipse1.magitekkit.common.misc.damage.MGTKDmgSrc.IMGTKDamageSource;

public class MGTKSimpleDamageSource extends DamageSource implements IMGTKDamageSource {
	public MGTKSimpleDamageSource(String id) {
		super(id);
	}

	private boolean bypassAlchShield;
	private boolean bypassDr;
	private boolean plasma;
	private boolean alchemy;
	private boolean divine;
	
	/** source should not be blocked by IAlchShield */
	public boolean isBypassAlchShield() {return this.bypassAlchShield;}
	public MGTKSimpleDamageSource bypassAlchShield() {
		this.bypassAlchShield = true;
		return this;
	}
	
	/** source should bypass any 'damage reduction', such as matter armors */
	public boolean isBypassDr() {return this.bypassDr;}
	public MGTKSimpleDamageSource bypassDr() {
		this.bypassDr = true;
		return this;
	}
	
	/** bypasses everything that isnt just invincible */
	public MGTKSimpleDamageSource bypassNotInvul() {
		return (MGTKSimpleDamageSource) this
				.bypassAlchShield()
				.bypassDr()
				.bypassMagic()
				.bypassArmor();
	}
	
	/** immovable object? never heard of it */
	public MGTKSimpleDamageSource bypassEverything() {
		return (MGTKSimpleDamageSource) this
				.bypassNotInvul()
				.bypassInvul();
	}
	
	/** source should behave like fire, but should NOT be blocked by standard fire immunity */
	public boolean isPlasma() {return this.plasma;}
	public MGTKSimpleDamageSource setPlasma() {
		this.plasma = true;
		return this;
	}
	
	/** source is "alchemical", which makes it stronger vs IAlchShield */
	public boolean isAlchemy() {return this.alchemy;}
	public MGTKSimpleDamageSource setAlchemy() {
		this.alchemy = true;
		return this;
	}
	
	/** source that comes from any higher power (deity, god, etc) */
	public boolean isDivine() {return this.divine;}
	public MGTKSimpleDamageSource setDivine() {
		this.divine = true;
		return this;
	}

}
