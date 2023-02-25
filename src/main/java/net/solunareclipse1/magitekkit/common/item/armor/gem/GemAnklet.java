package net.solunareclipse1.magitekkit.common.item.armor.gem;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import net.solunareclipse1.magitekkit.api.item.IAlchShield;

/**
 * Boots
 * 
 * @author solunareclipse1
 */
public class GemAnklet extends GemJewelryItemBase implements IAlchShield {
	public GemAnklet(Properties props, float baseDr) {
		super(EquipmentSlot.FEET, props, baseDr);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tips, isAdvanced);
		tips.add(new TranslatableComponent("tip.mgtk.gem_anklet"));
	}
}
