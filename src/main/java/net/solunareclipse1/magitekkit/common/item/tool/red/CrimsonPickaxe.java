package net.solunareclipse1.magitekkit.common.item.tool.red;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IModeChanger;
import moze_intel.projecte.capability.ExtraFunctionItemCapabilityWrapper;
import moze_intel.projecte.capability.ItemCapability;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;

import net.solunareclipse1.magitekkit.api.item.IEmpowerItem;
import net.solunareclipse1.magitekkit.api.item.IStaticSpeedBreaker;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.init.NetworkInit;
import net.solunareclipse1.magitekkit.network.packet.client.DrawParticleAABBPacket;
import net.solunareclipse1.magitekkit.network.packet.client.DrawParticleAABBPacket.ParticlePreset;
import net.solunareclipse1.magitekkit.util.MiscHelper;
import net.solunareclipse1.magitekkit.util.TextHelper;
import net.solunareclipse1.magitekkit.util.BoxHelper;
import net.solunareclipse1.magitekkit.util.ColorsHelper.Color;

import vazkii.botania.common.helper.ItemNBTHelper;

import net.solunareclipse1.magitekkit.api.capability.wrapper.ChargeItemCapabilityWrapperButBetter;
import net.solunareclipse1.magitekkit.api.item.ICapabilityItem;

public class CrimsonPickaxe extends PickaxeItem implements ICapabilityItem, IModeChanger, IEmpowerItem, IExtraFunction, IStaticSpeedBreaker {
	public CrimsonPickaxe(Tier tier, int damage, float speed, Properties props) {
		super(tier, damage, speed, props);
		addItemCapability(ModeChangerItemCapabilityWrapper::new);
		addItemCapability(ChargeItemCapabilityWrapperButBetter::new);
		addItemCapability(ExtraFunctionItemCapabilityWrapper::new);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		super.appendHoverText(stack, level, tips, flags);
		tips.add(new TextComponent(""));
		
		Component funcKeyText = ClientKeyHelper.getKeyName(PEKeybind.EXTRA_FUNCTION).copy().withStyle(ChatFormatting.AQUA);
		Component modeKeyText = ClientKeyHelper.getKeyName(PEKeybind.MODE).copy().withStyle(ChatFormatting.AQUA);
		// Style(color, bold, italic, underline, strikethrough, obfuscated, clickevent, hoverevent, insertion, font)
		tips.add(new TranslatableComponent("tip.mgtk.crimson.pickaxe").withStyle(ChatFormatting.UNDERLINE)); // Flavor
		tips.add(new TranslatableComponent("tip.mgtk.crimson.pickaxe.1", funcKeyText)); // Keys
		//TextHelper.appendSafetyTooltip(true, tips, modeKeyText);
		TextHelper.appendSpeedTooltip(getMode(stack), tips, modeKeyText);
		appendEmpowerTooltip(stack, level, tips, flags);
	}
	
	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return onlyChargeHasChanged(oldStack, newStack);
    }
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		if (slotChanged) return true;
		return onlyChargeHasChanged(oldStack, newStack);
	}

	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		int charge = getCharge(stack);
		if (charge > 0) {
			int toLeak = level.getGameTime() % 3 == 0 ? 1 : 0;
			if (toLeak > 0) {
				setCharge(stack, charge-toLeak);
				if (toLeak == 1)
					level.playSound(null, entity.blockPosition(), EffectInit.EMC_LEAK.get(), entity.getSoundSource(), 1, 1);
			}
		}
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack) {
		return getCharge(stack) > 0;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.round((float)getCharge(stack) * 13f / (float)getMaxChargePower(stack));
	}
	
	@Override
	public int getBarColor(ItemStack stack) {
		Color color;
		switch (getStage(stack)) {
		default:
		case 1:
			color = Color.RED_MATTER;
			break;
		case 2:
			color = Color.COVALENCE_GREEN;
			break;
		case 3:
			color = Color.COVALENCE_TEAL;
			break;
		case 4:
			color = Color.COVALENCE_BLUE;
			break;
		}
		return color.I;
	}

	@Override
	public boolean doExtraFunction(@NotNull ItemStack stack, @NotNull Player player, @Nullable InteractionHand hand) {
		int charge = getCharge(stack);
		boolean didDo = false;
		if (charge > 0) {
			int stage = getStage(stack);
			int size = 5 + 5*stage;
			AABB area = AABB.ofSize(player.getBoundingBox().getCenter(), size, size, size);
			didDo = MiscHelper.aoeOreCollect(player, area, player.level, stack);
			if (didDo) {
				PlayerHelper.swingItem(player, hand);
				setCharge(stack, getTotalChargeForStage(stack, stage-1));
				player.level.playSound(null, player.blockPosition(), PESoundEvents.CHARGE.get(), SoundSource.PLAYERS, 1, 1f);
				if (player.level instanceof ServerLevel lvl) {
					double rot1 = (double)(-Mth.sin(player.getYRot() * ((float)Math.PI / 180f)));
					double rot2 = (double)Mth.cos(player.getYRot() * ((float)Math.PI / 180f));
					lvl.sendParticles(ParticleTypes.SWEEP_ATTACK, player.getX()+rot1, player.getY(0.5), player.getZ()+rot2, 1, 0, 0, 0, 0);
				}
			}
		}
		return didDo;
	}

	@Override
	public byte getMode(@NotNull ItemStack stack) {
		return ItemNBTHelper.getByte(stack, TAG_BREAKSPEED, (byte)0);
	}

	@Override
	public boolean changeMode(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
		byte mode = getMode(stack);
		byte newMode = (byte)(mode >= 2 ? 0 : mode+1);
		Style speedStyle = Style.EMPTY;
		switch (newMode) {
		case 0:
			speedStyle = speedStyle.withColor(Color.RED_MATTER.I);
			break;
		case 1:
			speedStyle = speedStyle.withColor(Color.COVALENCE_TEAL.I);
			break;
		case 2:
			speedStyle = speedStyle.withColor(Color.COVALENCE_GREEN_TRUE.I);
			break;
		}
		ItemNBTHelper.setByte( stack, TAG_BREAKSPEED, newMode );
		player.displayClientMessage(new TranslatableComponent("tip.mgtk.crimson.tool.speed.hud",
				new TranslatableComponent("tip.mgtk.crimson.tool.speed.mode."+newMode).withStyle(speedStyle)), true);
		return true;
	}
	
	@Override
	public int blockBreakSpeedInTicks(ItemStack stack, BlockState state) {
		if (stack.isCorrectToolForDrops(state) && getCharge(stack) > 0) {
			return getMode(stack);
		}
		return 0;
	}
	
	
	
	
	
	
	@Override
	public @NotNull List<Supplier<ItemCapability<?>>> getSupportedCaps() {
		return supportedCapabilities;
	}
	private static final List<Supplier<ItemCapability<?>>> supportedCapabilities = new ArrayList<>();

}
