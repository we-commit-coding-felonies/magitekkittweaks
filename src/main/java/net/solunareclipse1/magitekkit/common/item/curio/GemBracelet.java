package net.solunareclipse1.magitekkit.common.item.curio;

import java.util.Objects;
import java.util.Random;
import java.util.Stack;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunk.BoundTickingBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk.RebindableTickingBlockEntityWrapper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent.Context;

import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.api.capabilities.item.IModeChanger;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.capability.ExtraFunctionItemCapabilityWrapper;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.capability.ProjectileShooterItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.PETags.BlockEntities;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;

import net.solunareclipse1.magitekkit.api.capability.wrapper.ChargeItemCapabilityWrapperButBetter;
import net.solunareclipse1.magitekkit.api.capability.wrapper.converter.ManaCovalentCapabilityWrapper;
import net.solunareclipse1.magitekkit.api.item.ISwingItem;
import net.solunareclipse1.magitekkit.common.entity.projectile.FreeLavaProjectile;
import net.solunareclipse1.magitekkit.common.item.MGTKItem;
import net.solunareclipse1.magitekkit.common.item.armor.gem.GemJewelryBase;
import net.solunareclipse1.magitekkit.common.misc.MGTKDmgSrc;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.init.NetworkInit;
import net.solunareclipse1.magitekkit.network.packet.client.MustangExplosionPacket;
import net.solunareclipse1.magitekkit.util.Constants.Xp;

import morph.avaritia.handler.ArmorHandler;

import net.solunareclipse1.magitekkit.util.EmcHelper;
import net.solunareclipse1.magitekkit.util.MiscHelper;
import net.solunareclipse1.magitekkit.util.PlrHelper;
import net.solunareclipse1.magitekkit.util.ProjectileHelper;

import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.mana.BurstProperties;
import vazkii.botania.api.mana.ILensEffect;
import vazkii.botania.common.entity.EntityManaBurst;
import vazkii.botania.common.handler.ModSounds;
import vazkii.botania.common.helper.ItemNBTHelper;


// TODO: serious code-cleanup
public class GemBracelet extends MGTKItem implements IModeChanger, IItemCharge, IProjectileShooter, IExtraFunction, ISwingItem, ILensEffect {

	//////////////////////////////////////////////
	// CONSTANTS, GLOBAL VARS, AND CONSTRUCTORS //
	//////////////////////////////////////////////
	public static final Vec3 FORWARD_RELATIVE = new Vec3(0,0,1);
	public static final String TAG_MODE = "arc_mode";
	public static final String TAG_EXP = "arc_experience";
	public static final String TAG_LVL = "arc_levels";
	public static final String TAG_LIQUID = "arc_liquid";
	public static final String TAG_WOFT = "arc_woft";
	public static final String TAG_OFFENSIVE = "arc_offensive";
	private static final String[] KEY_MODES = {
			"tip.mgtk.arc_mode_0", // Disabled
			"tip.mgtk.arc_mode_1", // Mind
			"tip.mgtk.arc_mode_2", // Watch
			"tip.mgtk.arc_mode_3", // Harvest
			"tip.mgtk.arc_mode_4", // Liquid
			"tip.mgtk.arc_mode_5", // Philo
			"tip.mgtk.arc_mode_6", // Archangels
			"tip.mgtk.arc_mode_7", // SWRG
			"tip.mgtk.arc_mode_8", // Zero
			"tip.mgtk.arc_mode_9"  // Ignition
	};
	
	public GemBracelet(Properties props) {
		super(props);
		
		// Listeners
		MinecraftForge.EVENT_BUS.addListener(this::onStopUsing);
		MinecraftForge.EVENT_BUS.addListener(this::onFinishUsing);
		MinecraftForge.EVENT_BUS.addListener(this::sendEmptySwingToServer);
		MinecraftForge.EVENT_BUS.addListener(this::onSwingBlock);

		// Capabilities
		addItemCapability(ManaCovalentCapabilityWrapper::new);
		addItemCapability(ModeChangerItemCapabilityWrapper::new);
		addItemCapability(ChargeItemCapabilityWrapperButBetter::new);
		addItemCapability(ExtraFunctionItemCapabilityWrapper::new);
		addItemCapability(ProjectileShooterItemCapabilityWrapper::new);
	}
	
	
	
	
	
	
	
	
	
	
	//////////////////////////////////////////////////////////////
	// KEYPRESS HANDLING, ABILITIES DEFINING, AND FUNCTIONALITY //
	//////////////////////////////////////////////////////////////
	
	// IExtraFunction (C)
	@Override
	public boolean doExtraFunction(@NotNull ItemStack stack, @NotNull Player player, @Nullable InteractionHand hand) {
		if (!player.level.isClientSide && playerHasFullPristineSet(player) && getCharge(player.getItemInHand(hand)) == 1) {
			long plrEmc = EmcHelper.getAvaliableEmc(player);
			boolean didDo = false;
			switch (getMode(stack)) {
			
			case 1: // Mind (deposit all xp)
				if (PlrHelper.getXp(player) > 0) {
					insertXp(stack, PlrHelper.extractAll(player));
					player.level.playSound(null, player, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1, 2f);
					didDo = true;
				}
				break;
				
			case 2: // Watch (toggle time accel)
				changeWoft(stack);
				player.level.playSound(null, player, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.PLAYERS, 1, 1.4f);
				didDo = true;
				break;
				
			case 3: // Harvest (aoe harvest & grow)
				if (plrEmc >= 24) {
					EmcHelper.consumeAvaliableEmc(player, 24);
					WorldHelper.growNearbyRandomly(true, player.level, player.blockPosition(), player);
					didDo = true;
				}
				break;
				
			case 4: // Liquid (swap current liquid)
				changeLiquid(stack);
				player.level.playSound(null, player, getLiquid(stack) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1, 0.7f);
				didDo = true;
				break;
				
			case 5: // Philo (crafting grid)
				didDo = PEItems.PHILOSOPHERS_STONE.get().doExtraFunction(stack, player, hand);
				break;
				
			case 6: // Archangels (scatter sniper-arrows)
				if (plrEmc >= 256 && !player.getCooldowns().isOnCooldown(PEItems.ARCHANGEL_SMITE.get())) {
					int shot;
					for (shot = 0; shot < 28; shot++) {
						if ((shot+1)*256 >= plrEmc) break;
						ProjectileHelper.shootArrow(player.level, player, 10, player.getRandom().nextFloat(10), 300, Byte.MAX_VALUE, true, false, Pickup.CREATIVE_ONLY);
						if (!didDo) didDo = true;
					}
					if (didDo) {
						EmcHelper.consumeAvaliableEmc(player, 256*shot);
						player.getCooldowns().addCooldown(PEItems.ARCHANGEL_SMITE.get(), 30);
					}
				} // TODO: aimbot
				break;
				
			case 7: // SWRG (aoe smite)
				if (player instanceof ServerPlayer) {
					if (plrEmc >= 1024 && !player.getCooldowns().isOnCooldown(PEItems.SWIFTWOLF_RENDING_GALE.get())) {
						didDo = EmcHelper.consumeAvaliableEmc(player, MiscHelper.smiteAllInArea(player.level, player.getBoundingBox().inflate(24), (ServerPlayer) player, plrEmc)) > 0;
						player.getCooldowns().addCooldown(PEItems.SWIFTWOLF_RENDING_GALE.get(), 30);
					}
				}
				break;
				
			case 8: // Zero (aoe freeze)
				if (player instanceof ServerPlayer) {
					if (plrEmc >= 256 && !player.getCooldowns().isOnCooldown(PEItems.ZERO_RING.get())) {
						didDo = EmcHelper.consumeAvaliableEmc(player, MiscHelper.slowAllInArea(player.level, player.getBoundingBox().inflate(24), (ServerPlayer) player, plrEmc)) > 0;
						player.getCooldowns().addCooldown(PEItems.ZERO_RING.get(), 30);
					}
				}
				break;
				
			case 9: // Ignition (aoe burn)
				if (player instanceof ServerPlayer) {
					if (plrEmc >= 512 && !player.getCooldowns().isOnCooldown(PEItems.IGNITION_RING.get())) {
						didDo = EmcHelper.consumeAvaliableEmc(player, MiscHelper.burnAllInArea(player.level, player.getBoundingBox().inflate(24), (ServerPlayer) player, plrEmc)) > 0;
						player.getCooldowns().addCooldown(PEItems.IGNITION_RING.get(), 30);
					}
				}
				break;
				
			default:
				break;
			}
			return didDo;
		}
		return false;
	}

	
	// IProjectileShooter
	@Override
	public boolean shootProjectile(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {		
		if (!player.level.isClientSide() && playerHasFullPristineSet(player) && getCharge(player.getItemInHand(hand)) == 1) {
			long plrEmc = EmcHelper.getAvaliableEmc(player);
			boolean didDo = false;
			ItemCooldowns cooldown = player.getCooldowns();
			switch (getMode(stack)) {
			
			case 1: // Mind (withdraw all)
				long stackXp = getXp(stack);
				long plrExp = PlrHelper.getXp(player);
				if (stackXp > 0) {
					long toWithdraw = Math.min(stackXp, Xp.VANILLA_MAX_POINTS - plrExp);
					if (toWithdraw > 0) {
						PlrHelper.insertXp(player, extractXp(stack, toWithdraw));
						player.level.playSound(null, player, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1, 2f);
						didDo = true;
					}
				}
				break;
				
			case 2: // Watch (Teleport)
				if (plrEmc >= 72) {
					didDo = PEItems.VOID_RING.get().doExtraFunction(stack, player, hand);
					if (didDo) EmcHelper.consumeAvaliableEmc(player, 72);
				}
				break;
				
			case 3: // Harvest
				System.out.println("NYI: " + getMode(stack));
				break;
				
			case 4: // Liquid (Liquid orb)
				boolean isLava = ItemNBTHelper.getBoolean(stack, TAG_LIQUID, false);
				
				ItemPE cdItem = isLava ? PEItems.VOLCANITE_AMULET.get() : PEItems.EVERTIDE_AMULET.get();
				
				if (!cooldown.isOnCooldown(cdItem)) {
					didDo = isLava ? shootLavaProjectile(player) : PEItems.EVERTIDE_AMULET.get().shootProjectile(player, stack, hand);
				}
				if (didDo) {
					cooldown.addCooldown(cdItem, 5);
				}
				break;
				
			case 5: // Philo (Mob transmute orb)
				if (!cooldown.isOnCooldown(PEItems.PHILOSOPHERS_STONE.get())) {
					didDo = PEItems.PHILOSOPHERS_STONE.get().shootProjectile(player, stack, hand);
				}
				break;
				
			case 6: // Archangels (sniper arrow)
				if (plrEmc >= 1024 && !cooldown.isOnCooldown(PEItems.ARCHANGEL_SMITE.get())) {
					ProjectileHelper.shootArrow(player.level, player, 10, 10, 0, Byte.MAX_VALUE, true, false, Pickup.CREATIVE_ONLY);
					EmcHelper.consumeAvaliableEmc(player, 1024);
					cooldown.addCooldown(PEItems.ARCHANGEL_SMITE.get(), 5);
					didDo = true;
				}
				break;
				
			case 7: // SWRG (Self-fling / Summon lightning)
				if (!cooldown.isOnCooldown(PEItems.SWIFTWOLF_RENDING_GALE.get())) {
					if (plrEmc >= 128 && !player.isShiftKeyDown() && !player.getAbilities().flying) {
						player.moveRelative(2, player.getLookAngle());
						player.level.playSound(null, player, PESounds.WIND, SoundSource.PLAYERS, 1, 1);
					} else if (plrEmc >= 8192 && player.isShiftKeyDown() && player.level.getLevelData() instanceof ServerLevelData lvlData) {
						lvlData.setRainTime(6000);
						lvlData.setThunderTime(6000);
						lvlData.setRaining(true);
						lvlData.setThundering(true);
						EmcHelper.consumeAvaliableEmc(player, 8192);
						didDo = true;
					}
					
				}
				break;
				
			case 8: // Zero
				System.out.println("NYI: " + getMode(stack));
				break;
				
			case 9: // Ignition
				if (plrEmc >= 139264 && !cooldown.isOnCooldown(PEItems.IGNITION_RING.get())) {
					if (shootMustang(player, stack)) {
						EmcHelper.consumeAvaliableEmc(player, 139264);
						didDo = true;
					}
					cooldown.addCooldown(PEItems.IGNITION_RING.get(), 30);
				}
				break;
				
			default:
				break;
			}
			return didDo;
		}
		return false;
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		if (/*!level.isClientSide() && playerHasFullPristineSet(player) &&*/ getCharge(player.getItemInHand(hand)) == 1) {
			boolean didDo = false;
			ItemStack stack = player.getItemInHand(hand);
			long plrEmc = EmcHelper.getAvaliableEmc(player);
			switch (getMode(stack)) {
			
			case 1: // Mind (deposit 1 lvl / 10 lvl)
				if (PlrHelper.getXp(player) > 0 && getXp(stack) < Long.MAX_VALUE) {
					int lvls = player.isShiftKeyDown() ? 10 : 1;
					insertXp(stack, PlrHelper.extractLvl(player, lvls));
				}
				player.level.playSound(null, player, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1, 1f);
				didDo = true;
				break;
				
			case 2: // Watch (gravity attract)
				player.startUsingItem(hand);
				didDo = true;
				break;
				
			case 6: // Archangels (debuff arrow stream init)
				player.startUsingItem(hand);
				didDo = true;
				break;
				
			case 7:
				System.out.println("NYI: 7");
				break;
				
			case 8: // Zero (place ice midair)
				System.out.println("NYI: 8");
				break;
				
			default:
				break;
			}
			if (didDo) return InteractionResultHolder.success(stack);
		}
		return InteractionResultHolder.fail(player.getItemInHand(hand));
	}
	
	@Override
	public void onUsingTick(ItemStack stack, LivingEntity entity, int timeBeingUsed) {
		//if (entity.level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
		//	Level world = entity.level;
		//	world.setDayTime(Math.min(entity.level.getDayTime() + ((69420/10) + 1) * 4L, Long.MAX_VALUE));
		//}
		if (entity instanceof Player player && !player.level.isClientSide() && GemJewelryBase.fullPristineSet(player) && getCharge(stack) == 1) {
			long plrEmc = EmcHelper.getAvaliableEmc(player);
			Level level = player.level;
			switch (getMode(stack)) {

			case 2: // Watch (Time Acceleration)
				// yes, this is a jojo reference
				if (plrEmc >= 1024) {
					int ticker = Integer.MAX_VALUE - timeBeingUsed;
					int bonusTicks = Math.min(180, ticker/12);
					EmcHelper.consumeAvaliableEmc(player, getWoft(stack) ? 1024*(bonusTicks/10) : 128*(bonusTicks/10));
					EmcHelper.consumeAvaliableEmc(player, 1024*(bonusTicks/10));
					double entSlow = Math.max(Double.MIN_VALUE, 1d - (bonusTicks/180d));
					double selfSpeed = Math.min(12d, Math.max(2d/3d, 2d/3d*(bonusTicks/10d)));
					//double rate = Math.min(2, timeBeingUsed/1000);
					//(20 - ((int)rate*10 - 1))
					if (timeBeingUsed % (20 - bonusTicks/10) == 0) {
						player.level.playSound(null, player, EffectInit.WOFT_TICK.get(), SoundSource.PLAYERS, 1, 1);
					}
					player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(selfSpeed);
					player.getAttribute(Attributes.ATTACK_SPEED).setBaseValue(selfSpeed*10);
					player.getAttribute(ForgeMod.SWIM_SPEED.get()).setBaseValue(selfSpeed*10);
					int size = getWoft(stack) ? 24 : 12;
					AABB area = AABB.ofSize( player.position(), size, size, size);
					// following is modified from projecte woft code
					for (LivingEntity ent : level.getEntitiesOfClass(LivingEntity.class, area)) {
						// dont affect ourself or players with full gem / infinity armor
						if ( ent.is(player) || (ent instanceof Player plr && (GemJewelryBase.fullPristineSet(plr) || ArmorHandler.isInfinite(plr))) ) {
							continue;
						}
						ent.setDeltaMovement(ent.getDeltaMovement().multiply(entSlow, entSlow, entSlow));
						ent.invulnerableTime = Math.max(0, ent.invulnerableTime-bonusTicks/20);
					}
					
					for (Projectile proj : level.getEntitiesOfClass(Projectile.class, area)) {
						proj.setDeltaMovement(proj.getDeltaMovement().multiply(entSlow, entSlow, entSlow));
					} // TODO: fix issues with projectiles not visually being where they are supposed to
					
					if (getWoft(stack)) {
						// speedUpBlockEntities()
						for (BlockEntity blockEntity : WorldHelper.getBlockEntitiesWithinAABB(level, area)) {
							if (!blockEntity.isRemoved() && !BlockEntities.BLACKLIST_TIME_WATCH_LOOKUP.contains(blockEntity.getType())) {
								BlockPos pos = blockEntity.getBlockPos();
								if (level.shouldTickBlocksAt(ChunkPos.asLong(pos))) {
									LevelChunk chunk = level.getChunkAt(pos);
									RebindableTickingBlockEntityWrapper tickingWrapper = chunk.tickersInLevel.get(pos);
									if (tickingWrapper != null && !tickingWrapper.isRemoved()) {
										if (tickingWrapper.ticker instanceof BoundTickingBlockEntity tickingBE) {
											//In general this should always be the case, so we inline some of the logic
											// to optimize the calls to try and make extra ticks as cheap as possible
											if (chunk.isTicking(pos)) {
												ProfilerFiller profiler = level.getProfiler();
												profiler.push(tickingWrapper::getType);
												BlockState state = chunk.getBlockState(pos);
												if (blockEntity.getType().isValid(state)) {
													for (int i = 0; i < bonusTicks; i++) {
														tickingBE.ticker.tick(level, pos, state, blockEntity);
													}
												}
												profiler.pop();
											}
										} else {
											//Fallback to just trying to make it tick extra
											for (int i = 0; i < bonusTicks; i++) {
												tickingWrapper.tick();
											}
										}
									}
								}
							}
						}
						
						// speedUpRandomTicks()
						for (BlockPos pos : WorldHelper.getPositionsFromBox(area)) {
							if (WorldHelper.isBlockLoaded(level, pos)) {
								BlockState state = level.getBlockState(pos);
								Block block = state.getBlock();
								if (state.isRandomlyTicking() && !state.is(PETags.Blocks.BLACKLIST_TIME_WATCH)
									&& !(block instanceof LiquidBlock) // Don't speed non-source fluid blocks - dupe issues
									&& !(block instanceof BonemealableBlock) && !(block instanceof IPlantable)) {// All plants should be sped using Harvest Goddess
									pos = pos.immutable();
									for (int i = 0; i < bonusTicks; i++) {
										state.randomTick((ServerLevel)level, pos, level.random);
									}
								}
							}
						}
						
						// global time acceleration
						// TODO: make the sun/moon not teleport
						if (level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
							ServerLevel serverWorld = (ServerLevel) level;
							serverWorld.setDayTime(Math.min(level.getDayTime() + ((bonusTicks/10) + 1) * 6L, Long.MAX_VALUE));
						}
					}
				}
				
				
				// old stuff
				//Vec3 delt = player.getDeltaMovement().normalize().scale(0.25);
				//System.out.println(UUID.randomUUID());
				//System.out.println(player.getSpeed());
				//player.setDeltaMovement(player.getDeltaMovement().add(delt));
				//player.moveRelative(0.5f, new Vec3(0,0,delt.x));
				//player.push(v.x, v.y, v.z);
				break;
				
			case 6: // Archangels (debuff arrow stream)
				if (plrEmc >= 128 && !player.getCooldowns().isOnCooldown(PEItems.ARCHANGEL_SMITE.get())) {
					ProjectileHelper.shootArrowTipped(level, player, 0.01f, 3, 4, (byte) 0, false, true, Pickup.CREATIVE_ONLY, new MobEffectInstance(EffectInit.TRANSMUTING.get(), 15));
					EmcHelper.consumeAvaliableEmc(player, 128);
				}
				break;
				
			default:
				break;
			}
		} else if (getCharge(stack) == 0) {
			resetTimeAccelSpeed(entity);
		}
	}
	
	/**
	 * sets the base movement & swim speed of an entity to 0.1 & 1 respectively(player default) <br>
	 * intended to be used to clear the WoFT speedup effect on players
	 * @param player player to reset the speed of
	 */
	public void resetTimeAccelSpeed(LivingEntity ent) {
		ent.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1);
		ent.getAttribute(Attributes.ATTACK_SPEED).setBaseValue(4.0);
		ent.getAttribute(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.0);
	}
	
	public void onStopUsing(LivingEntityUseItemEvent.Stop event) {resetTimeAccelSpeed(event.getEntityLiving());}
	public void onFinishUsing(LivingEntityUseItemEvent.Finish event) {resetTimeAccelSpeed(event.getEntityLiving());}
	
	@Override
	public boolean onSwingAir(Context ctx) {
		// this should never run clientside, so a check for that is unnecessary
		ServerPlayer player = ctx.getSender();
		if (playerHasFullPristineSet(player) && getCharge(player.getMainHandItem()) == 1) {
			boolean didDo = false;
			ServerLevel level = player.getLevel();
			ItemStack stack = player.getMainHandItem();
			long plrEmc = EmcHelper.getAvaliableEmc(player);
			switch (getMode(stack)) {
			
			case 1: // Mind (withdraw 1 / 10 levels)
				if (getXp(stack) > 0 && PlrHelper.getXp(player) < Xp.VANILLA_MAX_POINTS) {
					int lvls = player.isShiftKeyDown() ? 10 : 1;
					long amount = 0;
					for (int i = 0; i < lvls; i++) {
						int curLvl = player.experienceLevel + i;
						// only factor in current progress for the first level
						if (i == 0 && player.experienceProgress > 0) {
							amount += PlrHelper.xpNeededToLevelUpFrom(curLvl) - player.experienceProgress * PlrHelper.xpNeededToLevelUpFrom(curLvl);
						} else {
							amount += PlrHelper.xpNeededToLevelUpFrom(curLvl);
						}
					}
					PlrHelper.insertXp(player, extractXp(stack, amount));
					player.level.playSound(null, player, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1, 1f);
					didDo = true;
				}
				break;
				
			case 6: // Archangels (homing shotgun)
				didDo = shootHomingVolley(player);
				break;
				
			case 8: // Zero (high-velocity snowball shotgun)
				for (int i = 0; i < 10; i++) {
			        Snowball snowball = new Snowball(level, player);
			        snowball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 4F, 4.0F);
			        level.addFreshEntity(snowball);
			        level.playSound(null, player, SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
			        if (!didDo) didDo = true;
				}
				break;
				
			default:
				break;
			}
			return didDo;
		}
		return false;
	}

	@Override
	public boolean onSwingBlock(PlayerInteractEvent.LeftClickBlock evt) {
		if (!evt.getPlayer().level.isClientSide() && evt.getUseItem() != Event.Result.DENY && !evt.getItemStack().isEmpty() && evt.getItemStack().getItem() == this) {
			ItemStack stack = evt.getItemStack();
			Player player = evt.getPlayer();
			if (GemJewelryBase.fullPristineSet(player) && getCharge(stack) == 1) {
				boolean didDo = false;
				switch (getMode(stack)) {
				
				case 1: // Mind (withdraw 1 / 10 levels)
					if (getXp(stack) > 0 && PlrHelper.getXp(player) < Xp.VANILLA_MAX_POINTS) {
						int lvls = player.isShiftKeyDown() ? 10 : 1;
						long amount = 0;
						for (int i = 0; i < lvls; i++) {
							int curLvl = player.experienceLevel + i;
							// only factor in current progress for the first level
							if (i == 0 && player.experienceProgress > 0) {
								amount += PlrHelper.xpNeededToLevelUpFrom(curLvl) - player.experienceProgress * PlrHelper.xpNeededToLevelUpFrom(curLvl);
							} else {
								amount += PlrHelper.xpNeededToLevelUpFrom(curLvl);
							}
						}
						PlrHelper.insertXp(player, extractXp(stack, amount));
						player.level.playSound(null, player, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1, 1f);
						didDo = true;
					}
					break;
					
				case 6: // Archangels
					didDo = shootHomingVolley(player);
					break;
					
				default:
					break;
				}
				return didDo;
			}
		}
		return false;
		//return shootHomingVolley(evt.getPlayer());
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
		if (!player.level.isClientSide() && stack.getItem() instanceof GemBracelet) {
			boolean didDo = false;
			switch (getMode(stack)) {
			
			case 1: // Mind (withdraw 1 / 10 levels)
				if (getXp(stack) > 0 && PlrHelper.getXp(player) < Xp.VANILLA_MAX_POINTS) {
					int lvls = player.isShiftKeyDown() ? 10 : 1;
					long amount = 0;
					for (int i = 0; i < lvls; i++) {
						int curLvl = player.experienceLevel + i;
						// only factor in current progress for the first level
						if (i == 0 && player.experienceProgress > 0) {
							amount += PlrHelper.xpNeededToLevelUpFrom(curLvl) - player.experienceProgress * PlrHelper.xpNeededToLevelUpFrom(curLvl);
						} else {
							amount += PlrHelper.xpNeededToLevelUpFrom(curLvl);
						}
					}
					PlrHelper.insertXp(player, extractXp(stack, amount));
					player.level.playSound(null, player, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1, 1f);
					didDo = true;
				}
				break;
				
			case 5: // Philo (transmutation punch)
				break;
			
			case 6: // Archangels (homing shotgun)
				didDo = shootHomingVolley(player);
				break;
				
			case 9:
				if (!entity.fireImmune() && !entity.isOnFire() && !entity.isInWaterRainOrBubble() && !player.isInWaterRainOrBubble()) {
					
				}
				break;
				
			default:
				break;
			}
			return didDo;
		}
		return super.onLeftClickEntity(stack, player, entity);
	}
	
	
	
	
	
	
	
	
	
	
	///////////////////////////////////////////////
	// NBT MANAGEMENT, IITEMCHARGE, IMODECHANGER //
	///////////////////////////////////////////////
	public boolean getLiquid(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, TAG_LIQUID, false);
	}
	
	public void changeLiquid(ItemStack stack) {
		ItemNBTHelper.setBoolean(stack, TAG_LIQUID, !getLiquid(stack));
	}
	
	
	public boolean getWoft(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, TAG_WOFT, false);
	}
	
	public void changeWoft(ItemStack stack) {
		ItemNBTHelper.setBoolean(stack, TAG_WOFT, !getWoft(stack));
	}

	
	@Override
	public byte getMode(@NotNull ItemStack stack) {
		return ItemNBTHelper.getByte(stack, TAG_MODE, (byte) 0);
	}

	@Override
	public boolean changeMode(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
		if (getCharge(stack) == 0) return false;
		byte curMode = getMode(stack);
		
		byte newMode;
		if (player.isShiftKeyDown()) {
			if (curMode == 0) return false;
			ItemNBTHelper.setBoolean(stack, ManaCovalentCapabilityWrapper.TAG_STATE, false);
			newMode = 0; // shift + modeswitch = disable all functionality
		}
		else if (curMode == 9) newMode = 1;
		else newMode = (byte) (curMode + 1);
		
		if (newMode != 0 && curMode == 0) ItemNBTHelper.setBoolean(stack, ManaCovalentCapabilityWrapper.TAG_STATE, true);
		ItemNBTHelper.setByte(stack, TAG_MODE, newMode);
		
		// Displays a message over the hotbar on mode switch, corresponding to newMode
		player.displayClientMessage(new TranslatableComponent("tip.mgtk.arc_mode_swap", new TranslatableComponent(KEY_MODES[newMode])), true);
		player.level.playSound(null, player, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.PLAYERS, 1, 0.7f);
		return true;
	}

	
	@Override
	public int getNumCharges(@NotNull ItemStack stack) {return 1;}
	
	@Override
	public int getCharge(@NotNull ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, TAG_OFFENSIVE, false) ? 1 : 0;
	}
	
	@Override
	public float getChargePercent(@NotNull ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, TAG_OFFENSIVE, false) ? 1 : 0;
	}
	
	@Override
	public boolean changeCharge(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
		boolean isOffensive = getCharge(stack) == 1;

		if (isOffensive) player.level.playSound(null, player, PESounds.UNCHARGE, SoundSource.PLAYERS, 1, 1);
		else player.level.playSound(null, player, PESounds.CHARGE, SoundSource.PLAYERS, 1, 1);
		ItemNBTHelper.setBoolean(stack, TAG_OFFENSIVE, !isOffensive);
		
		return true;
	}
	
	
	
	
	
	
	
	
	
	


	//////////////////////////////////////////////
	// IGNITION, MUSTANG PROJECTILE, MANA BURST //
	//////////////////////////////////////////////
	@Override
	public void apply(ItemStack stack, BurstProperties props, Level level) {}
	
	@Override
	public boolean collideBurst(IManaBurst burst, HitResult pos, boolean isManaBlock, boolean shouldKill, ItemStack stack) {
		// flag to stop multi-explosion bug, also none of this needs to be done clientside
		if (burst.getColor() == 0xFFFFFF || burst.entity().level.isClientSide()) return true;
		AABB burnArea = AABB.ofSize( Vec3.atCenterOf(burst.entity().blockPosition()), 6, 6, 6 );
		superCoolHugeFireExplosionOfUnlimitedCarnage((ServerLevel)burst.entity().level, burst.entity().getOwner(), burnArea);
		burst.setColor(0xFFFFFF); // flag to stop multi-explosion bug
		return true;
	}
	
	@Override
	public void updateBurst(IManaBurst burst, ItemStack stack) {
		// flag to stop multi-explosion bug, also none of this needs to be done clientside
		if (burst.getColor() == 0xFFFFFF || burst.entity().level.isClientSide()) return;
		ThrowableProjectile entity = burst.entity();
		
		boolean doExplosion;

		FluidState fluid = entity.level.getFluidState(entity.blockPosition());
		if (fluid.is(Fluids.EMPTY)) {
			// det if started in water
			doExplosion = burst.getColor() == 0xFF4100;
		} else {
			// did not start in water, or fluid is not water
			doExplosion = burst.getColor() == 0xFF4000
					|| !(fluid.is(Fluids.WATER) || fluid.is(Fluids.FLOWING_WATER));
		}
		
		if (!doExplosion) {
			AABB hitBox = new AABB(entity.getX(), entity.getY(), entity.getZ(), entity.xOld, entity.yOld, entity.zOld).inflate(1);
			Entity shooter = entity.getOwner();
			
			for (LivingEntity living : entity.level.getEntitiesOfClass(LivingEntity.class, hitBox)) {
				if (living == shooter || living instanceof Player plrVictim && shooter instanceof Player plrShooter && !plrShooter.canHarmPlayer(plrVictim)) {
					continue;
				}

				if (living.hurtTime == 0 && !burst.isFake()) {
					doExplosion = true;
					break;
				}
			}
		}
		
		if (doExplosion || burst.getTicksExisted() > 12) {
			AABB burnArea = AABB.ofSize( Vec3.atCenterOf(burst.entity().blockPosition()), 6, 6, 6 );
			superCoolHugeFireExplosionOfUnlimitedCarnage((ServerLevel)entity.level, entity.getOwner(), burnArea);
			burst.setColor(0xFFFFFF); // flag to stop multi-explosion bug
			entity.discard();
		}
	}
	
	@Override
	public boolean doParticles(IManaBurst burst, ItemStack stack) {
		return burst.getColor() != 0xFFFFFF;
	} // no particles if weve exploded already
	
	
	
	//// Mind stone
	// https://github.com/sinkillerj/ProjectE/blob/mc1.18.x/src/main/java/moze_intel/projecte/gameObjs/items/rings/MindStone.java
	public long getXp(ItemStack stack) {
		return ItemNBTHelper.getLong(stack, TAG_EXP, 0);
	}
	
	public void setXp(ItemStack stack, long amount) {
		ItemNBTHelper.setLong(stack, TAG_EXP, amount);
	}
	
	public void insertXp(ItemStack stack, long amount) {
		long xp = getXp(stack);
		if (Long.MAX_VALUE - xp < amount) {
			xp = Long.MAX_VALUE;
		} else {
			xp += amount;
		}
		setXp(stack, xp);
	}
	
	public long extractXp(ItemStack stack, long amount) {
		long xp = getXp(stack);
		if (xp < amount) {
			setXp(stack, 0);
			return xp;
		} else {
			setXp(stack, xp - amount);
			return amount;
		}
	}
	
	
	// BORKED DENSEXP STUFF
	// TODO: make this work because storing 2^60 levels is cool
	//public static long[] getXp(ItemStack stack) {
	//	long[] stored = {
	//		ItemNBTHelper.getLong(stack, TAG_LVL, 0),
	//		ItemNBTHelper.getLong(stack, TAG_EXP, 0)
	//	};
	//	return stored;
	//}
	//
	//public static void setXp(ItemStack stack, long[] denseXp) {
	//	ItemNBTHelper.setLong(stack, TAG_LVL, denseXp[0]);
	//	ItemNBTHelper.setLong(stack, TAG_EXP, denseXp[1]);
	//}
	//
	//public static void insertXp(ItemStack stack, long amount) {
	//	if (amount <= 0 || xpStorageIsFull(stack)) {
	//		if (xpStorageIsFull(stack)) LoggerHelper.printInfo("BandOfArcana", "MindStoneFull",
	//				"Failed to insert XP into ItemStack " + stack + ", as it is full. XP has been voided.");
	//		return;
	//	}
	//	
	//	long[] newXp = getXp(stack);
	//	
	//	// rawPoints overflow protection
	//	if (Long.MAX_VALUE - newXp[1] < amount) {
	//		// we know for a fact we can afford at least 1 level
	//		// thus, we can take just enough from amount to convert a single level
	//		amount -= PlrHelper.xpCalcValueOfSingleLevel(newXp[0] + 1) - newXp[1];
	//		// we do the conversion, which makes the remainder value 0
	//		// as a result, rawPoints will no longer overflow
	//		newXp[1] = 0;
	//		newXp[0]++;
	//	}
	//	long rawPoints = newXp[1] + amount;
	//	
	//	
	//	newXp[1] = rawPoints;
	//	
	//	// convert the raw points into levels
	//	while (newXp[0] < Xp.MAX_LVL && rawPoints >= PlrHelper.xpCalcValueOfSingleLevel(newXp[0] + 1)) {
	//		newXp[1] -= PlrHelper.xpCalcValueOfSingleLevel(++newXp[0]);
	//	}		
	//	setXp(stack, newXp);
	//	
	//	// OLD
	//	//int newXp = getXp(stack) + amount;
	//	//if (newXp < 0) {
	//	//	newXp = Integer.MAX_VALUE;
	//	//}
	//	//setXp(stack, newXp);
	//}
	//
	//public static long extractXp(ItemStack stack, long amount) {
	//	if (amount <= 0 || xpStorageIsEmpty(stack)) return 0;
	//	
	//	long[] newXp = getXp(stack);
	//	long extracted;
	//	
	//	// if we have enough raw points stored, we can just use those
	//	if (newXp[1] >= amount) {
	//		extracted = amount;
	//		newXp[1] -= extracted;
	//	} else {
	//		
	//		// drain all the extra points first
	//		extracted = newXp[1];
	//		newXp[1] = 0;
	//		
	//		// converting levels into their value in points
	//		while (extracted < amount && newXp[0] > 0) {
	//			extracted += PlrHelper.xpCalcValueOfSingleLevel(newXp[0]);
	//			newXp[0]--;
	//			if (extracted > amount) {
	//				// leave extra points behind that we dont need
	//				newXp[1] = extracted - amount;
	//				extracted = amount;
	//			}
	//		}
	//		
	//	}
	//	setXp(stack, newXp);
	//	return extracted;
	//	
	//	// OLD
	//	//int curXp = getXp(stack);
	//	//int newXp, extracted;
	//	//
	//	//if (curXp < amount) {
	//	//	newXp = 0;
	//	//	extracted = curXp;
	//	//} else {
	//	//	newXp = curXp - amount;
	//	//	extracted = amount;
	//	//}
	//	//
	//	//setXp(stack, newXp);
	//	//return extracted;
	//}
	//
	//public static boolean xpStorageIsFull(ItemStack stack) {
	//	return getXp(stack)[0] >= Xp.MAX_LVL;
	//}
	//
	//public static boolean xpStorageIsEmpty(ItemStack stack) {
	//	return getXp(stack)[0] <= 0
	//			&& getXp(stack)[1] <= 0;
	//}

	// Player

	// Calculation stuff, TODO: move to utils
	// Math referenced from the MC wiki

	
	
	
	public boolean playerHasFullPristineSet(Player player) {
		for (ItemStack armorStack : player.getArmorSlots()) {
			if (armorStack.getItem() instanceof GemJewelryBase && !armorStack.isDamaged()) continue;
			return false; // if above check ever fails, false
		}
		return true;
	}
	
	@Override
	public int getUseDuration(ItemStack stack) {
		return Integer.MAX_VALUE;
	}
	
	// TODO: make first person anims not look dumb with these
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		if (getCharge(stack) == 1) {
			switch (getMode(stack)) {
			
			case 2:
				return getWoft(stack) ? UseAnim.SPEAR : UseAnim.BLOCK;
			
			case 6: // archangels
				return UseAnim.SPYGLASS;
			
			default:
				break;
			}
		}
		return UseAnim.NONE;
	}
	
	/**
	 * shoot lava orb funny<br>
	 * @param player the shooter
	 * @return if orb was sucessfully shot
	 */
	private boolean shootLavaProjectile(Player player) {
		long consumed = EmcHelper.consumeAvaliableEmc(player, 64);
		if (consumed >= 64) {
			player.level.playSound(null, player, PESoundEvents.TRANSMUTE.get(), SoundSource.PLAYERS, 0.5f, 1);
			player.level.playSound(null, player, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.PLAYERS, 1, 1);
			FreeLavaProjectile proj = new FreeLavaProjectile(player, player.level);
			proj.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 1);
			player.level.addFreshEntity(proj);
			return true;
		} else if (consumed > 0 && !player.isInWaterRainOrBubble()) {
			player.hurt(DamageSource.LAVA, 20);
			player.setSecondsOnFire(10);
		}
		return false;
	}
	
	/**
	 * the swarm
	 * @param player
	 * @return if any arrows were spawned at all
	 */
	private boolean shootHomingVolley(Player player) {
		if (player.getCooldowns().isOnCooldown(PEItems.ARCHANGEL_SMITE.get())) return false;
		long plrEmc = EmcHelper.getAvaliableEmc(player);
		int shot;
		for (shot = 0; shot < 14; shot++) {
			if (shot*56 >= plrEmc) break;
			ProjectileHelper.shootArrow(player.level, player, 2, 3, 14, (byte) 0, false, true, Pickup.CREATIVE_ONLY);
		}
		EmcHelper.consumeAvaliableEmc(player, shot*56);
		player.getCooldowns().addCooldown(PEItems.ARCHANGEL_SMITE.get(), 4);
		return shot > 0;
	}
	


	/**
	 * 
	 * @param player
	 * @return if projectile was shot
	 */
	public static boolean shootMustang(Player player, ItemStack stack) {
		if (player.level.isRainingAt(player.blockPosition())) {
			player.level.playSound(null, player, SoundEvents.NOTE_BLOCK_HAT, SoundSource.PLAYERS, 1F, 1.7F);
			player.level.playSound(null, player, SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 0.5F, 2f);
			return false;
		}
		EntityManaBurst burst = new EntityManaBurst(player);

		float motionModifier = 14f;

		burst.setColor(0xFF4000);
		burst.setMana(1);
		burst.setStartingMana(1);
		burst.setMinManaLoss(1);
		burst.setManaLossPerTick(0f);
		burst.setGravity(0F);
		burst.setDeltaMovement(burst.getDeltaMovement().scale(motionModifier));
		burst.setWarped(true);
		burst.setSourceLens(stack);
		if (player.isUnderWater() || burst.isUnderWater()) {
			// imperceptibly different color
			// used as a "spawnedUnderwater" flag
			burst.setColor(0xFF4100);
		}
		
		player.level.addFreshEntity(burst);
		player.level.playSound(null, player, SoundEvents.NOTE_BLOCK_HAT, SoundSource.PLAYERS, 1F, 1.7F);
		return true;
	}
	
	/**
	 * as advertised <br>
	 * everything is centered on the AABB
	 * 
	 * @param level the SERVER world to explode in
	 * @param culprit the entity that caused this kaboom
	 * @param box the AABB to deal stupid amounts of damage in (to entities)
	 */
	public void superCoolHugeFireExplosionOfUnlimitedCarnage(ServerLevel level, Entity culprit, AABB box) {
		Random rand = level.getRandom();
		Vec3 cent = box.getCenter();
		BlockPos bCent = new BlockPos(cent);
		boolean debug = false; // TODO: MAKE SURE THIS IS OFF
		
		////////////////////
		// DAMAGE & WORLD //
		////////////////////
		
		// hurting entities
		// based on vanilla explosion code
		for (LivingEntity ent : level.getEntitiesOfClass(LivingEntity.class, box)) {
			if (!ent.ignoreExplosion() && !(ent instanceof Blaze)) {
				double distance = Math.sqrt(ent.distanceToSqr(cent)) / 8d;
				if (distance <= 1.0D) {
					double xDiff = ent.getX() - cent.x();
					double yDiff = ent.getY() - cent.y();
					double zDiff = ent.getZ() - cent.z();
					double sqrtXYZ = Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
					if (sqrtXYZ != 0.0D) {
						xDiff /= sqrtXYZ;
						yDiff /= sqrtXYZ;
						zDiff /= sqrtXYZ;
						double seenPercent = (double)Explosion.getSeenPercent(cent, ent);
						double invDist = (1.0D - distance) * seenPercent;
						ent.setRemainingFireTicks(1200);
						ent.hurt(MGTKDmgSrc.MUSTANG, (float) Math.pow(((int)((invDist * invDist + invDist) / 2.0D * 7.0D * 8d + 1.0D)), 2));
					}
				}
			}
		}
		
		// screwing with blocks
		// we keep track of vaporized to do particles
		Stack<BlockPos> vaporized = new Stack<BlockPos>();
		BlockPos.betweenClosedStream(box).forEach(bPos -> {
			
			// debug: marks all for paticles
			if (debug) vaporized.push(bPos);
			
			// fire
			if (level.isEmptyBlock(bPos)) {
				if (rand.nextInt(4) != 0) return;
				if (culprit instanceof ServerPlayer player) {
					PlayerHelper.checkedPlaceBlock(player, bPos, Blocks.FIRE.defaultBlockState());
				} else {
					level.setBlockAndUpdate(bPos, Blocks.FIRE.defaultBlockState());
				}
			}
			
			// liquids
			else if (level.getFluidState(bPos) != Fluids.EMPTY.defaultFluidState()) {
				FluidState fState = level.getFluidState(bPos);
				
				// check if this fluid vaporizes when too hot (like in nether)
				if (fState.getType().getAttributes().doesVaporize(level, bPos, new FluidStack(fState.getType(), 1000))) {
					BlockState bState = level.getBlockState(bPos);
					
					// pure water
					if (bState.getBlock() instanceof LiquidBlock) {
						if (culprit instanceof ServerPlayer plr && PlayerHelper.checkedPlaceBlock(plr, bPos, Blocks.AIR.defaultBlockState())) {
							vaporized.push(bPos.immutable());
						} else if (level.setBlockAndUpdate(bPos, Blocks.AIR.defaultBlockState())) {
							vaporized.push(bPos.immutable());
						}
					}
					
					// water plants (kelp, seagrass)
					else if (bState.getMaterial() == Material.WATER_PLANT || bState.getMaterial() == Material.REPLACEABLE_WATER_PLANT) {
						Block.dropResources(bState, level, bPos, level.getBlockEntity(bPos));
						if (culprit instanceof ServerPlayer plr && PlayerHelper.checkedPlaceBlock(plr, bPos, Blocks.AIR.defaultBlockState())) {
							vaporized.push(bPos.immutable());
						} else if (level.setBlockAndUpdate(bPos, Blocks.AIR.defaultBlockState())) {
							vaporized.push(bPos.immutable());
						}
					}
					
					// waterlogged blocks
					else if (bState.getBlock() instanceof BucketPickup block) {
						if (culprit instanceof ServerPlayer plr && PlayerHelper.hasEditPermission(plr, bPos) && !block.pickupBlock(level, bPos, bState).isEmpty()) {
							vaporized.push(bPos.immutable());
						} else if (!block.pickupBlock(level, bPos, bState).isEmpty()) {
							vaporized.push(bPos.immutable());
						}
					}
				}
			}
			
			// ice & snow
			else if (level.getBlockState(bPos).is(BlockTags.ICE) || level.getBlockState(bPos).is(BlockTags.SNOW)) {
				if (rand.nextInt(3) != 0 && level.getBlockState(bPos).is(BlockTags.ICE)) return;
				if (culprit instanceof ServerPlayer plr && PlayerHelper.checkedPlaceBlock(plr, bPos, Blocks.AIR.defaultBlockState())) {
					vaporized.push(bPos.immutable());
				} else if (level.setBlockAndUpdate(bPos, Blocks.AIR.defaultBlockState())) {
					vaporized.push(bPos.immutable());
				}
			}
		});
		
		
		///////////////////////
		// PARTICLES & SOUND //
		///////////////////////

		// big fwoosh of fire!
		level.playSound(null, bCent, ModSounds.endoflame, SoundSource.NEUTRAL, 14, 1f);
		level.playSound(null, bCent, SoundEvents.BLAZE_SHOOT, SoundSource.NEUTRAL, 2.5f, 1f);
		//level.playSound(null, bCent, SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 2.5f, 0.1f);
		if (debug) {
			
			//// drawing the hitbox
			MiscHelper.drawAABBWithParticlesServer(box, ParticleTypes.DRIPPING_LAVA, 0.1, level);
			
			// center and blockcenter
			level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, cent.x(), cent.y(), cent.z(), 1, 0, 0, 0, 0);
			level.sendParticles(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, bCent.getX(), bCent.getY(), bCent.getZ(), 1, 0, 0, 0, 0);
			
			// marking every individual block
			while (!vaporized.empty()) {
				Vec3 pos = Vec3.atCenterOf(vaporized.pop());
				level.sendParticles(ParticleTypes.DRIPPING_WATER, pos.x(), pos.y(), pos.z(), 1, 0, 0, 0, 0);
			}
		}
		
		// badass does immolation
		for (ServerPlayer plr : level.players()) {
			if (plr.is(culprit) || plr.blockPosition().closerToCenterThan(cent, 512d)) {
				// :(
				NetworkInit.toClient(new MustangExplosionPacket(cent.x, cent.y, cent.z), plr);
			}
		}
		
		// steam from the steamed clams were having
		while (!vaporized.empty() && !debug) {
			BlockPos bPos = vaporized.pop();
			level.playSound(null, bPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.1F, 2.6F + (rand.nextFloat() - rand.nextFloat()) * 0.8F);
			level.sendParticles(ParticleTypes.CLOUD, bPos.getX(), bPos.getY(), bPos.getZ(), 4, 0, 0, 0, 0.3);
		}
	}
}
