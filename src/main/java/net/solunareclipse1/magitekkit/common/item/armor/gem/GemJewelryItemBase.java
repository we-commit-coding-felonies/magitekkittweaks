package net.solunareclipse1.magitekkit.common.item.armor.gem;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import moze_intel.projecte.gameObjs.items.IFireProtector;
import moze_intel.projecte.gameObjs.items.armor.GemHelmet;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.handlers.InternalTimers;

import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.api.item.IAlchShield;
import net.solunareclipse1.magitekkit.common.item.armor.VoidArmorItem;
import net.solunareclipse1.magitekkit.common.item.armor.VoidArmorItem.VoidArmorMaterial;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.util.EmcHelper;

/**
 * Helmet
 * 
 * @author solunareclipse1
 */
public class GemJewelryItemBase extends VoidArmorItem implements IAlchShield, IFireProtector {
	public GemJewelryItemBase(EquipmentSlot slot, Properties props, float baseDr) {
		super(GemJewelryMaterial.MAT, slot, props, baseDr);
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		if ((stack.getMaxDamage() - stack.getDamageValue()) - 1 < amount) {
			entity.level.playSound(null, entity, SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 2.0F, 1.0F);
		}
		return amount;
	}
	
	/**
	 * Common tick function for all 4 pieces
	 * called in onArmorTick
	 * <p>
	 * returns players avaliable emc so we dont have to call getAvaliableEmc() multiple times per tick
	 * 
	 * @param stack The armor piece ItemStack
	 * @param level The level
	 * @param player The player with the armor
	 * @return The avaliable EMC in the player's inventory
	 */
	protected long jewelryTick(ItemStack stack, Level level, Player player) {
		long plrEmc = EmcHelper.getAvaliableEmc(player);
		
		// slow, expensive auto-repair
		if (stack.isDamaged() && level.getGameTime() % 200 == 0 && player instanceof ServerPlayer) {
			if (plrEmc >= 65536) {
				plrEmc -= EmcHelper.consumeAvaliableEmc(player, 65536);
				stack.hurt(-1, player.getRandom(), (ServerPlayer) player);
			}
		}
		
		return plrEmc;
	}


	// IFireProtector
	// only if full set + undamaged
	@Override
	public boolean canProtectAgainstFire(ItemStack stack, ServerPlayer player) {return fullPristineSet(player);}
	
	
	
	
	
	
	// IAlchShield stuff
	@Override
	public boolean shieldCondition(Player player, float damage, DamageSource source, ItemStack stack) {
    	if (isUnblockableSource(source)) return false; // Unblockable damages
    	return fullPristineSet(player);
    }
	
	/**
	 * Checks if a DamageSource should not be blocked
	 * 
	 * @param source The DamageSource to check
	 * @return
	 */
	public boolean isUnblockableSource(DamageSource source) {
		if (source.isCreativePlayer() || source == DamageSource.OUT_OF_WORLD ||
			source == DamageSource.DROWN ||
			source == DamageSource.FREEZE ||
			source == DamageSource.IN_WALL ||
			source == DamageSource.STARVE) {
			return true;
		}
		else return false;
	}

	public boolean fullPristineSet(Player player) {
		for (ItemStack stack : player.getArmorSlots()) {
			if (stack.getItem() instanceof GemJewelryItemBase && !stack.isDamaged()) continue;
			else return false;
		}
		return true;
	}
	
	
	
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
}
