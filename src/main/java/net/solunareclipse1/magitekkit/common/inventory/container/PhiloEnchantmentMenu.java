package net.solunareclipse1.magitekkit.common.inventory.container;

import java.util.List;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import moze_intel.projecte.gameObjs.PETags;

import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.init.ObjectInit;
import net.solunareclipse1.magitekkit.util.EmcHelper;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

/**
 * buffed, portable enchantment table
 * @author solunareclipse1
 */
public class PhiloEnchantmentMenu extends AbstractContainerMenu {
	private final Container enchantSlots = new SimpleContainer(2) {
	/**
	 * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think
	 * it hasn't changed and skip it.
	 */
		public void setChanged() {
		   super.setChanged();
		   PhiloEnchantmentMenu.this.slotsChanged(this);
		}
	};
	private final ContainerLevelAccess access;
	private final Random random = new Random();
	private final DataSlot enchantmentSeed = DataSlot.standalone();
	public final int[] costs = new int[3];
	public final int[] enchantClue = new int[]{-1, -1, -1};
	public final int[] levelClue = new int[]{-1, -1, -1};

	public PhiloEnchantmentMenu(int window, Inventory inv) {
	   this(window, inv, ContainerLevelAccess.NULL);
	}

	public PhiloEnchantmentMenu(int window, Inventory inv, ContainerLevelAccess access) {
		super(ObjectInit.PHILO_ENCHANTER.get(), window);
		this.access = access;
		
		// enchant target
		this.addSlot(new Slot(this.enchantSlots, 0, 15, 47) {
			/** Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel. */
			public boolean mayPlace(ItemStack stack) {return true;}
			/** Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case of armor slots) */
			public int getMaxStackSize() {return 1;}
		});
		
		// enchant fuel
		this.addSlot(new Slot(this.enchantSlots, 1, 35, 47) {
			/** Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel. */
			public boolean mayPlace(ItemStack stack) {
				return stack.is(PETags.Items.COVALENCE_DUST);
			}
		});

		// player inventory
		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		// player hotbar
		for(int k = 0; k < 9; ++k) {
			this.addSlot(new Slot(inv, k, 8 + k * 18, 142));
		}

		this.addDataSlot(DataSlot.shared(this.costs, 0));
		this.addDataSlot(DataSlot.shared(this.costs, 1));
		this.addDataSlot(DataSlot.shared(this.costs, 2));
		this.addDataSlot(this.enchantmentSeed).set(inv.player.getEnchantmentSeed());
		this.addDataSlot(DataSlot.shared(this.enchantClue, 0));
		this.addDataSlot(DataSlot.shared(this.enchantClue, 1));
		this.addDataSlot(DataSlot.shared(this.enchantClue, 2));
		this.addDataSlot(DataSlot.shared(this.levelClue, 0));
		this.addDataSlot(DataSlot.shared(this.levelClue, 1));
		this.addDataSlot(DataSlot.shared(this.levelClue, 2));
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	public void slotsChanged(Container inv) {
		if (inv == this.enchantSlots) {
			ItemStack enchTarget = inv.getItem(0);
			if (!enchTarget.isEmpty() && enchTarget.isEnchantable()) {
				this.access.execute((level, tablePos) -> {
					float j = 15;

					//for (BlockPos shelfPos : EnchantmentTableBlock.BOOKSHELF_OFFSETS) {
					//	if (EnchantmentTableBlock.isValidBookShelf(level, tablePos, shelfPos)) {
					//		j += level.getBlockState(tablePos.offset(shelfPos)).getEnchantPowerBonus(level, tablePos.offset(shelfPos));
					//	}
					//}

					this.random.setSeed((long)this.enchantmentSeed.get());

					for (int k = 0; k < 3; ++k) {
						this.costs[k] = EnchantmentHelper.getEnchantmentCost(this.random, k, (int)j, enchTarget);
						this.enchantClue[k] = -1;
						this.levelClue[k] = -1;
						if (this.costs[k] < k + 1) {
							this.costs[k] = 0;
						}
						this.costs[k] = net.minecraftforge.event.ForgeEventFactory.onEnchantmentLevelSet(level, tablePos, k, (int)j, enchTarget, costs[k]);
					}

					for (int l = 0; l < 3; ++l) {
						if (this.costs[l] > 0) {
							List<EnchantmentInstance> enchList = this.getEnchantmentList(enchTarget, l, this.costs[l]);
							if (enchList != null && !enchList.isEmpty()) {
								EnchantmentInstance ench = enchList.get(this.random.nextInt(enchList.size()));
								this.enchantClue[l] = Registry.ENCHANTMENT.getId(ench.enchantment);
								this.levelClue[l] = ench.level;
							}
						}
					}

					this.broadcastChanges();
				});
			} else {
				for (int i = 0; i < 3; ++i) {
					this.costs[i] = 0;
					this.enchantClue[i] = -1;
					this.levelClue[i] = -1;
				}
			}
		}
	}

	/**
	 * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
	 */
	public boolean clickMenuButton(Player player, int id) {
		if (id >= 0 && id < this.costs.length) {
			ItemStack enchTarget = this.enchantSlots.getItem(0);
			ItemStack enchFuel = this.enchantSlots.getItem(1);
			int i = id + 1;
			if ((enchFuel.isEmpty() || enchFuel.getCount() < i) && !player.getAbilities().instabuild) {
				return false;
			} else if (this.costs[id] <= 0 || enchTarget.isEmpty() && !player.getAbilities().instabuild) {
				return false;
			} else {
				performEnch(player, enchTarget, enchFuel, id);
				return true;
			}
		} else {
			Util.logAndPauseIfInIde(player.getName() + " pressed invalid button id: " + id);
			return false;
		}
	}
	
	private void performEnch(Player player, ItemStack enchTarget, ItemStack enchFuel, int id) {
		int i = id + 1;
		ItemStack enchResult = enchTarget;
		List<EnchantmentInstance> enchList = this.getEnchantmentList(enchTarget, id, this.costs[id]);
		if (!enchList.isEmpty()) {
			player.onEnchantmentPerformed(enchTarget, 0);
			boolean isBook = enchTarget.is(Items.BOOK);
			if (isBook) {
				enchResult = new ItemStack(Items.ENCHANTED_BOOK);
				CompoundTag compoundtag = enchTarget.getTag();
				if (compoundtag != null) {
					enchResult.setTag(compoundtag.copy());
				}
				this.enchantSlots.setItem(0, enchResult);
			}

			if (EmcHelper.COVALENCE_MAP == null) {
				EmcHelper.initializeCovalenceDustMap();
			}
			int tier = EmcHelper.COVALENCE_MAP.indexOf(enchFuel.getItem());
			int amountOfIncreases = random.nextInt(tier+1);
			boolean increaseAll = amountOfIncreases >= enchList.size();
			IntOpenHashSet idxs = new IntOpenHashSet(tier);
			while (amountOfIncreases > 0 && idxs.size() < amountOfIncreases && !increaseAll) {
				int newIdx = random.nextInt(enchList.size());
				if (!idxs.contains(newIdx)) {
					idxs.add(newIdx);
				}
			}
			int increased = 0;
			for (int j = 0; j < enchList.size(); ++j) {
				EnchantmentInstance ench = enchList.get(j);
				if (tier > 0 && (increaseAll || idxs.contains(j)) ) {
					int bonus = i*random.nextInt(tier+1);
					if (bonus > 0) {
						ench = new EnchantmentInstance(ench.enchantment, Math.min(10, ench.level + bonus));
						increased++;
						player.level.playSound(null, player.blockPosition(), EffectInit.PHILO_TRANSMUTE.get(), SoundSource.PLAYERS, 1, increased/2f);
					}
				}
				if (isBook) {
					EnchantedBookItem.addEnchantment(enchResult, ench);
				} else {
					enchResult.enchant(ench.enchantment, ench.level);
				}
			}

			if (!player.getAbilities().instabuild) {
				enchFuel.shrink(i);
				if (enchFuel.isEmpty()) {
					this.enchantSlots.setItem(1, ItemStack.EMPTY);
				}
			}

			player.awardStat(Stats.ENCHANT_ITEM);
			if (player instanceof ServerPlayer plr) {
				CriteriaTriggers.ENCHANTED_ITEM.trigger(plr, enchResult, i);
			}

			this.enchantSlots.setChanged();
			this.enchantmentSeed.set(player.getEnchantmentSeed());
			this.slotsChanged(this.enchantSlots);
			player.level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, player.level.random.nextFloat() * 0.1F + 0.9F);
		}
	}

	private List<EnchantmentInstance> getEnchantmentList(ItemStack stack, int enchSlot, int enchLevel) {
		this.random.setSeed((long)(this.enchantmentSeed.get() + enchSlot));
		List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(this.random, stack, enchLevel, true);
		return list;
	}

	public int getGoldCount() {
		ItemStack itemstack = this.enchantSlots.getItem(1);
		return itemstack.isEmpty() ? 0 : itemstack.getCount();
	}

   public int getEnchantmentSeed() {
      return this.enchantmentSeed.get();
   }

   /**
    * Called when the container is closed.
    */
   public void removed(Player player) {
      super.removed(player);
      this.clearContainer(player, this.enchantSlots);
      //this.access.execute((level, pos) -> {
      //   this.clearContainer(player, this.enchantSlots);
      //});
   }

   /**
    * Determines whether supplied player can use this container
    */
   public boolean stillValid(Player player) {
      return true;//stillValid(this.access, player, Blocks.ENCHANTING_TABLE);
   }

   /**
    * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
    * inventory and the other inventory(s).
    */
   public ItemStack quickMoveStack(Player player, int idx) {
      ItemStack ogClickedStack = ItemStack.EMPTY;
      Slot slot = this.slots.get(idx);
      if (slot != null && slot.hasItem()) {
         ItemStack clickedStack = slot.getItem();
         ogClickedStack = clickedStack.copy();
         if (idx == 0) {
            if (!this.moveItemStackTo(clickedStack, 2, 38, true)) {
               return ItemStack.EMPTY;
            }
         } else if (idx == 1) {
            if (!this.moveItemStackTo(clickedStack, 2, 38, true)) {
               return ItemStack.EMPTY;
            }
         } else if (clickedStack.is(PETags.Items.COVALENCE_DUST)) {
            if (!this.moveItemStackTo(clickedStack, 1, 2, true)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (this.slots.get(0).hasItem() || !this.slots.get(0).mayPlace(clickedStack)) {
               return ItemStack.EMPTY;
            }

            ItemStack enchTargetStack = clickedStack.copy();
            enchTargetStack.setCount(1);
            clickedStack.shrink(1);
            this.slots.get(0).set(enchTargetStack);
         }

         if (clickedStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (clickedStack.getCount() == ogClickedStack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(player, clickedStack);
      }

      return ogClickedStack;
   }
}
