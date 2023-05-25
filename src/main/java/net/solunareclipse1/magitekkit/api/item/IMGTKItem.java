package net.solunareclipse1.magitekkit.api.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.fml.ModList;

import moze_intel.projecte.capability.ItemCapability;

import net.solunareclipse1.magitekkit.api.capability.MGTKCapabilityProvider;

/**
 * common stuff for items <br>
 * perhaps extending IForgeItem is a mistake but it works lol
 * @author solunareclipse1
 *
 */
public interface IMGTKItem extends IForgeItem {

	/**
	 * used to store supported capabilities during initialization <br>
	 * @return a List of ItemCapability Suppliers
	 */
	@NotNull
	public List<Supplier<ItemCapability<?>>> getSupportedCaps();

	/**
	 * adds capability to this item,
	 * it should be called in the constructor
	 * @param capabilitySupplier
	 */
	default void addItemCapability(Supplier<ItemCapability<?>> capabilitySupplier) {
		getSupportedCaps().add(capabilitySupplier);
	}
	
	/**
	 * adds the capability only if the given modid is loaded
	 * @param modid
	 * @param capabilitySupplier
	 */
	default void addItemCapability(String modid, Supplier<Supplier<ItemCapability<?>>> capabilitySupplier) {
		if (ModList.get().isLoaded(modid)) {
			getSupportedCaps().add(capabilitySupplier.get());
		}
	}

	@Override
	default ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
		List<Supplier<ItemCapability<?>>> supportedCapabilities = getSupportedCaps();
		if (supportedCapabilities.isEmpty()) {
			return null;
		}
		return new MGTKCapabilityProvider(stack, supportedCapabilities);
	}
}
