package net.solunareclipse1.magitekkit.api.item;

import net.minecraft.world.item.ItemStack;

import mekanism.api.radiation.capability.IRadiationShielding;

/**
 * item that provides resistance to mekanism radiation
 * @author solunareclipse1
 */
public interface IHazmatItem {
	double protectionPercent(ItemStack stack);
}
