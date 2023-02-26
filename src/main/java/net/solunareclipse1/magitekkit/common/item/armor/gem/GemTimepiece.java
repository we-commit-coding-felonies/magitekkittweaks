package net.solunareclipse1.magitekkit.common.item.armor.gem;

import java.util.List;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;

import net.solunareclipse1.magitekkit.util.EmcHelper;

import vazkii.botania.api.item.IManaProficiencyArmor;

/**
 * Leggings
 * 
 * @author solunareclipse1
 */
public class GemTimepiece extends GemJewelryItemBase implements IManaProficiencyArmor {
	public GemTimepiece(Properties props, float baseDr) {
		super(EquipmentSlot.LEGS, props, baseDr);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tips, isAdvanced);
		tips.add(new TranslatableComponent("tip.mgtk.gem_timepiece"));
	}
	
	
	// Modified Gem Leggings repulsion & descend
	// Repulsion has a cost
	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		long plrEmc = jewelryTick(stack, level, player);
		if (!stack.isDamaged()) {
			
			if (player.isShiftKeyDown()) {
				fastDescend(player, level);
			} else {
				plrEmc = vacuumItems(player, level, plrEmc);
			}
			
			
			if (fullPristineSet(player) && player.isShiftKeyDown()) {
				plrEmc = repelEntities(player, level, plrEmc);
			}
		}
	}
	
	// nearly identical to normal gem leggings fast descend
	private void fastDescend(Player player, Level level) {
		if (level.isClientSide) {
			if (!player.isOnGround() && player.getDeltaMovement().y() > -8) {
				player.setDeltaMovement(player.getDeltaMovement().add(0, -0.32F, 0));
			}
		}
	}
	
	/**
	 * Calls repelEntitiesSWRG, at the cost of EMC
	 * Fails if not enough EMC
	 * 
	 * @param player the player repelling
	 * @param level the level to repel in
	 * @param plrEmc the players EMC total
	 * @return modified plrEmc
	 */
	private long repelEntities(Player player, Level level, long plrEmc) {
		if (plrEmc >= 1) {
			plrEmc -= EmcHelper.consumeAvaliableEmc(player, 1);
			AABB box = new AABB(player.getX() - 3.5, player.getY() - 3.5, player.getZ() - 3.5,
					player.getX() + 3.5, player.getY() + 3.5, player.getZ() + 3.5);
			WorldHelper.repelEntitiesSWRG(level, box, player);
		}
		
		return plrEmc;
	}
	
	/**
	 * Black hole band item magnet
	 * costs 1 emc per item it tries to magnet
	 * 
	 * @param player the player magneting
	 * @param level the level to magnet in
	 * @param plrEmc how much emc the player has
	 * @return modified plrEmc total
	 */
	public long vacuumItems(@NotNull Player player, @NotNull Level level, long plrEmc) {
		for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(7))) {
			if (plrEmc >= 1) {
				if (ItemHelper.simulateFit(player.getInventory().items, item.getItem()) < item.getItem().getCount()) {
					plrEmc -= EmcHelper.consumeAvaliableEmc(player, 1);
					WorldHelper.gravitateEntityTowards(item, player.getX(), player.getY(), player.getZ());
				}
			}
			
		}
		
		return plrEmc;
	}
	
	public boolean shouldGiveProficiency(ItemStack stack, EquipmentSlot slot, Player player, ItemStack rod) {
		return !stack.isDamaged();
	}
	
	// TODO: gem of eternal density, watch of flowing time, shrinker
	// density gem maybe put emc into klein stars?
	// need to figure out how to handle keypresses and stuff
}
