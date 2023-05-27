package net.solunareclipse1.magitekkit.common.item.armor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Level;

import moze_intel.projecte.capability.ItemCapability;

import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.api.item.IDamageReducer;
import net.solunareclipse1.magitekkit.api.item.IEnchantmentSynergizer;
import net.solunareclipse1.magitekkit.api.item.IMGTKItem;
import net.solunareclipse1.magitekkit.common.misc.damage.MGTKDmgSrc;
import net.solunareclipse1.magitekkit.common.misc.damage.MGTKDmgSrc.IMGTKDamageSource;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.util.EntityHelper;

import mekanism.common.registries.MekanismDamageSource;

public class VoidArmorBase extends ArmorItem implements IMGTKItem, IDamageReducer, IEnchantmentSynergizer {
	private float baseDr;
	
	/** Damage sources with corresponging DR multipliers. 0.5 would mean 1/2 DR */
	public static final Map<DamageSource, Float> DMG_SRC_MODS_DR = new HashMap<>();
	/** Damage sources in here will *never* be affected by DR */
	public static DamageSource[] dmgSrcBlacklistDr = {
			DamageSource.DROWN,
			DamageSource.FREEZE,
			DamageSource.OUT_OF_WORLD,
			DamageSource.STARVE,
			MekanismDamageSource.RADIATION,
			DamageSource.WITHER
	};
	/**
	 * Semiclone of Dark Matter armor. <br>
	 * Has reduced damage reduction when enchanted. <br>
	 * Cannot be enchanted with protection enchantments.
	 * 
	 * @param slot The EquipmentSlot this item belongs in
	 * @param baseDR The base amount of Damage Reduction this item provides
	 * @param props The properties of the item
	 */
	public VoidArmorBase(ArmorMaterial mat, EquipmentSlot slot, Properties props, float baseDr) {
		super(mat, slot, props);
		this.baseDr = baseDr;

		DMG_SRC_MODS_DR.put(DamageSource.LIGHTNING_BOLT, 0.99f/4f);
		DMG_SRC_MODS_DR.put(DamageSource.ANVIL, 0.1f/4f);
		DMG_SRC_MODS_DR.put(DamageSource.badRespawnPointExplosion(), 0.8f/4f);
		DMG_SRC_MODS_DR.put(MekanismDamageSource.LASER, 0.12f/4f);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tips, isAdvanced);

		tips.add(new TranslatableComponent("tip.mgtk.enchsynergy"));
		double bonus = calculateBonus(stack)*100d;
		if (bonus > 0 && shouldApplyBonus(stack)) {
			Component typeText = new TranslatableComponent("tip.mgtk.enchbonus."+getBonusType(stack).toString());
			Component bonusText = new TranslatableComponent("tip.mgtk.enchbonus", ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(bonus), typeText);
			tips.add(bonusText);
		}
		tips.add(new TextComponent(""));
	}
	
	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		//if (level.getGameTime() % 160 != 0) return;
		//level.playSound(null, player, SoundEvents.BEACON_AMBIENT, SoundSource.PLAYERS, 0.3F, 0.5F);
	}
	
	private static final List<Supplier<ItemCapability<?>>> supportedCapabilities = new ArrayList<>();
	
	@Override
	public double calculateBonus(ItemStack stack) {
		return getBonusStrength(stack);
	}

	@Override
	public BonusType getBonusType(ItemStack stack) {
		return BonusType.ARMOR;
	}
	
	public float getBaseDr() {
		return baseDr;
	}
	
	/**
	 * Gets the current damage reduction value this itemstack can provide
	 * 
	 * @param stack the stack 
	 * @return
	 */
	public float getDr(ItemStack stack, DamageSource source) {
		if (sourceCanBeReduced(source)) {
			return (float) (getDrForSource(source) + calculateBonus(stack));
		}
		return 0;
	}
	
	public static boolean sourceCanBeReduced(DamageSource source) {
		// hardcoded checks for things that should absolutely never be blocked
		if (source.isCreativePlayer()
			|| source.isBypassInvul()
			|| EntityHelper.isDamageSourceInfinite(source)) {
			return false;
		}
		if (source instanceof IMGTKDamageSource src && src.isBypassDr()) return false;
		
		for (int i = 0; i < dmgSrcBlacklistDr.length; i++) {
			if (source == dmgSrcBlacklistDr[i]) return false;
		}
		return true;
	}
	
	public float getDrForSource(DamageSource source) {
		// explicit overrides
		if (DMG_SRC_MODS_DR.containsKey(source)) {
			return DMG_SRC_MODS_DR.get(source);
		}
		
		float dr = getBaseDr();
		if (source.isBypassArmor()) dr *= 0.9;
		if (source.isMagic() || source.isBypassMagic()) dr *= 0.75;
		if (source.isFire()) dr *= 1.1;
		if (source instanceof IMGTKDamageSource src) {
			if (src.isPlasma()) dr *= 1.1;
			if (src.isAlchemy()) dr -= 0.2;
		}
		return dr;
	}
	
	@Override
	public boolean isEnchantable(@NotNull ItemStack stack) {return true;}
	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {return true;}
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment ench) {
		if (ench instanceof ProtectionEnchantment) return false;
		return super.canApplyAtEnchantingTable(stack, ench);
	}
	
	// using projecte textures for texture pack compatability
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return slot != EquipmentSlot.LEGS ?
			"projecte:textures/models/armor/dark_matter_layer_1.png":
			"projecte:textures/models/armor/dark_matter_layer_2.png";
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
		public SoundEvent getEquipSound() {return EffectInit.ARMOR_EQUIP.get();}
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


	@Override
	public @NotNull List<Supplier<ItemCapability<?>>> getSupportedCaps() {
		return supportedCapabilities;
	}
}
