package net.solunareclipse1.magitekkit.common.entity.projectile;

import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.solunareclipse1.magitekkit.common.item.armor.gem.GemJewelryBase;
import net.solunareclipse1.magitekkit.common.misc.MGTKDmgSrc;
import net.solunareclipse1.magitekkit.data.MGTKEntityTags;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.init.NetworkInit;
import net.solunareclipse1.magitekkit.network.packet.client.DrawParticleAABBPacket;
import net.solunareclipse1.magitekkit.network.packet.client.DrawParticleLinePacket;
import net.solunareclipse1.magitekkit.util.ColorsHelper.Color;
import net.solunareclipse1.magitekkit.util.EntityHelper;
import net.solunareclipse1.magitekkit.util.LoggerHelper;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.entity.EntityDoppleganger;
import vazkii.botania.common.entity.EntityPixie;

/** relentlessly seeks target & pathfinds */
public class SentientArrow extends Arrow {
	private static boolean DEBUG = false;
	/** 0 = searching, 1 = found & currently chasing, 2 = target lost */
	private static final EntityDataAccessor<Byte> AI_STATE = SynchedEntityData.defineId(SentientArrow.class,
			EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(SentientArrow.class,
			EntityDataSerializers.INT);
	private final MobEffectInstance TRANSMUTING_INSTANCE = new MobEffectInstance(EffectInit.TRANSMUTING.get(), 7, 1);

	private int searchTime;
	protected int maxLife;
	/** used for pathfinding when no direct line of sight to target */
	@Nullable
	private Path targetPath = null;

	public SentientArrow(EntityType<? extends SentientArrow> type, Level level) {
		super(type, level);
		// Zombie
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
		entityData.define(AI_STATE, (byte) 0);
		entityData.define(TARGET_ID, -1);
	}

	@Override
	protected void onHit(HitResult pResult) {
		// super.onHit(pResult);
		HitResult.Type hitresult$type = pResult.getType();
		if (hitresult$type == HitResult.Type.ENTITY) {
			this.onHitEntity((EntityHitResult) pResult);
		} else if (hitresult$type == HitResult.Type.BLOCK && !hasTarget()) {
			this.onHitBlock((BlockHitResult) pResult);
		}

		if (hitresult$type != HitResult.Type.MISS) {
			this.gameEvent(GameEvent.PROJECTILE_LAND, this.getOwner());
		}
	}

	@Override
	protected boolean canHitEntity(Entity ent) {
		// we will never hit our owner
		boolean canHit = !ent.is(getOwner()) && !EntityHelper.isInvincible(ent)
				&& (!EntityHelper.isTamedOrTrusting(ent) || ent.is(getTarget())); // && !isInert();
		return canHit && super.canHitEntity(ent);
	}

	@Override
	protected void onHitEntity(EntityHitResult hitRes) {
		if (hasTarget()) {
			if (canHitEntity(hitRes.getEntity())) {
				if (hitRes.getEntity() instanceof LivingEntity entity) {
					MobEffectInstance transEffect = new MobEffectInstance(EffectInit.TRANSMUTING.get(), 7, 1);
					if (entity instanceof EntityDoppleganger gaia && getOwner() instanceof Player plr) {
						// gaia refuses to take damage unless its player damage
						gaia.hurt(DamageSource.playerAttack(plr), gaia.getMaxHealth());
						entity.invulnerableTime = 0;
					} else if (entity instanceof Player plr && GemJewelryBase.isBarrierActive(plr)) {
						// massive damage to alchshield
						entity.hurt(MGTKDmgSrc.TRANSMUTATION, entity.getMaxHealth() * 10);
						entity.invulnerableTime = 0;
					} else if (entity instanceof EntityPixie pixie) {
						// die
						pixie.setHealth(0);
					} else if (!entity.addEffect(transEffect)) {
						// if we cant do the effect, try to itemize
						// if (!BandOfArcana.entityItemizer(entity, getOwner(), this)) {
						// if that doesnt work, just do a shitload of damage
						entity.hurt(MGTKDmgSrc.TRANSMUTATION, entity.getMaxHealth() / 5);
						entity.invulnerableTime = 0;
						// }
					}
					entity.playSound(EffectInit.ARCHANGELS_SENTIENT_HIT.get(), 1, 2f);

					if (entity.is(getTarget())) {
						resetTarget();
						if (trySwappingTargetTo(findNewTarget())) {
							doParticles();
						} else
							changeAiState((byte) 0);
					}
				}
			}
		} else
			super.onHitEntity(hitRes);
	}

	@Override
	protected void onHitBlock(BlockHitResult hitRes) {
		if (!hasTarget()) {
			if (canChangeTarget() && trySwappingTargetTo(findNewTarget())) {
				doParticles();
				return;
			}
			becomeInert();
			super.onHitBlock(hitRes);
		}
	}

	@Override
	public void tick() {
		// System.out.println(position() + " | " + getDeltaMovement());
		// if (hasTarget()) {
		// System.out.println(getTarget().position() + " | " +
		// getTarget().getDeltaMovement());
		// }
		float r = Color.PHILOSOPHERS.R / 255.0f;
		float g = Color.PHILOSOPHERS.G / 255.0f;
		float b = Color.PHILOSOPHERS.B / 255.0f;
		// float o = isInert() ? 0.1f : 0.5f;
		((ServerLevel) level).sendParticles(WispParticleData.wisp(0.1f, r, g, b), this.getX(), this.getY(), this.getZ(),
				(int) 10, 0.1, 0.1, 0.1, 0);
		// updates
		// if (!level.isClientSide() && isNoGravity() && tickCount % 3 == 0) {
		// this.hasImpulse = true;
		// }
		if (tickCount > maxLife) {
			expire();
		} else if (tickCount > 4) {
			// try a few times to find target
			if (canChangeTarget()) {
				if (searchTime > 9) {
					becomeInert();
				} else {
					if (trySwappingTargetTo(findNewTarget())) {
						doParticles();
					} else
						searchTime++;
					// Entity newTarget = findNewTarget();
					// if (newTarget != null) {
					// changeTarget(newTarget);
					// changeAiState((byte) 1);
					// searchTime = 0;
					// for (ServerPlayer plr : ((ServerLevel)level).players()) {
					// if (plr.blockPosition().closerToCenterThan(newTarget.position(), 64d)) {
					// NetworkInit.toClient(new DrawParticleLinePacket(getBoundingBox().getCenter(),
					// newTarget.getBoundingBox().getCenter(), 2), plr);
					// }
					// }
					// }
					// setDeltaMovement(getDeltaMovement().scale(0.35));
				}
			}
			if (hasTarget()) {
				seekTarget();
				Vec3 predictedPos = position().add(getDeltaMovement());
				EntityHitResult hitresult = this.findHitEntity(position(), predictedPos);

				if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
					Entity entity = ((EntityHitResult) hitresult).getEntity();
					Entity entity1 = this.getOwner();
					if (entity instanceof Player && entity1 instanceof Player
							&& !((Player) entity1).canHarmPlayer((Player) entity)) {
						hitresult = null;
					}
				}

				if (hitresult != null && hitresult.getType() != HitResult.Type.MISS
						&& !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
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

	/**
	 * majorly trimmed down verson of isValidHomingTarget(), called when actively
	 * homing
	 * 
	 * @param entity
	 * @return
	 */
	protected boolean shouldContinueHomingTowards(LivingEntity entity) {
		return entity != null && canHitEntity(entity) && (!entity.isInvisible() || entity.isCurrentlyGlowing())
				&& !entity.hasEffect(EffectInit.TRANSMUTING.get());
	}

	protected boolean isValidHomingTarget(LivingEntity entity) {
		return entity != null && getOwner() instanceof Player owner && owner != null && canHitEntity(entity)
				&& !entity.getType().is(MGTKEntityTags.PHILO_HOMING_ARROW_BLACKLIST)
				&& (!entity.isInvisible() || entity.isCurrentlyGlowing())
				&& !entity.hasEffect(EffectInit.TRANSMUTING.get()) && !EntityHelper.isTamedByOrTrusts(entity, owner);
	}

	protected boolean isValidHomingTarget(Entity entity) {
		if (entity instanceof LivingEntity ent) {
			return isValidHomingTarget(ent);
		} else
			return false;
	}

	/**
	 * same as isValidHomingTarget(), but excludes anything that is tamed by anyone
	 * (rather than just by the owner) <br>
	 * exists to make murdering of other players pets require manual targeting
	 * 
	 * @param entity
	 * @return
	 */
	protected boolean isValidHomingTargetForAutomatic(LivingEntity entity) {
		return isValidHomingTarget(entity) && !EntityHelper.isTamedOrTrusting(entity);
	}

	protected Entity findNewTarget() {
		if (!level.isClientSide()) {
			List<LivingEntity> validTargets = level.getEntitiesOfClass(LivingEntity.class,
					this.getBoundingBox().inflate(64), SentientArrow.this::isValidHomingTargetForAutomatic);
			if (!validTargets.isEmpty()) {
				validTargets.sort(Comparator.comparing(SentientArrow.this::distanceToSqr, Double::compare));
				LivingEntity chosenTarget = null;
				for (LivingEntity candidate : validTargets) {
					// gets closest entity with line of sight
					if (level.clip(
							new ClipContext(this.getBoundingBox().getCenter(), candidate.getBoundingBox().getCenter(),
									ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this))
							.getType() == HitResult.Type.MISS) {
						chosenTarget = candidate;
						break;
					}
				}
				if (chosenTarget == null) {
					// if there were none with line of sight, just go with closest
					chosenTarget = validTargets.get(0);
				}
				return chosenTarget;
			}
		}
		return null;
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
	
	protected boolean hasLineOfSight(Vec3 pos1, Vec3 pos2) {
		BlockHitResult lineOfSight = level.clip(new ClipContext(pos1,
				pos2, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
		return (lineOfSight.getType() != HitResult.Type.BLOCK);
	}
	
	protected void seekTarget() {
		LivingEntity target = getTarget();
		if (target != null && shouldContinueHomingTowards(target)) {
			// line of sight check between AABB centers
			if (hasLineOfSight(this.getBoundingBox().getCenter(), target.getBoundingBox().getCenter())) {
//				LoggerHelper.printDebug("SentientArrow","LineOfSight", target.getName().getContents());
				this.targetPath = null;
				shootAt(target, 5);
			} else {
//				LoggerHelper.printDebug("SentientArrow","NoLineOfSight", "No Line of Sight");
				if (this.targetPath == null || this.targetPath.isDone()) {
					this.targetPath = findPathToTarget(target);					
					if (DEBUG) {
						LoggerHelper.printDebug("SentientArrow", "Path", this.targetPath.toString());
						drawDebugPath(true);
					}
				}
				if (DEBUG) {
					drawDebugPath(false);
				}
				Node node = this.targetPath.getPreviousNode();
				if (node == null) {
					node = this.targetPath.getNode(0);
				}
				Node nextNode = this.targetPath.getNextNode();
				while (hasLineOfSight(node.asVec3(),nextNode.asVec3()) && !this.targetPath.isDone()) {
					nextNode = this.targetPath.getNextNode();
					this.targetPath.advance();
				}
				shootAt(Vec3.atCenterOf(nextNode.asBlockPos()), 5);
				// Remove true to make sure we're successfully following, but currently borked.
				if (node.asVec3().subtract(this.position()).length() < 1) {
					this.targetPath.advance();
				}
			}
			this.hasImpulse = true;
		} else {
			// NO TARGET
			resetTarget();
			if (!isInert())
				changeAiState((byte) 0);
		}
	}
	
	protected void drawDebugPath(Boolean whole) {
		Node lastNode = null;
		Node thisNode = null;
		for (int i = 0; i < this.targetPath.getNodeCount() - 1; i++) {
			Node node = this.targetPath.getNode(i);
			lastNode = thisNode;
			thisNode = node;
			Node nextNode = this.targetPath.getNode(i+1);
			if (!whole) {
				int j = i + 1;
				while (hasLineOfSight(thisNode.asVec3(),nextNode.asVec3()) && j < this.targetPath.getNodeCount()) {
					nextNode = this.targetPath.getNode(j++);
				}
				i = j;
				thisNode = nextNode;
			}
			if (lastNode == null) {
				NetworkInit.toClient(
						new DrawParticleLinePacket(this.getBoundingBox().getCenter(),
								Vec3.atCenterOf(thisNode.asBlockPos()), 0),
						(ServerPlayer) this.getOwner());
			} else {
				NetworkInit.toClient(
						new DrawParticleLinePacket(Vec3.atCenterOf(lastNode.asBlockPos()),
								Vec3.atCenterOf(thisNode.asBlockPos()), 0),
						(ServerPlayer) this.getOwner());
			}
		}
	}
	protected Path findPathToTarget(LivingEntity target) {
		if (level.isClientSide()) {
			return null;
		}
		ArrowPathNavigation nav = new ArrowPathNavigation(this, level);
		Path path = nav.createPath(target, 0);
		return path;
	}

	protected Vec3 getMoveVector(Vec3 target, float velocity) {
		Vec3 between = target.subtract(position());
		if (between.length() < velocity) {
			velocity = (float) between.length();
		}
		Vec3 move = between.normalize().scale(velocity);
		return move;
	}

	protected void shootAt(LivingEntity target, float velocity) {
		Vec3 move = getMoveVector(target.position(), 5);
		Vec3 heading = null;
		if (move.equals(Vec3.ZERO) || move.length() < 0.33) {
			heading = target.getBoundingBox().getCenter().subtract(this.getBoundingBox().getCenter()).normalize();
			move = heading.scale(5);
		} else {
			heading = move.normalize();
		}
		shoot(heading.x, heading.y, heading.z, (float) move.length(), 0);
	}

	protected void shootAt(Vec3 target, float velocity) {
		Vec3 move = getMoveVector(target, 5);
		Vec3 heading = move.normalize();
		shoot(heading.x, heading.y, heading.z, (float) move.length(), 0);
	}

	/**
	 * called when owner shoots again before current arrow dies <br>
	 * tries to swap target to the entity they are looking at, or just the nearest
	 * entity to them <br>
	 * if no valid targets were found, we continue with our current target
	 * 
	 * @param cancelInert if true, finding a valid target will make the arrow stop
	 *                    being inert
	 * @return if the redirect was successful
	 */
	public boolean manualRedirectByOwner(boolean cancelInert) {
		if (cancelInert || !isInert()) {
			Entity owner = getOwner();
			if (owner == null)
				return false;
			// Entity oldTarget = getTarget();
			Vec3 ray = owner.getLookAngle().scale(128);
			EntityHitResult hitRes = ProjectileUtil.getEntityHitResult(level, owner, owner.getEyePosition(),
					owner.getEyePosition().add(ray), owner.getBoundingBox().expandTowards(ray).inflate(1.0D),
					SentientArrow.this::isValidHomingTarget);
			if (hitRes != null) {
				BlockHitResult sightCheck = level
						.clip(new ClipContext(owner.getEyePosition(), hitRes.getEntity().getBoundingBox().getCenter(),
								ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, owner));
				if (sightCheck != null && sightCheck.getType() != HitResult.Type.MISS)
					return false; // check for blocks in the way
				if (trySwappingTargetTo(hitRes.getEntity())) {
					doParticles();
					return true;
				}
			}
			Vec3 oldPos = this.position();
			setPos(owner.getEyePosition());
			Entity newTarget = findNewTarget();
			setPos(oldPos); // lol, lmao
			if (trySwappingTargetTo(newTarget)) {
				doParticles();
				return true;
			}
			return false;
			// if (getTarget() != null && !getTarget().is(oldTarget)) {
			// // our new target is acceptable
			// changeAiState((byte)1);
			// System.out.println(hasTarget());
			// return true;
			// } else if (getTarget() == null) {
			// if (oldTarget != null) changeTarget(oldTarget);
			// return false;
			// } else {
			// return false;
			// }
		}
		return false;
	}

	/**
	 * checks if new target isnt null or the same as current target, then changes
	 * 
	 * @return if the change was successful
	 */
	protected boolean trySwappingTargetTo(Entity newTarget) {
		if (newTarget != null && !newTarget.is(getTarget())) {
			changeTarget(newTarget);
			changeAiState((byte) 1);
			searchTime = 0;
			return true;
		}
		return false;
	}

	/**
	 * draws line to target
	 */
	protected void doParticles() {
		Entity target = getTarget();
		for (ServerPlayer plr : ((ServerLevel) level).players()) {
			BlockPos pos = plr.blockPosition();
			if (pos.closerToCenterThan(target.position(), 64d) || pos.closerToCenterThan(this.position(), 64d)) {
				NetworkInit.toClient(new DrawParticleLinePacket(this.getBoundingBox().getCenter(),
						target.getBoundingBox().getCenter(), 2), plr);
			}
		}
	}

	/** makes the smart arrow stop being smart */
	public void becomeInert() {
		if (level.isClientSide() || getAiState() == 2)
			return;
		changeAiState((byte) 2);
		resetTarget();
		for (ServerPlayer plr : ((ServerLevel) level).players()) {
			if (plr.is(getOwner()) || plr.blockPosition().closerToCenterThan(this.position(), 64d)) {
				Vec3 min = new Vec3(getBoundingBox().minX, getBoundingBox().minY, getBoundingBox().minZ),
						max = new Vec3(getBoundingBox().maxX, getBoundingBox().maxY, getBoundingBox().maxZ);
				NetworkInit.toClient(new DrawParticleAABBPacket(min, max, 1), plr);
			}
		}
	}

	public void expire() {
		playSound(EffectInit.ARCHANGELS_EXPIRE.get(), 1, 0.5f);
		level.playSound(null, getOwner().blockPosition(), EffectInit.ARCHANGELS_EXPIRE.get(), SoundSource.PLAYERS, 1,
				0.1f);
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
		// if (this.getTeam() == null) {
		// return 0xB32F67;
		// }
		return super.getTeamColor();
	}

	@Override
	public boolean shouldBeSaved() {
		return false;
	}
}
