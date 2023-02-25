package net.solunareclipse1.magitekkit.common.item.armor.gem;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage.EmcAction;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.capability.EmcHolderItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.utils.WorldHelper;

import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.util.EmcHelper;
import net.solunareclipse1.magitekkit.util.ShenanigansHelper;

/**
 * Chestplate
 * 
 * @author solunareclipse1
 */
public class GemAmulet extends GemJewelryItemBase implements IItemEmcHolder {
	
	public GemAmulet(Properties props, float baseDr) {
		super(EquipmentSlot.CHEST, props, baseDr);
		addItemCapability(EmcHolderItemCapabilityWrapper::new);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tips, isAdvanced);
		tips.add(new TranslatableComponent("tip.mgtk.gem_amulet"));
	}
	
	//@Override
	//public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
	//	entity.level.playSound(null, entity, PESoundEvents.POWER.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
	//	return 0;
	//}
	
	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		long plrEmc = jewelryTick(stack, level, player);
		
		// leaks emc when below half durability
		plrEmc = leakEmc(stack, level, player, plrEmc);
		
		
		// TODO: self-refill
		
		// life stone
		plrEmc = rejuvenatePlayer(level, player, plrEmc);
		
		// hum sound, should be done last so that plrEmc is accurate
		if (level.getGameTime() % 160 == 0) { // nested if statement to we dont run shieldCondition every tick
			if (shieldCondition(player, 1f, DamageSource.GENERIC, stack) && plrEmc > 0) {
				level.playSound(player, player, EffectInit.SHIELD_AMBIENT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
			}
		}
	}
	
	
	
	// body / soul / life stone
	public long rejuvenatePlayer(Level level, Player player, long plrEmc) {
		if (!level.isClientSide && plrEmc >= 72) {
			if (level.getGameTime() % 16 == 0) {
				byte used = 0;
				// prioritize direct healing
				if (player.getHealth() < player.getMaxHealth()) {
					used++;
					player.heal(2.0f);
				}
				
				if (player.getFoodData().needsFood() && (plrEmc >= 144 || used == 0) ) {
					used++;
					player.getFoodData().eat(2, 10);
				}
				
				if (used != 0) level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.HEAL.get(), SoundSource.PLAYERS, 1, 1);
				plrEmc -= EmcHelper.consumeAvaliableEmc(player, 72*used);
			}
		}
		
		return plrEmc;
	}
	
	
	public long leakEmc(ItemStack stack, Level level, Player player, long plrEmc) {
		if (getDamage(stack) >= getMaxDamage(stack)/2) {
			int remaining = getMaxDamage(stack)-getDamage(stack);
			if (Math.round(level.getGameTime() % remaining) == 0) {
				if (extractEmc(stack, 1, EmcAction.EXECUTE) > 0) {
					if (player.getRandom().nextInt(Math.max(remaining, 16)) == 0 && !level.isClientSide) plrEmc -= performShenanigans(stack, level, player, plrEmc);
				}
			}
		}
		
		return plrEmc;
	}

	/**
	 * Does random stuff when called, most of which isnt pleasant
	 * 
	 * @return emc consumed by shenanigans
	 */
	public long performShenanigans(ItemStack stack, Level level, Player player, long plrEmc) {
		Random rand = player.getRandom();
		long consumed = 0;
		int chosen = rand.nextInt(12);
		switch (chosen) {
		case 0:
			break;
		case 1:
			ShenanigansHelper.funnySound(rand, level, player.blockPosition());
			consumed++;
			break;
		case 2:
			WorldHelper.freezeInBoundingBox(level, player.getBoundingBox().inflate(10), player, true);
			consumed += 16;
			break;
		case 3:
			WorldHelper.extinguishNearby(level, player);
			consumed += 4;
			break;
		case 4:
			WorldHelper.igniteNearby(level, player);
			consumed += 24;
			break;
		case 5:
			WorldHelper.growNearbyRandomly(rand.nextBoolean(), level, player.blockPosition(), player);
			consumed += 64;
			break;
		case 6:
			WorldHelper.repelEntitiesInterdiction(level, player.getBoundingBox().inflate(10), player.position().x, player.position().y, player.position().z);
			consumed++;
			break;
		case 7:
			int oldXp = player.totalExperience;
			player.giveExperienceLevels(rand.nextInt(-1, 2));
			int diff = player.totalExperience - oldXp;
			consumed += Math.max(diff, 1);
			break;
		case 8:
			consumed += 32*ShenanigansHelper.fiftyTwoCardPickup(rand, level, player);
			break;
		case 9:
			ShenanigansHelper.pokeNearby(level, player, stack);
			break;
		case 10:
			if (player instanceof ServerPlayer) { // TODO: this check might be unneccessary
				ShenanigansHelper.smitePlayer(level, (ServerPlayer) player);
				consumed += 1024;
			} break;
		case 11:
			consumed += rand.nextInt(1, 8193);
			break;
		}
		
		if (consumed < 0) consumed = Long.MAX_VALUE;
		
		return EmcHelper.consumeAvaliableEmc(player, consumed);
	}
	
	// Built-in klein star stuff
	// Modified from KleinStar.java
	// https://github.com/sinkillerj/ProjectE/blob/mc1.18.x/src/main/java/moze_intel/projecte/gameObjs/items/KleinStar.java
	@Override
	public long insertEmc(@NotNull ItemStack stack, long toInsert, EmcAction action) {
		if (toInsert < 0) {
			return extractEmc(stack, -toInsert, action);
		}
		long toAdd = Math.min(getNeededEmc(stack), toInsert);
		if (action.execute()) {
			ItemPE.addEmcToStack(stack, toAdd);
		}
		return toAdd;
	}
	
	@Override
	public long extractEmc(@NotNull ItemStack stack, long toExtract, EmcAction action) {
		if (toExtract < 0) {
			return insertEmc(stack, -toExtract, action);
		}
		long storedEmc = getStoredEmc(stack);
		long toRemove = Math.min(storedEmc, toExtract);
		if (action.execute()) {
			ItemPE.setEmc(stack, storedEmc - toRemove);
		}
		return toRemove;
	}

	@Override
	public @Range(from = 1, to = Long.MAX_VALUE) long getMaximumEmc(@NotNull ItemStack stack) {return 384000;}

	@Override
	public @Range(from = 0, to = Long.MAX_VALUE) long getStoredEmc(@NotNull ItemStack stack) {
		return ItemPE.getEmc(stack);
	}
	
	
	
	
	// TODO: botania mana / hex casting media compat
	// emc should be converted as necessary
}
