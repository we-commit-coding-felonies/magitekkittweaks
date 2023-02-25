package net.solunareclipse1.magitekkit.common.item.armor;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Level;

import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;

import net.solunareclipse1.magitekkit.MagiTekkit;

public class VoidArmorItem extends ArmorItem {
	private float baseDr;
	/**
	 * Semiclone of Dark Matter armor. <br>
	 * Has reduced damage reduction when enchanted. <br>
	 * Cannot be enchanted with protection enchantments.
	 * 
	 * @param slot The EquipmentSlot this item belongs in
	 * @param baseDR The base amount of Damage Reduction this item provides
	 * @param props The properties of the item
	 */
	public VoidArmorItem(ArmorMaterial mat, EquipmentSlot slot, Properties props, float baseDr) {
		super(mat, slot, props);
		this.baseDr = baseDr;
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		System.out.println(entity.getName());
		entity.level.playSound(null, entity, PESoundEvents.POWER.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
		return 0;
	}
	
	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		//if (level.getGameTime() % 160 != 0) return;
		//level.playSound(null, player, SoundEvents.BEACON_AMBIENT, SoundSource.PLAYERS, 0.3F, 0.5F);
	}

	/**
	 * Gets the max possible damage reduction value of this item
	 * 
	 * @return Damage Reduction percentage
	 */
	public float getDrMax() {return this.baseDr;}
	
	/**
	 * Gets the current damage reduction value this itemstack can provide
	 * 
	 * @param stack the stack 
	 * @return
	 */
	public float getDr(ItemStack stack) {return this.baseDr;}
	
	@Override
	public boolean isEnchantable(@NotNull ItemStack stack) {return true;}
	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {return true;}
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment ench) {
		if (ench instanceof ProtectionEnchantment) return false;
		System.out.println(PECapabilities.EMC_STORAGE_CAPABILITY);
		return true;
	}
	
	
	
	
	public static class VoidArmorMaterial implements ArmorMaterial {
		public static final VoidArmorMaterial MAT = new VoidArmorMaterial();
		@Override
		public int getDefenseForSlot(@NotNull EquipmentSlot slot) {
			switch(slot) {
			case HEAD:
				return 3;
			case CHEST:
				return 8;
			case LEGS:
				return 6;
			case FEET:
				return 3;
			default:
				return 0;
			}
		}
		@Override
		public int getDurabilityForSlot(@NotNull EquipmentSlot slot) {return 0;}
		@Override
		public int getEnchantmentValue() {return 50;}
		@NotNull
		@Override
		public SoundEvent getEquipSound() {return SoundEvents.AMBIENT_BASALT_DELTAS_ADDITIONS;}
		@NotNull
		@Override
		public Ingredient getRepairIngredient() {return Ingredient.EMPTY;}
		@NotNull
		@Override
		public String getName() {return new ResourceLocation(MagiTekkit.MODID, "void_armor").toString();}
		@Override
		public float getToughness() {return 5;}
		@Override
		public float getKnockbackResistance() {return 0.25F;}
	}
}
