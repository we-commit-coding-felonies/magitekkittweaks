package net.solunareclipse1.magitekkit.common.item.curio;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.AABB;

import net.minecraftforge.network.NetworkHooks;

import top.theillusivec4.curios.api.type.capability.ICurio;

import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.api.capabilities.item.IModeChanger;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.capability.ExtraFunctionItemCapabilityWrapper;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.capability.ProjectileShooterItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.items.PhilosophersStone;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.WorldHelper;

import net.solunareclipse1.magitekkit.api.capability.wrapper.converter.CurioCovalentCapabilityWrapper;
import net.solunareclipse1.magitekkit.api.capability.wrapper.converter.ManaCovalentCapabilityWrapper;
import net.solunareclipse1.magitekkit.common.item.MGTKItem;
import net.solunareclipse1.magitekkit.common.item.armor.gem.GemJewelryBase;
import net.solunareclipse1.magitekkit.util.EmcHelper;
import net.solunareclipse1.magitekkit.util.MiscHelper;

import vazkii.botania.common.helper.ItemNBTHelper;

public class GemBracelet extends MGTKItem implements ICurio, IModeChanger, IItemCharge, IProjectileShooter, IExtraFunction {

	private static final String TAG_MODE = "arc_mode";
	private static final String TAG_EXP = "arc_experience";
	private static final String TAG_LIQUID = "arc_liquid";
	private static final String TAG_WOFT = "arc_woft";
	private static final String TAG_OFFENSIVE = "arc_offensive";
	private static final String[] KEY_MODES = {
			"tip.mgtk.arc_mode_0", // Disabled
			"tip.mgtk.arc_mode_1", // Mind
			"tip.mgtk.arc_mode_2", // Watch
			"tip.mgtk.arc_mode_3", // Harvest
			"tip.mgtk.arc_mode_4", // Liquid
			"tip.mgtk.arc_mode_5", // Philo
			"tip.mgtk.arc_mode_6", // Archangels
			"tip.mgtk.arc_mode_7", // SWRG
			"tip.mgtk.arc_mode_8", // Zero
			"tip.mgtk.arc_mode_9"  // Ignition
	};
	
	public GemBracelet(Properties props) {
		super(props);
		//addItemCapability(CurioCovalentCapabilityWrapper::new);
		addItemCapability(ManaCovalentCapabilityWrapper::new);
		addItemCapability(ModeChangerItemCapabilityWrapper::new);
		addItemCapability(ExtraFunctionItemCapabilityWrapper::new);
		addItemCapability(ProjectileShooterItemCapabilityWrapper::new);
	}
	
	//// Mind stone
	// https://github.com/sinkillerj/ProjectE/blob/mc1.18.x/src/main/java/moze_intel/projecte/gameObjs/items/rings/MindStone.java
	
	// Item
	public int getXpItem(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_EXP, 0);
	}

	public void setXpItem(ItemStack stack, int amount) {
		ItemNBTHelper.setInt(stack, TAG_EXP, amount);
	}
	
	public void insertXpItem(ItemStack stack, int amount) {
		if (amount <= 0 || getXpItem(stack) == Integer.MAX_VALUE) return;
		int newXp = getXpItem(stack) + amount;
		if (newXp < 0) {
			newXp = Integer.MAX_VALUE;
		}
		setXpItem(stack, newXp);
	}

	public int extractXpItem(ItemStack stack, int amount) {
		if (amount <= 0 || getXpItem(stack) <= 0) return 0;
		int curXp = getXpItem(stack);
		int newXp, extracted;

		if (curXp < amount) {
			newXp = 0;
			extracted = curXp;
		} else {
			newXp = curXp - amount;
			extracted = amount;
		}

		setXpItem(stack, newXp);
		return extracted;
	}

	// Player
	public int getXpPlayer(Player player) {
		return (int) (getXPForLvl(player.experienceLevel) + player.experienceProgress * player.getXpNeededForNextLevel());
	}
	
	public void extractXpPlayer(Player player, int amount) {
		int newXp = getXpPlayer(player) - amount;
		if (newXp < 0) {
			player.totalExperience = 0;
			player.experienceLevel = 0;
			player.experienceProgress = 0;
		} else {
			player.totalExperience = newXp;
			player.experienceLevel = getLvlForXP(newXp);
			player.experienceProgress = (float) (newXp - getXPForLvl(player.experienceLevel)) / (float) player.getXpNeededForNextLevel();
		}
	}

	public void insertXpPlayer(Player player, int amount) {
		int experiencetotal = getXpPlayer(player) + amount;
		player.totalExperience = experiencetotal;
		player.experienceLevel = getLvlForXP(experiencetotal);
		player.experienceProgress = (float) (experiencetotal - getXPForLvl(player.experienceLevel)) / (float) player.getXpNeededForNextLevel();
	}


	// Calculation stuff, TODO: move to utils
	// Math referenced from the MC wiki
	public static int getXPForLvl(int level) {
		if (level < 0) {
			return Integer.MAX_VALUE;
		}

		if (level <= 16) {
			return level * level + 6 * level;
		}

		if (level <= 31) {
			return (int) (level * level * 2.5D - 40.5D * level + 360.0D);
		}

		return (int) (level * level * 4.5D - 162.5D * level + 2220.0D);
	}

	public static int getLvlForXP(int totalXP) {
		int result = 0;

		while (getXPForLvl(result) <= totalXP) {
			result++;
		}

		return --result;
	}


	
	
	// ICurio cringe
	// should be fine because its covered by CurioCovalentCapabilityWrapper?
	@Override
	public ItemStack getStack() {
		return ItemStack.EMPTY;
	}

	
	// IModeChanger
	@Override
	public byte getMode(@NotNull ItemStack stack) {
		return ItemNBTHelper.getByte(stack, TAG_MODE, (byte) 0);
	}

	@Override
	public boolean changeMode(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
		byte curMode = getMode(stack);
		byte newMode;
		if (player.isShiftKeyDown()) {
			if (curMode == 0) return false;
			newMode = 0;
		}
		else if (curMode == 9) newMode = 1;
		else newMode = (byte) (curMode + 1);
		// Displays a message over the hotbar on mode switch, corresponding to newMode
		ItemNBTHelper.setByte(stack, TAG_MODE, newMode);
		player.displayClientMessage(new TranslatableComponent("tip.mgtk.arc_mode_swap", new TranslatableComponent(KEY_MODES[newMode])), true);
		player.level.playSound(null, player, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.PLAYERS, 1, 0.7f);
		return true;
	}

	
	// IItemCharge
	@Override
	public int getNumCharges(@NotNull ItemStack stack) {
		return 1;
	}
	
	@Override
	public int getCharge(@NotNull ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, TAG_OFFENSIVE, false) ? 1 : 0;
	}
	
	@Override
	public float getChargePercent(@NotNull ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, TAG_OFFENSIVE, false) ? 1 : 0;
	}
	
	@Override
	public boolean changeCharge(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
		boolean offensive = getCharge(stack) == 1;

		if (player.isShiftKeyDown() && offensive) {
			player.level.playSound(null, player, PESounds.UNCHARGE, SoundSource.PLAYERS, 1, 1);
			ItemNBTHelper.setBoolean(stack, TAG_OFFENSIVE, false);
			return true;
		} else if (!offensive) {
			player.level.playSound(null, player, PESounds.CHARGE, SoundSource.PLAYERS, 1, 1);
			ItemNBTHelper.setBoolean(stack, TAG_OFFENSIVE, true);
			return true;
		}
		return false;
	}
	
	public boolean playerHasFullPristineSet(Player player) {
		for (ItemStack armorStack : player.getArmorSlots()) {
			if (armorStack.getItem() instanceof GemJewelryBase && !armorStack.isDamaged()) continue;
			return false; // if above check ever fails, false
		}
		return true;
	}

	// IExtraFunction (C)
	@Override
	public boolean doExtraFunction(@NotNull ItemStack stack, @NotNull Player player, @Nullable InteractionHand hand) {
		if (!playerHasFullPristineSet(player)) return false;
		boolean didDo = false;
		switch (getMode(stack)) {
		case 0: // Disabled
			break;
		case 1: // Mind
			if (getXpItem(stack) > 0) {
				didDo = true;
				insertXpPlayer(player, extractXpItem(stack, getXpItem(stack)));
			}
			break;
		case 2: // Watch
			// TODO: mob slow & tick accel
			System.out.println("NYI: " + getMode(stack));
			ItemNBTHelper.setBoolean(stack, TAG_WOFT, !ItemNBTHelper.getBoolean(stack, TAG_WOFT, false));
			break;
		case 3: // Harvest
			for (long i = Math.min(16, EmcHelper.consumeAvaliableEmc(player, 16)); i > 0; i--) {
				WorldHelper.growNearbyRandomly(true, player.level, player.blockPosition(), player);
				if (!didDo) didDo = true;
			}
			break;
		case 4: // Liquid
			// true = lava
			ItemNBTHelper.setBoolean(stack, TAG_LIQUID, !ItemNBTHelper.getBoolean(stack, TAG_LIQUID, false));
			player.level.playSound(null, player, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.PLAYERS, 1, 1.4f);
			didDo = true;
			break;
		case 5: // Philo
			didDo = PEItems.PHILOSOPHERS_STONE.get().doExtraFunction(stack, player, hand);
			break;
		case 6: // Archangels
			EmcHelper.consumeAvaliableEmc(player, MiscHelper.fiftyTwoCardPickup(player.getRandom(), player.level, player, false));
			didDo = true;
			break;
		case 7: // SWRG
			if (player instanceof ServerPlayer) {
				didDo = MiscHelper.smiteAllInArea(player.level, player.getBoundingBox().inflate(24), (ServerPlayer) player);
			}
			break;
		case 8: // Zero
			if (player instanceof ServerPlayer) {
				didDo = MiscHelper.slowAllInArea(player.level, player.getBoundingBox().inflate(24), (ServerPlayer) player);
			}
			break;
		case 9: // Ignition
			if (player instanceof ServerPlayer) {
				didDo = MiscHelper.burnAllInArea(player.level, player.getBoundingBox().inflate(24), (ServerPlayer) player);
			}
			break;
		}
		return didDo;
	}

	// IProjectileShooter (B) TODO: put the actual default here instead of my bind
	@Override
	public boolean shootProjectile(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
		if (!playerHasFullPristineSet(player)) return false;
		boolean didDo = false;
		switch (getMode(stack)) {
		case 0: // Disabled
			return false;
		case 1: // Mind
			System.out.println("NYI: " + getMode(stack));
			break;
		case 2: // Watch
			System.out.println("NYI: " + getMode(stack));
			break;
		case 3: // Harvest
			System.out.println("NYI: " + getMode(stack));
			break;
		case 4: // Liquid
			didDo = ItemNBTHelper.getBoolean(stack, TAG_LIQUID, false) ? PEItems.VOLCANITE_AMULET.get().shootProjectile(player, stack, hand) : PEItems.EVERTIDE_AMULET.get().shootProjectile(player, stack, hand);
			break;
		case 5: // Philo
			didDo = PEItems.PHILOSOPHERS_STONE.get().shootProjectile(player, stack, hand);
			break;
		case 6: // Archangels
			Arrow arrow = new Arrow(player.level, player);
			arrow.setBaseDamage(20);
			arrow.pickup = Pickup.CREATIVE_ONLY;
			arrow.setCritArrow(true);
			arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 10, 0);
			player.level.addFreshEntity(arrow);
			player.level.playSound(null, player, SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.5F, 2.0F / (player.level.random.nextFloat() * 0.4F + 1.2F));
			break;
		case 7: // SWRG
			if (player.level.getLevelData() instanceof ServerLevelData lvlData) {
				lvlData.setRainTime(6000);
				lvlData.setThunderTime(6000);
				lvlData.setRaining(true);
				lvlData.setThundering(true);
			}
			break;
		case 8: // Zero
			System.out.println("NYI: " + getMode(stack));
			break;
		case 9: // Ignition
			System.out.println("NYI: " + getMode(stack));
			break;
		}
		return didDo;
	}
}
