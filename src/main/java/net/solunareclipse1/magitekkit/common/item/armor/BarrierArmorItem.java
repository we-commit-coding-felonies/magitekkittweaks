package net.solunareclipse1.magitekkit.common.item.armor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

import net.solunareclipse1.magitekkit.api.item.IAlchShield;

/**
 * PTArmors with IAlchShield
 * 
 * @author solunareclipse1
 */
public class BarrierArmorItem extends AlchemicalArmorItem implements IAlchShield {
	public BarrierArmorItem(ArmorMaterial mat, EquipmentSlot slot, float drAmount, int maxBurnOut, Properties props) {
		super(mat, slot, drAmount, maxBurnOut, props);
	}
	
	@Override
	public boolean shieldCondition(Player player, float damage, DamageSource source, ItemStack stack) {
    	if (source.isCreativePlayer() || source == DamageSource.OUT_OF_WORLD) return false; // Unblockable damages
    	return fullPristineSet(player);
    }
	
	public boolean fullPristineSet(Player player) {
		for (ItemStack stack : player.getArmorSlots()) {
			if (stack.getItem() instanceof BarrierArmorItem && !stack.getItem().isBarVisible(stack)) continue;
			else return false;
		}
		return true;
	}
}
