package net.solunareclipse1.magitekkit.common.item.armor.gem;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import net.minecraftforge.fml.DistExecutor;

import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.IStepAssister;
import moze_intel.projecte.gameObjs.items.armor.GemFeet;
import moze_intel.projecte.gameObjs.registries.PEItems;

/**
 * Boots
 * 
 * @author solunareclipse1
 */
public class GemAnklet extends GemJewelryBase implements IFlightProvider, IStepAssister {
	public GemAnklet(Properties props, float baseDr) {
		super(EquipmentSlot.FEET, props, baseDr);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tips, isAdvanced);
		tips.add(new TranslatableComponent("tip.mgtk.gem_anklet").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
	}

	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		//long plrEmc = jewelryTick(stack, level, player); //
		
		// Set-bonus
		//if (fullPristineSet(player)) {
		//}

		// Standalone
		if (!stack.isDamaged()) {
			PEItems.GEM_BOOTS.get().onArmorTick(stack, level, player);
		}
	}

	@Override
	public boolean canAssistStep(ItemStack stack, ServerPlayer player) {
		return !player.isCrouching() && fullPristineSet(player);
	}

	@Override
	public boolean canProvideFlight(ItemStack stack, ServerPlayer player) {
		return fullPristineSet(player);
	}
}
