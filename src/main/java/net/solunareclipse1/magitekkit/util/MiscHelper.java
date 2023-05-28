package net.solunareclipse1.magitekkit.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Predicate;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.foundation.utility.BlockHelper;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.FishingSpeedEnchantment;
import net.minecraft.world.item.enchantment.QuickChargeEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.HangingRootsBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.NetherSproutsBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.RootsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.ToolActions;

import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;

import net.solunareclipse1.magitekkit.common.item.armor.gem.GemJewelryBase;
import net.solunareclipse1.magitekkit.common.misc.damage.MGTKDmgSrc;
import net.solunareclipse1.magitekkit.common.misc.damage.MGTKEntityDamageSource;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.init.NetworkInit;
import net.solunareclipse1.magitekkit.network.packet.client.CutParticlePacket;
import net.solunareclipse1.magitekkit.network.packet.client.DrawParticleAABBPacket;
import net.solunareclipse1.magitekkit.network.packet.client.DrawParticleAABBPacket.ParticlePreset;

import codechicken.lib.util.ItemUtils;
import codechicken.lib.vec.Vector3;
import morph.avaritia.item.MatterClusterItem;

/**
 * Some common functions that don't really fit in anywhere else
 */
public class MiscHelper {
	private static final ItemStack HARVEST_HOE = getHarvestHoe();
	private static ItemStack getHarvestHoe() {
		if (HARVEST_HOE == null) {
			ItemStack hoe = new ItemStack(Items.GOLDEN_HOE);
			hoe.enchant(Enchantments.BLOCK_FORTUNE, 5);
			return hoe;
		}
		return HARVEST_HOE.copy();
	}
	private static final SoundEvent[] soundsList = {SoundEvents.ZOMBIFIED_PIGLIN_ANGRY, SoundEvents.CREEPER_PRIMED, SoundEvents.ENDERMAN_STARE, SoundEvents.AMBIENT_CAVE, SoundEvents.DROWNED_SHOOT, SoundEvents.ELDER_GUARDIAN_CURSE, SoundEvents.END_PORTAL_SPAWN, SoundEvents.ENDER_DRAGON_DEATH, SoundEvents.ENDER_DRAGON_FLAP, SoundEvents.ENDER_DRAGON_GROWL, SoundEvents.GHAST_AMBIENT, SoundEvents.GHAST_HURT, SoundEvents.PHANTOM_SWOOP, SoundEvents.PORTAL_AMBIENT, SoundEvents.PORTAL_TRIGGER, SoundEvents.WANDERING_TRADER_AMBIENT,
			PESoundEvents.CHARGE.get(), PESoundEvents.DESTRUCT.get(), PESoundEvents.HEAL.get(), PESoundEvents.POWER.get(), PESoundEvents.TRANSMUTE.get(), PESoundEvents.UNCHARGE.get(), PESoundEvents.WATER_MAGIC.get(), PESoundEvents.WIND_MAGIC.get(), EffectInit.EMC_WASTE.get(), EffectInit.ARMOR_BREAK.get(), EffectInit.SHIELD_FAIL.get()};
	
	public static void funnySound(Random rand, Level level, BlockPos pos) {
		level.playSound(null, pos, soundsList[rand.nextInt(soundsList.length)], SoundSource.PLAYERS, 1, 1);
	}
	
	public static void smiteSelf(Level level, ServerPlayer sPlayer) {
		LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
		if (bolt != null) {
			bolt.moveTo(Vec3.atCenterOf(sPlayer.blockPosition()));
			bolt.setCause(sPlayer);
			level.addFreshEntity(bolt);
		}
	}
	
	public static long smiteAllInArea(Level level, AABB area, ServerPlayer culprit, long plrEmc, int costPer) {
		int smitten = 0;
		for (LivingEntity ent : level.getEntitiesOfClass(LivingEntity.class, area)) {
			if (ent.is(culprit)) continue;
			if (plrEmc <= costPer*smitten) break;
			LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
			if (bolt != null) {
				bolt.moveTo(Vec3.atCenterOf(ent.blockPosition()));
				bolt.setCause(culprit);
				level.addFreshEntity(bolt);
			}
			smitten++;
		}
		return costPer*smitten;
	}
	
	public static long slowAllInArea(Level level, AABB area, ServerPlayer culprit, long plrEmc, int costPer) {
		int frozen = 0;
		for (LivingEntity ent : level.getEntitiesOfClass(LivingEntity.class, area)) {
			if (ent.is(culprit) || ent instanceof Stray) continue;
			if (plrEmc <= costPer*frozen) break;
			if (ent instanceof Skeleton skel) {
				skel.convertTo(EntityType.STRAY, true);
				WorldHelper.freezeInBoundingBox(level, ent.getBoundingBox().inflate(1), culprit, false);
			    if (!skel.isSilent()) {
			        skel.level.levelEvent((Player)null, 1048, skel.blockPosition(), 0);
			    }
				continue;
			} else if (ent instanceof Husk husk) {
				husk.convertTo(EntityType.ZOMBIE, true);
				if (!husk.isSilent()) {
					husk.level.levelEvent((Player)null, 1041, husk.blockPosition(), 0);
				}
			};
			ent.clearFire();
			ent.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 100));
			WorldHelper.freezeInBoundingBox(level, ent.getBoundingBox().inflate(1), culprit, false);
			level.playSound(null, ent, EffectInit.ZERO_FREEZE.get(), SoundSource.PLAYERS, 10f, 1f);
			if (ent instanceof Blaze) ent.hurt(DamageSource.FREEZE, Float.MAX_VALUE);
			ent.hurt(DamageSource.FREEZE, 1);
			frozen++;
		}
		return costPer*frozen;
	}
	
	public static long burnAllInArea(Level level, AABB area, ServerPlayer culprit, long plrEmc, int costPer) {
		if (level.isRainingAt(culprit.blockPosition())) {
			level.playSound(null, culprit, SoundEvents.LAVA_EXTINGUISH, SoundSource.HOSTILE, 1, 1);
			return 0;
		}
		int burnt = 0;
		for (LivingEntity ent : level.getEntitiesOfClass(LivingEntity.class, area)) {
			if (ent.is(culprit) || ent instanceof Blaze || ent instanceof Husk || !culprit.hasLineOfSight(ent)) continue;
			if (plrEmc <= costPer*burnt) break;
			if (ent instanceof Stray stray) {
				stray.convertTo(EntityType.SKELETON, true);
				WorldHelper.freezeInBoundingBox(level, ent.getBoundingBox().inflate(1), culprit, false);
			    if (!stray.isSilent()) {
					level.playSound(null, stray, SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE, 1, 1);
			    }
				continue;
			} else if (ent instanceof Zombie zombie && !(ent instanceof ZombifiedPiglin)) {
				if (!(zombie instanceof Husk)) {
					zombie.convertTo(EntityType.HUSK, true);
					level.playSound(null, zombie, SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE, 1, 1);
				}
			};
			//for (ServerPlayer plr : ((ServerLevel)level).players()) {
			//	if (plr.blockPosition().closerToCenterThan(culprit.position(), 64)) {
			//		NetworkInit.toClient(new DrawParticleLinePacket(culprit.getBoundingBox().getCenter(), ent.getBoundingBox().getCenter(), 3), plr);
			//	}
			//}
			ent.setRemainingFireTicks(costPer);
			burnInBoundingBox(level, ent.getBoundingBox().inflate(1), culprit, false);
			level.playSound(null, ent.blockPosition(), EffectInit.IGNITION_BURN.get(), SoundSource.PLAYERS, 1, 1);
			ent.hurt(MGTKDmgSrc.mustang(culprit), 10);
			burnt++;
		}
		return costPer*burnt;
	}
	
	/**
	 * Like WorldHelper.freezeInBoundingBox(), but with fire/air instead of snow/ice
	 */
	public static void burnInBoundingBox(Level level, AABB box, Player player, boolean random) {
		for (BlockPos pos : WorldHelper.getPositionsFromBox(box)) {
			BlockState state = level.getBlockState(pos);
			Block b = state.getBlock();
			//Ensure we are immutable so that changing blocks doesn't act weird
			pos = pos.immutable();
			if (b == Blocks.WATER) {
				if (player != null) {
					PlayerHelper.checkedReplaceBlock((ServerPlayer) player, pos, Blocks.AIR.defaultBlockState());
				} else {
					level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				}
				level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1, 1);
			} else if (Block.isFaceFull(state.getCollisionShape(level, pos.below()), Direction.UP)) {
				BlockPos up = pos.above();
				BlockState stateUp = level.getBlockState(up);
				BlockState newState = null;
				
				if (stateUp.isAir()) {
					newState = Blocks.FIRE.defaultBlockState();
				}
				if (newState != null) {
					if (player != null) {
						PlayerHelper.checkedReplaceBlock((ServerPlayer) player, up, newState);
					} else {
						level.setBlockAndUpdate(up, newState);
					}
				}
			}
		}
	}
	
	public static long pokeNearby(Level level, Player player, ItemStack stack) {
		List<Entity> toAttack = level.getEntities(player, player.getBoundingBox().inflate((stack.getDamageValue()-38)/10), entity -> !entity.isSpectator() && (entity instanceof Enemy || entity instanceof LivingEntity));
		DamageSource src = DamageSource.playerAttack(player).bypassArmor();
		long consumed = 0;
		
		player.hurt(src, 1);
		for (Entity entity : toAttack) {
			entity.hurt(src, 1);
			consumed++;
		}
		level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.CHARGE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		return consumed;
	}
	
	public static boolean attackRandomInRange(int power, AABB area, Level level, Player culprit, Predicate<LivingEntity> validator) {
		List<LivingEntity> validTargets = level.getEntitiesOfClass(LivingEntity.class, area, validator);
		Map<Entity,Integer> hit = new HashMap<>();
		if (!validTargets.isEmpty()) {
			int limit = power;// + validTargets.size()/2;
			for (int i = 0; i < limit; i++) {
				LivingEntity victim = validTargets.get(level.random.nextInt(validTargets.size()));
				float damage = Mth.clamp(victim.getMaxHealth()/10, 1, 10);
				if (victim instanceof Player plr && GemJewelryBase.fullPristineSet(plr)) {
					damage = 16*power;
				}
				int iFrames = victim.invulnerableTime;
				victim.invulnerableTime = 0;
				victim.hurt(MGTKDmgSrc.matterAoe(culprit), damage);
				victim.invulnerableTime = iFrames;
				if (hit.containsKey(victim)) {
					hit.put(victim, hit.get(victim)+1);
				} else hit.put(victim, 1);
				level.playSound(null, victim.blockPosition(), PESoundEvents.DESTRUCT.get(), SoundSource.PLAYERS, 0.3f, 1.5f);
			}
			if (level instanceof ServerLevel lvl) {
				double rot1 = (double)(-Mth.sin(culprit.getYRot() * ((float)Math.PI / 180f)));
				double rot2 = (double)Mth.cos(culprit.getYRot() * ((float)Math.PI / 180f));
				lvl.sendParticles(ParticleTypes.SWEEP_ATTACK, culprit.getX()+rot1, culprit.getY(0.5), culprit.getZ()+rot2, 1, 0, 0, 0, 0);
				for (Entry<Entity,Integer> hitEnt : hit.entrySet()) {
					// this kinda sucks and i should find a better way to do it
					AABB box = BoxHelper.growToCube(hitEnt.getKey().getBoundingBox());
					for (ServerPlayer plr : lvl.players()) {
						BlockPos pos = plr.blockPosition();
						if (pos.closerToCenterThan(box.getCenter(), 128)) {
							NetworkInit.toClient(new CutParticlePacket(hitEnt.getValue(), box), plr);
						}
						if (pos.closerToCenterThan(box.getCenter(), 128)) {
						}
					}
				}
				
			}
			level.playSound(null, culprit.blockPosition(), PESoundEvents.CHARGE.get(), SoundSource.PLAYERS, 1f, 1.0F);
			return true;
		}
		return false;
	}
	
	/**
	 * Draws a line between 2 vectors using particles <br>
	 * 
	 * @param start point A
	 * @param end point B
	 * @param particle the particle to use
	 * @param stepSize lower = more particles
	 * @param level world/level particles are in
	 */
	public static void drawVectorWithParticles(Vec3 start, Vec3 end, ParticleOptions particle, double stepSize, ClientLevel level) {
		Vec3 line = end.subtract(start);
		Vec3 step = line.normalize().scale(stepSize);
		int numSteps = (int) (line.length() / step.length());
		if (step.length() <= 0) {
			// avoids floating point nonsense
			numSteps = 1;
		}
		
		Vec3 curPos = start;
		for (int i = 0; i < numSteps; i++) {
			level.addParticle(particle, true, curPos.x, curPos.y, curPos.z, 0, 0, 0);
			curPos = curPos.add(step);
		}
	}
	
	
	
	
	/**
	 * grows crops in specified area
	 * @param level
	 * @param area
	 * @param chance 1/x chance to succeed
	 * @return amount of things grown
	 */
	public static int growNearby(ServerLevel level, AABB area, int chance) {
		int grown = 0;
		boolean doRand = chance > 1,
				grewWater = false;
		Random rand = level.random;
		for (BlockPos pos : WorldHelper.getPositionsFromBox(area)) {
			if (doRand && rand.nextInt(chance) != 0) {
				continue;
			}
			pos = pos.immutable();
			BlockState state = level.getBlockState(pos);
			Block block = state.getBlock();
			
			if (block instanceof BonemealableBlock growable) {
				growable.performBonemeal(level, rand, pos, state);
				level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, pos, 0);
				grown++;
			} else if (block instanceof IPlantable) {
				state.randomTick(level, pos, rand);
				grown++;
			} else if (!grewWater && rand.nextInt(512) == 0 && WorldHelper.growWaterPlant(level, pos, state, null)) {
				level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, pos, 0);
				grewWater = true;
				grown++;
			}
		}
		return grown;
	}
	
	/**
	 * harvest and attempts to replant crops in area
	 * @param level
	 * @param area
	 * @param chance 1/x chance to succeed
	 * @return amount of things harvested
	 */
	public static int harvestNearby(ServerPlayer player, ServerLevel level, AABB area, int chance) {
		int harvested = 0;
		boolean doRand = chance > 1;
		for (BlockPos pos : WorldHelper.getPositionsFromBox(area)) {
			if (doRand && level.random.nextInt(chance) != 0) continue;
			
			pos = pos.immutable();
			BlockState state = level.getBlockState(pos);
			
			// try using Creates method since it replants things
			// falls back on projecte if that doesnt work
			if (tryCreateHarv(player, level, pos, state) || tryPeHarv(player, level, pos, state)) {
				harvested++;
			}
			
		}
		return harvested;
	}
	
	public static int harvestNearbyNoReplant(ServerPlayer player, ServerLevel level, AABB area, int chance) {
		int harvested = 0;
		boolean doRand = chance > 1;
		for (BlockPos pos : WorldHelper.getPositionsFromBox(area)) {
			if (doRand && level.random.nextInt(chance) != 0) continue;
			
			pos = pos.immutable();
			BlockState state = level.getBlockState(pos);
			
			// try using Creates method since it replants things
			// falls back on projecte if that doesnt work
			if (tryPeHarv(player, level, pos, state)) {
				harvested++;
			}
			
		}
		return harvested;
	}
	
	/**
	 * HarvesterMovementBehaviour.visitNewPosition()
	 * @return if successful
	 */
	private static boolean tryCreateHarv(ServerPlayer player, ServerLevel world, BlockPos pos, BlockState stateVisited) {
		HarvesterMovementBehaviour harv = (HarvesterMovementBehaviour) AllMovementBehaviours.getBehaviour(AllBlocks.MECHANICAL_HARVESTER.getDefaultState());
		boolean notCropButCuttable = false;

		if (!harv.isValidCrop(world, pos, stateVisited)) {
			if (harv.isValidOther(world, pos, stateVisited)) notCropButCuttable = true;
			else return false;
		}

		ItemStack item = getHarvestHoe();
		float effectChance = 1;

		if (stateVisited.is(BlockTags.LEAVES)) {
			item = new ItemStack(Items.SHEARS);
			effectChance = .45f;
		}

		MutableBoolean seedSubtracted = new MutableBoolean(notCropButCuttable);
		BlockState state = stateVisited;
		BlockHelper.destroyBlockAs(world, pos, player, item.copy(), effectChance, stack -> {
			if (!seedSubtracted.getValue() && stack.sameItem(new ItemStack(state.getBlock()))) {
				stack.shrink(1);
				seedSubtracted.setTrue();
			}
			makeDrop(stack, world, Vec3.atCenterOf(pos));
		});

		BlockState cutCrop = cut(world, pos, stateVisited);
		world.setBlockAndUpdate(pos, cutCrop.canSurvive(world, pos) ? cutCrop : Blocks.AIR.defaultBlockState());
		return true;
	}
	
	private static boolean tryPeHarv(ServerPlayer player, ServerLevel level, BlockPos currentPos, BlockState state) {
		Block crop = state.getBlock();
		
		// Vines, leaves, tallgrass, deadbush, doubleplants
		if (crop instanceof IForgeShearable
				|| crop instanceof FlowerBlock
				|| crop instanceof DoublePlantBlock
				|| crop instanceof RootsBlock
				|| crop instanceof NetherSproutsBlock
				|| crop instanceof HangingRootsBlock) {
			harvestBlock(level, currentPos, player);
			return true;
		}
		// Carrot, cocoa, wheat, grass (creates flowers and tall grass in vicinity),
		// Mushroom, potato, sapling, stems, tallgrass
		else if (crop instanceof BonemealableBlock growable) {
			if (!growable.isValidBonemealTarget(level, currentPos, state, false)) {
				if (!state.is(PETags.Blocks.BLACKLIST_HARVEST)) {
					if (!(crop == Blocks.KELP_PLANT || crop == Blocks.BAMBOO) || level.getBlockState(currentPos.below()).is(crop)) {
						// Don't harvest the bottom of kelp but otherwise allow harvesting them
						harvestBlock(level, currentPos, (ServerPlayer) player);
						return true;
					}
				}
			}
		}
		// All modded
		// Cactus, Reeds, Netherwart, Flower
		else if (crop instanceof IPlantable) {
			if (crop == Blocks.SUGAR_CANE || crop == Blocks.CACTUS) {
				if (level.getBlockState(currentPos.above()).is(crop) && level.getBlockState(currentPos.above(2)).is(crop)) {
					for (int i = crop == Blocks.SUGAR_CANE ? 1 : 0; i < 3; i++) {
						harvestBlock(level, currentPos.above(i), (ServerPlayer) player);
						return true;
					}
				}
			} else if (crop == Blocks.NETHER_WART) {
				if (state.getValue(NetherWartBlock.AGE) == 3) {
					harvestBlock(level, currentPos, (ServerPlayer) player);
					return true;
				}
			}
		}
		return false;
	}
	
	private static void harvestBlock(Level level, BlockPos pos, @Nullable ServerPlayer player) {
		if (player == null || PlayerHelper.hasBreakPermission(player, pos)) {
			level.destroyBlock(pos, true, player);
		}
	}
	
	private static void makeDrop(ItemStack stack, Level world, Vec3 vec) {
		ItemStack remainder = stack;
		if (remainder.isEmpty()) return;
		
		ItemEntity itemEntity = new ItemEntity(world, vec.x, vec.y, vec.z, remainder);
		world.addFreshEntity(itemEntity);
	}
	
	private static BlockState cut(Level world, BlockPos pos, BlockState state) {
		Block block = state.getBlock();
		if (block instanceof CropBlock) {
			CropBlock crop = (CropBlock) block;
			return crop.getStateForAge(0);
		}
		if (block == Blocks.SWEET_BERRY_BUSH) {
			return state.setValue(BlockStateProperties.AGE_3, Integer.valueOf(1));
		}
		if (block == Blocks.SUGAR_CANE || block instanceof GrowingPlantBlock) {
			if (state.getFluidState()
				.isEmpty())
				return Blocks.AIR.defaultBlockState();
			return state.getFluidState()
				.createLegacyBlock();
		}
		if (state.getCollisionShape(world, pos)
			.isEmpty() || block instanceof CocoaBlock) {
			for (Property<?> property : state.getProperties()) {
				if (!(property instanceof IntegerProperty))
					continue;
				if (!property.getName()
					.equals(BlockStateProperties.AGE_1.getName()))
					continue;
				return state.setValue((IntegerProperty) property, Integer.valueOf(0));
			}
		}

		if (state.getFluidState()
			.isEmpty())
			return Blocks.AIR.defaultBlockState();
		return state.getFluidState()
			.createLegacyBlock();
	}
	
	public static int getTrueEnchMaxLevel(Enchantment ench) {
		if (ench instanceof FishingSpeedEnchantment || ench instanceof QuickChargeEnchantment)
			return 5;
		return 10;
	}

	/**
	 * veinmines all ores in the given AABB <br>
	 * based on ToolHelper.mineOreVeinsInAOE <br>
	 * uses avaritia matter clusters to imitate old EE2 loot balls
	 */
	public static boolean aoeOreCollect(Player player, AABB area, Level level, ItemStack stack) {
		boolean hasAction = false;
		List<ItemStack> drops = new ArrayList<>();
		ItemStack fortunePick = stack.copy();
		fortunePick.enchant(Enchantments.BLOCK_FORTUNE, 5);
		int count = 1;
		for (BlockPos pos : WorldHelper.getPositionsFromBox(area)) {
			if (level.isEmptyBlock(pos)) continue;
			BlockState state = level.getBlockState(pos);
			if (ItemHelper.isOre(state) && state.getDestroySpeed(level, pos) != -1 && stack.isCorrectToolForDrops(state)) {
				if (level.isClientSide) {
					return true;
				}
				//Ensure we are immutable so that changing blocks doesn't act weird
				int oresMined = WorldHelper.harvestVein(level, player, fortunePick, pos.immutable(), state.getBlock(), drops, 0);
				if (oresMined > 0) {
					if (level.random.nextInt(count) == 0) {
						// prevents extreme LOUD
						if (count < 50) count++;
						level.playSound(null, pos, PESoundEvents.DESTRUCT.get(), SoundSource.PLAYERS, 0.5f, 0.8f);
					}
					hasAction = true;
				}
			}
		}
		if (hasAction) {
			List<ItemStack> lootBalls = MatterClusterItem.makeClusters(drops);
			for (ItemStack cluster : lootBalls) {
				ItemUtils.dropItem(cluster, level, Vector3.fromEntityCenter(player));
			}
			return true;
		}
		return false;
	}

	/**
	 * harvests tree blocks in the given area <br>
	 * based on ToolHelper.clearTagAOE <br>
	 * uses avaritia matter clusters to imitate old EE2 loot balls
	 */
	public static boolean ecosystemDestroyer(Player player, AABB area, Level level, ItemStack stack) {
		boolean hasAction = false;
		List<ItemStack> drops = new ArrayList<>();
		int count = 1;
		for (BlockPos pos : WorldHelper.getPositionsFromBox(area)) {
			BlockState state = level.getBlockState(pos);
			if (state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS)) {
				//Ensure we are immutable so that changing blocks doesn't act weird
				pos = pos.immutable();
				if (!level.isClientSide && PlayerHelper.hasBreakPermission((ServerPlayer) player, pos)) {
					drops.addAll(Block.getDrops(state, (ServerLevel) level, pos, WorldHelper.getBlockEntity(level, pos), player, stack));
					level.removeBlock(pos, false);
					if (level.random.nextInt(count) == 0) {
						// prevents extreme LOUD
						if (count < 50) count++;
						level.playSound(null, pos, PESoundEvents.DESTRUCT.get(), SoundSource.PLAYERS, 0.5f, 0.8f);
					}
					hasAction = true;
				}
			}
		}
		if (hasAction) {
			List<ItemStack> lootBalls = MatterClusterItem.makeClusters(drops);
			for (ItemStack cluster : lootBalls) {
				ItemUtils.dropItem(cluster, level, Vector3.fromEntityCenter(player));
			}
			return true;
		}
		return false;
	}
	
	/**
	 * morning-star right click <br>
	 * based on ToolHelper.digAOE
	 * @return
	 */
	public static boolean insuranceClaimGenerator(Player player, BlockPos pos, Direction dir, int size, Level level, ItemStack stack) {
		AABB box = WorldHelper.getBroadDeepBox(pos, dir, size);
		if (player.isOnGround() && !dir.getAxis().isVertical()) {
			// move the area up so that it matches with player feet
			double shift = box.getSize()/2d;
			box = box.move(0, (int)shift, 0);
		}
		boolean hasAction = false;
		List<ItemStack> drops = new ArrayList<>();
		ItemStack breakerStack = new ItemStack(PEItems.RED_MATTER_MORNING_STAR.get());
		for (BlockPos newPos : WorldHelper.getPositionsFromBox(box)) {
			if (level.isEmptyBlock(newPos)) {
				continue;
			}
			BlockState state = level.getBlockState(newPos);
			if (state.getDestroySpeed(level, newPos) != -1 && breakerStack.isCorrectToolForDrops(state)) {
				if (level.isClientSide) {
					return true;
				}
				//Ensure we are immutable so that changing blocks doesn't act weird
				newPos = newPos.immutable();
				if (PlayerHelper.hasBreakPermission((ServerPlayer) player, newPos)) {
					drops.addAll(Block.getDrops(state, (ServerLevel) level, newPos, WorldHelper.getBlockEntity(level, newPos), player, breakerStack));
					level.removeBlock(newPos, false);
					hasAction = true;
				}
			}
		}
		if (hasAction) {
			List<ItemStack> lootBalls = MatterClusterItem.makeClusters(drops);
			for (ItemStack cluster : lootBalls) {
				ItemUtils.dropItem(cluster, level, Vector3.fromEntityCenter(player));
			}
			level.playSound(null, new BlockPos(box.getCenter()), EffectInit.EMC_EXPLODE.get(), SoundSource.PLAYERS, 2, 1.5f);
			if (level instanceof ServerLevel lvl) {
				lvl.sendParticles(ParticleTypes.EXPLOSION_EMITTER, box.getCenter().x, box.getCenter().y, box.getCenter().z, 1, 0, 0, 0, 0);
			}
			return true;
		}
		return false;
	}
}
