package net.solunareclipse1.magitekkit.common.item.armor.gem;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import net.minecraftforge.fml.DistExecutor;

import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.IStepAssister;

import net.solunareclipse1.magitekkit.api.item.IAlchShield;

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

	private static boolean isJumpPressed() {
		return DistExecutor.unsafeRunForDist(() -> () -> Minecraft.getInstance().options.keyJump.isDown(), () -> () -> false);
	}

	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		if (!level.isClientSide) {
			ServerPlayer playerMP = (ServerPlayer) player;
			playerMP.fallDistance = 0;
		} else {
			if (!player.getAbilities().flying && isJumpPressed()) {
				player.setDeltaMovement(player.getDeltaMovement().add(0, 0.1, 0));
			}
			if (!player.isOnGround()) {
				if (player.getDeltaMovement().y() <= 0) {
					player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0.9, 1));
				}
				if (!player.getAbilities().flying) {
					if (player.zza < 0) {
						player.setDeltaMovement(player.getDeltaMovement().multiply(0.9, 1, 0.9));
					} else if (player.zza > 0 && player.getDeltaMovement().lengthSqr() < 3) {
						player.setDeltaMovement(player.getDeltaMovement().multiply(1.1, 1, 1.1));
					}
				}
			}
		}
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
