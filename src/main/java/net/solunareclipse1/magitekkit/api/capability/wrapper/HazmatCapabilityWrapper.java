package net.solunareclipse1.magitekkit.api.capability.wrapper;

import net.minecraftforge.common.capabilities.Capability;

import moze_intel.projecte.capability.BasicItemCapability;

import net.solunareclipse1.magitekkit.api.item.IHazmatItem;

import mekanism.api.radiation.capability.IRadiationShielding;
import mekanism.common.capabilities.Capabilities;

/**
 * anti-radiation mekanism capability <br>
 * please use IHazmatItem on the item class itself
 * @author solunareclipse1
 */
public class HazmatCapabilityWrapper extends BasicItemCapability<IRadiationShielding> implements IRadiationShielding {

	@Override
	public double getRadiationShielding() {
		if (getStack().getItem() instanceof IHazmatItem hazmat) {
			return hazmat.protectionPercent(getStack());
		}
		return 0;
	}

	@Override
	public Capability<IRadiationShielding> getCapability() {
		return Capabilities.RADIATION_SHIELDING_CAPABILITY;
	}

}
