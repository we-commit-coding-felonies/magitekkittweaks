package net.solunareclipse1.magitekkit.common.item.armor.gem;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.fml.DistExecutor;

import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.IStepAssister;
import moze_intel.projecte.gameObjs.items.armor.GemFeet;
import moze_intel.projecte.gameObjs.registries.PEItems;

import net.solunareclipse1.magitekkit.common.item.tool.BandOfArcana;

/**
 * Boots
 * 
 * @author solunareclipse1
 */
public class GemAnklet extends GemJewelryBase implements IFlightProvider, IStepAssister {
	public GemAnklet(Properties props, float baseDr) {
		super(EquipmentSlot.FEET, props, baseDr);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tips, isAdvanced);
		tips.add(new TranslatableComponent("tip.mgtk.gem_anklet").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
	}

	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		//long plrEmc = jewelryTick(stack, level, player); //
		
		// Set-bonus
		//if (fullPristineSet(player)) {
		//}

		// Standalone
		if (!stack.isDamaged()) {
			attemptGustFlight(player, level);
			//PEItems.GEM_BOOTS.get().onArmorTick(stack, level, player);
		}
	}
	
	private void attemptGustFlight(Player player, Level level) {
		if (!level.isClientSide) {
			ServerPlayer playerMP = (ServerPlayer)player;
			playerMP.fallDistance = 0.0F;
		} else {
			Vec3 newVec = player.getDeltaMovement();
			if (!player.getAbilities().flying && isJumpPressed()) {
				newVec = newVec.add(0, 0.1, 0);
			}
			if (!player.isOnGround()) {
				if (newVec.y() <= 0) {
					newVec = newVec.multiply(1, 0.9, 1);
				}
				if (!player.getAbilities().flying) {
					AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
					double timeAccelBonus = 0;
					if (moveSpeed.getModifier(BandOfArcana.TIME_ACCEL_UUID) != null) {
						timeAccelBonus = moveSpeed.getModifier(BandOfArcana.TIME_ACCEL_UUID).getAmount();
					}
					if (player.zza < 0) {
						newVec = newVec.multiply(0.9, 1, 0.9);
					} else if (player.zza > 0 && newVec.lengthSqr() < 3 + (1*timeAccelBonus)) {
						newVec = newVec.multiply(1.1, 1, 1.1);
					}
				}//
			}
			player.setDeltaMovement(newVec);
		}
	}
	private static boolean isJumpPressed() {
		return DistExecutor.unsafeRunForDist(() -> () -> Minecraft.getInstance().options.keyJump.isDown(), () -> () -> false);
	}

	@Override
	public boolean canAssistStep(ItemStack stack, ServerPlayer player) {
		return !player.isCrouching() && fullPristineSet(player);
	}

	@Override
	public boolean canProvideFlight(ItemStack stack, ServerPlayer player) {
		return fullPristineSet(player);
	}
}
