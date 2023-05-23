package net.solunareclipse1.magitekkit.common.item.tool.red;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IModeChanger;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.capability.ExtraFunctionItemCapabilityWrapper;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.capability.ProjectileShooterItemCapabilityWrapper;

import net.solunareclipse1.magitekkit.api.capability.wrapper.ChargeItemCapabilityWrapperButBetter;
import net.solunareclipse1.magitekkit.api.item.IEmpowerItem;
import net.solunareclipse1.magitekkit.api.item.IMagitekkitItem;
import vazkii.botania.common.helper.ItemNBTHelper;

public class CrimsonSword extends SwordItem implements IMagitekkitItem, IModeChanger, IEmpowerItem, IProjectileShooter, IExtraFunction {
	public CrimsonSword(Tier tier, int damage, float speed, Properties props) {
		super(tier, damage, speed, props);
		addItemCapability(ModeChangerItemCapabilityWrapper::new);
		addItemCapability(ChargeItemCapabilityWrapperButBetter::new);
		addItemCapability(ExtraFunctionItemCapabilityWrapper::new);
		addItemCapability(ProjectileShooterItemCapabilityWrapper::new);
	}

	private static final String TAG_KILLMODE = "kill_mode";
	private static final String TAG_SLASHING = "currently_slashing";

	@Override
	public boolean shootProjectile(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
		// TODO air slash projectile
		return false;
	}

	@Override
	public boolean doExtraFunction(@NotNull ItemStack stack, @NotNull Player player, @Nullable InteractionHand hand) {
		
		return false;
	}

	@Override
	public byte getMode(@NotNull ItemStack stack) {
		return ItemNBTHelper.getByte(stack, TAG_KILLMODE, (byte)0);
	}

	@Override
	public boolean changeMode(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
		byte mode = getMode(stack);
		player.displayClientMessage(new TranslatableComponent("tip.mgtk.rm.sword.killall."+getMode(stack)), true);
		ItemNBTHelper.setByte( stack, TAG_KILLMODE, (byte)(mode >= 3 ? 0 : mode+1) );
		return true;
	}

}
