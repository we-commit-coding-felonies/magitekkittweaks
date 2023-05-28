package net.solunareclipse1.magitekkit.api.item;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.PEKeybind;

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
		return getMaxStages();
	}
	
	/**
	 * the highest amount of stages this item can have
	 * @return
	 */
	default int getMaxStages() {
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
		return (float)getCharge(stack) / (float)getMaxChargePower(stack);
	}
	
	@Override
	default boolean changeCharge(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
		// TODO: figure out if manual charge reduction should be allowed
		boolean sneaking = false;//player.isShiftKeyDown();
		int charge = getCharge(stack);
		int currentStage = getStage(stack);
		int stages = getNumCharges(stack);
		boolean shouldTry = !player.getCooldowns().isOnCooldown(stack.getItem()) && ( (sneaking && charge > 0) || (!sneaking && currentStage < stages) );
		if (shouldTry) {
			// plrEmc doesnt matter if were reducing charge, so we do this to prevent unnecessary inventory scanning
			long plrEmc = sneaking ? 0 : EmcHelper.getAvaliableEmc(player);
			long toConsume = 0;
			int desiredCharge = getTotalChargeForStage(stack, sneaking ? currentStage-1 : currentStage+1);
			int newCharge = charge;
			if (sneaking) {
				newCharge = desiredCharge;
			} else if (plrEmc > 0) {
				toConsume = 8*(desiredCharge-charge);
				if (plrEmc >= toConsume) {
					newCharge = desiredCharge;
				} else {
					newCharge = (int) (charge + plrEmc/8);
					toConsume = 8*(newCharge-charge);
				}
			}
			if (newCharge != charge) {
				if (!sneaking) {
					EmcHelper.consumeAvaliableEmc(player, toConsume);
				}
				player.level.playSound(null, player, sneaking ? EffectInit.EMC_WASTE.get() : PESounds.CHARGE, SoundSource.PLAYERS, 0.8f, 0.5f + 0.5f / stages * currentStage);
				setCharge(stack, newCharge);
				return true;
			}
		}
		return false;
	}
	
	default int getTotalChargeForStage(ItemStack stack, int stage) {
		return stage * (getMaxChargePower(stack)/getNumCharges(stack));
	}
	
	default void appendEmpowerTooltip(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		tips.add(new TextComponent(" "));
		Component empowerKeyText = ClientKeyHelper.getKeyName(PEKeybind.CHARGE).copy().withStyle(ChatFormatting.AQUA);
		tips.add(new TranslatableComponent("tip.mgtk.crimson.empower.1")); // Info
		tips.add(new TranslatableComponent("tip.mgtk.crimson.empower.2", empowerKeyText)); // Mode
	}
	
	/**
	 * @param oldStack
	 * @param newStack
	 * @return true if the only change was empowerment charge
	 */
	default boolean onlyChargeHasChanged(ItemStack oldStack, ItemStack newStack) {
		if (!newStack.is(oldStack.getItem()))
			return true;

		CompoundTag newTag = newStack.getTag();
		CompoundTag oldTag = oldStack.getTag();

		if (newTag == null || oldTag == null)
			return !(newTag == null && oldTag == null);

		Set<String> newKeys = new HashSet<>(newTag.getAllKeys());
		Set<String> oldKeys = new HashSet<>(oldTag.getAllKeys());

		newKeys.remove(TAG_EMPOWERMENT);
		oldKeys.remove(TAG_EMPOWERMENT);

		if (!newKeys.equals(oldKeys))
			return true;

		return !newKeys.stream().allMatch(key -> Objects.equals(newTag.get(key), oldTag.get(key)));
	}
}
