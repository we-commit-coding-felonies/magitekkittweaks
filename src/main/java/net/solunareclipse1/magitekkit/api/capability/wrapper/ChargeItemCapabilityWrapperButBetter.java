package net.solunareclipse1.magitekkit.api.capability.wrapper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import moze_intel.projecte.capability.ChargeItemCapabilityWrapper;

/**
 * projecte moment (real, certified)
 * 
 * @author solunareclipse1
 */
public class ChargeItemCapabilityWrapperButBetter extends ChargeItemCapabilityWrapper {

	@Override
	public int getNumCharges(@NotNull ItemStack stack) {
		return getItem().getNumCharges(stack);
	}
	
	@Override
	public int getCharge(@NotNull ItemStack stack) {
		return getItem().getCharge(stack);
	}
	
	@Override
	public float getChargePercent(@NotNull ItemStack stack) {
		return getItem().getChargePercent(stack);
	}
	
	@Override
	public boolean changeCharge(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
		return getItem().changeCharge(player, stack, hand);
	}
	
	
}
