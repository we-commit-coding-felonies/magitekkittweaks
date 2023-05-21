package net.solunareclipse1.magitekkit.common.item.curio;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import top.theillusivec4.curios.api.type.capability.ICurio;

import net.solunareclipse1.magitekkit.api.capability.wrapper.CovalentCapabilityWrapper;
import net.solunareclipse1.magitekkit.api.capability.wrapper.converter.CurioCovalentCapabilityWrapper;
import net.solunareclipse1.magitekkit.common.item.MGTKCovalenceItem;

public class CovalenceBracelet extends MGTKCovalenceItem implements ICurio {
	
	public CovalenceBracelet(Properties props) {
		super(props);
		addItemCapability(CurioCovalentCapabilityWrapper::new);
	}

	// should be fine because its covered by CurioCovalentCapabilityWrapper?
	@Override
	public ItemStack getStack() {
		return ItemStack.EMPTY;
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (slot != -1) {
			// when in curio slot is apparently -1
			CovalentCapabilityWrapper.setState(stack, false);
		}
	}
}
