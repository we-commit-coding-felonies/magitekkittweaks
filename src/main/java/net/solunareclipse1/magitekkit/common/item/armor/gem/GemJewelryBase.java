package net.solunareclipse1.magitekkit.common.item.armor.gem;

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

import moze_intel.projecte.gameObjs.items.IFireProtector;
import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.api.item.IAlchShield;
import net.solunareclipse1.magitekkit.common.event.EntityLivingEventHandler;
import net.solunareclipse1.magitekkit.common.item.armor.VoidArmorBase;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.util.EmcHelper;

import vazkii.botania.api.mana.IManaDiscountArmor;

/**
 * Helmet
 * 
 * @author solunareclipse1
 */
public class GemJewelryBase extends VoidArmorBase implements IAlchShield, IFireProtector, IManaDiscountArmor {
	public GemJewelryBase(EquipmentSlot slot, Properties props, float baseDr) {
		super(GemJewelryMaterial.MAT, slot, props, baseDr);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return Mth.hsvToRgb(0, 1, 1);
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
		//if (stack.isDamaged() && level.getGameTime() % 200 == 0 && player instanceof ServerPlayer) {
		//	if (plrEmc >= 65536) {
		//		plrEmc -= EmcHelper.consumeAvaliableEmc(player, 65536);
		//		stack.hurt(-1, player.getRandom(), (ServerPlayer) player);
		//	}
		//}
		
		return plrEmc;
	}


	// IFireProtector
	// only if full set + undamaged
	@Override
	public boolean canProtectAgainstFire(ItemStack stack, ServerPlayer player) {return fullPristineSet(player);}
	
	
	
	
	
	
	// IAlchShield stuff
	@Override
	public boolean shieldCondition(Player player, float damage, DamageSource source, ItemStack stack) {
    	//System.out.println(source);
		if (EntityLivingEventHandler.isUnblockableSource(source)) return false; // Unblockable damages
    	return fullPristineSet(player);
    }

	public static boolean fullPristineSet(Player player) {
		for (ItemStack stack : player.getArmorSlots()) {
			if (stack.getItem() instanceof GemJewelryBase && !stack.isDamaged()) continue;
			else return false;
		}
		return true;
	}
	
	public long calcShieldingCost(Player player, float damage, DamageSource source, ItemStack stack) {
		float modifier = 1;
		if (source.isBypassMagic()) modifier = 1.5f;
		else if (source.isBypassArmor()) modifier = 1.1f;
		// cost = max( 64, (modifier*damage)^2 )
		return Math.round( Math.max( 64, Math.pow(modifier*damage, 2) ) );
	}
	
	public float calcAffordableDamage(Player player, float damage, DamageSource source, ItemStack stack, long emcHeld) {
		// does the inverse of calcShieldingCost()
		float mod = 1;
		if (source.isBypassMagic()) mod = 2/3;
		else if (source.isBypassArmor()) mod = 10/11;
		return (float) (mod*Math.sqrt(emcHeld));
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
}
