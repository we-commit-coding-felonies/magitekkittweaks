package net.solunareclipse1.magitekkit.common.item.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.UUID;

import org.abego.treelayout.internal.util.java.util.ListUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.item.CustomArmPoseItem;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.PETags.BlockEntities;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;

import net.solunareclipse1.magitekkit.api.capability.wrapper.ChargeItemCapabilityWrapperButBetter;
import net.solunareclipse1.magitekkit.api.capability.wrapper.CovalentCapabilityWrapper;
import net.solunareclipse1.magitekkit.api.capability.wrapper.converter.ManaCovalentCapabilityWrapper;
import net.solunareclipse1.magitekkit.api.item.ISwingItem;
import net.solunareclipse1.magitekkit.common.entity.projectile.FreeLavaProjectile;
import net.solunareclipse1.magitekkit.common.entity.projectile.SentientArrow;
import net.solunareclipse1.magitekkit.common.entity.projectile.WitherVineProjectile;
import net.solunareclipse1.magitekkit.common.inventory.container.GravityAnvilMenu;
import net.solunareclipse1.magitekkit.common.inventory.container.PhiloEnchantmentMenu;
import net.solunareclipse1.magitekkit.common.item.CovalenceItem;
import net.solunareclipse1.magitekkit.common.item.armor.gem.GemJewelryBase;
import net.solunareclipse1.magitekkit.common.misc.damage.MGTKDmgSrc;
import net.solunareclipse1.magitekkit.config.DebugCfg;
import net.solunareclipse1.magitekkit.config.EmcCfg.Arcana.*;
import net.solunareclipse1.magitekkit.data.MGTKEntityTags;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.init.NetworkInit;
import net.solunareclipse1.magitekkit.init.ObjectInit;
import net.solunareclipse1.magitekkit.network.packet.client.CreateLoopingSoundPacket;
import net.solunareclipse1.magitekkit.network.packet.client.DrawParticleAABBPacket;
import net.solunareclipse1.magitekkit.network.packet.client.DrawParticleAABBPacket.ParticlePreset;
import net.solunareclipse1.magitekkit.network.packet.client.DrawParticleLinePacket.LineParticlePreset;
import net.solunareclipse1.magitekkit.network.packet.client.DrawParticleLinePacket;
import net.solunareclipse1.magitekkit.network.packet.client.GustParticlePacket;
import net.solunareclipse1.magitekkit.network.packet.client.ModifyPlayerVelocityPacket;
import net.solunareclipse1.magitekkit.network.packet.client.MustangExplosionPacket;
import net.solunareclipse1.magitekkit.util.Constants.Xp;
import net.solunareclipse1.magitekkit.util.EmcHelper;
import net.solunareclipse1.magitekkit.util.EntityHelper;
import net.solunareclipse1.magitekkit.util.LoggerHelper;
import net.solunareclipse1.magitekkit.util.MiscHelper;
import net.solunareclipse1.magitekkit.util.PlrHelper;
import net.solunareclipse1.magitekkit.util.ProjectileHelper;
import net.solunareclipse1.magitekkit.util.ProjectileHelper.*;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongComparators;
import it.unimi.dsi.fastutil.longs.LongList;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.mana.BurstProperties;
import vazkii.botania.api.mana.ILensEffect;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.entity.EntityDoppleganger;
import vazkii.botania.common.entity.EntityManaBurst;
import vazkii.botania.common.entity.EntityManaStorm;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.relic.ItemDice;
import vazkii.botania.xplat.IXplatAbstractions;
import wayoftime.bloodmagic.api.compat.EnumDemonWillType;
import wayoftime.bloodmagic.api.compat.IDemonWill;
import wayoftime.bloodmagic.api.compat.IMultiWillTool;

public class BandOfArcana extends CovalenceItem
	implements IModeChanger, IItemCharge, IProjectileShooter, IExtraFunction, ISwingItem, ILensEffect, CustomArmPoseItem, IMultiWillTool {

	//////////////////////////////////////////////
	// CONSTANTS, GLOBAL VARS, AND CONSTRUCTORS //
	//////////////////////////////////////////////
	public static final String TAG_MODE = "boa_mode";
	public static final String TAG_EXP = "boa_experience";
	public static final String TAG_LIQUID = "boa_liquid";
	public static final String TAG_WOFT = "boa_woft";
	public static final String TAG_COVALENCE = CovalentCapabilityWrapper.TAG_STATE;
	public static final String TAG_ARROWTRACKER = "boa_arrowtracker";
	private static final String[] KEY_MODES = {
			"tip.mgtk.arcana.mode.0", // Disabled
			"tip.mgtk.arcana.mode.1", // Mind
			"tip.mgtk.arcana.mode.2", // Watch
			"tip.mgtk.arcana.mode.3", // Harvest
			"tip.mgtk.arcana.mode.4", // Liquid
			"tip.mgtk.arcana.mode.5", // Philo
			"tip.mgtk.arcana.mode.6", // Archangels
			"tip.mgtk.arcana.mode.7", // SWRG
			"tip.mgtk.arcana.mode.8", // Zero
			"tip.mgtk.arcana.mode.9"  // Ignition
	};
	public static final UUID TIME_ACCEL_UUID = UUID.fromString("311f77f1-5573-431d-8340-06511e72d28f");
	/** splits things into categories so that multiple similar items dont clog up drops */
	public static final Item[][] ITEMIZER_DEFAULTS = {
			{
				Items.APPLE,
				Items.ENCHANTED_GOLDEN_APPLE,
				Items.GOLDEN_APPLE,
				AllItems.HONEYED_APPLE.get()
			},
			{
				Items.CLAY_BALL,
				Items.MAGMA_CREAM,
				Items.SLIME_BALL
			},
			{
				Items.BEETROOT_SEEDS,
				Items.MELON_SEEDS,
				Items.PUMPKIN_SEEDS,
				Items.WHEAT_SEEDS,
				ModItems.drySeeds,
				ModItems.goldenSeeds,
				ModItems.grassSeeds,
				ModItems.infusedSeeds,
				ModItems.mutatedSeeds,
				ModItems.mycelSeeds,
				ModItems.overgrowthSeed,
				ModItems.podzolSeeds,
				ModItems.scorchedSeeds,
				ModItems.vividSeeds,
				ModItems.worldSeed
			},
			{
				Items.GLOW_BERRIES,
				Items.SWEET_BERRIES,
				AllItems.CHOCOLATE_BERRIES.get()
			},
			{
				Items.HONEY_BOTTLE,
				Items.MILK_BUCKET,
				AllItems.BUILDERS_TEA.get()
			},
			{
				Items.ENDER_EYE,
				Items.ENDER_PEARL
			},
			{
				Items.ARROW,
				Items.BLAZE_ROD,
				Items.END_ROD,
				Items.LIGHTNING_ROD,
				Items.SPECTRAL_ARROW,
				Items.STICK,
				Items.TRIDENT
			},
			{
				Items.MUSIC_DISC_11,
				Items.MUSIC_DISC_13,
				Items.MUSIC_DISC_BLOCKS,
				Items.MUSIC_DISC_CAT,
				Items.MUSIC_DISC_CHIRP,
				Items.MUSIC_DISC_FAR,
				Items.MUSIC_DISC_MALL,
				Items.MUSIC_DISC_MELLOHI,
				Items.MUSIC_DISC_OTHERSIDE,
				Items.MUSIC_DISC_PIGSTEP,
				Items.MUSIC_DISC_STAL,
				Items.MUSIC_DISC_STRAD,
				Items.MUSIC_DISC_WAIT,
				Items.MUSIC_DISC_WARD,
				ModItems.recordGaia1,
				ModItems.recordGaia2
			}
	};
	
	public BandOfArcana(Properties props) {
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
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tips, isAdvanced);
		tips.add(new TextComponent(""));
		Minecraft mc = Minecraft.getInstance();
		Options o = mc.options;
		byte mode = getMode(stack);
		Component[] bind = {
				o.keyAttack.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.AQUA),					// 0
				o.keyUse.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.AQUA),						// 1
				ClientKeyHelper.getKeyName(PEKeybind.FIRE_PROJECTILE).copy().withStyle(ChatFormatting.AQUA),	// 2
				ClientKeyHelper.getKeyName(PEKeybind.EXTRA_FUNCTION).copy().withStyle(ChatFormatting.AQUA),		// 3
				ClientKeyHelper.getKeyName(PEKeybind.MODE).copy().withStyle(ChatFormatting.AQUA),				// 4
				ClientKeyHelper.getKeyName(PEKeybind.CHARGE).copy().withStyle(ChatFormatting.AQUA),				// 5
				o.keyShift.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.AQUA)						// 6
		};
		// Style(color, bold, italic, underline, strikethrough, obfuscated, clickevent, hoverevent, insertion, font)
		Style modeStyle = getModeTextStyle(mode, getLiquid(stack));
		tips.add(new TranslatableComponent("tip.mgtk.arcana.1").withStyle(ChatFormatting.UNDERLINE)); // Flavor
		tips.add(new TranslatableComponent("tip.mgtk.arcana.2", bind[4], bind[5])); // Keys
		tips.add(new TranslatableComponent("tip.mgtk.arcana.3", bind[6])); // Sneak
		tips.add(new TranslatableComponent("tip.mgtk.arcana.4", new TranslatableComponent(KEY_MODES[mode]).withStyle(modeStyle))); // Mode
		//Minecraft.getInstance().options.keyAttack.getTranslatedKeyMessage()
		if (mode != 0) {
			boolean sneak = InputConstants.isKeyDown(mc.getWindow().getWindow(), o.keyShift.getKey().getValue());
			for (int i = 0; i < 4; i++) {
				if (!sneak) {
					tips.add(new TranslatableComponent("tip.mgtk.arcana.guide", bind[i],
							new TranslatableComponent("tip.mgtk.arcana.guide."+mode+"."+(i+1)).withStyle(modeStyle.withBold(false).withItalic(true)))
					);
				} else {
					tips.add(new TranslatableComponent("tip.mgtk.arcana.guide.alt", bind[6], bind[i],
							new TranslatableComponent("tip.mgtk.arcana.guide."+mode+"."+(i+1)+".alt").withStyle(modeStyle.withBold(false).withItalic(true)))
					);
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	//////////////////////////////////////////////////////////////
	// KEYPRESS HANDLING, ABILITIES DEFINING, AND FUNCTIONALITY //
	//////////////////////////////////////////////////////////////
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (entity instanceof Player plr) {
			while (!selected) {
				// if player is currently accelerating with a different band, dont reset
				if (plr.isUsingItem()) {
					ItemStack using = plr.getUseItem();
					if (using.getItem() instanceof BandOfArcana && getMode(using) == 2) {
						break;
					}
				}
				resetTimeAccelSpeed(plr);
				break;
			}
			if (covalenceActive(stack)) {
				long need = CovalentCapabilityWrapper.getPoolNeeded(stack);
				if (need > 0) {
					long consumed = EmcHelper.consumeAvaliableEmc(plr, need);
					long newAmount = Math.min(CovalentCapabilityWrapper.getPoolMax(stack), CovalentCapabilityWrapper.getPool(stack) + Mth.clamp(consumed, 0, need));
					CovalentCapabilityWrapper.setPool(stack, newAmount);
					if (consumed < need) {
						// shut off covalence when player runs out of EMC so that we dont search inventory every tick
						changeCharge(plr, stack, null);
					}
				}
			}
		}
	}
	
	@Override
	public boolean onSwingAir(Context ctx) {
		ServerPlayer player = ctx.getSender();
		ItemStack stack = player.getMainHandItem();
		if (isValidRingUser(player, stack)) {
			boolean didDo = false;
			ServerLevel level = player.getLevel();
			boolean client = level.isClientSide;
			byte mode = getMode(stack);
			boolean liquid = getLiquid(stack);
			ItemCooldowns cd = player.getCooldowns();
			Item cdItem = getModeItem(mode, liquid);
			boolean ready = !cd.isOnCooldown(cdItem);
			long plrEmc = EmcHelper.getAvaliableEmc(player);
			
			switch (mode) {
			case 1: // Mind (withdraw 1 / 10 levels)
				didDo = tryWithdrawXp(player.isShiftKeyDown() ? 10 : 1, stack, player);
				break;
			case 2: // Watch (gravity attract / repel)
				if (plrEmc >= WOFT.GRAVITY.get()) {
					Vec3 singularity = player.getBoundingBox().getCenter();
					long victimCount = gravitateEntities( level, singularity, plrEmc/WOFT.GRAVITY.get(), player.isShiftKeyDown() );
					if (victimCount > 0) {
						EmcHelper.consumeAvaliableEmc(player, victimCount*WOFT.GRAVITY.get());
						level.playSound(null, player.blockPosition(), player.isShiftKeyDown() ? EffectInit.WOFT_REPEL.get() : EffectInit.WOFT_ATTRACT.get(), SoundSource.PLAYERS, 0.5f, 1);
						didDo = true;
					}
				}
				break;
			case 3: // Harvest (wither vine)
				if (plrEmc >= Harvest.WITHERVINE.get() && ready) {
					WitherVineProjectile vine = new WitherVineProjectile(level, player);
					vine.setDeltaMovement(player.getLookAngle().scale(2));
					vine.setPos(player.getEyePosition());
					level.addFreshEntity(vine);
					EmcHelper.consumeAvaliableEmc(player, Harvest.WITHERVINE.get());
					level.playSound(null, player.blockPosition(), SoundEvents.BONE_MEAL_USE, SoundSource.PLAYERS, 100, 1);
					cd.addCooldown(cdItem, 30);
					didDo = true;
				}
				break;
			case 4: // Liquid (destroy liquid)
				if (plrEmc >= Liquid.DESTROY.get() && ModItems.openBucket.use(level, player, InteractionHand.MAIN_HAND).getResult().equals(InteractionResult.CONSUME)) {
					EmcHelper.consumeAvaliableEmc(player, Liquid.DESTROY.get());
					level.playSound(null, player.blockPosition(), PESoundEvents.DESTRUCT.get(), SoundSource.PLAYERS, 0.2f, 1);
					didDo = true;
				}
				break;
			case 5: // Philo (nothing)
				break;
			case 6: // Archangel (shotgun)
				if (plrEmc >= Archangel.ARROW.get() && ready) {
					EmcHelper.consumeAvaliableEmc(player,
							ProjectileHelper.shootArrow((int)Math.min(16, plrEmc/Archangel.ARROW.get()), ArrowType.STRAIGHT,
									new ShootContext(level, player),
									new ArrowOptions(3, 3, 8, (byte)0, false, Pickup.DISALLOWED))
							.size());
					cd.addCooldown(cdItem, 5);
					didDo = true;
				}
				break;
			case 7: // SWRG (gust)
				if (plrEmc >= SWRG.GUST.get() && ready && !client) {
					windGust((ServerPlayer)player);
					EmcHelper.consumeAvaliableEmc(player, SWRG.GUST.get());
					cd.addCooldown(cdItem, 30);
					didDo = true;
				}
				break;
			case 8: // Zero (extinguish AOE)
				if (plrEmc >= Zero.EXTINGUISH.get()) {
					EmcHelper.consumeAvaliableEmc(player,
							extinguishAoe(player,
									AABB.ofSize(player.getBoundingBox().getCenter(), 16, 16, 16),
									plrEmc/Zero.EXTINGUISH.get())
							* Zero.EXTINGUISH.get()
					);
					cd.addCooldown(cdItem, 5);
					didDo = true;
				}
				break;
			case 9: // Ignition (fireball / tnt)
				if (plrEmc >= Ignition.FIREBALL.get() && ready) {
					int cdTime = 2;
					if (player.isShiftKeyDown() && plrEmc >= Ignition.TNT.get()) {
						cdTime = 5;
						PrimedTnt tnt = tnt(level, player.getEyePosition(), player.getLookAngle().scale(2), player);
						EmcHelper.consumeAvaliableEmc(player, Ignition.TNT.get());
						level.playSound(null, tnt.blockPosition(), SoundEvents.TNT_PRIMED, SoundSource.PLAYERS, 1, 1);
					} else {
						for (int i = 0; i < 5; i++) {
							fireball(level, player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().scale(2)), player);
						}
						EmcHelper.consumeAvaliableEmc(player, Ignition.FIREBALL.get());
						level.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1, 1);
					}
					level.playSound(null, player.blockPosition(), EffectInit.IGNITION_CLICK.get(), SoundSource.PLAYERS, 1, level.random.nextFloat(0.7f, 1.4f));
					cd.addCooldown(cdItem, cdTime);
					didDo = true;
				}
				break;

			default:
				LoggerHelper.printWarn("BandOfArcana", "InvalidMode", "'"+player.getScoreboardName()+"' tried to use a Band of Arcana with an invalid mode, it may have corrupt NBT!");
			case 0:
				break;
			}
			
			return didDo;
		} else {
			changeMode(player, stack, InteractionHand.MAIN_HAND);
			return false;
		}
	}
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity victim) {
		if (isValidRingUser(player, stack)) {
			boolean didDo = false;
			Level level = player.getLevel();
			boolean client = level.isClientSide;
			byte mode = getMode(stack);
			boolean liquid = getLiquid(stack);
			ItemCooldowns cd = player.getCooldowns();
			Item cdItem = getModeItem(mode, liquid);
			boolean ready = !cd.isOnCooldown(cdItem);
			long plrEmc = EmcHelper.getAvaliableEmc(player);
			
			switch (mode) {
			case 1: // Mind (withdraw 1 / 10 levels)
				didDo = tryWithdrawXp(player.isShiftKeyDown() ? 10 : 1, stack, player);
				break;
			case 2: // Watch (gravity attract / repel)
				if (plrEmc >= WOFT.GRAVITY.get() && ready) {
					Vec3 singularity = player.getBoundingBox().getCenter();
					long victimCount = gravitateEntities( level, singularity, plrEmc/WOFT.GRAVITY.get(), player.isShiftKeyDown() );
					if (victimCount > 0) {
						EmcHelper.consumeAvaliableEmc(player, victimCount*WOFT.GRAVITY.get());
						level.playSound(null, player.blockPosition(), player.isShiftKeyDown() ? EffectInit.WOFT_REPEL.get() : EffectInit.WOFT_ATTRACT.get(), SoundSource.PLAYERS, 0.5f, 1);
						didDo = true;
					}
				}
				break;
			case 3: // Harvest (wither vine)
				if (plrEmc >= Harvest.WITHERVINE.get() && ready) {
					WitherVineProjectile vine = new WitherVineProjectile(level, player);
					vine.setDeltaMovement(player.getLookAngle().scale(2));
					vine.setPos(player.getEyePosition());
					level.addFreshEntity(vine);
					EmcHelper.consumeAvaliableEmc(player, Harvest.WITHERVINE.get());
					level.playSound(null, player.blockPosition(), SoundEvents.BONE_MEAL_USE, SoundSource.PLAYERS, 100, 1);
					cd.addCooldown(cdItem, 30);
					didDo = true;
				}
				break;
			case 4: // Liquid (nothing)
				break;
			case 5: // Philo (transmute)
				if (plrEmc >= Philo.TRANSMUTE.get() && !client && ready
						&& victim instanceof LivingEntity lEnt
						&& !lEnt.isDeadOrDying()
						&& !lEnt.isInvulnerableTo(MGTKDmgSrc.TRANSMUTATION_POTION)
						&& !EntityHelper.isInvincible(lEnt)
				) {
					if (plrEmc >= Philo.ITEMIZE.get() && !cd.isOnCooldown(stack.getItem())) {	
						int cdTime = (int) lEnt.getHealth()*7;
						if (entityItemizer(lEnt, player, null)) {
							didDo = true;
							level.playSound(null, lEnt.blockPosition(), EffectInit.PHILO_ITEMIZE.get(), SoundSource.PLAYERS, 1, 2);
							cd.addCooldown(stack.getItem(), cdTime);
							EmcHelper.consumeAvaliableEmc(player, Philo.ITEMIZE.get());
							break;
						}
					}
					lEnt.hurt(MGTKDmgSrc.strongTransmutation(player), Math.max(1, lEnt.getMaxHealth()/2));
					//lEnt.setLastHurtByPlayer(player);
					//if (lEnt instanceof NeutralMob mob) {
					//	mob.setPersistentAngerTarget(player.getUUID());
					//}
					lEnt.addEffect(new MobEffectInstance(EffectInit.TRANSMUTING.get(), 3, 2), player);
					EmcHelper.consumeAvaliableEmc(player, Philo.TRANSMUTE.get());
					level.playSound(null, player, EffectInit.PHILO_ATTACK.get(), SoundSource.PLAYERS, 1, 2);
					cd.addCooldown(cdItem, 30);
					didDo = true;
				}
				break;
			case 6: // Archangel (shotgun)
				if (plrEmc >= Archangel.ARROW.get() && ready) {
					EmcHelper.consumeAvaliableEmc(player,
							ProjectileHelper.shootArrow((int)Math.min(16, plrEmc/Archangel.ARROW.get()), ArrowType.STRAIGHT,
									new ShootContext(level, player),
									new ArrowOptions(3, 3, 8, (byte)0, false, Pickup.DISALLOWED))
							.size());
					cd.addCooldown(cdItem, 5);
					didDo = true;
				}
				break;
			case 7: // SWRG (gust)
				if (plrEmc >= SWRG.GUST.get() && ready && !client) {
					windGust((ServerPlayer)player);
					EmcHelper.consumeAvaliableEmc(player, SWRG.GUST.get());
					cd.addCooldown(cdItem, 30);
					didDo = true;
				}
				break;
			case 8: // Zero (extinguish AOE)
				if (plrEmc >= Zero.EXTINGUISH.get()) {
					EmcHelper.consumeAvaliableEmc(player,
							extinguishAoe(player,
									AABB.ofSize(player.getBoundingBox().getCenter(), 16, 16, 16),
									plrEmc/Zero.EXTINGUISH.get())
							* Zero.EXTINGUISH.get()
					);
					cd.addCooldown(cdItem, 5);
					didDo = true;
				}
				break;
			case 9: // Ignition (fireball / tnt)
				if (plrEmc >= Ignition.FIREBALL.get() && ready) {
					int cdTime = 2;
					if (player.isShiftKeyDown() && plrEmc >= Ignition.TNT.get()) {
						cdTime = 5;
						PrimedTnt tnt = tnt(level, player.getEyePosition(), player.getLookAngle().scale(2), player);
						EmcHelper.consumeAvaliableEmc(player, Ignition.TNT.get());
						level.playSound(null, tnt.blockPosition(), SoundEvents.TNT_PRIMED, SoundSource.PLAYERS, 1, 1);
					} else {
						for (int i = 0; i < 5; i++) {
							fireball(level, player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().scale(2)), player);
						}
						EmcHelper.consumeAvaliableEmc(player, Ignition.FIREBALL.get());
						level.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1, 1);
					}
					level.playSound(null, player.blockPosition(), EffectInit.IGNITION_CLICK.get(), SoundSource.PLAYERS, 1, level.random.nextFloat(0.7f, 1.4f));
					cd.addCooldown(cdItem, cdTime);
					didDo = true;
				}
				break;
			
			default:
				LoggerHelper.printWarn("BandOfArcana", "InvalidMode", "'"+player.getScoreboardName()+"' tried to use a Band of Arcana with an invalid mode, it may have corrupt NBT!");
			case 0:
				break;
			}
			
			return didDo;
		} else {
			changeMode(player, stack, InteractionHand.MAIN_HAND);
			return false;
		}
	}
	
	@Override
	public boolean onSwingBlock(PlayerInteractEvent.LeftClickBlock evt) {
		ItemStack stack = evt.getItemStack();
		if (stack.isEmpty() || !(stack.getItem() instanceof BandOfArcana)) return false;
		Player player = evt.getPlayer();
		if (isValidRingUser(player, stack)) {
			boolean didDo = false;
			Level level = player.getLevel();
			boolean client = level.isClientSide;
			byte mode = getMode(stack);
			boolean liquid = getLiquid(stack);
			ItemCooldowns cd = player.getCooldowns();
			Item cdItem = getModeItem(mode, liquid);
			boolean ready = !cd.isOnCooldown(cdItem);
			long plrEmc = EmcHelper.getAvaliableEmc(player);
			
			switch (mode) {
			case 1: // Mind (withdraw 1 / 10 levels)
				didDo = tryWithdrawXp(player.isShiftKeyDown() ? 10 : 1, stack, player);
				break;
			case 2: // Watch (gravity attract / repel)
				if (plrEmc >= WOFT.GRAVITY.get()) {
					Vec3 singularity = Vec3.atCenterOf(evt.getPos().relative(evt.getFace(), 1));
					long victimCount = gravitateEntities( level, singularity, plrEmc/WOFT.GRAVITY.get(), player.isShiftKeyDown() );
					if (victimCount > 0) {
						EmcHelper.consumeAvaliableEmc(player, victimCount*WOFT.GRAVITY.get());
						level.playSound(null, player.blockPosition(), player.isShiftKeyDown() ? EffectInit.WOFT_REPEL.get() : EffectInit.WOFT_ATTRACT.get(), SoundSource.PLAYERS, 0.5f, 1);
						didDo = true;
					}
				}
				break;
			case 3: // Harvest (wither vine)
				if (plrEmc >= Harvest.WITHERVINE.get() && ready) {
					WitherVineProjectile vine = new WitherVineProjectile(level, player);
					vine.setDeltaMovement(player.getLookAngle().scale(2));
					vine.setPos(player.getEyePosition());
					level.addFreshEntity(vine);
					EmcHelper.consumeAvaliableEmc(player, Harvest.WITHERVINE.get());
					level.playSound(null, player.blockPosition(), SoundEvents.BONE_MEAL_USE, SoundSource.PLAYERS, 100, 1);
					cd.addCooldown(cdItem, 30);
					didDo = true;
				}
				break;
			case 4: // Liquid (destroy liquid)
				if (plrEmc >= Liquid.DESTROY.get() && ModItems.openBucket.use(level, player, InteractionHand.MAIN_HAND).getResult().equals(InteractionResult.CONSUME)) {
					EmcHelper.consumeAvaliableEmc(player, Liquid.DESTROY.get());
					level.playSound(null, player.blockPosition(), PESoundEvents.DESTRUCT.get(), SoundSource.PLAYERS, 0.2f, 1);
					didDo = true;
				}
				break;
			case 5: // Philo (transmute block)
				if (ready) {
					if (PEItems.PHILOSOPHERS_STONE.get().useOn(new UseOnContext(player, evt.getHand(), new BlockHitResult( Vec3.atCenterOf(evt.getPos()), evt.getFace(), evt.getPos(), false ))) == InteractionResult.SUCCESS) {
						didDo = true;
						cd.addCooldown(cdItem, 5);
					}
				}
				break;
			case 6: // Archangel (shotgun)
				if (plrEmc >= Archangel.ARROW.get() && ready) {
					EmcHelper.consumeAvaliableEmc(player,
							ProjectileHelper.shootArrow((int)Math.min(16, plrEmc/Archangel.ARROW.get()), ArrowType.STRAIGHT,
									new ShootContext(level, player),
									new ArrowOptions(3, 3, 8, (byte)0, false, Pickup.DISALLOWED))
							.size() * Archangel.ARROW.get());
					cd.addCooldown(cdItem, 5);
					didDo = true;
				}
				break;
			case 7: // SWRG (gust)
				if (plrEmc >= SWRG.GUST.get() && ready && !client) {
					windGust((ServerPlayer)player);
					EmcHelper.consumeAvaliableEmc(player, SWRG.GUST.get());
					cd.addCooldown(cdItem, 30);
					didDo = true;
				}
				break;
			case 8: // Zero (extinguish AOE)
				if (plrEmc >= Zero.EXTINGUISH.get()) {
					EmcHelper.consumeAvaliableEmc(player,
							extinguishAoe(player,
									AABB.ofSize(player.getBoundingBox().getCenter(), 16, 16, 16),
									plrEmc/Zero.EXTINGUISH.get())
							* Zero.EXTINGUISH.get()
					);
					cd.addCooldown(cdItem, 5);
					didDo = true;
				}
				break;
			case 9: // Ignition (fireball / tnt)
				if (plrEmc >= Ignition.FIREBALL.get() && ready) {
					int cdTime = 2;
					if (player.isShiftKeyDown() && plrEmc >= Ignition.TNT.get()) {
						cdTime = 5;
						PrimedTnt tnt = tnt(level, player.getEyePosition(), player.getLookAngle().scale(2), player);
						EmcHelper.consumeAvaliableEmc(player, Ignition.TNT.get());
						level.playSound(null, tnt.blockPosition(), SoundEvents.TNT_PRIMED, SoundSource.PLAYERS, 1, 1);
					} else {
						for (int i = 0; i < 5; i++) {
							fireball(level, player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().scale(2)), player);
						}
						EmcHelper.consumeAvaliableEmc(player, Ignition.FIREBALL.get());
						level.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1, 1);
					}
					level.playSound(null, player.blockPosition(), EffectInit.IGNITION_CLICK.get(), SoundSource.PLAYERS, 1, level.random.nextFloat(0.7f, 1.4f));
					cd.addCooldown(cdItem, cdTime);
					didDo = true;
				}
				break;
			
			default:
				LoggerHelper.printWarn("BandOfArcana", "InvalidMode", "'"+player.getScoreboardName()+"' tried to use a Band of Arcana with an invalid mode, it may have corrupt NBT!");
			case 0:
				break;
			}
			
			return didDo;
		} else {
			changeMode(player, stack, InteractionHand.MAIN_HAND);
			return false;
		}
	}
	
	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (isValidRingUser(player, stack)) {
			boolean didDo = false;
			boolean client = level.isClientSide;
			byte mode = getMode(stack);
			boolean liquid = getLiquid(stack);
			ItemCooldowns cd = player.getCooldowns();
			Item cdItem = getModeItem(mode, liquid);
			boolean ready = !cd.isOnCooldown(cdItem);
			long plrEmc = EmcHelper.getAvaliableEmc(player);
			
			switch (mode) {
			case 1: // Mind (deposit 1 / 10 levels)
				didDo = tryDepositXp(player.isShiftKeyDown() ? 10 : 1, stack, player);
				break;
			case 2: // Watch (Time acceleration start / toggle)
				if (player.isShiftKeyDown()) {
					changeWoft(stack);
					level.playSound(null, player, EffectInit.WOFT_MODE.get(), SoundSource.PLAYERS, 1, 1.4f);
				} else {
					ItemNBTHelper.setBoolean(stack, "boa_tickhighpitch", false);
					player.startUsingItem(hand);
				}
				didDo = true;
				break;
			case 3: // Harvest (none)
				break;
			case 4: // Liquid (none)
				break;
			case 5: // Philo (none)
				break;
			case 6: // Archangel (arrow machine gun)
				player.startUsingItem(hand);
				didDo = true;
				break;
			case 7: // SWRG (smite target)
				if (plrEmc >= SWRG.SMITE.get() && ready && !client) {
					HitResult hitRes = swrgSuperSmite(player, level);
					if (hitRes.getType() != HitResult.Type.MISS) {
						level.playSound(null, player.blockPosition(), EffectInit.IGNITION_CLICK.get(), SoundSource.PLAYERS, 1, 0.5f);
						EmcHelper.consumeAvaliableEmc(player, SWRG.SMITE.get());
						NetworkInit.toClient(new DrawParticleLinePacket(player.getEyePosition(), hitRes.getLocation(), LineParticlePreset.SMITE), (ServerPlayer)player);
						cd.addCooldown(cdItem, level.isThundering() ? 3 : 9);
						didDo = true;
					}
				}
				break;
			case 8: // Zero (freeze air / entity)
				if (plrEmc >= Zero.FREEZE.get() && ready) {
					Vec3 pos = player.getEyePosition();
					Vec3 ray = player.getLookAngle().scale(player.getReachDistance()-0.5);
					EntityHitResult hitRes = ProjectileUtil.getEntityHitResult(player, pos, pos.add(ray), AABB.ofSize(pos, 0.05, 0.05, 0.05).expandTowards(ray), this::canBeFrozen, 0);
					BlockPos bPos = new BlockPos(pos.add(ray));
					if (hitRes != null && hitRes.getType() == HitResult.Type.ENTITY) {
						LivingEntity ent = (LivingEntity) hitRes.getEntity();
						bPos = ent.blockPosition();
						if (!client) freezeEntity(ent, (ServerPlayer)player);
						didDo = true;
					} else if (level.getBlockState(bPos).isAir()) {
						level.setBlockAndUpdate(bPos, ObjectInit.AIR_ICE.get().defaultBlockState());
						didDo = true;
					} else if (level.getBlockState(bPos).is(Blocks.WATER)) {
						level.setBlockAndUpdate(bPos, Blocks.FROSTED_ICE.defaultBlockState());
						didDo = true;
					}
					if (didDo) {
						level.playSound(null, bPos, EffectInit.ZERO_FREEZE.get(), SoundSource.BLOCKS, 1, 1);
						EmcHelper.consumeAvaliableEmc(player, Zero.FREEZE.get());
						cd.addCooldown(cdItem, 3);
					}
				}
				break;
			case 9: // Ignition (burn entity)
				if (plrEmc >= Ignition.BURN.get() && ready) {
					Vec3 pos1 = player.getEyePosition();
					Vec3 pos2 = pos1.add(player.getLookAngle().scale(player.getReachDistance()-0.5));
					EntityHitResult hitRes = ProjectileUtil.getEntityHitResult(player, pos1, pos2, AABB.ofSize(pos1, 0.05, 0.05, 0.05), this::canBeBurnt, 0);
					if (hitRes != null && hitRes.getType() == HitResult.Type.ENTITY) {
						LivingEntity ent = (LivingEntity) hitRes.getEntity();
						BlockPos bPos = ent.blockPosition();
						if (!client) burnEntity(ent, (ServerPlayer)player);
						level.playSound(null, player.blockPosition(), EffectInit.IGNITION_CLICK.get(), SoundSource.BLOCKS, 1, 1);
						level.playSound(null, bPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1, 1);
						EmcHelper.consumeAvaliableEmc(player, Ignition.BURN.get());
						didDo = true;
					}
				}
				break;
			
			default:
				LoggerHelper.printWarn("BandOfArcana", "InvalidMode", "'"+player.getScoreboardName()+"' tried to use a Band of Arcana with an invalid mode, it may have corrupt NBT!");
			case 0:
				break;
			}
			
			return didDo ? InteractionResultHolder.success(stack) : InteractionResultHolder.pass(stack);
		} else {
			changeMode(player, stack, hand);
			return InteractionResultHolder.fail(stack);
		}
	}
	
	public InteractionResult useOn(UseOnContext ctx) {
		ItemStack stack = ctx.getItemInHand();
		Player player = ctx.getPlayer();
		if (isValidRingUser(player, stack)) {
			boolean didDo = false;
			Level level = player.getLevel();
			boolean client = level.isClientSide;
			byte mode = getMode(stack);
			boolean liquid = getLiquid(stack);
			ItemCooldowns cd = player.getCooldowns();
			Item cdItem = getModeItem(mode, liquid);
			boolean ready = !cd.isOnCooldown(cdItem);
			long plrEmc = EmcHelper.getAvaliableEmc(player);
			
			switch (mode) {
			case 1: // Mind (none)
				break;
			case 2: // Watch (none)
				break;
			case 3: // Harvest (bonemeal)
				if (plrEmc >= Harvest.INSTAGROW.get() && !client) {
					ItemStack meal = new ItemStack(Items.BONE_MEAL);
					meal.setCount(12);
					UseOnContext mealCtx = new UseOnContext(level, player, ctx.getHand(), meal,
							new BlockHitResult(ctx.getClickLocation(), ctx.getClickedFace(), ctx.getClickedPos(), ctx.isInside()));
					if (Items.BONE_MEAL.useOn(mealCtx) != InteractionResult.PASS) {
						for (int i = 0; i < 11; i++) {
							// it worked once, do it 11 more times
							Items.BONE_MEAL.useOn(mealCtx);
						}
						EmcHelper.consumeAvaliableEmc(player, Harvest.INSTAGROW.get());
						didDo = true;
					}
				}
				break;
			case 4: // Liquid
				if (plrEmc >= Liquid.CREATE.get() && liquid) {
					// lava
					Items.LAVA_BUCKET.use(level, player, ctx.getHand());
					EmcHelper.consumeAvaliableEmc(player, Liquid.CREATE.get());
				} else {
					// water
					Items.WATER_BUCKET.use(level, player, ctx.getHand());
				}
				break;
			case 5: // Philo (divining rod)
				if (plrEmc >= Philo.DIVINING.get() && ready) {
					AABB area = WorldHelper.getBroadBox(ctx.getClickedPos(), ctx.getClickedFace(), 2).expandTowards(Vec3.atLowerCornerOf(ctx.getClickedFace().getNormal()).scale(-128));
					EmcHelper.consumeAvaliableEmc(player, diviningRod(stack, player, area, plrEmc/Philo.DIVINING.get()) * Philo.DIVINING.get());
					level.playSound(null, ctx.getClickedPos(), EffectInit.PHILO_XRAY.get(), SoundSource.PLAYERS, 1, 2);
					cd.addCooldown(cdItem, 40);
				}
				break;
			case 6: // Archangel (none)
				break;
			case 7: // SWRG (none)
				break;
			case 8: // Zero (freeze air)
				if (plrEmc >= Zero.FREEZE.get() && ready) {
					BlockPos bPos = ctx.getClickedPos().relative(ctx.getClickedFace());
					if (level.getBlockState(bPos).isAir()) {
						level.setBlockAndUpdate(bPos, ObjectInit.AIR_ICE.get().defaultBlockState());
						didDo = true;
					} else if (level.getBlockState(bPos).is(Blocks.WATER)) {
						level.setBlockAndUpdate(bPos, Blocks.FROSTED_ICE.defaultBlockState());
						didDo = true;
					}
					if (didDo) {
						level.playSound(null, bPos, EffectInit.ZERO_FREEZE.get(), SoundSource.BLOCKS, 1, 1);
						EmcHelper.consumeAvaliableEmc(player, Zero.FREEZE.get());
						cd.addCooldown(cdItem, 3);
					}
				}
				break;
			case 9: // Ignition (flint and steel)
				if (plrEmc >= Ignition.BURN.get() && ready) {
					InteractionResult res = Items.FLINT_AND_STEEL.useOn(ctx);
					if (res == InteractionResult.SUCCESS) {
						level.playSound(null, ctx.getClickedPos().relative(ctx.getClickedFace()), EffectInit.IGNITION_CLICK.get(), SoundSource.PLAYERS, 1, 1);
						EmcHelper.consumeAvaliableEmc(player, Ignition.BURN.get());
						didDo = true;
					}
				}
				break;
			
			default:
				LoggerHelper.printWarn("BandOfArcana", "InvalidMode", "'"+player.getScoreboardName()+"' tried to use a Band of Arcana with an invalid mode, it may have corrupt NBT!");
			case 0:
				break;
			}
			
			return didDo ? InteractionResult.SUCCESS : InteractionResult.PASS;
		} else {
			changeMode(player, stack, InteractionHand.MAIN_HAND);
			return InteractionResult.FAIL;
		}
	}
	
	@Override
	public void onUsingTick(ItemStack stack, LivingEntity user, int time) {
		if (user instanceof Player player) {
			if (isValidRingUser(player, stack)) {
				boolean didDo = false;
				Level level = player.getLevel();
				boolean client = level.isClientSide;
				byte mode = getMode(stack);
				boolean liquid = getLiquid(stack);
				Item cdItem = getModeItem(mode, liquid);
				long plrEmc = EmcHelper.getAvaliableEmc(player);
				
				switch (mode) {
				case 1: // Mind (none)
					break;
				case 2: // Watch (time acceleration tick)
					if (!client && plrEmc >= (getWoft(stack) ? WOFT.JOJO_STRONG.get() : WOFT.JOJO.get())) {
						plrEmc -= EmcHelper.consumeAvaliableEmc(player, getWoft(stack) ? WOFT.JOJO_STRONG.get() : WOFT.JOJO.get());
						jojoReference(player, stack, 60, Integer.MAX_VALUE - time, 1200, getWoft(stack) ? 24 : 0, plrEmc);
					}
					break;
				case 3: // Harvest (none)
					break;
				case 4: // Liquid (none)
					break;
				case 5: // Philo (none)
					break;
				case 6: // Archangel (arrow machine gun)
					if (plrEmc >= Archangel.ARROW.get()) {
						List<AbstractArrow> arrows = ProjectileHelper.shootArrow(1, ArrowType.STRAIGHT, new ShootContext(level, player), new ArrowOptions(1, 5, 0, Byte.MAX_VALUE, true, Pickup.DISALLOWED));
					}
					break;
				case 7: // SWRG (none)
					break;
				case 8: // Zero (none)
					break;
				case 9: // Ignition (none)
					break;
				
				default:
					LoggerHelper.printWarn("BandOfArcana", "InvalidMode", "'"+player.getScoreboardName()+"' tried to use a Band of Arcana with an invalid mode, it may have corrupt NBT!");
				case 0:
					break;
				}
				
				return;
			} else {
				changeMode(player, stack, InteractionHand.MAIN_HAND);
				return;
			}
		}
	}
	
	@Override
	public boolean shootProjectile(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
		if (isValidRingUser(player, stack)) {
			boolean didDo = false;
			Level level = player.getLevel();
			boolean client = level.isClientSide;
			byte mode = getMode(stack);
			boolean liquid = getLiquid(stack);
			ItemCooldowns cd = player.getCooldowns();
			Item cdItem = getModeItem(mode, liquid);
			boolean ready = !cd.isOnCooldown(cdItem);
			long plrEmc = EmcHelper.getAvaliableEmc(player);
			
			switch (mode) {
			case 1: // Mind (deposit all)
				if (PlrHelper.getXp(player) > 0) {
					insertXp(stack, PlrHelper.extractAll(player));
					player.level.playSound(null, player, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1, 2f);
					didDo = true;
				}
				break;
			case 2: // Watch (blink)
				if (plrEmc >= WOFT.TELEPORT.get()) {
					didDo = PEItems.VOID_RING.get().doExtraFunction(stack, player, hand);
					if (didDo) EmcHelper.consumeAvaliableEmc(player, WOFT.TELEPORT.get());
				}
				break;
			case 3: // Harvest
				if (ready) {
					player.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
					int cdTime = 60;
					if (player.hasEffect(EffectInit.TRANSMUTING.get())) {
						player.curePotionEffects(new ItemStack(PEItems.PHILOSOPHERS_STONE.get()));
						level.playSound(null, player, PESoundEvents.POWER.get(), SoundSource.PLAYERS, 0.5f, 1);
						cdTime *= 10;
					}
					level.playSound(null, player, SoundEvents.HONEY_DRINK, SoundSource.PLAYERS, 1, level.random.nextFloat(1, 2));
					cd.addCooldown(cdItem, cdTime);
				}
				break;
			case 4: // Liquid (orb)
				if (plrEmc >= Liquid.ORB.get() && ready) {
					didDo = liquid ? shootLavaProjectile(player) : PEItems.EVERTIDE_AMULET.get().shootProjectile(player, stack, hand);
				}
				if (didDo) {
					cd.addCooldown(cdItem, 3);
					EmcHelper.consumeAvaliableEmc(player, Liquid.ORB.get());
				}
				break;
			case 5: // Philo (mob transmute orb)
				if (plrEmc >= Philo.ORB.get() && ready) {
					didDo = PEItems.PHILOSOPHERS_STONE.get().shootProjectile(player, stack, hand);
					if (didDo) EmcHelper.consumeAvaliableEmc(player, Philo.ORB.get());
					cd.addCooldown(cdItem, 5);
				}
				break;
			case 6: // Archangel (sentient arrow)
				if (ready) {
					
					if (hasTrackedArrow(stack)) {
						SentientArrow arrow = getTrackedArrow(stack, player.level);
						if (arrow == null) resetTrackedArrow(stack);
						else {
							sentientArrowControl(getTrackedArrow(stack, level), player);
							cd.addCooldown(cdItem, 15);
							break;
						}
					}
					
					if (plrEmc >= Archangel.HOMING.get()) {
						SentientArrow arrow = (SentientArrow) ProjectileHelper.shootArrow(1, ArrowType.SENTIENT,
								new ShootContext(player.level, player),
								new ArrowOptions(1, 1, 0, (byte)0, false, Pickup.DISALLOWED)).get(0);
						changeTrackedArrow(stack, arrow);
						for (ServerPlayer plr : ((ServerLevel)player.level).players()) {
							NetworkInit.toClient(new CreateLoopingSoundPacket((byte)1, arrow.getId()), plr);
						}
						EmcHelper.consumeAvaliableEmc(player, Archangel.HOMING.get());
						cd.addCooldown(cdItem, 15);
						didDo = true;
					}
				}
				break;
			case 7: // SWRG (aoe smite)
				if (plrEmc >= SWRG.SMITE.get() && ready && !client) {
					long costPer = SWRG.SMITE.get();
					int smitten = 0;
					AABB area = AABB.ofSize(player.getBoundingBox().getCenter(), 32, 32, 32);
					List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area, this::canBeSmitten);
					Collections.shuffle(targets, level.random);
					for (LivingEntity ent : targets) {
						if (ent.is(player)) continue;
						if (smitten > (level.isThundering() ? 30 : 10) || plrEmc <= costPer*smitten) break;
						swrgSuperSmiteEnt(player, level, ent);
						smitten++;
					}
					long totalCost = costPer*smitten;
					EmcHelper.consumeAvaliableEmc(player, totalCost);
					int cdTime = (int) Math.max(13, smitten * (level.isThundering() ? 2.6 : 26));
					cd.addCooldown(cdItem, cdTime);
					didDo = true;
				}
				break;
			case 8: // Zero (ice shield)
				if (plrEmc >= Zero.ICESHIELD.get() && ready) {
					EmcHelper.consumeAvaliableEmc(player, Zero.ICESHIELD.get());
					AABB box = AABB.ofSize(player.getBoundingBox().getCenter(), 4, 4, 4);
					BlockPos.betweenClosedStream(box).forEach(pos -> {
						if (level.getBlockState(pos).is(Blocks.AIR)) {
							level.setBlockAndUpdate(pos, ObjectInit.AIR_ICE.get().defaultBlockState());
						} else if (level.getBlockState(pos).is(Blocks.WATER)) {
							level.setBlockAndUpdate(pos, Blocks.FROSTED_ICE.defaultBlockState());
						}
					});
					box = AABB.ofSize(player.getBoundingBox().getCenter(), 2, 2, 2);
					BlockPos.betweenClosedStream(box).forEach(pos -> {
						if (level.getBlockState(pos).is(ObjectInit.AIR_ICE.get())
								|| level.getBlockState(pos).is(Blocks.FROSTED_ICE)) {
							level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
						}
					});
					EmcHelper.consumeAvaliableEmc(player, Zero.ICESHIELD.get());
					level.playSound(null, new BlockPos(box.getCenter()), EffectInit.ZERO_FREEZE.get(), SoundSource.PLAYERS, 1, 0.7f);
					player.hurt(DamageSource.FREEZE, 5);
					player.addEffect(new MobEffectInstance(EffectInit.ICESHIELD.get(), 100));
					cd.addCooldown(cdItem, 150);
					didDo = true;
				}
				break;
			case 9: // Ignition (flame nuke)
				if (plrEmc >= Ignition.MUSTANG.get() && ready) {
					if (shootMustang(player, stack)) {
						EmcHelper.consumeAvaliableEmc(player, Ignition.MUSTANG.get());
						cd.addCooldown(cdItem, 30);
						didDo = true;
					}
				}
				break;
			
			default:
				LoggerHelper.printWarn("BandOfArcana", "InvalidMode", "'"+player.getScoreboardName()+"' tried to use a Band of Arcana with an invalid mode, it may have corrupt NBT!");
			case 0:
				break;
			}
			
			return didDo;
		} else {
			changeMode(player, stack, InteractionHand.MAIN_HAND);
			return false;
		}
	}
	
	@Override
	public boolean doExtraFunction(@NotNull ItemStack stack, @NotNull Player player, @Nullable InteractionHand hand) {
		if (isValidRingUser(player, stack)) {
			boolean didDo = false;
			Level level = player.getLevel();
			boolean client = level.isClientSide;
			byte mode = getMode(stack);
			boolean liquid = getLiquid(stack);
			ItemCooldowns cd = player.getCooldowns();
			Item cdItem = getModeItem(mode, liquid);
			boolean ready = !cd.isOnCooldown(cdItem);
			long plrEmc = EmcHelper.getAvaliableEmc(player);
			
			switch (mode) {
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
			case 2: // Watch (ender chest / anvil)
				player.level.playSound(null, player, EffectInit.PHILO_3X3GUI.get(), SoundSource.PLAYERS, 1, 2f);
				if (player.isShiftKeyDown()) {
					// anvil
					player.openMenu(new SimpleMenuProvider((window, inv, plr) -> {
				         return new GravityAnvilMenu(window, inv, ContainerLevelAccess.create(level, plr.blockPosition()));
				      }, new TranslatableComponent("gui.mgtk.woft.anvil.name")));
					player.level.playSound(null, player, SoundEvents.ANVIL_PLACE, SoundSource.PLAYERS, 0.5F, 1f);
				} else {
					// echest
					player.openMenu(new SimpleMenuProvider((window, inv, plr) -> {
						return ChestMenu.threeRows(window, inv, plr.getEnderChestInventory());
					}, stack.getHoverName()));
					player.level.playSound(null, player, SoundEvents.ENDER_CHEST_OPEN, SoundSource.PLAYERS, 1F, 1f);
				}
				didDo = true;
				break;
			case 3: // Harvest (grow / harvest nearby)
				AABB area = AABB.ofSize(player.getBoundingBox().getCenter(), 8, 5, 8);
				if (plrEmc >= Harvest.AOEGROW.get() && ready && !client) {
					boolean harv = player.isShiftKeyDown() && plrEmc >= Harvest.HARVEST.get();
					if (harv) {
						MiscHelper.harvestNearby((ServerPlayer)player, (ServerLevel)level, area, 1);
					} else MiscHelper.growNearby((ServerLevel)level, area, 3);
					EmcHelper.consumeAvaliableEmc(player, harv ? Harvest.HARVEST.get() : Harvest.AOEGROW.get());
					//NetworkInit.toClient(new DrawParticleAABBPacket(new Vec3(area.minX, area.minY, area.minZ), new Vec3(area.maxX, area.maxY, area.maxZ), ParticlePreset.DEBUG), (ServerPlayer)player);
					didDo = true;
				}
				//if (client) {
				//	MiscHelper.drawAABBWithParticles(area, ParticleTypes.DRIPPING_LAVA, 0.1, (ClientLevel)level, false, true);
				//}
				break;
			case 4: // Liquid (change liquid)
				changeLiquid(stack);
				player.level.playSound(null, player, getLiquid(stack) ? EffectInit.LIQUID_LAVA_SWITCH.get() : EffectInit.LIQUID_WATER_SWITCH.get(), SoundSource.PLAYERS, 1, 0.7f);
				didDo = true;
				break;
			case 5: // Philo (crafting / enchant)
				player.level.playSound(null, player, EffectInit.PHILO_3X3GUI.get(), SoundSource.PLAYERS, 0.6f, 2f);
				if (player.isShiftKeyDown()) {
					// enchant
					player.openMenu( new SimpleMenuProvider((window, inv, plr) -> {
						return new PhiloEnchantmentMenu(window, inv, ContainerLevelAccess.create(level, player.blockPosition()));
					}, new TranslatableComponent("gui.mgtk.philo.enchanter.name")));
					player.level.playSound(null, player, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1, 1f);
					didDo = true;
				} else {
					// crafting
					didDo = PEItems.PHILOSOPHERS_STONE.get().doExtraFunction(stack, player, hand);
				}
				break;
			case 6: // Archangel
				if (plrEmc >= Archangel.SMART.get() && ready) {
					boolean up = player.isOnGround();
					long amount = Math.min(up ? 28 : 56, plrEmc/Archangel.SMART.get());
					ShootContext ctx = up ?
							new ShootContext(level, player, new Vec3(-90, 0, 0)) :
							new ShootContext(level, player);
					List<AbstractArrow> arrows = ProjectileHelper.shootArrow((int)amount, ArrowType.SMART, ctx,
							new ArrowOptions(1, 0.5f, up ? 75 : 300, (byte)0, false, Pickup.DISALLOWED));
					cd.addCooldown(cdItem, arrows.size() / 2);
				}
				break;
			case 7: // SWRG
				if (plrEmc >= SWRG.STORM.get() && ready && !level.isThundering()
					&& player.level.getLevelData() instanceof ServerLevelData dat) {
					smite(level, player.position(), (ServerPlayer)player, true);
					dat.setRainTime(6000);
					dat.setThunderTime(6000);
					dat.setRaining(true);
					dat.setThundering(true);
					EmcHelper.consumeAvaliableEmc(player, SWRG.STORM.get());
					cd.addCooldown(cdItem, 100);
					didDo = true;
				}
				break;
			case 8: // Zero
				if (plrEmc >= Zero.FREEZE.get() && ready && !client) {
					AABB range = AABB.ofSize(player.getBoundingBox().getCenter(), 32, 32, 32);
					int frozen = 0;
					for (LivingEntity ent : level.getEntitiesOfClass(LivingEntity.class, range, ent -> canBeFrozen(ent) && !ent.is(player) )) {
						this.freezeEntity(ent, (ServerPlayer)player);
						frozen++;
					}
					WorldHelper.freezeInBoundingBox(level, range, player, false);
					EmcHelper.consumeAvaliableEmc(player, Zero.FREEZE.get()*frozen);
					level.playSound(null, player.blockPosition(), EffectInit.ZERO_FREEZE.get(), SoundSource.PLAYERS, 5, 0.5f);
					//cd.addCooldown(cdItem, 273);
					cd.addCooldown(cdItem, 60);
				}
				break;
			case 9: // Ignition
				if (plrEmc >= Ignition.BURN.get() && ready && !client) {
					AABB range = AABB.ofSize(player.getBoundingBox().getCenter(), 32, 32, 32);
					int burnt = 0;
					for (LivingEntity ent : level.getEntitiesOfClass(LivingEntity.class, range, ent -> canBeBurnt(ent) && !ent.is(player) )) {
						this.burnEntity(ent, (ServerPlayer)player);
						burnt++;
					}
					MiscHelper.burnInBoundingBox(level, range, player, false);
					EmcHelper.consumeAvaliableEmc(player, Ignition.BURN.get()*burnt);
					level.playSound(null, player.blockPosition(), EffectInit.IGNITION_BURN.get(), SoundSource.PLAYERS, 5, 1.5f);
					cd.addCooldown(cdItem, 451);
				}
				break;
			
			default:
				LoggerHelper.printWarn("BandOfArcana", "InvalidMode", "'"+player.getScoreboardName()+"' tried to use a Band of Arcana with an invalid mode, it may have corrupt NBT!");
			case 0:
				break;
			}
			
			return didDo;
		} else {
			changeMode(player, stack, InteractionHand.MAIN_HAND);
			return false;
		}
	}
	
	/**
	 * removes all time accel attribute modifiers from a player
	 * @param player
	 */
	public void resetTimeAccelSpeed(Player player) {
		for (Attribute atr : getTimeAccelAttributes()) {
			player.getAttribute(atr).removeModifier(TIME_ACCEL_UUID);
		}
	}
	
	@Override
	public boolean onDroppedByPlayer(ItemStack stack, Player player) {
		// this is to prevent issues when dropping the item while using it (such as time accel speed sticking around)
		resetTimeAccelSpeed(player);
		return true;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (entity instanceof Player player) {
			resetTimeAccelSpeed(player);
			player.sendMessage(new TranslatableComponent(
					"chat.type.text",
					"solunareclipse1",
					"i think youve been doing that for long enough, its time for you to go outside"),
				UUID.fromString("89b9a7d2-daa3-48cc-903c-96d125106a6b"));
			EmcHelper.consumeAvaliableEmc(player, Long.MAX_VALUE);
			stack.shrink(1);
			player.hurt(MGTKDmgSrc.GOD, Float.MAX_VALUE);
		} // punishment for holding down right click for almost 3.5 straight years
		return stack;
	}
	
	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int time) {
		if (entity instanceof Player player) {
			resetTimeAccelSpeed(player);
		}
	}
	
	@Override
	public boolean canContinueUsing(ItemStack before, ItemStack after) {
		return ItemStack.isSameIgnoreDurability(before, after) && getMode(after) != 0 && (getMode(before) == getMode(after));
	}
	
	/** returns the item corresponding to mode */
	private Item getModeItem(int mode, boolean liquid) {
		switch (mode) {
		case 1:
			return PEItems.MIND_STONE.get();
		case 2:
			return PEItems.WATCH_OF_FLOWING_TIME.get();
		case 3:
			return PEItems.HARVEST_GODDESS_BAND.get();
		case 4:
			return liquid ? PEItems.VOLCANITE_AMULET.get() : PEItems.EVERTIDE_AMULET.get();
		case 5:
			return PEItems.PHILOSOPHERS_STONE.get();
		case 6:
			return PEItems.ARCHANGEL_SMITE.get();
		case 7:
			return PEItems.SWIFTWOLF_RENDING_GALE.get();
		case 8:
			return PEItems.ZERO_RING.get();
		case 9:
			return PEItems.IGNITION_RING.get();
		default:
			return ObjectInit.GEM_BRACELET.get();
		}
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
	
	public boolean hasTrackedArrow(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_ARROWTRACKER, -1) != -1;
	}
	
	@Nullable
	public SentientArrow getTrackedArrow(ItemStack stack, Level level) {
		Entity tracked = level.getEntity(ItemNBTHelper.getInt(stack, TAG_ARROWTRACKER, -1));
		if (tracked != null && tracked instanceof SentientArrow arrow) {
			return arrow;
		} else return null;
	}
	
	public void changeTrackedArrow(ItemStack stack, SentientArrow arrow) {
		ItemNBTHelper.setInt(stack, TAG_ARROWTRACKER, arrow.getId());
	}
	
	public void resetTrackedArrow(ItemStack stack) {
		ItemNBTHelper.setInt(stack, TAG_ARROWTRACKER, -1);
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
		if (player.isUsingItem() || player.getCooldowns().isOnCooldown(stack.getItem())) return false;
		for (ItemStack armor : player.getArmorSlots()) {
			if (armor.getItem() instanceof GemJewelryBase) {
				byte ogMode = getMode(stack);
				byte curMode = ogMode;
				byte newMode;
				if (player.isShiftKeyDown()) {
					if (curMode == 0) return false;
					newMode = 0; // shift + modeswitch = disable all functionality
				} else {
					do {
						if (curMode == 9) newMode = 1;
						else newMode = (byte) (curMode + 1);
						curMode = newMode;
					} while (!isModeValid(player, curMode));
				}
				
				// Displays a message over the hotbar on mode switch, corresponding to newMode
				setMode(stack, newMode);
				player.displayClientMessage(new TranslatableComponent(KEY_MODES[newMode]).withStyle(getModeTextStyle(newMode, getLiquid(stack)).withUnderlined(true)), true);
				player.level.playSound(null, player, EffectInit.BOA_MODE.get(), SoundSource.PLAYERS, 1, 0.7f);
				return true;
			}
		}
		setMode(stack, (byte)0);
		player.displayClientMessage(new TranslatableComponent(KEY_MODES[0]).withStyle(getModeTextStyle((byte)0, getLiquid(stack)).withUnderlined(true)), true);
		player.level.playSound(null, player, EffectInit.BOA_MODE.get(), SoundSource.PLAYERS, 1, 0.7f);
		return false;
	}
	
	private void setMode(ItemStack stack, byte mode) {
		ItemNBTHelper.setByte(stack, TAG_MODE, mode);
	}
	
	public Style getModeTextStyle(byte mode, boolean liquid) {
		Style modeStyle = Style.EMPTY.withBold(true);
		switch (mode) {
		case 1:
			modeStyle = modeStyle.withColor(0x7db700);
			break;
		case 2:
			modeStyle = modeStyle.withColor(0x5c00d7);
			break;
		case 3:
			modeStyle = modeStyle.withColor(0x1ba200);
			break;
		case 4:
			modeStyle = modeStyle.withColor(liquid ? 0xca4528 : 0x234eca);
			break;
		case 5:
			modeStyle = modeStyle.withColor(0xb32f67);
			break;
		case 6:
			modeStyle = modeStyle.withColor(0x734814);
			break;
		case 7:
			modeStyle = modeStyle.withColor(0xc4c602);
			break;
		case 8:
			modeStyle = modeStyle.withColor(0x3e8df8);
			break;
		case 9:
			modeStyle = modeStyle.withColor(0xf73f47);
			break;
		}
		return modeStyle;
	}

	
	@Override
	public int getNumCharges(@NotNull ItemStack stack) {return 1;}
	
	@Override
	public int getCharge(@NotNull ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, TAG_COVALENCE, false) ? 1 : 0;
	}
	
	@Override
	public float getChargePercent(@NotNull ItemStack stack) {
		return getCharge(stack);
	}
	
	@Override
	public boolean changeCharge(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
		boolean on = getCharge(stack) == 1;
		
		// dont turn on covalence if player has no emc
		if (!on && !EmcHelper.hasEmc(player)) {
			player.level.playSound(null, player, PESounds.UNCHARGE, SoundSource.PLAYERS, 1, 0.5f);
			return false;
		}

		player.level.playSound(null, player, on ? PESounds.UNCHARGE : PESounds.CHARGE, SoundSource.PLAYERS, 1, 1);
		ItemNBTHelper.setBoolean(stack, TAG_COVALENCE, !on);
		
		return true;
	}

	// blood magic will stuff
	@Override
	public EnumDemonWillType getCurrentType(ItemStack stack) {
		EnumDemonWillType type = EnumDemonWillType.DEFAULT;
		if (covalenceActive(stack)) {
			switch (getMode(stack)) {
			
			default:
				LoggerHelper.printWarn("BandOfArcana", "InvalidMode", "Band of Arcana '"+stack+"' may have corrupt NBT!");
			case 0:
			case 1:
				break;

			case 2:
			case 3:
				type = EnumDemonWillType.CORROSIVE;
				break;

			case 4:
			case 5:
				type = EnumDemonWillType.STEADFAST;
				break;

			case 6:
			case 7:
				type = EnumDemonWillType.VENGEFUL;
				break;

			case 8:
			case 9:
				type = EnumDemonWillType.DESTRUCTIVE;
				break;
			}
		}
		return type;
	}

	@Override
	public ItemStack fillDemonWillGem(ItemStack gemStack, ItemStack soulStack) {
		if (soulStack.getItem() instanceof IDemonWill will && will.getType(soulStack) == getCurrentType(gemStack))
			return super.fillDemonWillGem(gemStack, soulStack);
		return soulStack;
	}

	@Override
	public double getWill(EnumDemonWillType type, ItemStack stack) {
		if (type == getCurrentType(stack)) return super.getWill(type, stack);
		return 0;
	}
	
	@Override
	public void setWill(EnumDemonWillType type, ItemStack stack, double amount) {
		if (type == getCurrentType(stack)) super.setWill(type, stack, amount);
	}

	@Override
	public int getMaxWill(EnumDemonWillType type, ItemStack stack) {
		if (type == getCurrentType(stack)) return super.getMaxWill(type, stack);
		return 0;
	}

	@Override
	public double drainWill(EnumDemonWillType type, ItemStack stack, double requested, boolean exec) {
		if (type == getCurrentType(stack)) return super.drainWill(type, stack, requested, exec);
		return 0;
	}

	@Override
	public double fillWill(EnumDemonWillType type, ItemStack stack, double requested, boolean exec) {
		if (type == getCurrentType(stack)) return super.fillWill(type, stack, requested, exec);
		return 0;
	}
	
	/**
	 * Checks for armor piece for corresponding item modes
	 * @param player
	 * @return
	 */
	private boolean isValidRingUser(Player player, ItemStack stack) {
		if (player.getCooldowns().isOnCooldown(stack.getItem())) return false;
		switch (getMode(stack)) {
		case 1: // Mind
		case 5: // Philo
			return GemJewelryBase.getInfo(player, EquipmentSlot.HEAD).exists();
		case 6: // Archangel
		case 8: // Zero
		case 9: // Ignition
			return GemJewelryBase.getInfo(player, EquipmentSlot.CHEST).exists();
		case 2: // Watch
		case 4: // Liquid
			return GemJewelryBase.getInfo(player, EquipmentSlot.LEGS).exists();
		case 3: // Harvest
		case 7: // SWRG
			return GemJewelryBase.getInfo(player, EquipmentSlot.FEET).exists();
		case 0: // Disabled
			return true;
			
		default:
			break;
		}
		return false;
	}
	
	private boolean isModeValid(Player player, byte mode) {
		switch (mode) {
		case 1: // Mind
		case 5: // Philo
			return GemJewelryBase.getInfo(player, EquipmentSlot.HEAD).exists();
		case 6: // Archangel
		case 8: // Zero
		case 9: // Ignition
			return GemJewelryBase.getInfo(player, EquipmentSlot.CHEST).exists();
		case 2: // Watch
		case 4: // Liquid
			return GemJewelryBase.getInfo(player, EquipmentSlot.LEGS).exists();
		case 3: // Harvest
		case 7: // SWRG
			return GemJewelryBase.getInfo(player, EquipmentSlot.FEET).exists();
			
		default:
			break;
		}
		return true;
	}
	
	/// xp stuff
	public boolean tryDepositXp(int lvls, ItemStack stack, Player player) {
		if (PlrHelper.getXp(player) > 0 && getXp(stack) < Long.MAX_VALUE) {
			//int lvls = player.isShiftKeyDown() ? 10 : 1;
			insertXp(stack, PlrHelper.extractLvl(player, lvls));
			player.level.playSound(null, player, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1, 2f);
			return true;
		}
		return false;
	}
	
	public boolean tryWithdrawXp(int lvls, ItemStack stack, Player player) {
		long stored = getXp(stack);
		long max = Math.min(stored, Xp.VANILLA_MAX_POINTS - PlrHelper.getXp(player));
		if (stored > 0 && max > 0) {
			long amount = 0;
			for (int i = 0; i < lvls; i++) {
				int curLvl = player.experienceLevel + i;
				// only factor in current progress for the first level
				long toAdd = 0;
				if (i == 0 && player.experienceProgress > 0) {
					toAdd += PlrHelper.xpNeededToLevelUpFrom(curLvl) - player.experienceProgress * PlrHelper.xpNeededToLevelUpFrom(curLvl);
				} else {
					toAdd += PlrHelper.xpNeededToLevelUpFrom(curLvl);
				}
				if (amount + toAdd >= max) {
					amount = max;
					break;
				} else {
					amount += toAdd;
				}
			}
			PlrHelper.insertXp(player, extractXp(stack, amount));
			player.level.playSound(null, player, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1, 2f);
			return true;
		}
		return false;
	}
	
	// Mind stone
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
	
	public long gravitateEntities(Level level, Vec3 pos, long max, boolean invert) {
		long amount = 0;
		List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(pos, 24, 24, 24), ent -> !EntityHelper.isImmuneToGravityManipulation(ent));
		for ( LivingEntity ent : victims ) {
			if (amount >= max) break;
			if (ent.position() == pos) {
				LoggerHelper.printWarn("BandOfArcana.gravitateEntities()", "IdenticalPosition", "Attempting to avoid NaN positional values for entity with UUID: "+ent.getStringUUID());
				continue; // Heres a funny joke: NaN
			}
			
			double dX = pos.x - ent.getX();
			double dY = pos.y - ent.getY();
			double dZ = pos.z - ent.getZ();
			double dist = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
			double vel = 5d - dist / 15d;
			if (vel > 0d) {
				amount++;
				vel *= vel;
				Vec3 addVec = new Vec3(dX / dist * vel * 0.1, dY / dist * vel * 0.1, dZ / dist * vel * 0.1);
				if (invert) addVec = addVec.reverse();
				if (ent instanceof ServerPlayer plr) {
					NetworkInit.toClient(new ModifyPlayerVelocityPacket(addVec, (byte)1), plr);
				} else {
					ent.setDeltaMovement(ent.getDeltaMovement().add(addVec));
				}
			}
		}
		return amount;
	}
	
	/**
	 * Frankenstein of Botania & ProjectE's divining rods, with some minor tweaks
	 * 
	 * @param stack the item doing the divining
	 * @param player player doing
	 * @param area AABB we are divining in
	 * @param max maximum amount of blocks that are allowed to be scanned
	 * @return
	 */
	public int diviningRod(ItemStack stack, Player player, AABB area, long max) {
		Level level = player.level;
		//long seedxor = level.random.nextLong();
		LongList emcValues = new LongArrayList();
		long totalEmc = 0;
		int numBlocks = 0;
		//Lazily retrieve the values for the furnace recipes
		NonNullLazy<List<SmeltingRecipe>> furnaceRecipes = NonNullLazy.of(() -> level.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING));
		
		int count = 0;
		for (BlockPos pos : WorldHelper.getPositionsFromBox(area)) {
			if (count >= max) break;
			count++;
			if (level.isEmptyBlock(pos)) {
				continue;
			}
			BlockState state = level.getBlockState(pos);
			if (level.isClientSide && player.isLocalPlayer()) {
				Block block = state.getBlock();
				if (state.is(IXplatAbstractions.INSTANCE.getOreTag())) {
					@SuppressWarnings("deprecation")
					Random rand = new Random(Registry.BLOCK.getKey(block).hashCode()/* ^ seedxor*/);
					WispParticleData data = WispParticleData.wisp(0.25F, rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 8, false);
					for (int i = 0; i < 8; i++) {
						level.addParticle(data, true, pos.getX() + level.random.nextFloat(), pos.getY() + level.random.nextFloat(), pos.getZ() + level.random.nextFloat(), 0, 0, 0);
					}
				}
			} else {
				List<ItemStack> drops = Block.getDrops(state, (ServerLevel) level, pos, WorldHelper.getBlockEntity(level, pos), player, stack);
				if (drops.isEmpty()) {
					continue;
				}
				ItemStack blockStack = drops.get(0);
				long blockEmc = EMCHelper.getEmcValue(blockStack);
				if (blockEmc == 0) {
					for (SmeltingRecipe furnaceRecipe : furnaceRecipes.get()) {
						if (furnaceRecipe.getIngredients().get(0).test(blockStack)) {
							long currentValue = EMCHelper.getEmcValue(furnaceRecipe.getResultItem());
							if (currentValue != 0) {
								if (!emcValues.contains(currentValue)) {
									emcValues.add(currentValue);
								}
								totalEmc += currentValue;
								break;
							}
						}
					}
				} else {
					if (!emcValues.contains(blockEmc)) {
						emcValues.add(blockEmc);
					}
					totalEmc += blockEmc;
				}
				numBlocks++;
			}
		}
		if (numBlocks != 0) {
			player.sendMessage(PELang.DIVINING_AVG_EMC.translate(numBlocks, totalEmc / numBlocks).withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.UNDERLINE, ChatFormatting.ITALIC), Util.NIL_UUID);
			long[] maxValues = new long[3];
			for (int i = 0; i < 3; i++) {
				maxValues[i] = 1;
			}
			emcValues.sort(LongComparators.OPPOSITE_COMPARATOR);
			int num = Math.min(emcValues.size(), 3);
			for (int i = 0; i < num; i++) {
				maxValues[i] = emcValues.getLong(i);
			}
			player.sendMessage(PELang.DIVINING_MAX_EMC.translate(maxValues[0]).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC), Util.NIL_UUID);
			player.sendMessage(PELang.DIVINING_SECOND_MAX.translate(maxValues[1]).withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC), Util.NIL_UUID);
			player.sendMessage(PELang.DIVINING_THIRD_MAX.translate(maxValues[2]).withStyle(ChatFormatting.GREEN, ChatFormatting.ITALIC), Util.NIL_UUID);
		}
		return count;
	}
	
	public boolean sentientArrowControl(SentientArrow arrow, Player player) {
		// try redirecting the arrow
		boolean foundTarget = arrow.attemptManualRetarget();
		player.level.playSound(null, player, EffectInit.ARCHANGELS_SENTIENT_YONDU.get(), SoundSource.PLAYERS, 1, player.getRandom().nextFloat(0.1f, 2f));
		if (foundTarget) {
			for (ServerPlayer plr : ((ServerLevel)player.level).players()) {
				Entity target = arrow.getTarget();
				BlockPos pos = plr.blockPosition();
				boolean nearOwner = pos.closerToCenterThan(player.getEyePosition(), 128);
				// owner -> arrow communicate
				if (nearOwner || pos.closerToCenterThan(arrow.getBoundingBox().getCenter(), 128)) {
					NetworkInit.toClient(new DrawParticleLinePacket(player.getEyePosition(), arrow.getBoundingBox().getCenter(), LineParticlePreset.SENTIENT_COMMUNICATE), plr);
				}
				// owner -> target tracer
				if (nearOwner || pos.closerToCenterThan(target.getBoundingBox().getCenter(), 128)) {
					NetworkInit.toClient(new DrawParticleLinePacket(player.getEyePosition(), target.getBoundingBox().getCenter(), LineParticlePreset.SENTIENT_RETARGET), plr);
				}
			}
		} else {
			// returning to owner
			for (ServerPlayer plr : ((ServerLevel)player.level).players()) {
				BlockPos pos = plr.blockPosition();
				boolean nearOwner = pos.closerToCenterThan(player.getEyePosition(), 128);
				// arrow -> owner tracer
				if (nearOwner || pos.closerToCenterThan(arrow.getBoundingBox().getCenter(), 128)) {
					NetworkInit.toClient(new DrawParticleLinePacket(player.getEyePosition(), arrow.getBoundingBox().getCenter(), LineParticlePreset.SENTIENT_RETARGET), plr);
				}
			}
		}
		return foundTarget;
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
	
	@Override
	public int getUseDuration(ItemStack stack) {
		return Integer.MAX_VALUE;
	}
	
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		switch (getMode(stack)) {
		
		case 2: // watch
		case 6: // archangels
			return UseAnim.BOW;
		
		default:
			return UseAnim.NONE;
		}
	}
	
	@Override
	public @Nullable ArmPose getArmPose(ItemStack stack, AbstractClientPlayer player, InteractionHand hand) {
		if (!player.swinging) {
			ArmPose pose = null;
			switch (getMode(stack)) {
			
			case 2: // Watch
				if (player.isUsingItem()) {
					pose = getWoft(stack) ? ArmPose.THROW_SPEAR : ArmPose.BLOCK;
				}
				break;
			
			case 6: // Archangels
				if (player.isUsingItem()) pose = ArmPose.SPYGLASS;
				break;
			
			default:
				break;
			}
			return pose;
		}
		return null;
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack before, ItemStack after, boolean slotChanged) {
		if (ItemNBTHelper.getBoolean(before, "boa_tickhighpitch", false) != ItemNBTHelper.getBoolean(after, "boa_tickhighpitch", false)
				|| CovalentCapabilityWrapper.getPool(before) != CovalentCapabilityWrapper.getPool(after)) {
			return false;
		}
		return super.shouldCauseReequipAnimation(before, after, slotChanged);
	}
	
	/**
	 * shoot lava orb funny<br>
	 * @param player the shooter
	 * @return if orb was sucessfully shot
	 */
	private boolean shootLavaProjectile(Player player) {
		long consumed = EmcHelper.consumeAvaliableEmc(player, 64);
		if (consumed >= 64) {
			player.level.playSound(null, player, EffectInit.LIQUID_LAVA_CREATE.get(), SoundSource.PLAYERS, 1, 1);
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
	
	private void windGust(ServerPlayer player) {
		byte factor = player.isOnGround() ? (byte)8 : !player.isFallFlying() ? (byte)4 : 2;
		Vec3 gust = player.getLookAngle().scale(factor);
		//NetworkInit.toClient(new ModifyPlayerVelocityPacket(gust, (byte)1), (ServerPlayer)player);
		AABB area = AABB.ofSize(player.getBoundingBox().getCenter().subtract(gust), factor/2, factor/2, factor/2).expandTowards(gust.scale(factor)); //player.getBoundingBox().inflate(2).expandTowards(gust);
		if (DebugCfg.GUST_HITBOX.get()) NetworkInit.toClient(new DrawParticleAABBPacket(new Vec3(area.minX, area.minY, area.minZ), new Vec3(area.maxX, area.maxY, area.maxZ), ParticlePreset.DEBUG), player);
		for (LivingEntity ent : player.level.getEntitiesOfClass(LivingEntity.class, area, ent -> canBeGusted(ent, player))) {
			if (ent instanceof ServerPlayer plr) {
				NetworkInit.toClient(new ModifyPlayerVelocityPacket(gust, (byte)1), plr);
			} else {
				ent.setDeltaMovement(ent.getDeltaMovement().add(gust));
			}
		}
		for (ServerPlayer plr : player.getLevel().players()) {
			if (plr.blockPosition().closerToCenterThan(area.getCenter(), 256d)) {
				NetworkInit.toClient(new GustParticlePacket((byte)(factor/2), area.getCenter(), gust), plr);
			}
		}
		player.level.playSound(null, player.blockPosition(), EffectInit.SWRG_BOOST.get(), SoundSource.PLAYERS, factor, factor/3f);
	}
	
	private boolean canBeGusted(LivingEntity ent, Player guster) {
		return ent.is(guster) || !EntityHelper.isImmuneToGravityManipulation(ent);
	}
	
	private void freezeEntity(LivingEntity ent, ServerPlayer culprit) {
		if (ent instanceof Stray) return;
		if (ent instanceof Skeleton skel) {
			skel.convertTo(EntityType.STRAY, true);
			WorldHelper.freezeInBoundingBox(ent.level, ent.getBoundingBox().inflate(1), culprit, false);
		    if (!skel.isSilent()) {
		        skel.level.levelEvent((Player)null, 1048, skel.blockPosition(), 0);
		    }
			return;
		} else if (ent instanceof Husk husk) {
			husk.convertTo(EntityType.ZOMBIE, true);
			if (!husk.isSilent()) {
				husk.level.levelEvent((Player)null, 1041, husk.blockPosition(), 0);
			}
		}
		ent.clearFire();
		ent.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 100));
		WorldHelper.freezeInBoundingBox(ent.level, ent.getBoundingBox().inflate(1), culprit, false);
		ent.hurt(DamageSource.FREEZE, ent instanceof Blaze || ent instanceof MagmaCube ? Float.MAX_VALUE : 1);
	}
	
	private void burnEntity(LivingEntity ent, ServerPlayer culprit) {
		if (ent instanceof Blaze || ent instanceof MagmaCube) return;
		if (ent instanceof Stray stray) {
			stray.convertTo(EntityType.SKELETON, true);
		    if (!stray.isSilent()) {
				ent.level.playSound(null, stray, SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE, 1, 1);
		    }
		} else if (ent instanceof Skeleton skeleton) {
			skeleton.convertTo(EntityType.WITHER_SKELETON, true);
		    if (!skeleton.isSilent()) {
				ent.level.playSound(null, skeleton, SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE, 1, 1);
		    }
		    return;
		} else if (ent instanceof Zombie zombie && !(ent instanceof ZombifiedPiglin)) {
			if (!(zombie instanceof Husk)) {
				if (zombie instanceof Drowned) {
					zombie.convertTo(EntityType.ZOMBIE, true);
				} else {
					zombie.convertTo(EntityType.HUSK, true);
				}
				ent.level.playSound(null, zombie, SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE, 1, 1);
			}
		};
		ent.setRemainingFireTicks(1200);
		MiscHelper.burnInBoundingBox(ent.level, ent.getBoundingBox().inflate(1), culprit, false);
		ent.hurt(MGTKDmgSrc.mustang(culprit), ent instanceof SnowGolem ? Float.MAX_VALUE : 8);
	}
	
	private SmallFireball fireball(Level level, Vec3 pos, Vec3 target, @Nullable LivingEntity owner) {
        double dist = pos.distanceToSqr(target);//owner.distanceToSqr(target);
        Vec3 shootVec = new Vec3(
        	target.x - pos.x,
        	target.y - pos.y,
        	target.z - pos.z
        );
        double acc = Math.sqrt(Math.sqrt(dist)) * 0.25;
		SmallFireball fb = new SmallFireball(level, owner,
				shootVec.x + owner.getRandom().nextGaussian() * acc,
				shootVec.y + owner.getRandom().nextGaussian() * acc,
				shootVec.z + owner.getRandom().nextGaussian() * acc);
		fb.setPos(pos);
		level.addFreshEntity(fb);
		return fb;
	}
	
	private PrimedTnt tnt(Level level, Vec3 pos, Vec3 vel, @Nullable LivingEntity owner) {
		PrimedTnt tnt = new PrimedTnt(level, pos.x, pos.y, pos.z, owner);
		tnt.setDeltaMovement(tnt.getDeltaMovement().add(vel));
		level.addFreshEntity(tnt);
		return tnt;
	}
	
	@Nullable
	private LightningBolt smite(Level level, Vec3 pos, @Nullable ServerPlayer culprit, boolean harmless) {
		LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
		if (bolt != null) {
			bolt.moveTo(pos);
			bolt.setCause(culprit);
			bolt.setVisualOnly(harmless);
			level.addFreshEntity(bolt);
		}
		return bolt;
	}
	
	private int extinguishAoe(Player player, AABB area, long limit) {
		Vec3 min = new Vec3(area.minX, area.minY, area.minZ),
			max = new Vec3(area.maxX, area.maxY, area.maxZ);
		
		int amount = 0;
		if (!player.level.isClientSide) {
			for (BlockPos pos : BlockPos.betweenClosed(new BlockPos(min), new BlockPos(max))) {
				if (amount >= limit) break;
				pos = pos.immutable();
				if (player.level.getBlockState(pos).getBlock() == Blocks.FIRE && PlayerHelper.hasBreakPermission((ServerPlayer)player, pos)) {
					player.level.removeBlock(pos, false);
		            player.level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (player.level.random.nextFloat() - player.level.random.nextFloat()) * 0.8f);
					amount++;
				}
			}
			if (amount < limit) {
				for (Entity ent : player.level.getEntitiesOfClass(Entity.class, area, ent -> canBeExtinguished(ent))) {
					if (amount >= limit) break;
					if (ent instanceof PrimedTnt || ent instanceof Fireball) {
						ItemStack drop;
						if (ent instanceof Fireball fb) {
							drop = fb.getItem();
						} else {
							drop = ent.getPickedResult(null);
							if (drop == ItemStack.EMPTY) {
								drop = new ItemStack(Items.TNT);
							}
						}
						ent.spawnAtLocation(drop);
						ent.discard();
					} else if (ent instanceof MinecartTNT cart) {
						// bad!!!!
						MinecartTNT newCart = new MinecartTNT(cart.level, cart.getX(), cart.getY(), cart.getZ());
						newCart.setDeltaMovement(cart.getDeltaMovement());
						newCart.setXRot(cart.getXRot());
						newCart.setYRot(cart.getYRot());
						newCart.setDamage(cart.getDamage());
						newCart.setCanUseRail(cart.canUseRail());
						if (cart.hasCustomDisplay()) {
							newCart.setCustomDisplay(true);
							newCart.setDisplayBlockState(cart.getDisplayBlockState());
							newCart.setDisplayOffset(cart.getDisplayOffset());
						}
						if (cart.hasCustomName()) {
							newCart.setCustomName(cart.getCustomName());
							newCart.setCustomNameVisible(cart.isCustomNameVisible());
						}
						newCart.setSilent(cart.isSilent());
						newCart.setHurtTime(cart.getHurtTime());
						newCart.setHurtDir(cart.getHurtDir());
						cart.discard();
						ent.level.addFreshEntity(newCart);
					} else if (ent instanceof EntityManaStorm storm) {
						storm.discard();
					} else if (ent instanceof Creeper creeper) {
						// this seems dumb but it works?
						CompoundTag tag = creeper.getPersistentData();
						tag.putByte("ExplosionRadius", (byte) 0);
						tag.putShort("Fuse", Short.MAX_VALUE);
						creeper.readAdditionalSaveData(tag);
					} else {
						ent.clearFire();
					}
					amount++;
				}
			}
		}
		return amount;
	}
	
	private boolean canBeExtinguished(Entity ent) {
		return !ent.isRemoved()
				&& (ent instanceof PrimedTnt
					|| ent instanceof EntityManaStorm
					|| (ent instanceof MinecartTNT cart && cart.isPrimed())
					|| ent instanceof Fireball
					|| ent instanceof Creeper
					|| (ent.getRemainingFireTicks() > 0 && !(ent.fireImmune() || ent.isInLava()))
		);
	}
	
	private boolean canBeSmitten(Entity ent) {
		return ent instanceof LivingEntity
				&& !EntityHelper.isInvincible(ent);
	}
	
	private boolean canBeFrozen(Entity ent) {
		return ent instanceof LivingEntity
				&& !EntityHelper.isInvincible(ent)
				&& !ent.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES);
	}
	
	private boolean canBeBurnt(Entity ent) {
		return ent instanceof LivingEntity
				&& !EntityHelper.isInvincible(ent)
				&& !ent.fireImmune();
	}
	
	private HitResult swrgSuperSmite(Player player, Level level) {
		Vec3 pos1 = player.getEyePosition();
		Vec3 ray = player.getLookAngle().scale(120);
		Vec3 pos2 = pos1.add(ray);
		HitResult hitRes = ProjectileUtil.getEntityHitResult(player, pos1, pos2, AABB.ofSize(pos1, 0.05, 0.05, 0.05).expandTowards(ray).inflate(2), this::canBeSmitten, 0);
		if (hitRes != null && hitRes.getType() == HitResult.Type.ENTITY) {
			Entity ent = ((EntityHitResult)hitRes).getEntity();
			for (int i = 0; i < (level.isThundering() ? 9 : 3) ; i++) {
				level.playSound(null, smite(level, ent.position(), (ServerPlayer)player, false).blockPosition(), EffectInit.SWRG_SMITE.get(), SoundSource.PLAYERS, 1, 1);
			}
			// ding sound effect because cool
			((ServerPlayer)player).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
			ent.invulnerableTime = 0;
			ent.hurt(DamageSource.LIGHTNING_BOLT, level.isThundering() ? 81 : 9);
		} else {
			hitRes = PlayerHelper.getBlockLookingAt(player, 120);
			if (hitRes != null && hitRes.getType() == HitResult.Type.BLOCK) {
				for (int i = 0; i < (level.isThundering() ? 9 : 3) ; i++) {
					level.playSound(null, smite(level, hitRes.getLocation(), (ServerPlayer)player, false).blockPosition(), EffectInit.SWRG_SMITE.get(), SoundSource.PLAYERS, 1, 1);
				}
			}
		}
		return hitRes;
	}
	
	private void swrgSuperSmitePos(Player player, Level level, Vec3 pos) {
		for (int i = 0; i < (level.isThundering() ? 9 : 3) ; i++) {
			level.playSound(null, smite(level, pos, (ServerPlayer)player, false).blockPosition(), EffectInit.SWRG_SMITE.get(), SoundSource.PLAYERS, 1, 1);
		}
	}
	
	private void swrgSuperSmiteEnt(Player player, Level level, Entity victim) {
		for (int i = 0; i < (level.isThundering() ? 9 : 3) ; i++) {
			level.playSound(null, smite(level, victim.position(), (ServerPlayer)player, false).blockPosition(), EffectInit.SWRG_SMITE.get(), SoundSource.PLAYERS, 1, 1);
		}
		victim.invulnerableTime = 0;
		victim.hurt(DamageSource.LIGHTNING_BOLT, level.isThundering() ? 81 : 9);
	}
	


	/**
	 * 
	 * @param player
	 * @return if projectile was shot
	 */
	public static boolean shootMustang(Player player, ItemStack stack) {
		if (player.level.isRainingAt(player.blockPosition())) {
			player.level.playSound(null, player, EffectInit.IGNITION_CLICK.get(), SoundSource.PLAYERS, 1f, 1.7f);
			player.level.playSound(null, player, SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 0.5f, 2f);
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
		player.level.playSound(null, player, EffectInit.IGNITION_CLICK.get(), SoundSource.PLAYERS, 1f, 1.7f);
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
						ent.hurt(MGTKDmgSrc.mustang(culprit), (float) Math.pow(((int)((invDist * invDist + invDist) / 2.0D * 7.0D * 8d + 1.0D)), 2));
					}
				}
			}
		}
		
		// screwing with blocks
		// we keep track of vaporized to do particles
		Stack<BlockPos> vaporized = new Stack<BlockPos>();
		BlockPos.betweenClosedStream(box).forEach(bPos -> {
			
			// debug: marks all for paticles
			if (DebugCfg.MUSTANG_HITBOX.get()) vaporized.push(bPos);
			
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
				if ((rand.nextInt(3) != 0  && !level.getBlockState(bPos).is(ObjectInit.AIR_ICE.get())) && level.getBlockState(bPos).is(BlockTags.ICE)) return;
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
		level.playSound(null, bCent, EffectInit.IGNITION_BURN.get(), SoundSource.NEUTRAL, 3f, 1f);
		if (DebugCfg.MUSTANG_HITBOX.get()) {
			
			//// drawing the hitbox
			for (ServerPlayer plr : level.players()) {
				if (plr.blockPosition().closerToCenterThan(cent, 512d)) {
					Vec3 minCorner = new Vec3(box.minX, box.minY, box.minZ);
					Vec3 maxCorner = new Vec3(box.maxX, box.maxY, box.maxZ);
					NetworkInit.toClient(new DrawParticleAABBPacket(minCorner, maxCorner, ParticlePreset.DEBUG), plr);
				}
			}
			
			// center and blockcenter
			level.sendParticles(ParticleTypes.DRIPPING_HONEY, cent.x(), cent.y(), cent.z(), 1, 0, 0, 0, 0);
			level.sendParticles(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, bCent.getX(), bCent.getY(), bCent.getZ(), 1, 0, 0, 0, 0);
			
			// marking every individual block
			while (!vaporized.empty()) {
				Vec3 pos = Vec3.atCenterOf(vaporized.pop());
				level.sendParticles(ParticleTypes.DRIPPING_WATER, pos.x(), pos.y(), pos.z(), 1, 0, 0, 0, 0);
			}
		}
		
		// badass does immolation
		for (ServerPlayer plr : level.players()) {
			if (plr.is(culprit) || plr.blockPosition().closerToCenterThan(cent, 256d)) {
				// :(
				NetworkInit.toClient(new MustangExplosionPacket(cent.x, cent.y, cent.z), plr);
			}
		}
		
		// steam from the steamed clams were having
		while (!vaporized.empty() && !DebugCfg.MUSTANG_HITBOX.get()) {
			BlockPos bPos = vaporized.pop();
			level.playSound(null, bPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.1F, 2.6F + (rand.nextFloat() - rand.nextFloat()) * 0.8F);
			level.sendParticles(ParticleTypes.CLOUD, bPos.getX(), bPos.getY(), bPos.getZ(), 4, 0, 0, 0, 0.3);
		}
	}
	/**
	 * speeds up time near a player. gets stronger the longer it is active <br>
	 * should be called every tick while accelerating <br>
	 * effects of accelerated time include: <p>
	 * <li> movement / attack speed increase for player
	 * <li> player gets reduced i-frames
	 * <li> slowing of other nearby creatures (except players)
	 * <li> block entity tick acceleration
	 * <li> fast-forwarding of world time
	 * 
	 * @param player The player causing the acceleration
	 * @param stack item that is performing the acceleration
	 * @param potency A multiplier for the strength of the effects
	 * @param tick How long the effect has been active, in ticks
	 * @param max Number of ticks where the effect caps out (stops getting stronger)
	 * @param size side length of the AOE box. if <= 0 will disable all AOE effects
	 */
	@SuppressWarnings("unchecked")
	public void jojoReference(Player player, ItemStack stack, double potency, int tick, int max, int size, long plrEmc) {
		for (Attribute attribute : getTimeAccelAttributes()) {
			player.getAttribute(attribute).removeModifier(TIME_ACCEL_UUID);
		} // clearing old modifiers to make room for updated ones
		double curPow = Math.min(1d, (double)tick/(double)max);
		double selfSpeedMult = curPow * potency + (10d/3d-1d); // 10/3-1 cancels out the movespeed penalty when using an item
		for (Attribute attribute : getTimeAccelAttributes()) {
			player.getAttribute(attribute).addTransientModifier(new AttributeModifier(
				TIME_ACCEL_UUID,
				"magitekkit:time_acceleration",
				selfSpeedMult,
				Operation.MULTIPLY_TOTAL
			));
		}
		player.invulnerableTime = Math.max(0, player.invulnerableTime - (int)(10*(curPow)));
		// sound interval approach 1 per tick
		if (tick % (int)(20 - (19*curPow)) == 0) {
			boolean highPitch = ItemNBTHelper.getBoolean(stack, "boa_tickhighpitch", false);
			stack.setPopTime(3);
			player.level.playSound(null, player, EffectInit.WOFT_TICK.get(), SoundSource.PLAYERS, 1, highPitch ? 2f : 1);
			ItemNBTHelper.setBoolean(stack, "boa_tickhighpitch", !highPitch); // storing an nbt tag to know what pitch the sound should be is *totally* the best way of doing it
		}
		// aoe stuff
		if (size > 0) {
			double mobSlow = Math.min(potency, 1 - curPow);
			Vec3 slowVec = new Vec3(mobSlow, mobSlow, mobSlow);
			AABB aoe = AABB.ofSize( player.position(), size, size, size);
			
			for (LivingEntity ent : player.level.getEntitiesOfClass(LivingEntity.class, aoe, entity -> !EntityHelper.isImmuneToGravityManipulation(entity))) {
				if (!ent.level.isClientSide && ent instanceof ServerPlayer plr) {
					NetworkInit.toClient(new ModifyPlayerVelocityPacket(slowVec, (byte)2), plr);
				}
				else if ( !(ent instanceof Player) ) ent.setDeltaMovement(ent.getDeltaMovement().multiply(slowVec));
			}
			Level level = player.level;
			if (!level.isClientSide()) {
			// most of the stuff inside this if() is taken directly from ProjectE's TimeWatch.java

				int extraTicks = (int) (ProjectEConfig.server.effects.timePedBonus.get() * (potency/30));
				long toConsume = 0;
				for (BlockEntity blockEntity : WorldHelper.getBlockEntitiesWithinAABB(level, aoe)) {
					if (toConsume > plrEmc) break;
					else if (!blockEntity.isRemoved() && !BlockEntities.BLACKLIST_TIME_WATCH_LOOKUP.contains(blockEntity.getType())) {
						BlockPos pos = blockEntity.getBlockPos();
						if (level.shouldTickBlocksAt(ChunkPos.asLong(pos))) {
							LevelChunk chunk = level.getChunkAt(pos);
							RebindableTickingBlockEntityWrapper tickingWrapper = chunk.tickersInLevel.get(pos);
							if (tickingWrapper != null && !tickingWrapper.isRemoved()) {
								if (tickingWrapper.ticker instanceof @SuppressWarnings("rawtypes") BoundTickingBlockEntity tickingBE) {
									//In general this should always be the case, so we inline some of the logic
									// to optimize the calls to try and make extra ticks as cheap as possible
									if (chunk.isTicking(pos)) {
										ProfilerFiller profiler = level.getProfiler();
										profiler.push(tickingWrapper::getType);
										BlockState state = chunk.getBlockState(pos);
										if (blockEntity.getType().isValid(state)) {
											for (int i = 0; i < extraTicks && plrEmc >= WOFT.TICKACCEL.get(); i++) {
												toConsume += WOFT.TICKACCEL.get();
												tickingBE.ticker.tick(level, pos, state, blockEntity);
											}
										}
										profiler.pop();
									}
								} else {
									//Fallback to just trying to make it tick extra
									for (int i = 0; i < extraTicks && plrEmc >= WOFT.TICKACCEL.get(); i++) {
										toConsume += WOFT.TICKACCEL.get();
										tickingWrapper.tick();
									}
								}
							}
						}
					}
				}
				plrEmc -= EmcHelper.consumeAvaliableEmc(player, toConsume);
				toConsume = 0;
				
				// random ticks brr
				for (BlockPos pos : WorldHelper.getPositionsFromBox(aoe)) {
					if (plrEmc < WOFT.TICKACCEL.get()) break;
					else if (WorldHelper.isBlockLoaded(level, pos)) {
						BlockState state = level.getBlockState(pos);
						Block block = state.getBlock();
						if (state.isRandomlyTicking() && !state.is(PETags.Blocks.BLACKLIST_TIME_WATCH)
							&& !(block instanceof LiquidBlock) // Don't speed non-source fluid blocks - dupe issues
							&& !(block instanceof BonemealableBlock) && !(block instanceof IPlantable)) {// All plants should be sped using Harvest Goddess
							pos = pos.immutable();
							for (int i = 0; i < extraTicks && plrEmc >= WOFT.TICKACCEL.get(); i++) {
								toConsume += WOFT.TICKACCEL.get();
								state.randomTick((ServerLevel)level, pos, level.random);
							}
						}
					}
				}
				plrEmc -= EmcHelper.consumeAvaliableEmc(player, toConsume);
				
				// world time acceleration
				// TODO: make the sun/moon not teleport
				if (level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
					ServerLevel serverWorld = (ServerLevel) level;
					serverWorld.setDayTime(Math.min(level.getDayTime() + extraTicks, Long.MAX_VALUE));
				}
			}
		}
	}
	
	/**
	 * Exists to make adding/removing attributes from time acceleration easy in the future <br>
	 * Would just make it a static final array but SWIM_SPEED is registered after items
	 * @return Attributes that TimeAccel modifies
	 */
	private static Attribute[] getTimeAccelAttributes() {
		Attribute[] attribs = {
				Attributes.ATTACK_SPEED,
				Attributes.MOVEMENT_SPEED,
				ForgeMod.SWIM_SPEED.get()
		};
		return attribs;
	}
	
	/**
	 * Transforms an entity into a random item. Some entities are immune for technical reasons. <br>
	 * For most entities, possible items are chosen from their loot table, but there are some special exceptions. <br>
	 * There are also a few 'universal' items, which are always in the list of possible items.
	 * 
	 * @return if the entity was actually transformed
	 */
	public static boolean entityItemizer(LivingEntity entity, @Nullable Entity culprit, @Nullable Entity cause) {
		if (!entity.getType().is(MGTKEntityTags.ITEMIZER_ENTITY_BLACKLIST)) {
			if (entity instanceof Player player) {
				// TODO: something a bit more interesting than this
				if (player.getHealth() < player.getMaxHealth()/2 || !GemJewelryBase.isBarrierActive(player)) {
					player.setLastHurtByPlayer(null);
					player.hurt(MGTKDmgSrc.strongTransmutation(culprit), Float.MAX_VALUE);
				} else return false;
			}
			List<ItemStack> possible = new ArrayList<>();
			
			for (Item[] cat : ITEMIZER_DEFAULTS) {
				// we take a random item from each category and make itemstack with it, which goes on the list
				possible.add( new ItemStack(cat[entity.getRandom().nextInt(cat.length)]) );
			}
			
			// getting things from entity loot table
			ResourceLocation resourcelocation = entity.getLootTable();
			LootTable loottable = entity.level.getServer().getLootTables().get(resourcelocation);
			LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel)entity.level))
					.withRandom(entity.getRandom()).withParameter(LootContextParams.THIS_ENTITY, entity)
					.withParameter(LootContextParams.ORIGIN, entity.position())
					.withParameter(LootContextParams.DAMAGE_SOURCE, MGTKDmgSrc.TRANSMUTATION_POTION)
					.withOptionalParameter(LootContextParams.KILLER_ENTITY, culprit)
					.withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, cause);
			if (culprit instanceof Player player) {
				lootcontext$builder = lootcontext$builder
						.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
						.withLuck(player.getLuck());
			}
			LootContext ctx = lootcontext$builder.create(LootContextParamSets.ENTITY);
			for (ItemStack stack : loottable.getRandomItems(ctx)) {
				stack.setCount(1);
				possible.add(stack);
			}
			
			// entities with special drops
			if (entity instanceof EntityDoppleganger gaia) {
				if (gaia.isHardMode()) {
					possible.add(new ItemStack(ModItems.gaiaIngot));
					possible.add(new ItemStack(ModItems.dice));
					possible.add(ItemDice.RELIC_STACKS.get().get(entity.getRandom().nextInt(ItemDice.RELIC_STACKS.get().size())).copy());
				} else {
					possible.add(new ItemStack(ModItems.terrasteel));
				}
				possible.add(new ItemStack(ModItems.ancientWillAhrim));
				possible.add(new ItemStack(ModItems.ancientWillDharok));
				possible.add(new ItemStack(ModItems.ancientWillGuthan));
				possible.add(new ItemStack(ModItems.ancientWillKaril));
				possible.add(new ItemStack(ModItems.ancientWillTorag));
				possible.add(new ItemStack(ModItems.ancientWillVerac));
				possible.add(new ItemStack(ModItems.blackerLotus));
				possible.add(new ItemStack(ModBlocks.gaiaHead));
			} else if (entity instanceof WitherBoss) {
				possible.add(new ItemStack(Items.NETHER_STAR));
				possible.add(new ItemStack(Items.WITHER_SKELETON_SKULL));
				possible.add(new ItemStack(Items.BEACON));
				possible.add(new ItemStack(Items.END_CRYSTAL));
			}
			
			if (entity.hasCustomName()) {
				possible.add(new ItemStack(Items.NAME_TAG).setHoverName(entity.getDisplayName()));
			}
			
			// we spawn one of the stacks in possible, chosen randomly, then delete entity
			ItemStack resultItem = possible.get(entity.getRandom().nextInt(possible.size()));

			//////////
			// ported from Bookshelf 1.12 code
			final ListTag loreList = new ListTag();
			String type = new TranslatableComponent(entity.getType().toString()).getString();
			if (entity instanceof Player || entity.hasCustomName() && !resultItem.is(Items.NAME_TAG)) {
				String name = entity.getDisplayName().getString();
				loreList.addTag(0, StringTag.valueOf("{\"text\":\"Formerly '"+name+"' ("+type+")\"}"));
			} else {
				loreList.addTag(0, StringTag.valueOf("{\"text\":\"Formerly "+type+"\"}"));
			}
	        if (!resultItem.hasTag()) {
	        	resultItem.setTag(new CompoundTag());
	        }
	        final CompoundTag tag = resultItem.getTag();
	        if (!tag.contains("display", 10)) {
	            tag.put("display", new CompoundTag());
	        }
	        
			final CompoundTag displayTag = tag.getCompound("display");
	        displayTag.put("Lore", loreList);
			//////////
			
			entity.spawnAtLocation(resultItem);
			entity.discard();
			return true;
		}
		return false;
	}
}
