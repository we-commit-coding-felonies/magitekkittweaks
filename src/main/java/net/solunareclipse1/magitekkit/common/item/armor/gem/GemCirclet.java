package net.solunareclipse1.magitekkit.common.item.armor.gem;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.equipment.bauble.ItemThirdEye;

/**
 * Helmet
 * 
 * @author solunareclipse1
 */
public class GemCirclet extends GemJewelryBase {
	public GemCirclet(Properties props, float baseDr) {
		super(EquipmentSlot.HEAD, props, baseDr);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tips, isAdvanced);
		tips.add(new TranslatableComponent("tip.mgtk.gem_circlet").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
	}

	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		if (!level.isClientSide && !stack.isDamaged()) {
			if (fullPristineSet(player)) {
				// set bonus stuff
			}
			player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, true, false));
			((ItemThirdEye) ModItems.thirdEye).onWornTick(stack, player);
			player.setAirSupply(player.getMaxAirSupply());
		}
	}
}
