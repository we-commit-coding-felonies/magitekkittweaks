package net.solunareclipse1.magitekkit.common.item.curio;

import net.minecraft.world.item.ItemStack;

import top.theillusivec4.curios.api.type.capability.ICurio;

import net.solunareclipse1.magitekkit.api.capability.wrapper.converter.CurioCovalentCapabilityWrapper;
import net.solunareclipse1.magitekkit.api.capability.wrapper.converter.ManaCovalentCapabilityWrapper;
import net.solunareclipse1.magitekkit.common.item.MGTKItem;

public class CovalenceBracelet extends MGTKItem implements ICurio {
	
	public CovalenceBracelet(Properties props) {
		super(props);
		addItemCapability(CurioCovalentCapabilityWrapper::new);
		addItemCapability(ManaCovalentCapabilityWrapper::new);
	}

	// should be fine because its covered by CurioCovalentCapabilityWrapper?
	@Override
	public ItemStack getStack() {
		return ItemStack.EMPTY;
	}
}
