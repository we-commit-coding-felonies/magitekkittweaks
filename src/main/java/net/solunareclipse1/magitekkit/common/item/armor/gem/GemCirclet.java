package net.solunareclipse1.magitekkit.common.item.armor.gem;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import moze_intel.projecte.handlers.InternalTimers;

import net.solunareclipse1.magitekkit.api.item.IAlchShield;

/**
 * Helmet
 * 
 * @author solunareclipse1
 */
public class GemCirclet extends GemJewelryItemBase implements IAlchShield {
	public GemCirclet(Properties props, float baseDr) {
		super(EquipmentSlot.HEAD, props, baseDr);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tips, isAdvanced);
		tips.add(new TranslatableComponent("tip.mgtk.gem_circlet"));
	}

	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		if (!level.isClientSide && !stack.isDamaged()) {
			if (fullPristineSet(player)) {
				// set bonus stuff
			}
			player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, true, false));
			player.setAirSupply(player.getMaxAirSupply());
		}
	}
}
