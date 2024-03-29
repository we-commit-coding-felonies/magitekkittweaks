package net.solunareclipse1.magitekkit.common.item.armor.gem;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.solunareclipse1.magitekkit.config.DebugCfg;
import net.solunareclipse1.magitekkit.util.Constants;
import net.solunareclipse1.magitekkit.util.EmcHelper;

/**
 * Helmet
 * 
 * @author solunareclipse1
 */
public class GemCirclet extends GemJewelryBase {
	public GemCirclet(Properties props, float baseDr) {
		super(EquipmentSlot.HEAD, props, baseDr);
	}
	
	@Override
	public boolean isEnderMask(ItemStack stack, Player player, EnderMan enderman) {
		return !isDamaged(stack);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tips, isAdvanced);
		tips.add(new TranslatableComponent("tip.mgtk.gem.ref.1").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
	}

	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		if (level.isClientSide) {
			// Client
		} else {
			// Server
			if (!stack.isDamaged()) {
				GemJewelrySetInfo set = jewelryTick(stack, level, player);
				long plrEmc = set.plrEmc();
				if (plrEmc >= Constants.EmcCosts.JEWELRY_XRAY) {
					plrEmc -= Constants.EmcCosts.JEWELRY_XRAY;
					nightVision(player);
				}
				if (set.hasBonus() && plrEmc >= Constants.EmcCosts.JEWELRY_XRAY) {
					// amount of affected, multiplied by cost per entity
					plrEmc -= entityXray(player, plrEmc/Constants.EmcCosts.JEWELRY_XRAY) * Constants.EmcCosts.JEWELRY_XRAY;
				}
				if (set.feet().pristine() && plrEmc >= Constants.EmcCosts.JEWELRY_BREATH && player.getAirSupply() <= 0) {
					plrEmc -= EmcHelper.consumeAvaliableEmc(player, Constants.EmcCosts.JEWELRY_BREATH);
					player.setAirSupply(player.getMaxAirSupply());
				}
			}
		}
		/*
		if (!level.isClientSide && !stack.isDamaged()) {
			if (fullPristineSet(player)) {
				// set bonus stuff
			}
			player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, true, false));
			((ItemThirdEye) ModItems.thirdEye).onWornTick(stack, player);
			player.setAirSupply(player.getMaxAirSupply());
		}*/
	}
	
	private int entityXray(Player player, long max) {
		double range = 64;
		AABB box = //new AABB(player.getX(), player.getY(), player.getZ(), player.getX(), player.getY(), player.getZ()).inflate(range);
			AABB.ofSize(player.getEyePosition(), range/4, range/4, range/4).expandTowards(player.getLookAngle().scale(range));
		if (DebugCfg.XRAY_HITBOX.get()) {
			//NetworkInit.toClient(new DrawParticleAABBPacket(new Vec3(box.minX, box.minY, box.minZ), new Vec3(box.maxX, box.maxY, box.maxZ), ParticlePreset.DEBUG), (ServerPlayer)player);
		}
		List<LivingEntity> mobs = player.level.getEntitiesOfClass(LivingEntity.class, box, ent -> !ent.is(player));

		int applied = 0;
		for (LivingEntity ent : mobs) {
			if (applied >= max) break;
			MobEffectInstance potion = ent.getEffect(MobEffects.GLOWING);
			if ((potion == null || potion.getDuration() <= 2)) {
				ent.addEffect(new MobEffectInstance(MobEffects.GLOWING, 12, 0, true, false));
				applied++;
			}
		}
		return applied;
	}
	
	private void nightVision(Player player) {
		player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, true, false));
	}
}
