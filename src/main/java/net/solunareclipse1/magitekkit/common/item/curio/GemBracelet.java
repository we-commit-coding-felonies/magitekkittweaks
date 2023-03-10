package net.solunareclipse1.magitekkit.common.item.curio;

import java.util.Random;
import java.util.Stack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.MinecraftForge;
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
import net.solunareclipse1.magitekkit.common.misc.MGTKDamageSource;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.init.NetworkInit;
import net.solunareclipse1.magitekkit.network.packet.client.MustangExplosionPacket;
import net.solunareclipse1.magitekkit.util.EmcHelper;
import net.solunareclipse1.magitekkit.util.MiscHelper;
import net.solunareclipse1.magitekkit.util.PlrHelper;
import net.solunareclipse1.magitekkit.util.ProjectileHelper;

import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.mana.BurstProperties;
import vazkii.botania.api.mana.ILensEffect;
import vazkii.botania.common.entity.EntityManaBurst;
import vazkii.botania.common.handler.ModSounds;
import vazkii.botania.common.helper.ExperienceHelper;
import vazkii.botania.common.helper.ItemNBTHelper;


// This file is split into sections for organization
// Please try to keep it that way!
public class GemBracelet extends MGTKItem implements IModeChanger, IItemCharge, IProjectileShooter, IExtraFunction, ISwingItem, ILensEffect {

	//////////////////////////////////////////////
	// CONSTANTS, GLOBAL VARS, AND CONSTRUCTORS //
	//////////////////////////////////////////////
	public static final String TAG_MODE = "arc_mode";
	public static final String TAG_EXP = "arc_experience";
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
			case 1: // Mind
				if (ExperienceHelper.getPlayerXP(player) > 0 && plrEmc >= 8) {
					EmcHelper.consumeAvaliableEmc(player, 8);
					insertXp(stack, PlrHelper.extractXp(player, PlrHelper.getXp(player)));
					player.level.playSound(null, player, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1, 2f);
					didDo = true;
				}
				break;
			case 2: // Watch
				changeWoft(stack);
				player.level.playSound(null, player, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.PLAYERS, 1, 1.4f);
				didDo = true;
				break;
			case 3: // Harvest
				if (plrEmc >= 24) {
					EmcHelper.consumeAvaliableEmc(player, 24);
					WorldHelper.growNearbyRandomly(true, player.level, player.blockPosition(), player);
					didDo = true;
				}
				break;
			case 4: // Liquid
				changeLiquid(stack);
				player.level.playSound(null, player, getLiquid(stack) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1, 0.7f);
				didDo = true;
				break;
			case 5: // Philo
				didDo = PEItems.PHILOSOPHERS_STONE.get().doExtraFunction(stack, player, hand);
				break;
			case 6: // Archangels
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
			case 7: // SWRG
				if (player instanceof ServerPlayer) {
					if (plrEmc >= 1024 && !player.getCooldowns().isOnCooldown(PEItems.SWIFTWOLF_RENDING_GALE.get())) {
						didDo = EmcHelper.consumeAvaliableEmc(player, MiscHelper.smiteAllInArea(player.level, player.getBoundingBox().inflate(24), (ServerPlayer) player, plrEmc)) > 0;
						player.getCooldowns().addCooldown(PEItems.SWIFTWOLF_RENDING_GALE.get(), 30);
					}
				}
				break;
			case 8: // Zero
				if (player instanceof ServerPlayer) {
					if (plrEmc >= 256 && !player.getCooldowns().isOnCooldown(PEItems.ZERO_RING.get())) {
						didDo = EmcHelper.consumeAvaliableEmc(player, MiscHelper.slowAllInArea(player.level, player.getBoundingBox().inflate(24), (ServerPlayer) player, plrEmc)) > 0;
						player.getCooldowns().addCooldown(PEItems.ZERO_RING.get(), 30);
					}
				}
				break;
			case 9: // Ignition
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
		//this.use
	}

	// IProjectileShooter
	@Override
	public boolean shootProjectile(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
		if (!player.level.isClientSide && playerHasFullPristineSet(player) && getCharge(player.getItemInHand(hand)) == 1) {
			long plrEmc = EmcHelper.getAvaliableEmc(player);
			boolean didDo = false;
			switch (getMode(stack)) {
			case 1: // Mind
				if (getXp(stack) > 0) {
					ExperienceHelper.addPlayerXP(player, extractXp(stack, getXp(stack)));
					player.level.playSound(null, player, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1, 2f);
					didDo = true;
				}
				break;
			case 2: // Watch
				if (plrEmc >= 72) {
					didDo = PEItems.VOID_RING.get().doExtraFunction(stack, player, hand);
				}
				break;
			case 3: // Harvest
				System.out.println("NYI: " + getMode(stack));
				break;
			case 4: // Liquid
				didDo = ItemNBTHelper.getBoolean(stack, TAG_LIQUID, false) ? shootLavaProjectile(player) : PEItems.EVERTIDE_AMULET.get().shootProjectile(player, stack, hand);
				break;
			case 5: // Philo
				didDo = PEItems.PHILOSOPHERS_STONE.get().shootProjectile(player, stack, hand);
				break;
			case 6: // Archangels
				if (plrEmc >= 1024 && !player.getCooldowns().isOnCooldown(PEItems.ARCHANGEL_SMITE.get())) {
					ProjectileHelper.shootArrow(player.level, player, 10, 10, 0, Byte.MAX_VALUE, true, false, Pickup.CREATIVE_ONLY);
					EmcHelper.consumeAvaliableEmc(player, 1024);
					player.getCooldowns().addCooldown(PEItems.ARCHANGEL_SMITE.get(), 5);
					didDo = true;
				}
				break;
			case 7: // SWRG
				if (player.level.getLevelData() instanceof ServerLevelData lvlData) {
					lvlData.setRainTime(6000);
					lvlData.setThunderTime(6000);
					lvlData.setRaining(true);
					lvlData.setThundering(true);
				}
				break;
			case 8: // Zero
				System.out.println("NYI: " + getMode(stack));
				break;
			case 9: // Ignition
				if (plrEmc >= 139264) {
					if (shootMustang(player, stack)) {
						EmcHelper.consumeAvaliableEmc(player, 139264);
						didDo = true;
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

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		if (!level.isClientSide && playerHasFullPristineSet(player) && getCharge(player.getItemInHand(hand)) == 1) {
			boolean didDo = false;
			ItemStack stack = player.getItemInHand(hand);
			long plrEmc = EmcHelper.getAvaliableEmc(player);
			switch (getMode(stack)) {
			case 0:
				break;
			case 1:
				long toCalc = Long.MAX_VALUE-2;//player.getRandom().nextLong(0, Long.MAX_VALUE);
				player.sendMessage(new TextComponent(PlrHelper.xpLvlToPoints(1431655783)+""), player.getUUID());
				//System.out.println("The level is: " + PlrHelper.xpPointsToLvl(toCalc));
				break;
			case 2:
				System.out.println("NYI: 2");
				break;
			case 3: // Harvest (handled in useOn)
			case 4: // Liquid (handled in useOn)
			case 5: // Philo (handled in useOn)
				break;
			case 6: // Archangels (handled in onUsingTick)
				player.startUsingItem(hand);
				break;
			case 7:
				System.out.println("NYI: 7");
				break;
			case 8: // Zero (le snowball funny) (placeholder)
				for (int i = 0; i < 10; i++) {
			        Snowball snowball = new Snowball(level, player);
			        snowball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3F, 4.0F);
			        level.addFreshEntity(snowball);
			        level.playSound(null, player, SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
			        if (!didDo) didDo = true;
				}
				break;
			case 9: // Ignition (handled in useOn)
				break;
			}
			if (didDo) return InteractionResultHolder.success(stack);
		}
		return InteractionResultHolder.fail(player.getItemInHand(hand));
	}
	
	@Override
	public void onUsingTick(ItemStack stack, LivingEntity entity, int timeBeingUsed) {
		if (entity instanceof Player player && !player.level.isClientSide()) {
			long plrEmc = EmcHelper.getAvaliableEmc(player);
			Level level = player.level;
			switch (getMode(stack)) {
			case 6: // Archangels
				if (plrEmc >= 128 && !player.getCooldowns().isOnCooldown(PEItems.ARCHANGEL_SMITE.get())) {
					ProjectileHelper.shootArrowTipped(level, player, 0.01f, 3, 4, (byte) 0, false, true, Pickup.CREATIVE_ONLY, new MobEffectInstance(EffectInit.TRANSMUTING.get(), 15));
					EmcHelper.consumeAvaliableEmc(player, 128);
				}
				break;
			default:
				break;
			}
		}
	}
	
	@Override
	public boolean onSwingAir(Context ctx) {
		boolean didDo = false;
		switch (getMode(ctx.getSender().getMainHandItem())) {
		case 6: // archangels
			didDo = shootHomingVolley(ctx.getSender());
			break;
		default:
			break;
		}
		return didDo;
	}

	@Override
	public boolean onSwingBlock(PlayerInteractEvent.LeftClickBlock evt) {
		boolean didDo = false;
		if (!evt.getPlayer().level.isClientSide && evt.getUseItem() != Event.Result.DENY && !evt.getItemStack().isEmpty() && evt.getItemStack().getItem() == this) {
			ItemStack stack = evt.getItemStack();
			switch (getMode(stack)) {
			case 6: // Archangels
				didDo = shootHomingVolley(evt.getPlayer());
				break;
			default:
				break;
			}
		}
		return didDo;
		//return shootHomingVolley(evt.getPlayer());
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
		if (!player.level.isClientSide && stack.getItem() instanceof GemBracelet) {
			boolean didDo = false;
			switch (getMode(stack)) {
			case 6: // Archangels
				didDo = shootHomingVolley(player);
				break;
			case 9:
				
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
	
	// Item
	public int getXp(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_EXP, 0);
	}

	public void setXp(ItemStack stack, int amount) {
		ItemNBTHelper.setInt(stack, TAG_EXP, amount);
	}
	
	public void insertXp(ItemStack stack, int amount) {
		if (amount <= 0 || getXp(stack) == Integer.MAX_VALUE) return;
		int newXp = getXp(stack) + amount;
		if (newXp < 0) {
			newXp = Integer.MAX_VALUE;
		}
		setXp(stack, newXp);
	}

	public int extractXp(ItemStack stack, int amount) {
		if (amount <= 0 || getXp(stack) <= 0) return 0;
		int curXp = getXp(stack);
		int newXp, extracted;

		if (curXp < amount) {
			newXp = 0;
			extracted = curXp;
		} else {
			newXp = curXp - amount;
			extracted = amount;
		}

		setXp(stack, newXp);
		return extracted;
	}

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
		if (getMode(stack) == 6) {
			return Integer.MAX_VALUE;
		}
		return 0;
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
		if (player.getCooldowns().isOnCooldown(PEItems.IGNITION_RING.get())) return false;
		if (player.level.isRainingAt(player.blockPosition())) {
			player.level.playSound(null, player, SoundEvents.NOTE_BLOCK_HAT, SoundSource.PLAYERS, 1F, 1.7F);
			player.level.playSound(null, player, SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 0.5F, 2f);
			player.getCooldowns().addCooldown(PEItems.IGNITION_RING.get(), 30);
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
		player.getCooldowns().addCooldown(PEItems.IGNITION_RING.get(), 30);
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
						ent.hurt(MGTKDamageSource.MUSTANG, (float) Math.pow(((int)((invDist * invDist + invDist) / 2.0D * 7.0D * 8d + 1.0D)), 2));
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
