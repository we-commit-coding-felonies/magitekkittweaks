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
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IModeChanger;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.capability.ExtraFunctionItemCapabilityWrapper;
import moze_intel.projecte.capability.ItemCapability;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.capability.ProjectileShooterItemCapabilityWrapper;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;

import net.solunareclipse1.magitekkit.api.capability.wrapper.ChargeItemCapabilityWrapperButBetter;
import net.solunareclipse1.magitekkit.api.item.ICapabilityItem;
import net.solunareclipse1.magitekkit.api.item.IEmpowerItem;
import net.solunareclipse1.magitekkit.api.item.IStaticSpeedBreaker;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.util.MiscHelper;
import net.solunareclipse1.magitekkit.util.TextHelper;
import net.solunareclipse1.magitekkit.util.ColorsHelper.Color;

import vazkii.botania.common.helper.ItemNBTHelper;

public class CrimsonHoe extends HoeItem implements ICapabilityItem, IModeChanger, IEmpowerItem, IExtraFunction, IStaticSpeedBreaker, IProjectileShooter {
	public CrimsonHoe(Tier tier, int damage, float speed, Properties props) {
		super(tier, damage, speed, props);
		addItemCapability(ModeChangerItemCapabilityWrapper::new);
		addItemCapability(ProjectileShooterItemCapabilityWrapper::new);
		addItemCapability(ChargeItemCapabilityWrapperButBetter::new);
		addItemCapability(ExtraFunctionItemCapabilityWrapper::new);
	}
	
	public static final String TAG_OPERATION = "hyperscythe_mode";
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		super.appendHoverText(stack, level, tips, flags);
		tips.add(new TextComponent(""));
		
		Component funcKeyText = ClientKeyHelper.getKeyName(PEKeybind.EXTRA_FUNCTION).copy().withStyle(ChatFormatting.AQUA);
		Component projKeyText = ClientKeyHelper.getKeyName(PEKeybind.FIRE_PROJECTILE).copy().withStyle(ChatFormatting.AQUA);
		Component modeKeyText = ClientKeyHelper.getKeyName(PEKeybind.MODE).copy().withStyle(ChatFormatting.AQUA);
		Component shiftKeyText = Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.AQUA);
		Component safetyKeyText = new TranslatableComponent("tip.mgtk.keycombo", shiftKeyText, modeKeyText);
		String funcModeLang = "tip.mgtk.crimson.hoe.mode."+getOperation(stack);
		Component funcDescText = new TranslatableComponent(funcModeLang+".full");
		Component opText = new TranslatableComponent(funcModeLang).withStyle(ChatFormatting.BLUE);
		// Style(color, bold, italic, underline, strikethrough, obfuscated, clickevent, hoverevent, insertion, font)
		tips.add(new TranslatableComponent("tip.mgtk.crimson.hoe").withStyle(ChatFormatting.UNDERLINE)); // Flavor
		tips.add(new TranslatableComponent("tip.mgtk.crimson.hoe.1", funcKeyText, funcDescText, projKeyText)); // Keys
		tips.add(new TranslatableComponent("tip.mgtk.crimson.hoe.2", opText)); // Keys
		TextHelper.appendSafetyTooltip(getSafety(stack), tips, safetyKeyText);
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
	public boolean shootProjectile(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
		return changeOperation(player, stack);
	}

	@Override
	public boolean doExtraFunction(@NotNull ItemStack stack, @NotNull Player player, @Nullable InteractionHand hand) {
		boolean didDo = MiscHelper.aoeOreCollect(player, player.getBoundingBox().inflate(3), player.level, stack);
		if (didDo) PlayerHelper.swingItem(player, hand);
		return didDo;
	}
	
	public byte getOperation(ItemStack stack) {
		return ItemNBTHelper.getByte(stack, TAG_OPERATION, (byte)0);
	}
	
	public boolean changeOperation(Player player, ItemStack stack) {
		byte mode = getOperation(stack);
		byte newMode = (byte)(mode >= 2 ? 0 : mode+1);
		Style opStyle = Style.EMPTY.withColor(ChatFormatting.BLUE);
		ItemNBTHelper.setByte( stack, TAG_OPERATION, newMode );
		player.displayClientMessage(new TranslatableComponent("tip.mgtk.crimson.hoe.hud",
				new TranslatableComponent("tip.mgtk.crimson.hoe.mode."+newMode).withStyle(opStyle)), true);
		return true;
	}

	@Override
	public byte getMode(@NotNull ItemStack stack) {
		return ItemNBTHelper.getByte(stack, TAG_BREAKSPEED, (byte)0);
	}
	
	public boolean getSafety(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, "crimson_safety", true);
	}
	
	public void setSafety(ItemStack stack, boolean safety) {
		ItemNBTHelper.setBoolean(stack, "crimson_safety", safety);
	}

	@Override
	public boolean changeMode(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
		if (player.isShiftKeyDown()) {
			boolean safety = !getSafety(stack);
			setSafety(stack, safety);
			ChatFormatting safetyStyle = safety ? ChatFormatting.GREEN : ChatFormatting.RED;
			player.displayClientMessage(new TranslatableComponent("tip.mgtk.crimson.tool.hud",
					new TranslatableComponent("tip.mgtk.crimson.tool.mode."+safety).withStyle(safetyStyle)), true);
			return true;
		}
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