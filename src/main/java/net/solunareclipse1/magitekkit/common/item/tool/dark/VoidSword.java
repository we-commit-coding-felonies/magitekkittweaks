package net.solunareclipse1.magitekkit.common.item.tool.dark;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import net.solunareclipse1.magitekkit.api.item.IEnchantmentSynergizer;
import net.solunareclipse1.magitekkit.api.item.IMGTKItem;

public class VoidSword extends SwordItem implements IMGTKItem, IEnchantmentSynergizer {
	public VoidSword(Tier tier, int damage, float speed, Properties props) {
		super(tier, damage, speed, props);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tips, isAdvanced);

		tips.add(new TranslatableComponent("tip.mgtk.enchsynergy"));
		double bonus = calculateBonus(stack);
		if (bonus > 0 && shouldApplyBonus(stack)) {
			Component typeText = new TranslatableComponent("tip.mgtk.enchbonus."+getBonusType(stack).toString());
			Component bonusText = new TranslatableComponent("tip.mgtk.enchbonus", ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(bonus), typeText);
			tips.add(bonusText);
		}
		tips.add(new TextComponent(""));
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {return 0;}
	
	@Override
	public Multimap<Attribute,AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		Multimap<Attribute,AttributeModifier> attribs = super.getAttributeModifiers(slot, stack);
		double bonusDamage = calculateBonus(stack);
		if (bonusDamage > 0 && shouldApplyBonus(stack) && slot == EquipmentSlot.MAINHAND) {
			double baseDamage = 0;
			// filters out base damage, which we modify then apply later
			Builder<Attribute,AttributeModifier> extra = ImmutableMultimap.builder();
			for (Entry<Attribute,Collection<AttributeModifier>> attr : attribs.asMap().entrySet()) {
				if (attr.getKey() == Attributes.ATTACK_DAMAGE) {
					for (AttributeModifier mod : attr.getValue()) {
						if (mod.getOperation() == AttributeModifier.Operation.ADDITION) {
							baseDamage += mod.getAmount();
						} else {
							extra.put(Attributes.ATTACK_DAMAGE, mod);
						}
					}
				} else {
					extra.putAll(attr.getKey(), attr.getValue());
				}
			}
			Builder<Attribute,AttributeModifier> bonus = ImmutableMultimap.builder();
			bonus.put(Attributes.ATTACK_DAMAGE,
					new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "enchantment synergy - weapon", baseDamage + bonusDamage, AttributeModifier.Operation.ADDITION));
			bonus.putAll(extra.build());
			attribs = bonus.build();
		}
		return attribs;
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}
	
	@Override
	public BonusType getBonusType(ItemStack stack) {
		return BonusType.WEAPON;
	}

	@Override
	public double calculateBonus(ItemStack stack) {
		return getBonusStrength(stack);
	}

}
