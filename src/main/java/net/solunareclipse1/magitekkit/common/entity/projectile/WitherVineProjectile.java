package net.solunareclipse1.magitekkit.common.entity.projectile;

import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import moze_intel.projecte.gameObjs.entity.EntityHomingArrow;

import net.solunareclipse1.magitekkit.data.MGTKEntityTags;
import net.solunareclipse1.magitekkit.init.NetworkInit;
import net.solunareclipse1.magitekkit.init.ObjectInit;
import net.solunareclipse1.magitekkit.network.packet.client.DrawParticleLinePacket;
import net.solunareclipse1.magitekkit.network.packet.client.GustParticlePacket;
import net.solunareclipse1.magitekkit.util.CalcHelper;
import net.solunareclipse1.magitekkit.util.ColorsHelper.Color;
import net.solunareclipse1.magitekkit.util.MiscHelper;

import vazkii.botania.client.fx.WispParticleData;

/**
 * "Vine" that has aim assist, withers, and inflicts high level weakness effect <br>
 * its a horrifically butchered version of EntityHomingArrow
 * @author solunareclipse1
 */
public class WitherVineProjectile extends Projectile {

	public WitherVineProjectile(EntityType<? extends WitherVineProjectile> type, Level level) {
		super(type, level);
	}
	public WitherVineProjectile(Level level, LivingEntity shooter) {
		super(ObjectInit.WITHER_VINE.get(), level);
		this.setOwner(shooter);
	}
	private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(WitherVineProjectile.class, EntityDataSerializers.INT);

	@Override
	protected void defineSynchedData() {
		entityData.define(TARGET_ID, -1);
	}
	
	private ClipContext getClipContext(Vec3 from, Vec3 to) {
		return new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, null);
	}
	private ClipContext getClipContext(Vec3 to) {
		return new ClipContext(getBoundingBox().getCenter(), to, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, null);
	}
	
	private boolean hasTarget() {
		return entityData.get(TARGET_ID) != -1 && getTarget() != null;
	}
	
	@Nullable
	private Entity getTarget() {
		return level.getEntity(entityData.get(TARGET_ID));
	}
	
	private boolean canSeeTarget() {
		return hasTarget()
				&& level.clip(getClipContext(getTarget().getBoundingBox().getCenter())).getType() == HitResult.Type.MISS;
	}
	
	@Override
	public boolean shouldRender(double x, double y, double z) {
		return false;
	}
	
	@Override
	public void tick() {
		Vec3 nextPos = this.position().add(this.getDeltaMovement());
		if (level.isClientSide) {
			MiscHelper.drawVectorWithParticles(position(), nextPos, WispParticleData.wisp(0.5f, 0.35f, 0.5f, 0, 1.5f), 0.1, (ClientLevel)level);
			//((ClientLevel)level).addParticle(WispParticleData.wisp(1, Color.MIDGREEN.R, Color.MIDGREEN.G, Color.MIDGREEN.B), position().x, position().y, position().z, 0, 0, 0);
		}
		int hits = 0;
		for (LivingEntity ent : level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(3), ent -> !ent.is(getOwner()))) {
			if (ent.getHealth() > 2) {
				int time = (int) ((ent.getHealth()-1)*10);
				ent.addEffect(new MobEffectInstance(MobEffects.WITHER, time, 99));
				ent.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 99));
				hits++;
				if (!level.isClientSide) {
					// handle this on the server since it doesnt always seem to work properly clientside
					for (ServerPlayer plr : ((ServerLevel)level).players()) {
						if (plr.blockPosition().closerToCenterThan(ent.getBoundingBox().getCenter(), 256d)) {
							NetworkInit.toClient(new DrawParticleLinePacket(this.position(), ent.getBoundingBox().getCenter(), 6), plr);
						}
					}
					//((ServerLevel)level);
					//MiscHelper.drawVectorWithParticles(position(), ent.getBoundingBox().getCenter(), WispParticleData.wisp(0.5f, 0.35f, 0.5f, 0, 1.5f), 0.1, (ClientLevel)level);
					//((ClientLevel)level).addParticle(WispParticleData.wisp(1, Color.MIDGREEN.R, Color.MIDGREEN.G, Color.MIDGREEN.B), position().x, position().y, position().z, 0, 0, 0);
				}
			}
			ent.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 99));
		}
		if (this.tickCount > 20 || hits > 0 || level.clip(getClipContext(this.position(), nextPos)).getType() == HitResult.Type.BLOCK) {
			kill();
			return;
		}
		if (canSeeTarget()) {
			seekTarget();
		} else {
			List<LivingEntity> candidates = this.level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(4d, 4d, 4d), ent -> canTarget(ent));
			if (!candidates.isEmpty()) {
				candidates.sort(Comparator.comparing(this::distanceToSqr, Double::compare));
				entityData.set(TARGET_ID, Integer.valueOf(((LivingEntity)candidates.get(0)).getId()));
			}
		}
		this.setPos(nextPos);
	}
	
	private boolean canTarget(LivingEntity ent) {
		return !ent.getType().is(MGTKEntityTags.PHILO_HOMING_ARROW_BLACKLIST)
				&& !ent.is(getOwner())
				&& !ent.hasEffect(MobEffects.WITHER)
				&& ent.canBeAffected(new MobEffectInstance(MobEffects.WITHER, 20, 99))
				&& ent.getHealth() > 2;
	}
	
	private void seekTarget() {
		Vec3 arrowLoc = new Vec3(getX(), getY(), getZ());
		Vec3 targetLoc = getTarget().getBoundingBox().getCenter();  
		Vec3 lookVec = targetLoc.subtract(arrowLoc);
		Vec3 vel = getDeltaMovement();
		double theta = CalcHelper.wrap180Radian(CalcHelper.angleBetween(vel, lookVec));
		theta = CalcHelper.clampAbs(theta, Math.PI/5);
		Vec3 crossProduct = vel.cross(lookVec).normalize();
		Vec3 adjustedLookVec = CalcHelper.transform(crossProduct, theta, vel);
		shoot(adjustedLookVec.x, adjustedLookVec.y, adjustedLookVec.z, 2f, 0f);
	}

}
