package net.solunareclipse1.magitekkit.common.item.armor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.ModList;

import moze_intel.projecte.capability.ItemCapability;

import net.solunareclipse1.magitekkit.api.capability.MGTKCapabilityProvider;

public class MGTKArmorItem extends ArmorItem {
	
	public MGTKArmorItem(ArmorMaterial mat, EquipmentSlot slot, Properties props) {
		super(mat, slot, props);
	}

	// capability stuff from projecte, since i hate their method the least
	private final List<Supplier<ItemCapability<?>>> supportedCapabilities = new ArrayList<>();
	
	protected void addItemCapability(Supplier<ItemCapability<?>> capabilitySupplier) {
		supportedCapabilities.add(capabilitySupplier);
	}
	
	protected void addItemCapability(String modid, Supplier<Supplier<ItemCapability<?>>> capabilitySupplier) {
		if (ModList.get().isLoaded(modid)) {
			supportedCapabilities.add(capabilitySupplier.get());
		}
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
		if (supportedCapabilities.isEmpty()) {
			return super.initCapabilities(stack, nbt);
		}
		return new MGTKCapabilityProvider(stack, supportedCapabilities);
	}

}
