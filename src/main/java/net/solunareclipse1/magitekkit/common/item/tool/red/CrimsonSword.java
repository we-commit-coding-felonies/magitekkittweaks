package net.solunareclipse1.magitekkit.common.item.tool.red;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

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
import net.solunareclipse1.magitekkit.api.item.IEmpowerItem;
import net.solunareclipse1.magitekkit.api.item.IMGTKItem;
import net.solunareclipse1.magitekkit.data.MGTKEntityTags;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.util.ColorsHelper;
import net.solunareclipse1.magitekkit.util.ColorsHelper.Color;
import net.solunareclipse1.magitekkit.util.EntityHelper;
import net.solunareclipse1.magitekkit.util.MiscHelper;

import vazkii.botania.common.helper.ItemNBTHelper;

public class CrimsonSword extends SwordItem implements IMGTKItem, IModeChanger, IEmpowerItem, IExtraFunction {
	public CrimsonSword(Tier tier, int damage, float speed, Properties props) {
		super(tier, damage, speed, props);
		addItemCapability(ModeChangerItemCapabilityWrapper::new);
		addItemCapability(ChargeItemCapabilityWrapperButBetter::new);
		addItemCapability(ExtraFunctionItemCapabilityWrapper::new);
		addItemCapability(ProjectileShooterItemCapabilityWrapper::new);
	}
	
	public enum KillMode {
		HOSTILE(ent -> ent instanceof Enemy),
		HOSTILE_PLAYER(ent -> ent instanceof Enemy || ent instanceof Player),
		NOT_PLAYER(ent -> !(ent instanceof Player)),
		EVERYTHING(ent -> true);
		
		private Predicate<LivingEntity> test;
		private KillMode(Predicate<LivingEntity> test) {
			this.test = test;
		}
		
		public Predicate<LivingEntity> test() {
			return this.test;
		}
		
		public static KillMode byId(byte id) {
			switch (id) {
			default:
			case 0:
				return HOSTILE;
			case 1:
				return HOSTILE_PLAYER;
			case 2:
				return NOT_PLAYER;
			case 3:
				return EVERYTHING;
			}
		}
	}

	private static final String TAG_KILLMODE = "kill_mode";
	private static final String TAG_SLASHING = "slash_power";
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		super.appendHoverText(stack, level, tips, flags);
		tips.add(new TextComponent(""));
		
		Component autoslashKeyText = ClientKeyHelper.getKeyName(PEKeybind.EXTRA_FUNCTION).copy().withStyle(ChatFormatting.AQUA);
		Component modeKeyText = ClientKeyHelper.getKeyName(PEKeybind.MODE).copy().withStyle(ChatFormatting.AQUA);
		// Style(color, bold, italic, underline, strikethrough, obfuscated, clickevent, hoverevent, insertion, font)
		tips.add(new TranslatableComponent("tip.mgtk.crimson.sword.autoslash").withStyle(ChatFormatting.UNDERLINE)); // Flavor
		tips.add(new TranslatableComponent("tip.mgtk.crimson.sword.autoslash.1", autoslashKeyText, modeKeyText)); // Keys
		tips.add(new TranslatableComponent("tip.mgtk.crimson.sword.autoslash.2",
				new TranslatableComponent("tip.mgtk.crimson.sword.killall."+getMode(stack)))); // Mode
		appendEmpowerTooltip(stack, level, tips, flags);		
	}

	@Override
	public boolean doExtraFunction(@NotNull ItemStack stack, @NotNull Player player, @Nullable InteractionHand hand) {
		int charge = getCharge(stack);
		ItemCooldowns cd = player.getCooldowns();
		boolean onCooldown = player.getAttackStrengthScale(0) < 1 || cd.isOnCooldown(this);
		if (charge < 13 || isCurrentlySlashing(stack) || onCooldown)
			return false;
		
		byte stage = (byte) getStage(stack);
		startSlashing(stack, stage);
		cd.addCooldown(this, 1+(charge/13));
		return true;
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		int charge = getCharge(stack);
		boolean active = isCurrentlySlashing(stack);
		if (charge > 0) {
			int toLeak = level.getGameTime() % 2 == 0 ? 1 : 0;
			if (entity instanceof Player plr) {
				boolean held = selected || plr.getOffhandItem() == stack;
				if (active && held && charge >= 13) {
					byte power = getSlashingPower(stack);
					int range = 5+power*5;
					boolean didDo = MiscHelper.attackRandomInRange(power,
							AABB.ofSize(plr.getBoundingBox().getCenter(), range, range, range), level, plr,
							getKillMode(stack).test().and(ent -> isValidAutoslashTarget(ent, plr)));
					if (didDo) {
						toLeak = 13;
						plr.resetAttackStrengthTicker();
						PlayerHelper.swingItem(plr, plr.getOffhandItem() == stack ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
					} else // no targets
						ceaseSlashing(stack);
				} else if (active) // doesnt meet slash requirements
					ceaseSlashing(stack);
			} else if (active) // has tag but not a player
				ceaseSlashing(stack);
			if (toLeak > 0) {
				setCharge(stack, charge-toLeak);
				if (toLeak == 1)
					level.playSound(null, entity.blockPosition(), EffectInit.EMC_LEAK.get(), entity.getSoundSource(), 1, 1);
			}
		} else if (active) // no charge
			ceaseSlashing(stack);
		
		//if ((active || level.getGameTime() % 2 == 0) && charge > 0 && entity instanceof Player player) {
		//	int toLeak = active ? 13 : 1;
		//	if (active) {
		//		if (charge >= toLeak) {
		//			byte power = getSlashingPower(stack);
		//			int range = 5+power*5;
		//			boolean didDo = MiscHelper.attackRandomInRange(power, AABB.ofSize(player.getBoundingBox().getCenter(), range, range, range), level, player,
		//					getKillMode(stack).test().and(ent -> isValidAutoslashTarget(ent, player)));
		//			if (didDo) {
		//				player.resetAttackStrengthTicker();
		//				player.displayClientMessage(new TextComponent(""+selected), true);
		//				PlayerHelper.swingItem(player, player.getOffhandItem() == stack ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
		//			} else {
		//				ceaseSlashing(stack);
		//				toLeak = 1;
		//			}
		//		} else {
		//			ceaseSlashing(stack);
		//			toLeak = 1;
		//		}
		//	}
		//	setCharge(stack, charge-toLeak);
		//	if (!active) level.playSound(null, player.blockPosition(), EffectInit.EMC_LEAK.get(), SoundSource.PLAYERS, 1, 1);
		//} else if (active && charge <= 0) {
		//	ceaseSlashing(stack);
		//}
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
			return 0xd15400;
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
		return color.I;//ColorsHelper.covColorInt(0.5f+getBarWidth(stack)/13f);
	}

	@Override
	public byte getMode(@NotNull ItemStack stack) {
		return ItemNBTHelper.getByte(stack, TAG_KILLMODE, (byte)0);
	}
	
	public KillMode getKillMode(ItemStack stack) {
		return KillMode.byId(getMode(stack));
	}

	@Override
	public boolean changeMode(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
		if (!isCurrentlySlashing(stack)) {
			byte mode = getMode(stack);
			ItemNBTHelper.setByte( stack, TAG_KILLMODE, (byte)(mode >= 3 ? 0 : mode+1) );
			player.displayClientMessage(new TranslatableComponent("tip.mgtk.crimson.sword.autoslash.hud",
					new TranslatableComponent("tip.mgtk.crimson.sword.killall."+getMode(stack))), true);
			return true;
		}
		return false;
	}
	
	public boolean isCurrentlySlashing(ItemStack stack) {
		return ItemNBTHelper.getByte(stack, TAG_SLASHING, (byte)0) > 0;
	}
	
	public byte getSlashingPower(ItemStack stack) {
		return ItemNBTHelper.getByte(stack, TAG_SLASHING, (byte)0);
	}
	
	public void startSlashing(ItemStack stack, byte power) {
		ItemNBTHelper.setByte(stack, TAG_SLASHING, power);
	}
	
	public void ceaseSlashing(ItemStack stack) {
		ItemNBTHelper.setByte(stack, TAG_SLASHING, (byte)0);
	}
	
	public boolean isValidAutoslashTarget(LivingEntity victim, Entity culprit) {
		return victim != null
				&& !victim.is(culprit)
				&& canHit(victim)
				&& !(culprit instanceof Player plr && EntityHelper.isTamedByOrTrusts(victim, plr));
	}
	
	private static boolean canHit(LivingEntity victim) {
		if (!victim.isSpectator() && victim.isAlive() && victim.isPickable()) {
			return !EntityHelper.isInvincible(victim);
		} else {
			return false;
		}
	}
	@Override
	public @NotNull List<Supplier<ItemCapability<?>>> getSupportedCaps() {
		return supportedCapabilities;
	}
	private static final List<Supplier<ItemCapability<?>>> supportedCapabilities = new ArrayList<>();

}
