package net.solunareclipse1.magitekkit.common.effect;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import moze_intel.projecte.gameObjs.registries.PEItems;

public class IceShieldEffect extends MobEffect {
	
	// immovable object
	private static final MobEffectInstance RESISTANCE = new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 10, 4);
	private static final MobEffectInstance FATIGUE = new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 30, 4);

	public IceShieldEffect() {
		super(MobEffectCategory.NEUTRAL, 0x00bfff);
	}
	
	@Override
	public List<ItemStack> getCurativeItems() {
		ArrayList<ItemStack> cures = new ArrayList<ItemStack>();
		cures.add(new ItemStack(PEItems.PHILOSOPHERS_STONE));
		return cures;
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		if (entity.isAlive()) {
			Vec3 vel = entity.getDeltaMovement();
			Vec3 mult = new Vec3(0, vel.y > 0 ? 0 : 1, 0);
			entity.setDeltaMovement(entity.getDeltaMovement().multiply(mult));
			if (entity.isInLava()) return;
			entity.addEffect(new MobEffectInstance(RESISTANCE));
			entity.addEffect(new MobEffectInstance(FATIGUE));
		}
	}
	
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return duration >= 1;
	}
}
