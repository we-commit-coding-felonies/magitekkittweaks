package net.solunareclipse1.magitekkit.common.misc;

import net.minecraft.world.damagesource.DamageSource;

public class MGTKDamageSource extends DamageSource {
	
	public MGTKDamageSource(String msgId) {
		super(msgId);
	}
	public static final MGTKDamageSource TRANSMUTATION = (MGTKDamageSource) new MGTKDamageSource("transmutation").setAlchemical().bypassArmor().bypassMagic().bypassInvul();
	public static final MGTKDamageSource ALCHEMICAL_FISSION = (MGTKDamageSource) new MGTKDamageSource("alchFission").setAlchemical().bypassMagic().setExplosion();
	public static final MGTKDamageSource MUSTANG = (MGTKDamageSource) new MGTKDamageSource("mustang").setOverheat().bypassArmor().setMagic().setExplosion();
	public static final MGTKDamageSource MATTER_AOE = (MGTKDamageSource) new MGTKDamageSource("aoe").setAlchemical().bypassArmor();
	
	private boolean alchemical;
	private boolean overheat;
	
	/**
	 * marks as alchemical damage, and magic
	 * @return
	 */
	public MGTKDamageSource setAlchemical() {
		this.alchemical = true;
		this.setMagic();
		return this;
	}
	
	public boolean isAlchemical() {
		return this.alchemical;
	}
	
	/**
	 * marks as "even further beyond" fire damage <br>
	 * does not mark as actual fire damage to avoid being blocked by fire protection stuff <br>
	 * were way too damn hot for that B)
	 */
	public MGTKDamageSource setOverheat() {
		this.overheat = true;
		return this;
	}
	
	public boolean isOverheat() {
		return this.overheat;
	}
}
