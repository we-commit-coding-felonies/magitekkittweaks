package net.solunareclipse1.magitekkit.common.item.armor.gem;

import java.util.List;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;

import net.solunareclipse1.magitekkit.util.EmcHelper;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.item.IManaProficiencyArmor;
import vazkii.botania.common.lib.ModTags;
import vazkii.botania.mixin.AccessorItemEntity;
import vazkii.botania.xplat.IXplatAbstractions;

/**
 * Leggings
 * 
 * @author solunareclipse1
 */
public class GemTimepiece extends GemJewelryBase implements IManaProficiencyArmor {
	public GemTimepiece(Properties props, float baseDr) {
		super(EquipmentSlot.LEGS, props, baseDr);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tips, isAdvanced);
		tips.add(new TranslatableComponent("tip.mgtk.gem.ref.3").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
	}
	
	
	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		boolean sneaking = player.isShiftKeyDown();
		GemJewelrySetInfo set = jewelryTick(stack, level, player);
		long plrEmc = set.plrEmc();
		if (level.isClientSide) {
			// Client
			if (sneaking && plrEmc >= 10 && !player.getAbilities().flying) {
				fastDescend(player, level);
			}
		} //else {
			// Server
			if (!stack.isDamaged()) {
				if (plrEmc > 0 && set.chest().pristine()) {
					//if (sneaking && player.isOnGround()) {
					//	plrEmc -= EmcHelper.consumeAvaliableEmc(player, repelEntities(player, level, plrEmc));
					//}
					
					if (!sneaking) {
						plrEmc -= EmcHelper.consumeAvaliableEmc(player, vacuumItems(player, level, plrEmc));
					}
				}
				
				if (sneaking && plrEmc >= 10 && !player.getAbilities().flying) {
					plrEmc -= EmcHelper.consumeAvaliableEmc(player, 10);
				}
			}
		//}
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
		AABB box = new AABB(player.getX() - 3.5, player.getY() - 3.5, player.getZ() - 3.5, player.getX() + 3.5, player.getY() + 3.5, player.getZ() + 3.5);
		Vec3 vec = player.position();
		int consumed = 0;
		for (Entity ent : level.getEntitiesOfClass(Entity.class, box, entity -> validRepelTarget(entity))) {
			if (consumed >= plrEmc) break;
			if (ent instanceof Projectile) {
				Projectile projectile = (Projectile)ent;
				Entity owner = projectile.getOwner();
				if ((level.isClientSide() && owner == null) || (owner != null && player.getUUID().equals(owner.getUUID()))) {
					continue;
				}
			}
			consumed++;
			Vec3 t = new Vec3(ent.getX(), ent.getY(), ent.getZ());
			Vec3 r = new Vec3(t.x - vec.x, t.y - vec.y, t.z - vec.z);
			double distance = vec.distanceTo(t) + 0.1D;
			ent.setDeltaMovement(ent.getDeltaMovement().add(r.scale( (2d/3d)/distance )));
		}
		return EmcHelper.consumeAvaliableEmc(player, consumed);
	}
	
	private boolean validRepelTarget(Entity entity) {
		if (!entity.isSpectator() && !entity.getType().is(PETags.Entities.BLACKLIST_SWRG)) {
			if (entity instanceof Projectile) {
				return !entity.isOnGround();
			}
			return entity instanceof Mob;
		} 
		return false;
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
		int consumed = 0;
		if (!BotaniaAPI.instance().hasSolegnoliaAround(player)) {
			for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(7))) {
				if (consumed >= plrEmc) break;
				if (isMagnetable(item)) {
					if (ItemHelper.simulateFit(player.getInventory().items, item.getItem()) < item.getItem().getCount()) {
						consumed += Math.max(1, item.getItem().getCount());
						WorldHelper.gravitateEntityTowards(item, player.getX(), player.getY(), player.getZ());
					}
				}
				
			}
		}
		return consumed;
	}
	
	// Slightly modified from Botanias ItemMagnetRing.canPullItem()
	private boolean isMagnetable(ItemEntity item) {
		int pickupDelay = ((AccessorItemEntity) item).getPickupDelay();
		if (!item.isAlive() || pickupDelay >= 40
				|| BotaniaAPI.instance().hasSolegnoliaAround(item)
				|| IXplatAbstractions.INSTANCE.preventsRemoteMovement(item)) {
			return false;
		}

		ItemStack stack = item.getItem();
		if (stack.isEmpty()
				|| IXplatAbstractions.INSTANCE.findManaItem(stack) != null
				|| IXplatAbstractions.INSTANCE.findRelic(stack) != null
				|| stack.is(ModTags.Items.MAGNET_RING_BLACKLIST)) {
			return false;
		}

		BlockPos pos = item.blockPosition();

		if (item.level.getBlockState(pos).is(ModTags.Blocks.MAGNET_RING_BLACKLIST)
				|| item.level.getBlockState(pos.below()).is(ModTags.Blocks.MAGNET_RING_BLACKLIST)) {
			return false;
		}

		return true;
	}
	
	public boolean shouldGiveProficiency(ItemStack stack, EquipmentSlot slot, Player player, ItemStack rod) {
		return !stack.isDamaged();
	}
	
	// TODO: gem of eternal density, watch of flowing time, shrinker
	// density gem maybe put emc into klein stars?
	// need to figure out how to handle keypresses and stuff
}
