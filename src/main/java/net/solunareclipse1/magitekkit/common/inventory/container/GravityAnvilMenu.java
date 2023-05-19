package net.solunareclipse1.magitekkit.common.inventory.container;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.init.ObjectInit;
import net.solunareclipse1.magitekkit.util.MiscHelper;

public class GravityAnvilMenu extends ItemCombinerMenu {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final boolean DEBUG_COST = false;
	public static final int MAX_NAME_LENGTH = 50;
	public int repairItemCountCost;
	private String itemName;
	private final DataSlot cost = DataSlot.standalone();
	private static final int COST_FAIL = 0;
	private static final int COST_BASE = 1;
	private static final int COST_ADDED_BASE = 1;
	private static final int COST_REPAIR_MATERIAL = 1;
	private static final int COST_REPAIR_SACRIFICE = 2;
	private static final int COST_INCOMPATIBLE_PENALTY = 1;
	private static final int COST_RENAME = 1;

	public GravityAnvilMenu(int containerId, Inventory inv) {
		this(containerId, inv, ContainerLevelAccess.NULL);
	}

	public GravityAnvilMenu(int containerId, Inventory inv, ContainerLevelAccess access) {
		super(ObjectInit.GRAVITY_ANVIL.get(), containerId, inv, access);
		this.addDataSlot(this.cost);
	}

	protected boolean isValidBlock(BlockState state) {
		return true;
	}
	
	public boolean stillValid(Player pPlayer) {
		return true;
	}

	protected boolean mayPickup(Player player, boolean hasStack) {
		return (player.getAbilities().instabuild || player.experienceLevel >= this.cost.get()) && this.cost.get() > 0;
	}

	protected void onTake(Player player, ItemStack stack) {
		if (!player.getAbilities().instabuild) {
			player.giveExperienceLevels(-this.cost.get());
		}
		this.inputSlots.setItem(0, ItemStack.EMPTY);
		if (this.repairItemCountCost > 0) {
			ItemStack itemstack = this.inputSlots.getItem(1);
			if (!itemstack.isEmpty() && itemstack.getCount() > this.repairItemCountCost) {
				itemstack.shrink(this.repairItemCountCost);
				this.inputSlots.setItem(1, itemstack);
			} else {
				this.inputSlots.setItem(1, ItemStack.EMPTY);
			}
		} else {
			this.inputSlots.setItem(1, ItemStack.EMPTY);
		}

		this.cost.set(0);
		player.level.playSound(null, player.blockPosition(), EffectInit.WOFT_ATTRACT.get(), SoundSource.PLAYERS, 1, 1f);
		player.level.playSound(null, player.blockPosition(), EffectInit.WOFT_REPEL.get(), SoundSource.PLAYERS, 1, 1f);
	}

	/**
	 * called when the Anvil Input Slot changes, calculates the new result and puts it in the output slot
	 */
	public void createResult() {
		ItemStack firstStack = this.inputSlots.getItem(0);
		this.cost.set(1);
		int totalCost = 0;
		int baseCost = 0;
		int nameCost = 0;
		if (firstStack.isEmpty()) {
			this.resultSlots.setItem(0, ItemStack.EMPTY);
			this.cost.set(0);
		} else {
			ItemStack resultStack = firstStack.copy();
			ItemStack secondStack = this.inputSlots.getItem(1);
			Map<Enchantment, Integer> resultEnchList = EnchantmentHelper.getEnchantments(resultStack);
			baseCost += firstStack.getBaseRepairCost() + (secondStack.isEmpty() ? 0 : secondStack.getBaseRepairCost());
			this.repairItemCountCost = 0;
			boolean tryingToApplyBook = false;

			if (!secondStack.isEmpty()) {
				//if (!net.minecraftforge.common.ForgeHooks.onAnvilChange(this, itemstack, itemstack2, resultSlots, itemName, j, this.player)) return;
				tryingToApplyBook = secondStack.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(secondStack).isEmpty();
				if (resultStack.isDamageableItem() && resultStack.getItem().isValidRepairItem(firstStack, secondStack)) {
					int damageToRepair = Math.min(resultStack.getDamageValue(), resultStack.getMaxDamage() / 4);
					if (damageToRepair <= 0) {
						this.resultSlots.setItem(0, ItemStack.EMPTY);
						this.cost.set(0);
						return;
					}
					int repairsDone;
					for (repairsDone = 0; damageToRepair > 0 && repairsDone < secondStack.getCount(); ++repairsDone) {
						int newDamage = resultStack.getDamageValue() - damageToRepair;
						resultStack.setDamageValue(newDamage);
						++totalCost;
						damageToRepair = Math.min(resultStack.getDamageValue(), resultStack.getMaxDamage() / 4);
					}

					this.repairItemCountCost = repairsDone;
				} else {
					if (!tryingToApplyBook && (!resultStack.is(secondStack.getItem()) || !resultStack.isDamageableItem())) {
						this.resultSlots.setItem(0, ItemStack.EMPTY);
						this.cost.set(0);
						return;
					}

					if (resultStack.isDamageableItem() && !tryingToApplyBook) {
						int firstDuraLeft = firstStack.getMaxDamage() - firstStack.getDamageValue();
						int secondDuraLeft = secondStack.getMaxDamage() - secondStack.getDamageValue();
						int duraAdd = secondDuraLeft + resultStack.getMaxDamage() * 12 / 100;
						int damageToRepair = firstDuraLeft + duraAdd;
						int newDamage = resultStack.getMaxDamage() - damageToRepair;
						if (newDamage < 0) {
							newDamage = 0;
						}

						if (newDamage < resultStack.getDamageValue()) {
							resultStack.setDamageValue(newDamage);
							totalCost += 2;
						}
					}

					Map<Enchantment, Integer> secondEnchList = EnchantmentHelper.getEnchantments(secondStack);
					boolean canEnchFirst = false;
					boolean canNotEnchFirst = false;

					for(Enchantment ench : secondEnchList.keySet()) {
						if (ench != null) {
							int firstEnchLvl = resultEnchList.getOrDefault(ench, 0);
							int secondEnchLvl = secondEnchList.get(ench);
							secondEnchLvl = firstEnchLvl == secondEnchLvl ? secondEnchLvl + 1 : Math.max(secondEnchLvl, firstEnchLvl);
							boolean firstCanEnch = ench.canEnchant(firstStack);
							if (this.player.getAbilities().instabuild || firstStack.is(Items.ENCHANTED_BOOK)) {
								firstCanEnch = true;
							}

							for (Enchantment ench1 : resultEnchList.keySet()) {
								if (ench1 != ench && !ench.isCompatibleWith(ench1)) {
									firstCanEnch = false;
									++totalCost;
								}
							}

							if (!firstCanEnch) {
								canNotEnchFirst = true;
							} else {
								canEnchFirst = true;
								if (secondEnchLvl > MiscHelper.getTrueEnchMaxLevel(ench)) {
									secondEnchLvl = MiscHelper.getTrueEnchMaxLevel(ench);
								}

								resultEnchList.put(ench, secondEnchLvl);
								int enchRarityCost = 0;
								switch(ench.getRarity()) {
								case COMMON:
									enchRarityCost = 1;
									break;
								case UNCOMMON:
									enchRarityCost = 2;
									break;
								case RARE:
									enchRarityCost = 4;
									break;
								case VERY_RARE:
									enchRarityCost = 8;
								}

								if (tryingToApplyBook) {
									enchRarityCost = Math.max(1, enchRarityCost / 2);
								}

								totalCost += enchRarityCost * secondEnchLvl;
								if (firstStack.getCount() > 1) {
									totalCost = 40;
								}
							}
						}
					}

					if (canNotEnchFirst && !canEnchFirst) {
						this.resultSlots.setItem(0, ItemStack.EMPTY);
						this.cost.set(0);
						return;
					}
				}
			}

			if (StringUtils.isBlank(this.itemName)) {
				if (firstStack.hasCustomHoverName()) {
					nameCost = 1;
					totalCost += nameCost;
					resultStack.resetHoverName();
				}
			} else if (!this.itemName.equals(firstStack.getHoverName().getString())) {
				nameCost = 1;
				totalCost += nameCost;
				resultStack.setHoverName(new TextComponent(this.itemName));
			}
			if (tryingToApplyBook && !resultStack.isBookEnchantable(secondStack)) resultStack = ItemStack.EMPTY;

			this.cost.set(baseCost + totalCost);
			if (totalCost <= 0) {
				resultStack = ItemStack.EMPTY;
			}

			//if (nameCost == opCost && nameCost > 0 && this.cost.get() >= 40) {
			//	this.cost.set(39);
			//}

			//if (this.cost.get() >= 40 && !this.player.getAbilities().instabuild) {
			//	resultStack = ItemStack.EMPTY;
			//}

			if (!resultStack.isEmpty()) {
				int baseRepairCost = resultStack.getBaseRepairCost();
				if (!secondStack.isEmpty() && baseRepairCost < secondStack.getBaseRepairCost()) {
					baseRepairCost = secondStack.getBaseRepairCost();
				}

				if (nameCost != totalCost || nameCost == 0) {
					baseRepairCost = calculateIncreasedRepairCost(baseRepairCost);
				}

				resultStack.setRepairCost(baseRepairCost);
				EnchantmentHelper.setEnchantments(resultEnchList, resultStack);
			}

			this.resultSlots.setItem(0, resultStack);
			this.broadcastChanges();
		}
	}

	public static int calculateIncreasedRepairCost(int oldCost) {
		return oldCost * 2 + 1;
	}

	/**
	 * used by the Anvil GUI to update the Item Name being typed by the player
	 */
	public void setItemName(String newName) {
		this.itemName = newName;
		if (this.getSlot(2).hasItem()) {
			ItemStack itemstack = this.getSlot(2).getItem();
			if (StringUtils.isBlank(newName)) {
				itemstack.resetHoverName();
			} else {
				itemstack.setHoverName(new TextComponent(this.itemName));
			}
		}

		this.createResult();
	}

	/**
	 * Get's the maximum xp cost
	 */
	public int getCost() {
		return this.cost.get();
	}

	public void setMaximumCost(int value) {
		this.cost.set(value);
	}
	
	public void removed(Player pPlayer) {
		super.removed(pPlayer);
		this.clearContainer(pPlayer, this.inputSlots);
	}
}
