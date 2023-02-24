package net.solunareclipse1.magitekkit.common.item.armor.gem;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import moze_intel.projecte.gameObjs.items.armor.GemHelmet;

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
}
