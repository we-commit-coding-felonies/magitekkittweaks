package net.solunareclipse1.magitekkit.common.item.armor.gem;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion.BlockInteraction;

import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage.EmcAction;
import moze_intel.projecte.gameObjs.items.IFireProtector;
import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.api.capability.wrapper.HazmatCapabilityWrapper;
import net.solunareclipse1.magitekkit.api.item.IAlchShield;
import net.solunareclipse1.magitekkit.api.item.IHazmatItem;
import net.solunareclipse1.magitekkit.common.event.EntityLivingEventHandler;
import net.solunareclipse1.magitekkit.common.item.armor.VoidArmorBase;
import net.solunareclipse1.magitekkit.common.misc.MGTKDmgSrc;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.util.Constants.EmcCosts;

import mekanism.api.radiation.capability.IRadiationShielding;
import mekanism.common.registries.MekanismDamageSource;

import net.solunareclipse1.magitekkit.util.EmcHelper;
import net.solunareclipse1.magitekkit.util.EntityHelper;
import net.solunareclipse1.magitekkit.util.LoggerHelper;

import morph.avaritia.util.InfinityDamageSource;
import vazkii.botania.api.mana.IManaDiscountArmor;

/**
 * Helmet
 * 
 * @author solunareclipse1
 */
public class GemJewelryBase extends VoidArmorBase implements IAlchShield, IFireProtector, IManaDiscountArmor, IHazmatItem {
	public GemJewelryBase(EquipmentSlot slot, Properties props, float baseDr) {
		super(GemJewelryMaterial.MAT, slot, props, baseDr);
		addItemCapability(HazmatCapabilityWrapper::new);
		
		DMG_SRC_MODS_ALCHSHIELD.put(DamageSource.LIGHTNING_BOLT, 13f);
		DMG_SRC_MODS_ALCHSHIELD.put(DamageSource.ANVIL, 30f);
		DMG_SRC_MODS_ALCHSHIELD.put(DamageSource.badRespawnPointExplosion(), 8f);
		DMG_SRC_MODS_ALCHSHIELD.put(MekanismDamageSource.LASER, 1.2f);
	}

	/**
	 * convert radiation into durability / emc
	 * @param stack
	 * @return
	 */
	@Override
	public double protectionPercent(ItemStack stack) {
		if (stack.isDamaged()) {
			stack.setDamageValue(stack.getDamageValue() - 1);
		} else if (stack.getItem() instanceof GemAmulet amulet) {
			amulet.insertEmc(stack, 1, EmcAction.EXECUTE);
		}
		return 0.25 * ( 1 - (stack.getDamageValue() / stack.getMaxDamage()) );
	}
	
	/** Damage sources with corresponging cost multipliers. 0.5 would mean 1/2 cost */
	public static final Map<DamageSource, Float> DMG_SRC_MODS_ALCHSHIELD = new HashMap<>();
	/** Damage sources in here will *never* be blocked by the gem shield */
	public static DamageSource[] dmgSrcBlacklistAlchshield = {
			DamageSource.DROWN,
			DamageSource.FREEZE,
			DamageSource.OUT_OF_WORLD,
			DamageSource.STARVE,
			MekanismDamageSource.RADIATION
	};

	@Override
	public int getBarColor(ItemStack stack) {
		return 0x8f0000;
	}
	
	public float getDiscount(ItemStack stack, int slot, Player player, @Nullable ItemStack tool) {
		return stack.isDamaged() ? 0 : 0.16f;
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		if ((stack.getMaxDamage() - stack.getDamageValue()) - 1 < amount) {
			entity.level.playSound(null, entity, EffectInit.JEWELRY_BREAK.get(), SoundSource.PLAYERS, 2.0F, 1.0F);
			if (stack.getItem() instanceof GemAmulet && LivingEntity.getEquipmentSlotForItem(stack) == EquipmentSlot.CHEST) {
				GemAmulet amulet = (GemAmulet) stack.getItem();
				float multiplier = (float)amulet.getStoredEmc(stack) / (float)amulet.getMaximumEmc(stack);
				stack.shrink(1);
				entity.level.playSound(null, entity.blockPosition(), EffectInit.ARMOR_BREAK.get(), SoundSource.PLAYERS, 1*(2*multiplier), 1);
				entity.level.explode(null, entity.getX(), entity.getY(), entity.getZ(), 64*multiplier, BlockInteraction.BREAK);
			} else {
				stack.shrink(1);
			}
		}
		return amount;
	}
	
	/**
	 * Common tick function for all 4 pieces <br>
	 * gathers information about the wearer (such as pieces worn & inventory emc) for easy-access
	 * 
	 * @param stack The armor piece ItemStack
	 * @param level The level
	 * @param player The player with the armor
	 * @return Info about the gem set's current state
	 */
	protected GemJewelrySetInfo jewelryTick(ItemStack stack, Level level, Player player) {
		long plrEmc = EmcHelper.getAvaliableEmc(player);
		GemInfo head = getInfo(player, EquipmentSlot.HEAD),
				chest = getInfo(player, EquipmentSlot.CHEST),
				legs = getInfo(player, EquipmentSlot.LEGS),
				feet = getInfo(player, EquipmentSlot.FEET);
		boolean setBonus = head.id > 1 && chest.id > 1 && legs.id > 1 && feet.id > 1;
		return new GemJewelrySetInfo(head, chest, legs, feet, setBonus, plrEmc);
	}
	
	protected GemInfo getInfo(Player player, EquipmentSlot slot) {
		ItemStack stack = player.getItemBySlot(slot);
		if ( stack.isEmpty() || !(stack.getItem() instanceof GemJewelryBase) ) {
			return GemInfo.MISSING;
		} else if (stack.isDamaged()) {
			return GemInfo.BROKEN;
		} else {
			return GemInfo.PRISTINE;
		}
	}


	// IFireProtector
	@Override
	public boolean canProtectAgainstFire(ItemStack stack, ServerPlayer player) {
		return isBarrierActive(player);
	}

	// IAlchShield stuff
	@Override
	public boolean shieldCondition(Player player, float damage, DamageSource source, ItemStack stack) {
    	return sourceBlockedByGemShield(source) && isBarrierActive(player);
    }
	
	/** used in both canProtectAgainstFire and shieldCondition */
	public static boolean isBarrierActive(Player player) {
		if (fullPristineSet(player)) {
	    	return EmcHelper.hasEmc(player);
		}
		return false;
	}

	/**
	 * true if the player is wearing a full undamaged set of Gem Jewelry <br>
	 * used for set bonus logic
	 * @param player
	 * @return if player has the full set
	 */
	public static boolean fullPristineSet(Player player) {
		for (ItemStack stack : player.getArmorSlots()) {
			if (stack.getItem() instanceof GemJewelryBase && !stack.isDamaged()) continue;
			return false;
		}
		return true;
	}
	
	/**
	 * checks if a given damage source can be blocked by the gem shield
	 * @param source
	 * @return if the source can be blocked by the gem shield
	 */
	public static boolean sourceBlockedByGemShield(DamageSource source) {
		// hardcoded checks for things that should absolutely never be blocked
		if (source.isCreativePlayer()
			|| source.isBypassInvul()
			|| EntityHelper.isDamageSourceInfinite(source)) {
			return false;
		}
		if (source instanceof MGTKDmgSrc src && src.isBypassAlchShield()) return false;
		
		for (int i = 0; i < dmgSrcBlacklistAlchshield.length; i++) {
			if (source == dmgSrcBlacklistAlchshield[i]) return false;
		}
		return true;
	}
	
	/**
	 * Gets the cost multiplier for a given source <br>
	 * default to 1.0f (no multiplier)
	 * @param source
	 * @return
	 */
	public static float getCostMultiplierForSource(DamageSource source) {
		// explicit overrides
		if (DMG_SRC_MODS_ALCHSHIELD.containsKey(source)) {
			return DMG_SRC_MODS_ALCHSHIELD.get(source);
		}
		
		
		// overriders, biggest goes last so it takes priority
		float mult = 1f;
		if (source.isBypassArmor()) mult = 1.1f;
		if (source.isMagic() || source.isBypassMagic()) mult = 1.5f;
		if (source instanceof MGTKDmgSrc src) {
			if (src.isBypassDr()) mult = 2f;
			if (src.isDivine()) mult = 42f;
			// adders, order doesnt matter
			if (src.isAlchemy()) mult += 0.1f;
		}
		return mult;
	}
	
	public long calcShieldingCost(Player player, float damage, DamageSource source, ItemStack stack) {
		// (dmg*mod)^2 = emc
		return (long) Math.max(EmcCosts.ALCHSHIELD_MIN, Math.pow(damage*getCostMultiplierForSource(source), 2));
	}
	
	public float calcAffordableDamage(Player player, float damage, DamageSource source, ItemStack stack, long emcHeld) {
		// sqrt(emc)/mod = dmg
		return (float) (Math.sqrt(emcHeld)/getCostMultiplierForSource(source));
	}
	
	@Override
	public boolean isEnchantable(@NotNull ItemStack stack) {return false;}
	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {return false;}
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment ench) {return false;}
	
	
	// Material
	public static class GemJewelryMaterial implements ArmorMaterial {
		public static final GemJewelryMaterial MAT = new GemJewelryMaterial();
		@Override
		public int getDefenseForSlot(@NotNull EquipmentSlot slot) {return 1;}
		@Override
		public int getDurabilityForSlot(@NotNull EquipmentSlot slot) {return 96;}
		@Override
		public int getEnchantmentValue() {return 0;}
		@NotNull
		@Override
		public SoundEvent getEquipSound() {return SoundEvents.ARMOR_EQUIP_CHAIN;}
		@NotNull
		@Override
		public Ingredient getRepairIngredient() {return Ingredient.EMPTY;}
		@NotNull
		@Override
		public String getName() {
			return new ResourceLocation(MagiTekkit.MODID, "gem_jewelry").toString();
		}
		@Override
		public float getToughness() {return 0;}
		@Override
		public float getKnockbackResistance() {return 0.0f;}
	}
	
	record GemJewelrySetInfo(GemInfo head, GemInfo chest, GemInfo legs, GemInfo feet, boolean hasBonus, long plrEmc) {
		public GemInfo get(EquipmentSlot slot) {
			switch (slot) {
			case HEAD:
				return head;
			case CHEST:
				return chest;
			case LEGS:
				return legs;
			case FEET:
				return feet;
			default:
				LoggerHelper.printWarn("GemJewelrySetInfo.get()", "InvalidArmorSlot", slot.toString());
				return GemInfo.MISSING;
			}
		}
	}
	
	enum GemInfo {
		ACTIVE((byte)3), PRISTINE((byte)2), BROKEN((byte)1), MISSING((byte)0);
		
		public final byte id;
		private GemInfo(byte id) {
			this.id = id;
		}
		
		public boolean exists() {
			return id >= 1;
		}
		
		public boolean pristine() {
			return id >= 2;
		}
	}
}
