package net.solunareclipse1.magitekkit.common.item.armor.gem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.Explosion.BlockInteraction;

import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.ModList;

import moze_intel.projecte.capability.ItemCapability;
import moze_intel.projecte.capability.ItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.items.IFireProtector;
import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.api.item.IAlchShield;
import net.solunareclipse1.magitekkit.common.event.EntityLivingEventHandler;
import net.solunareclipse1.magitekkit.common.item.armor.VoidArmorItem;
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
			if (stack.getItem() instanceof GemAmulet && LivingEntity.getEquipmentSlotForItem(stack) == EquipmentSlot.CHEST) {
				entity.level.playSound(null, entity.blockPosition(), EffectInit.ARMOR_BREAK.get(), SoundSource.PLAYERS, 1, 1);
				GemAmulet amulet = (GemAmulet) stack.getItem();
				float multiplier = (float)amulet.getStoredEmc(stack) / (float)amulet.getMaximumEmc(stack);
				stack.shrink(1);
				entity.level.explode(null, entity.getX(), entity.getY(), entity.getZ(), 64*multiplier, BlockInteraction.BREAK);
				//if (multiplier > 0.075) {
					//entity.setHealth(0);
				//}
				//WorldHelper.createNovaExplosion(ent.level, ent, ent.position().x, ent.position().y, ent.position().z, 128*multiplier);
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
		if (stack.isDamaged() && level.getGameTime() % 200 == 0 && player instanceof ServerPlayer) {
			if (plrEmc >= 65536) {
				//plrEmc -= EmcHelper.consumeAvaliableEmc(player, 65536);
				//stack.hurt(-1, player.getRandom(), (ServerPlayer) player);
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
		if (EntityLivingEventHandler.isUnblockableSource(source)) return false; // Unblockable damages
    	return fullPristineSet(player);
    }

	public boolean fullPristineSet(Player player) {
		for (ItemStack stack : player.getArmorSlots()) {
			if (stack.getItem() instanceof GemJewelryItemBase && !stack.isDamaged()) continue;
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
	
	
	

	

	// capability stuff
	// TODO: understand
	private final List<Supplier<ItemCapability<?>>> supportedCapabilities = new ArrayList<>();
	
	protected void addItemCapability(Supplier<ItemCapability<?>> capabilitySupplier) {
		supportedCapabilities.add(capabilitySupplier);
	}

	protected void addItemCapability(String modid, Supplier<Supplier<ItemCapability<?>>> capabilitySupplier) {
		if (ModList.get().isLoaded(modid)) {
			supportedCapabilities.add(capabilitySupplier.get());
		}
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
		if (supportedCapabilities.isEmpty()) {
			return super.initCapabilities(stack, nbt);
		}
		return new ItemCapabilityWrapper(stack, supportedCapabilities);
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
