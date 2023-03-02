package net.solunareclipse1.magitekkit.common.item.armor;

import org.jetbrains.annotations.NotNull;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.api.item.IBurnoutItem;

public class CrimsonArmor extends VoidArmorBase implements IBurnoutItem {
	
	/**
	 * VoidArmor that weakens with consecutive attacks & regenerates over time
	 * 
	 * @param mat The material of the armor
	 * @param slot The slot the item goes in
	 * @param props The item's properties
	 * @param maxDR The maximum amount of damage reduction this item can provide
	 */
	public CrimsonArmor(ArmorMaterial mat, EquipmentSlot slot, Properties props, float maxDr) {
		super(mat, slot, props, maxDr);
	}

	@Override
	public int getBurnoutMax() {
		return 16384;
	}

	@Override
	public float getDr(ItemStack stack) {
		return (getBurnout(stack)/getBurnoutMax())*getDrMax();
	}
	
	@Override
	public boolean isEnchantable(@NotNull ItemStack stack) {return false;}
	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {return false;}
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment ench) {return false;}
	
	public static class CrimsonArmorMaterial implements ArmorMaterial {
		public static final CrimsonArmorMaterial MAT = new CrimsonArmorMaterial();
		@Override
		public int getDefenseForSlot(@NotNull EquipmentSlot slot) {
			switch(slot) {
			case HEAD:
				return 5;
			case CHEST:
				return 12;
			case LEGS:
				return 9;
			case FEET:
				return 4;
			default:
				return 0;
			}
		}
		@Override
		public int getDurabilityForSlot(@NotNull EquipmentSlot slot) {return 0;}
		@Override
		public int getEnchantmentValue() {return 0;}
		@NotNull
		@Override
		public SoundEvent getEquipSound() {return SoundEvents.AMBIENT_BASALT_DELTAS_ADDITIONS;}
		@NotNull
		@Override
		public Ingredient getRepairIngredient() {return Ingredient.EMPTY;}
		@NotNull
		@Override
		public String getName() {return new ResourceLocation(MagiTekkit.MODID, "crimson/").toString();}
		@Override
		public float getToughness() {return 5;}
		@Override
		public float getKnockbackResistance() {return 0.25F;}
	}
}
