package net.solunareclipse1.magitekkit.common.item.armor.gem;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
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
import net.solunareclipse1.magitekkit.common.item.tool.BandOfArcana;
import net.solunareclipse1.magitekkit.util.Constants;
import net.solunareclipse1.magitekkit.config.EmcCfg.Gem.Feet;
import net.solunareclipse1.magitekkit.util.EmcHelper;

/**
 * Boots
 * 
 * @author solunareclipse1
 */
public class GemAnklet extends GemJewelryBase implements IFlightProvider, IStepAssister {
	public GemAnklet(Properties props, float baseDr) {
		super(EquipmentSlot.FEET, props, baseDr);
	}

	private static final AttributeModifier JESUS_SPEED = new AttributeModifier("Walking on liquid", 0.3, AttributeModifier.Operation.ADDITION);
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tips, isAdvanced);
		tips.add(new TranslatableComponent("tip.mgtk.gem.ref.4").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
	}

	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		GemJewelrySetInfo set = jewelryTick(stack, level, player);
		long plrEmc = set.plrEmc();
		if (level.isClientSide) {
			// Client
			if (!stack.isDamaged()) {
				if (!player.getAbilities().flying && !player.isFallFlying()) {
					attemptGustFlight(player, level);
					if (set.legs().exists() && plrEmc >= Constants.EmcCosts.JEWELRY_JESUS && jesusTick(player, set.legs().pristine())) {
						//plrEmc -= EmcHelper.consumeAvaliableEmc(player, Feet.JESUS.get());
					}
				}
			}
		} else {
			// Server
			if (!stack.isDamaged()) {
				ServerPlayer plr = (ServerPlayer)player;
				if (!player.getAbilities().flying && !player.isFallFlying()) {
					if (plr.fallDistance > 0) {
						plr.fallDistance = 0;
						plrEmc -= EmcHelper.consumeAvaliableEmc(player, Feet.FLIGHT.get());
					}
					if (plrEmc >= Constants.EmcCosts.JEWELRY_JESUS && jesusTick(player, set.legs().pristine())) {
						plrEmc -= EmcHelper.consumeAvaliableEmc(player, Feet.JESUS.get());
					}
				} else {
					jesusTick(player, set.legs().pristine()); // keeps updating waterwalk speed bonus
					if (player.getAbilities().flying) {
						plrEmc -= EmcHelper.consumeAvaliableEmc(player, Feet.FLIGHT.get());
					}
				}
			}
		}
	}
	
	private void attemptGustFlight(Player player, Level level) {
		Vec3 newVec = player.getDeltaMovement();
		if (isJumpPressed()) {
			newVec = newVec.add(0, 0.1, 0);
		}
		if (!player.isOnGround()) {
			if (newVec.y() <= 0) {
				newVec = newVec.multiply(1, 0.9, 1);
			}
			//AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
			//double timeAccelBonus = 0;
			//if (moveSpeed.getModifier(BandOfArcana.TIME_ACCEL_UUID) != null) {
			//	timeAccelBonus = moveSpeed.getModifier(BandOfArcana.TIME_ACCEL_UUID).getAmount();
			//}
			if (player.zza < 0) {
				newVec = newVec.multiply(0.9, 1, 0.9);
			} else if (player.zza > 0 && newVec.lengthSqr() < 3) {
				newVec = newVec.multiply(1.1, 1, 1.1);
			}
		}
		player.setDeltaMovement(newVec);
	}
	private static boolean isJumpPressed() {
		return DistExecutor.unsafeRunForDist(() -> () -> Minecraft.getInstance().options.keyJump.isDown(), () -> () -> false);
	}
	
	/**
	 * projecte decided to hardcode this, because my life needed to be made harder
	 * @param player
	 */
	private boolean jesusTick(Player player, boolean speed) {
		boolean didDo = false;
		boolean fly = player.getAbilities().flying || player.isFallFlying();
		int x = (int)Math.floor(player.getX());
		int y = (int)(player.getY() - player.getMyRidingOffset());
		int z = (int)Math.floor(player.getZ());
		BlockPos pos = new BlockPos(x, y, z);
		boolean fluidUnder = !player.level.getFluidState(pos.below()).isEmpty();
		Vec3 vel = player.getDeltaMovement();
		if (fluidUnder && player.level.isEmptyBlock(pos)) {
			if (!fly && vel.y <= 0 && !player.isShiftKeyDown()) {
				player.setDeltaMovement(vel.multiply(1,0,1));
				player.fallDistance = 0.0F;
				player.setOnGround(true);
				didDo = true;
			}
		}

		if (!player.level.isClientSide) {
			AttributeInstance attribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
			if (attribute != null) {
				if (speed && !fly && vel.y <= 0 && fluidUnder && player.level.isEmptyBlock(pos)) {
					if (!attribute.hasModifier(JESUS_SPEED)) {
						attribute.addTransientModifier(JESUS_SPEED);
					}
				} else if (attribute.hasModifier(JESUS_SPEED)) {
					attribute.removeModifier(JESUS_SPEED);
				}
			}
		}
		return didDo;
	}

	@Override
	public boolean canAssistStep(ItemStack stack, ServerPlayer player) {
		return !stack.isDamaged() && !player.isCrouching();
	}

	@Override
	public boolean canProvideFlight(ItemStack stack, ServerPlayer player) {
		return !stack.isDamaged() && getInfo(player, EquipmentSlot.LEGS).pristine();
	}
}
