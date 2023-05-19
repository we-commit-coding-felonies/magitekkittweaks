package net.solunareclipse1.magitekkit.common.item.armor;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.api.item.IBurnoutItem;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.init.ObjectInit;
import net.solunareclipse1.magitekkit.util.ColorsHelper;
import vazkii.botania.common.helper.ItemNBTHelper;

public class CrimsonArmor extends VoidArmorBase implements IBurnoutItem {
	private static final ItemStack[] DEGRADE_REPLACEMENTS = {
			new ItemStack(ObjectInit.VOID_HELM.get()),
			new ItemStack(ObjectInit.VOID_CHEST.get()),
			new ItemStack(ObjectInit.VOID_LEGS.get()),
			new ItemStack(ObjectInit.VOID_BOOTS.get())
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
	public void appendHoverText(ItemStack stack, Level level, List<Component> tips, TooltipFlag advanced) {
		tips.add(new TranslatableComponent("tip.mgtk.dyndr", getDr(stack, DamageSource.GENERIC)));
		tips.add(new TranslatableComponent("tip.mgtk.burnout", getBurnout(stack), getBurnoutMax()));
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
		return 16384;
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		//int burnout = getBurnout(stack) + amount;
		//if (burnout > getBurnoutMax()) {
		//	
		//}
		setBurnout(stack, getBurnout(stack) + amount);
		if (getBurnout(stack) > getBurnoutMax()) {
			ItemNBTHelper.setBoolean(stack, "burnout_overload", true);
		}
		return 0;
	}
	
	/**
	 * catastrophic armor failure
	 * @param event
	 */
	public void checkDegrade(LivingEquipmentChangeEvent event) {
		//ItemStack from = event.getFrom();
		ItemStack to = event.getTo();
		if ( to.getItem() instanceof CrimsonArmor armor && armor.getBurnout(to) > armor.getBurnoutMax() ) {
			Optional<IItemHandler> itemHandlerCap = event.getEntity().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.NORTH).resolve();
			if (itemHandlerCap.isPresent()) {
				ItemStack replacement;
				IItemHandler inv = itemHandlerCap.get();
				for (int i = 0; i < inv.getSlots(); i++) {
					ItemStack item = inv.getStackInSlot(i);
					if (ItemNBTHelper.getBoolean(item, "burnout_overload", false)) {
						EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(item);
						inv.extractItem(slot.getIndex(), 1, false);
						inv.insertItem(slot.getIndex(), DEGRADE_REPLACEMENTS[slot.getIndex()].copy(), false);
					}
				}
				//switch (event.getSlot()) {
				//
				//case HEAD: // 4
				//	replacement = new ItemStack(ObjectInit.VOID_HELM.get());
				//	break;
				//	
				//case CHEST: // 3
				//	replacement = new ItemStack(ObjectInit.VOID_HELM.get());
				//	break;
				//	
				//case LEGS: // 2
				//	replacement = new ItemStack(ObjectInit.VOID_HELM.get());
				//	break;
				//	
				//case FEET: // 1
				//	replacement = new ItemStack(ObjectInit.VOID_HELM.get());
				//	break;
				//
				//default:
				//	HashMap<String, String> info = new HashMap<>();
				//	info.put("Entity", event.getEntity()+"");
				//	info.put("From", event.getFrom()+"");
				//	info.put("To", event.getTo()+"");
				//	LoggerHelper.printWarn("CrimsonArmor.checkDegrade()", "UnknownSlotType", "Slot type " + event.getSlot() + " is not valid here!", info);
				//	return;
				//}
				//WorldHelper.createNovaExplosion(event.getEntity().level, event.getEntity(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), armor.getBurnout(to) - armor.getBurnoutMax());
				event.getEntity().level.playSound(null, event.getEntity().blockPosition(), EffectInit.ARMOR_BREAK.get(), event.getEntity().getSoundSource(), 1, 1);
				//for (int i = 0; i < inv.getSlots(); i++) {
				//}
			}
		}
	}

	@Override
	public float getDr(ItemStack stack, DamageSource source) {
		return super.getDr(stack, source) * (1 - getBurnoutPercent(stack));
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
		public int getDurabilityForSlot(@NotNull EquipmentSlot slot) {return Integer.MAX_VALUE;}
		@Override
		public int getEnchantmentValue() {return 0;}
		@NotNull
		@Override
		public SoundEvent getEquipSound() {return SoundEvents.AXE_WAX_OFF;}
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
