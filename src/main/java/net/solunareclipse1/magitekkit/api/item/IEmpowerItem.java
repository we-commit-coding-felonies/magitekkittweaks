package net.solunareclipse1.magitekkit.api.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.capabilities.item.IItemCharge;

import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.util.EmcHelper;

import vazkii.botania.common.helper.ItemNBTHelper;

/**
 * IItemCharge but costs emc to charge it <br>
 * see crimson tools for an example use
 * @author solunareclipse1
 */
public interface IEmpowerItem extends IItemCharge {
	public static final String TAG_EMPOWERMENT = "empowerment_charge";
	
	@Override
	default int getNumCharges(@NotNull ItemStack stack) {
		return 4;
	}
	
	default int getStage(ItemStack stack) {
		double charge = getCharge(stack);
		int step = getMaxChargePower(stack)/getNumCharges(stack);
		if (charge == 0) return 0;
		else return (int) Math.ceil(charge/step);
	}
	
	default int getMaxChargePower(ItemStack stack) {
		return 128;
	}
	
	@Override
	default int getCharge(@NotNull ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_EMPOWERMENT, 0);
	}
	
	default void setCharge(ItemStack stack, int amount) {
		ItemNBTHelper.setInt(stack, TAG_EMPOWERMENT, amount);
	}
	
	@Override
	default float getChargePercent(@NotNull ItemStack stack) {
		return getCharge(stack) / getMaxChargePower(stack);
	}
	
	@Override
	default boolean changeCharge(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
		boolean sneaking = player.isShiftKeyDown();
		int charge = getCharge(stack);
		int maxCharge = getMaxChargePower(stack);
		boolean shouldTry = (sneaking && charge > 0) || (!sneaking && charge < maxCharge);
		if (shouldTry) {
			// plrEmc doesnt matter if were reducing charge, so we do this to prevent unnecessary inventory scanning
			long plrEmc = sneaking ? 0 : EmcHelper.getAvaliableEmc(player);
			int stages = getNumCharges(stack);
			int limit = maxCharge / stages;
			int newCharge = charge;
			if (sneaking) {
				newCharge = Math.max(0, charge-limit);
			} else if (plrEmc > 0) {
				int toAdd = (int) Math.min(limit, plrEmc);
				newCharge = Math.min(maxCharge, charge+toAdd);
			}
			if (newCharge != charge) {
				if (!sneaking) {
					EmcHelper.consumeAvaliableEmc(player, newCharge-charge);
				}
				player.level.playSound(null, player, sneaking ? EffectInit.EMC_WASTE.get() : PESounds.CHARGE, SoundSource.PLAYERS, 1, 0.5f + 0.5f / stages * getStage(stack));
				setCharge(stack, newCharge);
				return true;
			}
		}
		return false;
	}
}
