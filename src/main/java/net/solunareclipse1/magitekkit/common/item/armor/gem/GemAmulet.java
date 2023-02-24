package net.solunareclipse1.magitekkit.common.item.armor.gem;

import java.util.List;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage.EmcAction;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.utils.EMCHelper;

import net.solunareclipse1.magitekkit.api.item.IAlchShield;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.util.EmcHelper;

/**
 * Chestplate
 * 
 * @author solunareclipse1
 */
public class GemAmulet extends GemJewelryItemBase implements IAlchShield, IItemEmcHolder {
	public GemAmulet(Properties props, float baseDr) {
		super(EquipmentSlot.CHEST, props, baseDr);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tips, isAdvanced);
		tips.add(new TranslatableComponent("tip.mgtk.gem_amulet"));
	}
	
	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		if (level.getGameTime() % 160 == 0) { // nested if statement to we dont run shieldCondition() and getAvaliableEmc every tick
			if (shieldCondition(player, 1f, DamageSource.GENERIC, stack) && EmcHelper.getAvaliableEmc(player) > 0) {
				level.playSound(player, player, EffectInit.SHIELD_AMBIENT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
			}
		}
	}

	
	
	// Built-in klein star stuff
	// Modified from KleinStar.java
	// https://github.com/sinkillerj/ProjectE/blob/mc1.18.x/src/main/java/moze_intel/projecte/gameObjs/items/KleinStar.java

	@Override
	public long insertEmc(@NotNull ItemStack stack, long toInsert, EmcAction action) {
		if (toInsert < 0) {
			return extractEmc(stack, -toInsert, action);
		}
		long toAdd = Math.min(getNeededEmc(stack), toInsert);
		if (action.execute()) {
			ItemPE.addEmcToStack(stack, toAdd);
		}
		return toAdd;
	}
	
	@Override
	public long extractEmc(@NotNull ItemStack stack, long toExtract, EmcAction action) {
		if (toExtract < 0) {
			return insertEmc(stack, -toExtract, action);
		}
		long storedEmc = getStoredEmc(stack);
		long toRemove = Math.min(storedEmc, toExtract);
		if (action.execute()) {
			ItemPE.setEmc(stack, storedEmc - toRemove);
		}
		return toRemove;
	}

	@Override
	public @Range(from = 1, to = Long.MAX_VALUE) long getMaximumEmc(@NotNull ItemStack stack) {return 384000;}

	@Override
	public @Range(from = 0, to = Long.MAX_VALUE) long getStoredEmc(@NotNull ItemStack stack) {
		return ItemPE.getEmc(stack);
	}
	
	
	
	
	// Built-in mana tablet stuff
	// TODO: implement this
}
