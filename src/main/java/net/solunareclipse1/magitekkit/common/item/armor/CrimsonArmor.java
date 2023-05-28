package net.solunareclipse1.magitekkit.common.item.armor;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;

import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.api.item.IBurnoutItem;
import net.solunareclipse1.magitekkit.common.misc.NukeDamageCalculator;
import net.solunareclipse1.magitekkit.common.misc.damage.MGTKDmgSrc;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.init.ObjectInit;
import net.solunareclipse1.magitekkit.util.ColorsHelper;
import net.solunareclipse1.magitekkit.util.EntityHelper;
import net.solunareclipse1.magitekkit.util.LoggerHelper;
import net.solunareclipse1.magitekkit.util.MiscHelper;

import vazkii.botania.common.helper.ItemNBTHelper;

public class CrimsonArmor extends VoidArmorBase implements IBurnoutItem {
	private static final ItemStack[] DEGRADE_REPLACEMENTS = {
			new ItemStack(ObjectInit.VOID_BOOTS.get()),
			new ItemStack(ObjectInit.VOID_LEGS.get()),
			new ItemStack(ObjectInit.VOID_CHEST.get()),
			new ItemStack(ObjectInit.VOID_HELM.get())
	};
	
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
		MinecraftForge.EVENT_BUS.addListener(this::checkDegrade);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		superAppendHoverText(stack, level, tips, flags);
		tips.add(new TextComponent(" "));
		tips.add(new TranslatableComponent("tip.mgtk.crimson.armor").withStyle(ChatFormatting.UNDERLINE)); // Flavor
		tips.add(new TranslatableComponent("tip.mgtk.crimson.armor.1")); //
		tips.add(new TranslatableComponent("tip.mgtk.crimson.armor.2")); // info
		tips.add(new TranslatableComponent("tip.mgtk.crimson.armor.3")); //
		float dr = getDr(stack, DamageSource.GENERIC)*100;
		Component drText = new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(dr)+"%").withStyle(ChatFormatting.GREEN);
		tips.add(new TranslatableComponent("tip.mgtk.dyndr", drText));
		Style style = Style.EMPTY.withColor(getBarColor(stack));
		Component burnoutText = new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(getBurnout(stack))).withStyle(style);
		Component maxText = new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(getBurnoutMax())).withStyle(ChatFormatting.DARK_RED);
		tips.add(new TranslatableComponent("tip.mgtk.burnout", burnoutText, maxText));
	}
	
	@Override
	public boolean isDamageable(ItemStack stack) {
		return true;
	}
	
	@Override
	public int getBarColor(ItemStack stack) {
		return ColorsHelper.covColorInt(1 - getBurnoutPercent(stack));
	}
	
	@Override
	public int getBarWidth(ItemStack stack) {
		return  (int)( 13f * (1f - getBurnoutPercent(stack)) );
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack) {
		return getBurnout(stack) > 0;
	}

	@Override
	public int getBurnoutMax() {
		return (int) (16384f * (getDefense()/30f));
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		int burnout = getBurnout(stack);
		setBurnout(stack, burnout + 8*amount);
		if (burnout+amount > getBurnoutMax()) {
			ItemNBTHelper.setBoolean(stack, "burnout_overload", true);
		}
		return 0;
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (level.isClientSide) return;
		int burnout = getBurnout(stack);
		//int leakTime = Math.round(12f - (9f*getBurnoutPercent(stack)));
		if (burnout > 0 && level.getGameTime() % 12 == 0) {
			setBurnout(stack, burnout-1);
			level.playSound(null, entity.blockPosition(), EffectInit.EMC_LEAK.get(), entity.getSoundSource(), 0.5f, 1);
		}
	}
	
	/**
	 * catastrophic armor failure
	 * @param event
	 */
	public void checkDegrade(LivingEquipmentChangeEvent event) {
		ItemStack to = event.getTo();
		if ( to.getItem() instanceof CrimsonArmor && ItemNBTHelper.getBoolean(to, "burnout_overload", false) ) {
			LivingEntity wearer = event.getEntityLiving();
			Optional<IItemHandler> itemHandlerCap = wearer.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.NORTH).resolve();
			if (itemHandlerCap.isPresent()) {
				IItemHandler inv = itemHandlerCap.get();
				int detPower = 0;
				for (int i = 0; i < inv.getSlots(); i++) {
					ItemStack stack = inv.getStackInSlot(i);
					if (stack.getItem() instanceof CrimsonArmor) {
						EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(stack);
						inv.extractItem(slot.getIndex(), stack.getCount(), false);
						ItemStack toInsert = DEGRADE_REPLACEMENTS[slot.getIndex()].copy();
						ItemStack inserted = inv.insertItem(slot.getIndex(), toInsert, false);
						if (inserted == toInsert) {
							LoggerHelper.printWarn("CrimsonArmor.checkDegrade()", "ReplaceFailed", "Replacing ["+stack+"] with ["+toInsert+"] failed!");
						}
						detPower++;
					}
				}
				DamageSource dmgSrc = MGTKDmgSrc.emcNuke(wearer);
				if (!EntityHelper.isInvincible(wearer)) {
					wearer.hurt(dmgSrc, Float.MAX_VALUE);
				}
				NukeDamageCalculator nukeCalc = new NukeDamageCalculator(1f/detPower);
				Vec3 cent = wearer.getBoundingBox().getCenter();
				wearer.level.explode(wearer, dmgSrc, nukeCalc, cent.x, cent.y, cent.z, 4.5f*detPower, true, Explosion.BlockInteraction.BREAK);
				wearer.level.playSound(null, wearer.blockPosition(), EffectInit.ARMOR_BREAK.get(), wearer.getSoundSource(), 1, 1);
			}
		}
	}

	@Override
	public float getDr(ItemStack stack, DamageSource source) {
		return super.getDr(stack, source) * (1 - 0.75f*getBurnoutPercent(stack));
	}
	
	@Override
	public boolean isEnchantable(@NotNull ItemStack stack) {return false;}
	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {return false;}
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment ench) {return false;}
	
	// using projecte textures for texture pack compatability
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return slot != EquipmentSlot.LEGS ?
			"projecte:textures/models/armor/red_matter_layer_1.png":
			"projecte:textures/models/armor/red_matter_layer_2.png";
	}
	
	@Override
	public double calculateBonus(ItemStack stack) {
		return 0;
	}
	
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
		public int getDurabilityForSlot(@NotNull EquipmentSlot slot) {return Integer.MAX_VALUE;}
		@Override
		public int getEnchantmentValue() {return 0;}
		@NotNull
		@Override
		public SoundEvent getEquipSound() {return EffectInit.ARMOR_EQUIP.get();}
		@NotNull
		@Override
		public Ingredient getRepairIngredient() {return Ingredient.EMPTY;}
		@NotNull
		@Override
		public String getName() {return new ResourceLocation(MagiTekkit.MODID, "crimson_armor").toString();}
		@Override
		public float getToughness() {return 5;}
		@Override
		public float getKnockbackResistance() {return 0.25F;}
	}
}
