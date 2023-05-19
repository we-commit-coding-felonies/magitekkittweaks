package net.solunareclipse1.magitekkit.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage.EmcAction;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.PlayerHelper;

import net.solunareclipse1.magitekkit.common.item.armor.gem.GemAmulet;
import net.solunareclipse1.magitekkit.data.MGTKItemTags;
import net.solunareclipse1.magitekkit.init.EffectInit;

/**
 * Contains functions useful for working with EMC
 * 
 * @author solunareclipse1
 */
public class EmcHelper {
	/**
	 * if this is null, it should be initialized when needed<br>
	 * @see EmcHelper.initializeCovalenceDustMap()
	 */
	public static List<Item> COVALENCE_MAP = null;
	
	public static void initializeCovalenceDustMap() {
		if (COVALENCE_MAP != null) {
			LoggerHelper.printWarn("EmcHelper", "CovMapAlreadyInitialized", "Tried to initialize COVALENCE_DUST_MAP, but it already is!");
			return;
		}
		COVALENCE_MAP = MGTKItemTags.COVALENCE_DUST_LOOKUP.tag().stream()
			.filter(EMCHelper::doesItemHaveEmc)
			.sorted(Comparator.comparing(EMCHelper::getEmcValue))
			.toList();
	}
	
	/**
	 * checks if the player has any emc <br>
	 * very similar to getAvaliableEmc, but will return immediately upon finding any amount of EMC to speed things up a tad
	 * @param player
	 * @return true if player has any emc
	 */
	public static boolean hasEmc(Player player) {
		if (player.isCreative()) {
			return true;
		}
		
		// Curios
		IItemHandler curios = PlayerHelper.getCurios(player);
		if (curios != null) {
			for (int i = 0; i < curios.getSlots(); i++) {
				ItemStack stack = curios.getStackInSlot(i);
				if (stack.isEmpty()) continue;
				if (getAvaliableEmcOfStack(stack) > 0) return true;
			}
		}

		// Inventory
		Optional<IItemHandler> itemHandlerCap = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
		if (itemHandlerCap.isPresent()) {
			IItemHandler inv = itemHandlerCap.get();
			for (int i = 0; i < inv.getSlots(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if (stack.isEmpty()) continue;
				if (getAvaliableEmcOfStack(stack) > 0) return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets all available EMC held by a player, from klein stars and fuel items
	 * Returns Long.MAX_VALUE if the player is in creative
	 * 
	 * @param player The player to check
	 * @return How much EMC the player has available for use
	 */
	public static long getAvaliableEmc(Player player) {
		if (player.isCreative()) {
			return Long.MAX_VALUE;
		}
		long totalEmc = 0;
		
		// Offhand
		totalEmc = addEmcToTotal(totalEmc, getAvaliableEmcOfStack(player.getOffhandItem()), player);
		if (totalEmc == Long.MAX_VALUE) return Long.MAX_VALUE;
		
		// Curios
		IItemHandler curios = PlayerHelper.getCurios(player);
		if (curios != null) {
			for (int i = 0; i < curios.getSlots(); i++) {
				ItemStack stack = curios.getStackInSlot(i);
				if (stack.isEmpty()) continue;
				
				totalEmc = addEmcToTotal(totalEmc, getAvaliableEmcOfStack(stack), player);
				if (totalEmc == Long.MAX_VALUE) return Long.MAX_VALUE;
			}
		}

		// Inventory
		Optional<IItemHandler> itemHandlerCap = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
		if (itemHandlerCap.isPresent()) {
			IItemHandler inv = itemHandlerCap.get();			
			for (int i = 0; i < inv.getSlots(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if (stack.isEmpty()) continue;

				totalEmc = addEmcToTotal(totalEmc, getAvaliableEmcOfStack(stack), player);
				if (totalEmc == Long.MAX_VALUE) return Long.MAX_VALUE;
			}
		}
		return totalEmc;
	}
	
	/**
	 * A function to consume EMC from all sources in the inventory
	 * Acts as an alternative to ProjectE's consumePlayerFuel
	 * The key difference is that if a klein star has insufficient EMC,
	 * it will consume all of it instead of just skipping it
	 * 
	 * Useful for things that may consume extremely large amounts of EMC at once
	 * 
	 * @param player
	 * @param toConsume
	 * @return The amount of EMC consumed
	 */
	public static long consumeAvaliableEmc(Player player, @Range(from = 0, to = Long.MAX_VALUE) long toConsume) {
		if (player.isCreative() || toConsume == 0) {
			return toConsume;
		}
		boolean didConsume = false;
		long consumed = 0, totalConsumed = 0;

		consumed = consumeAvaliableEmcOfStack(player.getOffhandItem(), toConsume - totalConsumed, player);
		if (consumed != 0) {
			didConsume = true;
			totalConsumed += addEmcToTotal(totalConsumed, consumed, player);
			consumed = 0;
		}
		if (totalConsumed == Long.MAX_VALUE || totalConsumed >= toConsume) {
			if (didConsume) player.containerMenu.broadcastChanges();
			return totalConsumed;
		}
		
		IItemHandler curios = PlayerHelper.getCurios(player);
		if (curios != null) {
			for (int i = 0; i < curios.getSlots(); i++) {
				ItemStack stack = curios.getStackInSlot(i);
				consumed = consumeAvaliableEmcOfStack(stack, toConsume - totalConsumed, player);
				if (consumed != 0) {
					didConsume = true;
					totalConsumed += addEmcToTotal(totalConsumed, consumed, player);
					consumed = 0;
				}
				if (totalConsumed == Long.MAX_VALUE || totalConsumed >= toConsume) {
					if (didConsume) player.containerMenu.broadcastChanges();
					return totalConsumed;
				}
			}
		}


		Optional<IItemHandler> itemHandlerCap = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
		if (itemHandlerCap.isPresent()) {
			IItemHandler inv = itemHandlerCap.get();
			for (int i = 0; i < inv.getSlots(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if (stack.isEmpty()) continue;
				if ( stack.getItem() instanceof GemAmulet ) {
				}
				
				consumed = consumeAvaliableEmcOfStack(stack, toConsume - totalConsumed, player);
				
				
				if (consumed != 0) {
					didConsume = true;
					totalConsumed = addEmcToTotal(totalConsumed, consumed, player);
					consumed = 0;
				}
				if (totalConsumed == Long.MAX_VALUE || totalConsumed >= toConsume) {
					if (didConsume) player.containerMenu.broadcastChanges();
					return totalConsumed;
				}
			}
		}
		
		if (didConsume) player.containerMenu.broadcastChanges();
		return totalConsumed;
	}
	
	/**
	 * Same as consumeAvaliableEmc() but will ignore a specific itemstack <br>
	 * also doesnt play the "wasted emc" sound
	 * <p>
	 * intended for use with auto-refilling klein stars
	 * 
	 * @param player
	 * @param toConsume
	 * @return
	 */
	public static long consumeAvaliableEmcExcludeSelf(Player player, @Range(from = 0, to = Long.MAX_VALUE) long toConsume, ItemStack excluded) {
		if (player.isCreative() || toConsume == 0) {
			return toConsume;
		}
		boolean didConsume = false;
		long consumed = 0, totalConsumed = 0;

		ItemStack offhand = player.getOffhandItem();
		if (!offhand.isEmpty() && offhand != excluded) {
			consumed = consumeAvaliableEmcOfStack(offhand, toConsume - totalConsumed);
			if (consumed != 0) {
				didConsume = true;
				totalConsumed += addEmcToTotal(totalConsumed, consumed, player);
				consumed = 0;
			}
			if (totalConsumed == Long.MAX_VALUE || totalConsumed >= toConsume) {
				if (didConsume) player.containerMenu.broadcastChanges();
				return totalConsumed;
			}
		}
		
		IItemHandler curios = PlayerHelper.getCurios(player);
		if (curios != null) {
			for (int i = 0; i < curios.getSlots(); i++) {
				ItemStack stack = curios.getStackInSlot(i);
				if (stack.isEmpty() || stack == excluded) continue;
				consumed = consumeAvaliableEmcOfStack(stack, toConsume - totalConsumed);
				if (consumed != 0) {
					didConsume = true;
					totalConsumed += addEmcToTotal(totalConsumed, consumed, player);
					consumed = 0;
				}
				if (totalConsumed == Long.MAX_VALUE || totalConsumed >= toConsume) {
					if (didConsume) player.containerMenu.broadcastChanges();
					return totalConsumed;
				}
			}
		}


		Optional<IItemHandler> itemHandlerCap = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
		if (itemHandlerCap.isPresent()) {
			IItemHandler inv = itemHandlerCap.get();
			for (int i = 0; i < inv.getSlots(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if (stack.isEmpty() || stack == excluded) continue;
				
				consumed = consumeAvaliableEmcOfStack(stack, toConsume - totalConsumed);
				
				
				if (consumed != 0) {
					didConsume = true;
					totalConsumed = addEmcToTotal(totalConsumed, consumed, player);
					consumed = 0;
				}
				if (totalConsumed == Long.MAX_VALUE || totalConsumed >= toConsume) {
					if (didConsume) player.containerMenu.broadcastChanges();
					return totalConsumed;
				}
			}
		}
		
		if (didConsume) player.containerMenu.broadcastChanges();
		return totalConsumed;
	}
	
	/**
	 * Gets the avaliable EMC of an ItemStack, be it a Klein Star or a fuel item
	 * 
	 * @param stack The ItemStack to get EMC of
	 * @return EMC the ItemStack has avaliable for use
	 */
	public static long getAvaliableEmcOfStack(@NotNull ItemStack stack) {
		if (stack.isEmpty()) return 0;
		Optional<IItemEmcHolder> kleinStarCapability = stack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).resolve();
		
		if (kleinStarCapability.isPresent()) return kleinStarCapability.get().getStoredEmc(stack);
		
		else if (FuelMapper.isStackFuel(stack)) return EMCHelper.getEmcValue(stack) * stack.getCount();
		
		return 0;
	}
	
	/**
	 * Tries to consume EMC from a fuel item or IItemEmcHolder
	 * If the ItemStack doesnt have enough EMC, it will consume all of it
	 * 
	 * @param stack The ItemStack to consume from
	 * @param toConsume The amount of EMC to consume
	 * @return The amount of EMC that was consumed
	 */
	public static long consumeAvaliableEmcOfStack(@NotNull ItemStack stack, @Range(from = 0, to = Long.MAX_VALUE) long toConsume) {
		if (stack.isEmpty()) return 0;
		long consumed = 0;
		
		Optional<IItemEmcHolder> kleinStarCapability = stack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).resolve();
		if (kleinStarCapability.isPresent()) consumed = kleinStarCapability.get().extractEmc(stack, toConsume, EmcAction.EXECUTE);
		else if (FuelMapper.isStackFuel(stack)) {
			int itemsToConsume = (int) Math.ceil((double) toConsume / EMCHelper.getEmcValue(stack));
			if (itemsToConsume > stack.getCount()) {
				consumed = stack.getCount() * EMCHelper.getEmcValue(stack);
				stack.setCount(0);
			} else {
				consumed = EMCHelper.getEmcValue(stack) * itemsToConsume;
				stack.shrink(itemsToConsume);
			}
		}
		if (consumed < 0) return Long.MAX_VALUE;
		return consumed;
	}
	
	/**
	 * Tries to consume EMC from a fuel item or IItemEmcHolder <br>
	 * If the ItemStack doesnt have enough EMC, it will consume all of it
	 * <p>
	 * This version will play a sound when EMC is wasted (which is what the player argument is used for)
	 * 
	 * @param stack The ItemStack to consume from
	 * @param toConsume The amount of EMC to consume
	 * @return The amount of EMC that was consumed
	 */
	public static long consumeAvaliableEmcOfStack(@NotNull ItemStack stack, @Range(from = 0, to = Long.MAX_VALUE) long toConsume, Player player) {
		if (stack.isEmpty()) return 0;
		long consumed = 0;
		
		Optional<IItemEmcHolder> kleinStarCapability = stack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).resolve();
		if (kleinStarCapability.isPresent()) consumed = kleinStarCapability.get().extractEmc(stack, toConsume, EmcAction.EXECUTE);
		else if (FuelMapper.isStackFuel(stack)) {
			int itemsToConsume = (int) Math.ceil((double) toConsume / EMCHelper.getEmcValue(stack));
			if (itemsToConsume > stack.getCount()) {
				consumed = stack.getCount() * EMCHelper.getEmcValue(stack);
				stack.setCount(0);
			} else {
				consumed = EMCHelper.getEmcValue(stack) * itemsToConsume;
				stack.shrink(itemsToConsume);
			}
		}
		if (consumed > toConsume) player.getLevel().playSound(null, player.blockPosition(), EffectInit.EMC_WASTE.get(), SoundSource.PLAYERS, 1, 1);
		if (consumed < 0) return Long.MAX_VALUE;
		return consumed;
	}

	private static long addEmcToTotal(long currentTotal, @Range(from = 0, to = Long.MAX_VALUE) long toBeAdded, Player player) {
		if (currentTotal < 0) {
			HashMap<String,String> info = new HashMap<String,String>();
			info.put("Player", player.getName().getString());
			info.put("UUID", player.getStringUUID());
			info.put("currentTotal", currentTotal+" EMC");
			info.put("toBeAdded", toBeAdded+" EMC");
			LoggerHelper.printWarn(
				"getAvaliableEmc",
				"NegativeTotal",
				"A player was calculated to have a negative amount of avaliable EMC",
				info
			);
			return 0;
		}
		long newTotal = currentTotal + toBeAdded;
		if (newTotal < 0) return Long.MAX_VALUE;
		return newTotal;
	}
	
	/**
	 * Takes a given amount of emc, and calculates the smallest amount of covalence dust needed to represent it <br>
	 * designed to support both extra covalence dust items (using the item tag) and any values the dusts might have
	 * <p><br>
	 * Example: Passing in an emc value of 796, it would return (using projecte's default covalence values of 208, 8, 1):
	 * <li> 3 High Covalence Dust (624 emc)
	 * <li> 21 Medium Covalence Dust (168 emc)
	 * <li> 4 Low Covalence Dust (3 emc) <br>
	 * as an array: {3, 21, 4}
	 * <br><br>
	 * @param emc
	 * @return Map
	 */
	public static Map<Item, Long> emcToCovalenceDust(long emc) {
		if (COVALENCE_MAP == null) {
			initializeCovalenceDustMap();
		}
		Map<Item, Long> items = new HashMap<>();
		long total = emc;
		//long[] itemCount = new long[COVALENCE_MAP.size()];
		for (int i = COVALENCE_MAP.size()-1; i >= 0; i--) {
			long val = EMCHelper.getEmcValue(COVALENCE_MAP.get(i));
			if (total >= val) {
				long count = total / val;
				items.put(COVALENCE_MAP.get(i), count);
				total -= count*val;
			} else {
				items.put(COVALENCE_MAP.get(i), 0l);
			}
		}
		return items;
	}
}
