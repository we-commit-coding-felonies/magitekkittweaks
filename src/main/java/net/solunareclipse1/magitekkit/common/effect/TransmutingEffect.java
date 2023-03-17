package net.solunareclipse1.magitekkit.common.effect;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.solunareclipse1.magitekkit.common.item.armor.gem.GemAmulet;
import net.solunareclipse1.magitekkit.common.misc.MGTKDmgSrc;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.util.EmcHelper;
import net.solunareclipse1.magitekkit.util.ColorsHelper.Color;

public class TransmutingEffect extends MobEffect {
	
	// potion seller, i am going into battle...
	private static MobEffectInstance[] EFFECTS_OF_DOOM = {
			// Universal
			new MobEffectInstance(MobEffects.ABSORPTION, 60, 4, true, false),
			new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 4, true, false),
			new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 3, true, false),
			new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, true, false),
			new MobEffectInstance(MobEffects.GLOWING, 60, 0, true, false),
			new MobEffectInstance(MobEffects.INVISIBILITY, 60, 0, true, false),
			new MobEffectInstance(MobEffects.JUMP, -100, 255, true, false),
			new MobEffectInstance(MobEffects.LEVITATION, 60, 122, true, false),
			new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 122, true, false),
			new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 9, true, false),
			new MobEffectInstance(MobEffects.POISON, 60, 9, true, false),
			new MobEffectInstance(MobEffects.REGENERATION, 60, 5, true, false),
			new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, true, false),
			new MobEffectInstance(MobEffects.WATER_BREATHING, 60, 0, true, false),
			new MobEffectInstance(MobEffects.WEAKNESS, 60, 4, true, false),
			new MobEffectInstance(MobEffects.WITHER, 60, 2, true, false),
			
			// Player only,
			new MobEffectInstance(MobEffects.BAD_OMEN, 60, 233, true, false),
			new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, true, false),
			new MobEffectInstance(MobEffects.CONFUSION, 60, 0, true, false),
			new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 3, true, false),
			new MobEffectInstance(MobEffects.DIG_SPEED, 60, 3, true, false),
			new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 60, 0, true, false),
			new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 60, 233, true, false),
			new MobEffectInstance(MobEffects.HUNGER, 60, 233, true, false),
			new MobEffectInstance(MobEffects.LUCK, 60, 233, true, false),
			new MobEffectInstance(MobEffects.NIGHT_VISION, 60, 0, true, false),
			new MobEffectInstance(MobEffects.UNLUCK, 60, 233, true, false)
	};

	public TransmutingEffect() {
		super(MobEffectCategory.NEUTRAL, Mth.hsvToRgb(Color.PHILOSOPHERS.H, Color.PHILOSOPHERS.S, Color.PHILOSOPHERS.V));
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		//System.out.println(amplifier);
		entity.invulnerableTime = 0;
		if (amplifier > 0) {
			entity.setHealth(entity.getHealth()/1.1f);
			entity.addEffect(new MobEffectInstance(EFFECTS_OF_DOOM[3]));
			entity.hurt(MGTKDmgSrc.TRANSMUTATION, 0.1f);
			if (entity instanceof ServerPlayer player) {
				//EmcHelper.consumeAvaliableEmc(player, Long.MAX_VALUE);
			}
			return;
		}
		if (entity.level.isClientSide()) return;
		//System.out.println("pee");
		super.applyEffectTick(entity, amplifier);
		
		int rangeOfEffects = 15;
		
		if (entity instanceof ServerPlayer player) {
			ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
			if (chestplate.getItem() instanceof GemAmulet amulet) {
				amulet.leakEmc(chestplate, player.level, player, EmcHelper.getAvaliableEmc(player));
			}
			rangeOfEffects = EFFECTS_OF_DOOM.length; // if we are a player more effects are avaliable
		}
		
		if (entity.getRandom().nextInt(12) == 0) {
			int amount = entity.getRandom().nextInt(4);
			for (int i = 0; i < amount; i++) {
				MobEffectInstance fx = new MobEffectInstance(EFFECTS_OF_DOOM[entity.getRandom().nextInt(rangeOfEffects)]);
				//System.out.println(fx);
				entity.addEffect(fx);
			}
		} else if (entity.getRandom().nextInt(2048) == 0) {
			entity.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
			entity.addEffect(new MobEffectInstance(EffectInit.TRANSMUTING.get(), 300, 1));
			entity.setRemainingFireTicks(Integer.MAX_VALUE); // :)
		}
	}
	
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return duration > 1;
	}
}
