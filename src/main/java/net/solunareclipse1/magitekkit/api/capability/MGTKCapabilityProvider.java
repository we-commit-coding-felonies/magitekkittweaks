package net.solunareclipse1.magitekkit.api.capability;

import java.util.List;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import moze_intel.projecte.capability.IItemCapabilitySerializable;
import moze_intel.projecte.capability.ItemCapability;
import moze_intel.projecte.capability.ItemCapabilityWrapper;

/**
 * this is here because botania doesnt like passing
 * itemstacks as function arguments for IManaItem
 * <p>
 * either im insane or projecte names their
 * stuff in a very silly way, so thats why theres the discrepancy
 * <p>
 * i never want to see the word 'capability' ever again
 * @author solunareclipse1
 *
 */
public class MGTKCapabilityProvider extends ItemCapabilityWrapper {

	private final ItemCapability<?>[] capabilities;
	private final ItemStack itemStack;

	public MGTKCapabilityProvider(ItemStack stack, List<Supplier<ItemCapability<?>>> caps) {
		super(stack, caps);
		itemStack = stack;
		this.capabilities = new ItemCapability<?>[caps.size()];
		for (int i = 0; i < caps.size(); i++) {
			ItemCapability<?> cap = caps.get(i).get();
			this.capabilities[i] = cap;
			cap.setWrapper(this);
		}
	}

	public MGTKCapabilityProvider(ItemStack stack, ItemCapability<?>... caps) {
		super(stack, caps);
		itemStack = stack;
		this.capabilities = caps;
		for (ItemCapability<?> cap : this.capabilities) {
			cap.setWrapper(this);
		}
	}

	protected ItemStack getItemStack() {
		return itemStack;
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
		for (ItemCapability<?> cap : capabilities) {
			if (capability == cap.getCapability()) {
				return cap.getLazyCapability().cast();
			}
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag serializedNBT = new CompoundTag();
		for (ItemCapability<?> cap : capabilities) {
			if (cap instanceof IItemCapabilitySerializable serializableCap) {
				serializedNBT.put(serializableCap.getStorageKey(), serializableCap.serializeNBT());
			}
		}
		return serializedNBT;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		for (ItemCapability<?> cap : capabilities) {
			if (cap instanceof IItemCapabilitySerializable serializableCap && nbt.contains(serializableCap.getStorageKey())) {
				serializableCap.deserializeNBT(nbt.get(serializableCap.getStorageKey()));
			}
		}
	}
}