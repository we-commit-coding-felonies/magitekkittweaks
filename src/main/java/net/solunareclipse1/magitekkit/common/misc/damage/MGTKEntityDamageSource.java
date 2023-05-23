package net.solunareclipse1.magitekkit.common.misc.damage;

import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;

import net.solunareclipse1.magitekkit.common.misc.damage.MGTKDmgSrc.IMGTKDamageSource;

public class MGTKEntityDamageSource extends EntityDamageSource implements IMGTKDamageSource {
	public MGTKEntityDamageSource(String id, Entity entity) {
		super(id, entity);
	}

	private boolean bypassAlchShield;
	private boolean bypassDr;
	private boolean plasma;
	private boolean alchemy;
	private boolean divine;
	
	/** source should not be blocked by IAlchShield */
	public boolean isBypassAlchShield() {return this.bypassAlchShield;}
	public MGTKEntityDamageSource bypassAlchShield() {
		this.bypassAlchShield = true;
		return this;
	}
	
	/** source should bypass any 'damage reduction', such as matter armors */
	public boolean isBypassDr() {return this.bypassDr;}
	public MGTKEntityDamageSource bypassDr() {
		this.bypassDr = true;
		return this;
	}
	
	/** bypasses everything that isnt just invincible */
	public MGTKEntityDamageSource bypassNotInvul() {
		return (MGTKEntityDamageSource) this
				.bypassAlchShield()
				.bypassDr()
				.bypassMagic()
				.bypassArmor();
	}
	
	/** immovable object? never heard of it */
	public MGTKEntityDamageSource bypassEverything() {
		return (MGTKEntityDamageSource) this
				.bypassNotInvul()
				.bypassInvul();
	}
	
	/** source should behave like fire, but should NOT be blocked by standard fire immunity */
	public boolean isPlasma() {return this.plasma;}
	public MGTKEntityDamageSource setPlasma() {
		this.plasma = true;
		return this;
	}
	
	/** source is "alchemical", which makes it stronger vs IAlchShield */
	public boolean isAlchemy() {return this.alchemy;}
	public MGTKEntityDamageSource setAlchemy() {
		this.alchemy = true;
		return this;
	}
	
	/** source that comes from any higher power (deity, god, etc) */
	public boolean isDivine() {return this.divine;}
	public MGTKEntityDamageSource setDivine() {
		this.divine = true;
		return this;
	}
}