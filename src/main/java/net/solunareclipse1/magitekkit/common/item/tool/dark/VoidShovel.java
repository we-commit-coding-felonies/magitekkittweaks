package net.solunareclipse1.magitekkit.common.item.tool.dark;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.solunareclipse1.magitekkit.api.item.IEnchantmentSynergizer;

public class VoidShovel extends ShovelItem implements IEnchantmentSynergizer {
	public VoidShovel(Tier tier, int damage, float speed, Properties props) {
		super(tier, damage, speed, props);
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
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		float speed = super.getDestroySpeed(stack, state);
		if (speed > 1) {
			double bonus = calculateBonus(stack);
			if (bonus > 0 && shouldApplyBonus(stack)) {
				speed += speed*bonus;
			}
		}
		return speed;
	}
	
	@Override
	public Multimap<Attribute,AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		Multimap<Attribute,AttributeModifier> attribs = super.getAttributeModifiers(slot, stack);
		double bonusSpeed = calculateBonus(stack);
		if (bonusSpeed > 0 && shouldApplyBonus(stack) && slot == EquipmentSlot.MAINHAND) {
			double baseSpeed = 0;
			// filters out base damage, which we modify then apply later
			Builder<Attribute,AttributeModifier> extra = ImmutableMultimap.builder();
			for (Entry<Attribute,Collection<AttributeModifier>> attr : attribs.asMap().entrySet()) {
				if (attr.getKey() == Attributes.ATTACK_SPEED) {
					for (AttributeModifier mod : attr.getValue()) {
						if (mod.getOperation() == AttributeModifier.Operation.ADDITION) {
							baseSpeed += mod.getAmount();
						} else {
							extra.put(Attributes.ATTACK_SPEED, mod);
						}
					}
				} else {
					extra.putAll(attr.getKey(), attr.getValue());
				}
			}
			Builder<Attribute,AttributeModifier> bonus = ImmutableMultimap.builder();
			bonus.putAll(extra.build());
			bonus.put(Attributes.ATTACK_SPEED,
					new AttributeModifier(BASE_ATTACK_SPEED_UUID, "enchantment synergy - tool", baseSpeed + Math.abs(baseSpeed*bonusSpeed), AttributeModifier.Operation.ADDITION));
			attribs = bonus.build();
		}
		return attribs;
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {return 0;}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public double calculateBonus(ItemStack stack) {
		return getBonusStrength(stack)/10d;
	}

	@Override
	public BonusType getBonusType(ItemStack stack) {
		return BonusType.TOOL;
	}

}
