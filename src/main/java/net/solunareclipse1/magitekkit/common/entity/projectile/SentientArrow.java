package net.solunareclipse1.magitekkit.common.entity.projectile;

import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Lists;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;

import moze_intel.projecte.gameObjs.registries.PESoundEvents;

import net.solunareclipse1.magitekkit.data.MGTKEntityTags;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.init.NetworkInit;
import net.solunareclipse1.magitekkit.network.packet.client.DrawParticleAABBPacket;
import net.solunareclipse1.magitekkit.network.packet.client.DrawParticleLinePacket;
import net.solunareclipse1.magitekkit.util.CalcHelper;
import net.solunareclipse1.magitekkit.util.EntityHelper;

import vazkii.botania.client.fx.ModParticles;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.entity.EntityDoppleganger;
import vazkii.botania.common.entity.EntityPixie;

import net.solunareclipse1.magitekkit.util.ColorsHelper.Color;
import net.solunareclipse1.magitekkit.common.effect.TransmutingEffect;
import net.solunareclipse1.magitekkit.common.item.armor.gem.GemJewelryBase;
import net.solunareclipse1.magitekkit.common.item.tool.BandOfArcana;
import net.solunareclipse1.magitekkit.common.misc.MGTKDmgSrc;

/** relentlessly seeks target & pathfinds */
public class SentientArrow extends Arrow {
	/** 0 = searching, 1 = found & currently chasing, 2 = target lost */
	private static final EntityDataAccessor<Byte> AI_STATE = SynchedEntityData.defineId(SentientArrow.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(SentientArrow.class, EntityDataSerializers.INT);
	private final MobEffectInstance TRANSMUTING_INSTANCE = new MobEffectInstance(EffectInit.TRANSMUTING.get(), 7, 1);
	
	private int searchTime;
	protected int maxLife;
	/** used for pathfinding when no direct line of sight to target */
	@Nullable
	private Path targetPath = null;

	public SentientArrow(EntityType<? extends SentientArrow> type, Level level) {
		super(type, level);
		//Zombie
	}

	public SentientArrow(Level level, LivingEntity shooter, float damage) {
		super(level, shooter);
		this.setBaseDamage(damage);
		this.pickup = Pickup.CREATIVE_ONLY;
		this.maxLife = 200;
		this.searchTime = 0;
	}

	public SentientArrow(Level level, LivingEntity shooter, float damage, int maxLife) {
		super(level, shooter);
		this.setBaseDamage(damage);
		this.pickup = Pickup.CREATIVE_ONLY;
		this.maxLife = maxLife;
		this.searchTime = 0;
	}

	public SentientArrow(Level level, LivingEntity shooter, float damage, int maxLife, byte aiState) {
		super(level, shooter);
		this.setBaseDamage(damage);
		this.pickup = Pickup.CREATIVE_ONLY;
		changeAiState(aiState);
		this.maxLife = maxLife;
		this.searchTime = 0;
	}

	@Override
	public void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(AI_STATE, (byte)0);
		entityData.define(TARGET_ID, -1);
	}
	
	@Override
	protected void onHit(HitResult pResult) {
		//super.onHit(pResult);
		HitResult.Type hitresult$type = pResult.getType();
		if (hitresult$type == HitResult.Type.ENTITY) {
			this.onHitEntity((EntityHitResult)pResult);
		} else if (hitresult$type == HitResult.Type.BLOCK && !hasTarget()) {
			this.onHitBlock((BlockHitResult)pResult);
		}
		
		if (hitresult$type != HitResult.Type.MISS) {
			this.gameEvent(GameEvent.PROJECTILE_LAND, this.getOwner());
		}
	}
	
	@Override
	protected boolean canHitEntity(Entity ent) {
		// we will never hit our owner
		boolean canHit = !ent.is(getOwner()) && !EntityHelper.isInvincible(ent) && !isInert();
		return canHit && super.canHitEntity(ent);
	}
	
	@Override
	protected void onHitEntity(EntityHitResult hitRes) {
		if (hasTarget()) {
			if (canHitEntity(hitRes.getEntity())) {
				if (hitRes.getEntity() instanceof LivingEntity entity) {
					MobEffectInstance transEffect = new MobEffectInstance(EffectInit.TRANSMUTING.get(), 7, 1);
					if (entity instanceof EntityDoppleganger gaia && getOwner() instanceof Player plr ) {
						// gaia refuses to take damage unless its player damage
						gaia.hurt(DamageSource.playerAttack(plr), gaia.getMaxHealth());
						entity.invulnerableTime = 0;
					} else if (entity instanceof Player plr && GemJewelryBase.isBarrierActive(plr)) {
						// massive damage to alchshield
						entity.hurt(MGTKDmgSrc.TRANSMUTATION, entity.getMaxHealth()*10);
						entity.invulnerableTime = 0;
					} else if (entity instanceof EntityPixie pixie) {
						// die
						pixie.setHealth(0);
					} else if (!entity.addEffect(transEffect)) {
						// if we cant do the effect, try to itemize
						//if (!BandOfArcana.entityItemizer(entity, getOwner(), this)) {
							// if that doesnt work, just do a shitload of damage
							entity.hurt(MGTKDmgSrc.TRANSMUTATION, entity.getMaxHealth()/5);
							entity.invulnerableTime = 0;
						//}
					}
					entity.playSound(EffectInit.ARCHANGELS_SENTIENT_HIT.get(), 1, 2f);
					
					
					if (entity.is(getTarget())) {
						findNewTarget();
					}
				}
			}
		}
		else super.onHitEntity(hitRes);
	}
	
	@Override
	protected void onHitBlock(BlockHitResult hitRes) {
		if (!hasTarget()) {
			becomeInert();
			super.onHitBlock(hitRes);
		}
	}

	@Override
	public void tick() {
        float r = Color.PHILOSOPHERS.R / 255.0f;
        float g = Color.PHILOSOPHERS.G / 255.0f;
        float b = Color.PHILOSOPHERS.B / 255.0f;
        //float o = isInert() ? 0.1f : 0.5f;
        ((ServerLevel)level).sendParticles(WispParticleData.wisp(0.1f, r, g, b), this.getX(), this.getY(), this.getZ(), (int) 10, 0.1, 0.1, 0.1, 0);
		// updates 
		//if (!level.isClientSide() && isNoGravity() && tickCount % 3 == 0) {
		//	this.hasImpulse = true;
		//}
		if (tickCount > maxLife) {
			expire();
		} else if (tickCount > 4) {
			// try a few times to find target
			if (canChangeTarget()) {
				if (searchTime > 9) {
					becomeInert();
				} else {
					searchTime++;
					findNewTarget();
					//setDeltaMovement(getDeltaMovement().scale(0.35));
				}
			}
			if (hasTarget()) {
				seekTarget();
				Vec3 predictedPos = position().add(getDeltaMovement());
				EntityHitResult hitresult = this.findHitEntity(position(), predictedPos);
				
				if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
					Entity entity = ((EntityHitResult)hitresult).getEntity();
					Entity entity1 = this.getOwner();
					if (entity instanceof Player && entity1 instanceof Player && !((Player)entity1).canHarmPlayer((Player)entity)) {
						hitresult = null;
					}
				}
				
				if (hitresult != null && hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
					this.onHit(hitresult);
					predictedPos = hitresult.getEntity().position();
					this.hasImpulse = true;
				}
				this.setPos(predictedPos);
				return;
			}
		}
		super.tick();
	}
	

	@NotNull
	@Override
	protected ItemStack getPickupItem() {
		return ItemStack.EMPTY;
	}
	
	protected boolean isValidHomingTarget(LivingEntity entity) {
		return entity != null
				&& canHitEntity(entity)
				&& !entity.is(getOwner())
				&& !entity.getType().is(MGTKEntityTags.PHILO_HOMING_ARROW_BLACKLIST)
				&& (!entity.isInvisible() || entity.isCurrentlyGlowing())
				&& !entity.hasEffect(EffectInit.TRANSMUTING.get());
	}

	protected void findNewTarget() {
		if (level.isClientSide()) return;
		
		List<LivingEntity> validTargets = level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(64), SentientArrow.this::isValidHomingTarget);
		if (!validTargets.isEmpty()) {
			validTargets.sort(Comparator.comparing(SentientArrow.this::distanceToSqr, Double::compare));
			LivingEntity chosenTarget = null;
			for (LivingEntity candidate : validTargets) {
				// gets closest entity with line of sight
				if (level.clip(new ClipContext(this.getBoundingBox().getCenter(), candidate.getBoundingBox().getCenter(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS) {
					chosenTarget = candidate;
					break;
				}
			}
			if (chosenTarget == null) {
				// if there were none with line of sight, just go with closest
				chosenTarget = validTargets.get(0);
			}
			for (ServerPlayer plr : ((ServerLevel)level).players()) {
				if (plr.blockPosition().closerToCenterThan(this.position(), 64d)) {
					NetworkInit.toClient(new DrawParticleLinePacket(getBoundingBox().getCenter(), chosenTarget.getBoundingBox().getCenter(), 2), plr);
				}
			}
			changeTarget(chosenTarget);
			changeAiState((byte) 1);
			searchTime = 0;
		}
	}

	@Nullable
	public LivingEntity getTarget() {
		Entity tEnt = level.getEntity(entityData.get(TARGET_ID));
		if (tEnt instanceof LivingEntity target) {
			return target;
		}
		return null;
	}
	
	protected byte getAiState() {
		return entityData.get(AI_STATE);
	}
	
	protected boolean canChangeTarget() {
		return getAiState() == 0;
	}

	public boolean hasTarget() {
		return getAiState() == 1;
	}


	public boolean isInert() {
		return getAiState() == 2;
	}
	
	protected void changeAiState(byte newState) {
		entityData.set(AI_STATE, newState);
	}
	
	protected void changeTarget(Entity tEnt) {
		entityData.set(TARGET_ID, tEnt.getId());
	}
	
	protected void resetTarget() {
		entityData.set(TARGET_ID, -1);
	}
	
	protected void seekTarget() {
		LivingEntity target = getTarget();
		if (target != null && isValidHomingTarget(target)) {
			// line of sight check between AABB centers
			BlockHitResult lineOfSight = level.clip(new ClipContext(getBoundingBox().getCenter(), target.getBoundingBox().getCenter(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
			if (true || lineOfSight.getType() != HitResult.Type.BLOCK) {
				// we have direct line of sight, beeline
				// virtually identical to SmartArrow
				Vec3 vel = getDeltaMovement();
				Vec3 arrowLoc = position();
				Vec3 targetLoc = target.getBoundingBox().getCenter();
				Vec3 lookVec = targetLoc.subtract(arrowLoc);
				double theta = CalcHelper.wrap180Radian(CalcHelper.angleBetween(vel, lookVec));
				//theta = CalcHelper.clampAbs(theta, Math.PI / 2); // gives arrow a turn radius
				Vec3 crossProduct = vel.cross(lookVec).normalize();
				Vec3 adjustedLookVec = CalcHelper.transform(crossProduct, theta, vel);
				shoot(adjustedLookVec.x, adjustedLookVec.y, adjustedLookVec.z, 5F, 0);
				this.hasImpulse = true;
			} /*else {
				System.out.println(target);
				// block in the way so we try pathfinding
			}*/
		} else {
			// NO TARGET
			resetTarget();
			changeAiState((byte) 0);
		}
	}
	
	/** makes the smart arrow stop being smart */
	public void becomeInert() {
		if (level.isClientSide() || getAiState() == 2) return;
		changeAiState((byte) 2);
		for (ServerPlayer plr : ((ServerLevel)level).players()) {
			if (plr.blockPosition().closerToCenterThan(this.position(), 64d)) {
				Vec3 min = new Vec3(getBoundingBox().minX, getBoundingBox().minY, getBoundingBox().minZ),
						max = new Vec3(getBoundingBox().maxX, getBoundingBox().maxY, getBoundingBox().maxZ);
				NetworkInit.toClient(new DrawParticleAABBPacket(min, max, 1), plr);
			}
		}
	}
	
	public void expire() {
		playSound(EffectInit.ARCHANGELS_EXPIRE.get(), 1, 0.5f);
		level.playSound(null, getOwner().blockPosition(), EffectInit.ARCHANGELS_EXPIRE.get(), SoundSource.PLAYERS, 1, 0.1f);
		discard();
	}
	
	@Override
	public boolean isNoGravity() {
		return getAiState() < 2 || super.isNoGravity();
	}

	@Override
	public boolean ignoreExplosion() {
		return getAiState() < 2;
	}
	
	// doesnt seem to work
	@Override
	public int getTeamColor() {
		//if (this.getTeam() == null) {
		//	return 0xB32F67;
		//}
		return super.getTeamColor();
	}
}
