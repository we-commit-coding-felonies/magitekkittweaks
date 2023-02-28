package net.solunareclipse1.magitekkit.capability;

import java.util.List;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.capability.IItemCapabilitySerializable;
import moze_intel.projecte.capability.ItemCapability;
import moze_intel.projecte.capability.ItemCapabilityWrapper;

import net.solunareclipse1.magitekkit.common.item.armor.gem.GemAmulet;
import net.solunareclipse1.magitekkit.common.item.curio.ConverterBracelet;
import net.solunareclipse1.magitekkit.common.item.curio.ConverterBracelet.KleinBridgeManaItem;

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
		boolean isBracelet = (stack.getItem() instanceof ConverterBracelet);
		for (int i = 0; i < caps.size(); i++) {
			ItemCapability<?> cap = caps.get(i).get();
			this.capabilities[i] = cap;
			cap.setWrapper(this);
			if (isBracelet) {
				KleinBridgeManaItem braceletManaItem = new ConverterBracelet.KleinBridgeManaItem(stack);
				if (cap instanceof ManaItemCapabilityWrapper) {
					ManaItemCapabilityWrapper wrapper = (ManaItemCapabilityWrapper) cap;
					wrapper.manaItem = braceletManaItem;
				}
				if (cap instanceof CurioItemCapabilityButBetter) {
					CurioItemCapabilityButBetter wrapper = (CurioItemCapabilityButBetter) cap;
					wrapper.kleinManaItem = braceletManaItem;
				}
			}
		}
	}

	public MGTKCapabilityProvider(ItemStack stack, ItemCapability<?>... caps) {
		super(stack, caps);
		itemStack = stack;
		this.capabilities = caps;
		//boolean isKlein = (stack.getItem() instanceof IItemEmcHolder);
		for (ItemCapability<?> cap : this.capabilities) {
			cap.setWrapper(this);
			if (cap instanceof ManaItemCapabilityWrapper) {
				ManaItemCapabilityWrapper manaWrapper = (ManaItemCapabilityWrapper) cap;
				manaWrapper.manaItem = new ConverterBracelet.KleinBridgeManaItem(stack);
			}
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