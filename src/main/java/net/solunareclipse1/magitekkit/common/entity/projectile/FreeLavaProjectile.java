package net.solunareclipse1.magitekkit.common.entity.projectile;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

import net.minecraftforge.network.NetworkHooks;

import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;

import net.solunareclipse1.magitekkit.common.misc.MGTKDmgSrc;
import net.solunareclipse1.magitekkit.init.ObjectInit;

// i am at my fucking limit i stg
public class FreeLavaProjectile extends ThrowableProjectile {

	// projecte take notes:	   V look, right here, extensible code! V
	public FreeLavaProjectile(EntityType<? extends FreeLavaProjectile> type, Level level) {
		super(type, level);
	}

	public FreeLavaProjectile(Player entity, Level level) {
		super(ObjectInit.FREE_LAVA_PROJECTILE.get(), entity, level);
	}
	
	// basically the same as projectes lava projectile tick
	// https://github.com/sinkillerj/ProjectE/blob/mc1.18.x/src/main/java/moze_intel/projecte/gameObjs/entity/EntityLavaProjectile.java
	@Override
	public void tick() {
		super.tick();
		if (!level.isClientSide) {
			if (tickCount > 400 || !level.isLoaded(blockPosition())) {
				discard();
				return;
			}
			Entity thrower = getOwner();
			if (thrower instanceof ServerPlayer player) {
				BlockPos.betweenClosedStream(blockPosition().offset(-3, -3, -3), blockPosition().offset(3, 3, 3)).forEach(pos -> {
					if (level.isLoaded(pos)) {
						BlockState state = level.getBlockState(pos);
						if (state.getFluidState().is(FluidTags.WATER)) {
							pos = pos.immutable();
							if (PlayerHelper.hasEditPermission(player, pos)) {
								WorldHelper.drainFluid(level, pos, state, Fluids.WATER);
								level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F,
										2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
							}
						}
					}
				});
				if (isInWaterOrBubble() && tickCount > 5) {
					WorldHelper.placeFluid(player, level, this.blockPosition(), this.getMotionDirection(), Fluids.LAVA, false);
					discard();
					return;
				}
			}
			if (getY() > 128) {
				LevelData worldInfo = level.getLevelData();
				worldInfo.setRaining(false);
				discard();
			}
		}
	}

	@Override
	public float getGravity() {return 0;}
	
	@Override
	public boolean ignoreExplosion() {return true;}

	@Override
	protected void onHit(@NotNull HitResult result) {
		super.onHit(result);
		if (result.getType() == Type.ENTITY) return;
		discard();
	}

	@Override
	protected void onHitBlock(@NotNull BlockHitResult result) {
		super.onHitBlock(result);
		if (!level.isClientSide && getOwner() instanceof ServerPlayer player) {
			WorldHelper.placeFluid(player, level, result.getBlockPos(), result.getDirection(), Fluids.LAVA, false);
		}
	}

	@Override
	protected void onHitEntity(@NotNull EntityHitResult result) {
		super.onHitEntity(result);
		if (!level.isClientSide) {
			Entity ent = result.getEntity();
			if (ent.fireImmune()) return;
			ent.setSecondsOnFire(5);
			ent.hurt(MGTKDmgSrc.MUSTANG, 512);
		}
	}

	@NotNull
	@Override
	public Packet<?> getAddEntityPacket() {return NetworkHooks.getEntitySpawningPacket(this);}

	@Override
	protected void defineSynchedData() {}

}
