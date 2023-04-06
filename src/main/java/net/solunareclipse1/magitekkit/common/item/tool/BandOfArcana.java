package net.solunareclipse1.magitekkit.common.item.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.item.CustomArmPoseItem;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.NameTagItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
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
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.NonNullLazy;
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
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.PETags.BlockEntities;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;

import net.solunareclipse1.magitekkit.api.capability.wrapper.ChargeItemCapabilityWrapperButBetter;
import net.solunareclipse1.magitekkit.api.capability.wrapper.converter.ManaCovalentCapabilityWrapper;
import net.solunareclipse1.magitekkit.api.item.ISwingItem;
import net.solunareclipse1.magitekkit.common.entity.projectile.FreeLavaProjectile;
import net.solunareclipse1.magitekkit.common.entity.projectile.SentientArrow;
import net.solunareclipse1.magitekkit.common.entity.projectile.SmartArrow;
import net.solunareclipse1.magitekkit.common.item.MGTKItem;
import net.solunareclipse1.magitekkit.common.item.armor.gem.GemJewelryBase;
import net.solunareclipse1.magitekkit.common.misc.MGTKDmgSrc;
import net.solunareclipse1.magitekkit.data.MGTKEntityTags;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.init.NetworkInit;
import net.solunareclipse1.magitekkit.init.ObjectInit;
import net.solunareclipse1.magitekkit.network.packet.client.CreateLoopingSoundPacket;
import net.solunareclipse1.magitekkit.network.packet.client.DrawParticleAABBPacket;
import net.solunareclipse1.magitekkit.network.packet.client.DrawParticleLinePacket;
import net.solunareclipse1.magitekkit.network.packet.client.ModifyPlayerVelocityPacket;
import net.solunareclipse1.magitekkit.network.packet.client.MustangExplosionPacket;
import net.solunareclipse1.magitekkit.util.Constants.Cooldowns;
import net.solunareclipse1.magitekkit.util.Constants.EmcCosts;
import net.solunareclipse1.magitekkit.util.Constants.Xp;
import net.solunareclipse1.magitekkit.util.Constants;
import net.solunareclipse1.magitekkit.util.EmcHelper;
import net.solunareclipse1.magitekkit.util.EntityHelper;
import net.solunareclipse1.magitekkit.util.MiscHelper;
import net.solunareclipse1.magitekkit.util.PlrHelper;
import net.solunareclipse1.magitekkit.util.ProjectileHelper;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongComparators;
import it.unimi.dsi.fastutil.longs.LongList;
import morph.avaritia.handler.ArmorHandler;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.mana.BurstProperties;
import vazkii.botania.api.mana.ILensEffect;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.tile.ModTiles;
import vazkii.botania.common.entity.EntityDoppleganger;
import vazkii.botania.common.entity.EntityManaBurst;
import vazkii.botania.common.handler.ModSounds;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.relic.ItemDice;
import vazkii.botania.xplat.IXplatAbstractions;

public class BandOfArcana extends MGTKItem
	implements IModeChanger, IItemCharge, IProjectileShooter, IExtraFunction, ISwingItem, ILensEffect, CustomArmPoseItem {

	//////////////////////////////////////////////
	// CONSTANTS, GLOBAL VARS, AND CONSTRUCTORS //
	//////////////////////////////////////////////
	public static final Vec3 FORWARD_RELATIVE = new Vec3(0,0,1);
	public static final String TAG_MODE = "boa_mode";
	public static final String TAG_EXP = "boa_experience";
	public static final String TAG_LVL = "boa_levels";
	public static final String TAG_LIQUID = "boa_liquid";
	public static final String TAG_WOFT = "boa_woft";
	public static final String TAG_OFFENSIVE = "boa_offensive";
	public static final String TAG_ARROWTRACKER = "boa_arrowtracker";
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
	
	
	
	
	
	
	
	
	
	
	//////////////////////////////////////////////////////////////
	// KEYPRESS HANDLING, ABILITIES DEFINING, AND FUNCTIONALITY //
	//////////////////////////////////////////////////////////////
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (!selected) {
			if ( entity instanceof Player player && !(player.isUsingItem() && player.getUseItem().equals(stack)) ) {
				for (Attribute attribute : getTimeAccelAttributes()) {
					player.getAttribute(attribute).removeModifier(TIME_ACCEL_UUID);
				}
			}
		}
	}
	
	// IExtraFunction (C)
	@Override
	public boolean doExtraFunction(@NotNull ItemStack stack, @NotNull Player player, @Nullable InteractionHand hand) {
		//this.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
		if (!player.level.isClientSide && GemJewelryBase.fullPristineSet(player) && getCharge(player.getItemInHand(hand)) == 1) {
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
				
			case 2: // Watch (ender chest / toggle local/global time manip)
				if (!player.isUsingItem()) {
					if (player.isShiftKeyDown()) {
						changeWoft(stack);
						player.level.playSound(null, player, EffectInit.WOFT_MODE.get(), SoundSource.PLAYERS, 1, 1.4f);
						didDo = true;
					} else {
						// thanks botania :D
						player.openMenu(new SimpleMenuProvider((windowId, playerInv, p) -> {
							return ChestMenu.threeRows(windowId, playerInv, p.getEnderChestInventory());
						}, stack.getHoverName()));
						player.level.playSound(null, player, SoundEvents.ENDER_CHEST_OPEN, SoundSource.PLAYERS, 1F, 1f);
						player.level.playSound(null, player, SoundEvents.ENDER_CHEST_CLOSE, SoundSource.PLAYERS, 1F, 1f);
						didDo = true;
					}
				}
				break;
				
			case 3: // Harvest (aoe harvest & grow)
				if (plrEmc >= EmcCosts.BOA_BONEMEAL) {
					EmcHelper.consumeAvaliableEmc(player, EmcCosts.BOA_BONEMEAL);
					WorldHelper.growNearbyRandomly(true, player.level, player.blockPosition(), player);
					didDo = true;
				}
				break;
				
			case 4: // Liquid (swap current liquid)
				changeLiquid(stack);
				player.level.playSound(null, player, getLiquid(stack) ? EffectInit.LIQUID_LAVA_SWITCH.get() : EffectInit.LIQUID_WATER_SWITCH.get(), SoundSource.PLAYERS, 1, 0.7f);
				didDo = true;
				break;
				
			case 5: // Philo (crafting grid)
				player.level.playSound(null, player, EffectInit.PHILO_3X3GUI.get(), SoundSource.PLAYERS, 1, 2f);
				didDo = PEItems.PHILOSOPHERS_STONE.get().doExtraFunction(stack, player, hand);
				break;
				
			case 6: // Archangels (scatter smart arrows)
				if (plrEmc >= EmcCosts.BOA_ARROW && !player.getCooldowns().isOnCooldown(PEItems.ARCHANGEL_SMITE.get())) {
					int shot;
					for (shot = 0; shot < (player.isOnGround() ? 28 : 56); shot++) {
						if ((shot+1)*EmcCosts.BOA_ARROW >= plrEmc) break;
						else {
							SmartArrow arrow = new SmartArrow(player.level, player, 1);
							if (!player.isOnGround()) {
								arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 0.5f, 300);
							} else {
								arrow.shootFromRotation(player, -90, 0, 0, 0.5f, 75);
							}
							player.level.addFreshEntity(arrow);
							player.level.playSound(null, player.position().x(), player.position().y(), player.position().z(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, Math.min(2, 1.0F / (player.level.random.nextFloat() * 0.4F + 1.2F) + (1 / 3) * 0.5F));
							didDo = true;
						}
					}
					if (didDo) {
						EmcHelper.consumeAvaliableEmc(player, EmcCosts.BOA_ARROW*shot);
						player.getCooldowns().addCooldown(PEItems.ARCHANGEL_SMITE.get(), shot/2);
					}
				}
				break;
				
			case 7: // SWRG (aoe smite)
				if (player instanceof ServerPlayer) {
					if (plrEmc >= EmcCosts.BOA_LIGHTNING && !player.getCooldowns().isOnCooldown(PEItems.SWIFTWOLF_RENDING_GALE.get())) {
						long consumed = EmcHelper.consumeAvaliableEmc(player, MiscHelper.smiteAllInArea(player.level, player.getBoundingBox().inflate(24), (ServerPlayer) player, plrEmc, EmcCosts.BOA_LIGHTNING));
						if (consumed > 0) {
							didDo = true;
							player.level.playSound(null, player.blockPosition(), EffectInit.SWRG_SMITE.get(), SoundSource.PLAYERS, 1f, 1f);
							player.getCooldowns().addCooldown( PEItems.SWIFTWOLF_RENDING_GALE.get(), (int)(10*(consumed/EmcCosts.BOA_LIGHTNING)) );
						} else player.playSound(PESounds.UNCHARGE, 1, 2);
					}
				}
				break;
				
			case 8: // Zero (aoe freeze)
				if (player instanceof ServerPlayer) {
					if (plrEmc >= EmcCosts.BOA_TEMPERATURE && !player.getCooldowns().isOnCooldown(PEItems.ZERO_RING.get())) {
						player.level.playSound(null, player.blockPosition(), PESoundEvents.POWER.get(), SoundSource.PLAYERS, 1f, 1f);
						didDo = EmcHelper.consumeAvaliableEmc(player, MiscHelper.slowAllInArea(player.level, player.getBoundingBox().inflate(24), (ServerPlayer) player, plrEmc, EmcCosts.BOA_TEMPERATURE)) > 0;
						player.getCooldowns().addCooldown(PEItems.ZERO_RING.get(), Cooldowns.BOA_TEMPERATURE_AOE);
					}
				}
				break;
				
			case 9: // Ignition (aoe burn)
				if (player instanceof ServerPlayer) {
					if (plrEmc >= EmcCosts.BOA_TEMPERATURE && !player.getCooldowns().isOnCooldown(PEItems.IGNITION_RING.get())) {
						player.level.playSound(null, player.blockPosition(), EffectInit.IGNITION_CLICK.get(), SoundSource.PLAYERS, 1f, 1f);
						didDo = EmcHelper.consumeAvaliableEmc(player, MiscHelper.burnAllInArea(player.level, player.getBoundingBox().inflate(24), (ServerPlayer) player, plrEmc, EmcCosts.BOA_TEMPERATURE)) > 0;
						player.getCooldowns().addCooldown(PEItems.IGNITION_RING.get(), Cooldowns.BOA_TEMPERATURE_AOE);
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
		if (!player.level.isClientSide() && GemJewelryBase.fullPristineSet(player) && getCharge(player.getItemInHand(hand)) == 1) {
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
				
			case 6: // Archangels (sentient arrow)
				//if (plrEmc >= 1024 && !cooldown.isOnCooldown(PEItems.ARCHANGEL_SMITE.get())) {
				//	ProjectileHelper.shootArrow(player.level, player, 10, 10, 0, Byte.MAX_VALUE, true, false, Pickup.CREATIVE_ONLY);
				//	EmcHelper.consumeAvaliableEmc(player, 1024);
				//	cooldown.addCooldown(PEItems.ARCHANGEL_SMITE.get(), 5);
				//	didDo = true;
				//}
				if (hasTrackedArrow(stack)) {
					SentientArrow arrow = getTrackedArrow(stack, player.level);
					if (arrow == null) {
						// arrow doesnt exist, stop trackingt
						resetTrackedArrow(stack);
					} else if (!player.getCooldowns().isOnCooldown(PEItems.ARCHANGEL_SMITE.get())) {
						// try redirecting the arrow
						if (arrow.manualRedirectByOwner(true)) {
							// success
							AllSoundEvents.WHISTLE_TRAIN_MANUAL.playFrom(player, 1, 3);
							//player.level.playSound(null, player, AllSoundEvents.WHISTLE_HIGH, SoundSource.PLAYERS, 1, 1);
							for (ServerPlayer plr : ((ServerLevel)player.level).players()) {
								if (plr.blockPosition().closerToCenterThan(player.position(), 64)) {
									NetworkInit.toClient(new DrawParticleLinePacket(player.getEyePosition(), arrow.getBoundingBox().getCenter(), 4), plr);
									NetworkInit.toClient(new DrawParticleLinePacket(player.getEyePosition(), arrow.getTarget().getBoundingBox().getCenter(), 4), plr);
								}
							}
							didDo = true;
						}
						player.getCooldowns().addCooldown(PEItems.ARCHANGEL_SMITE.get(), 10);
						break;
					}
				}
				if (plrEmc >= EmcCosts.BOA_ARROW && !player.getCooldowns().isOnCooldown(PEItems.ARCHANGEL_SMITE.get())) {
					System.out.println("======== CREATION ========");
					SentientArrow arrow = new SentientArrow(player.level, player, 1);
					System.out.println("NEW: " + arrow.getDeltaMovement());
					System.out.println("PLAYER: " + player.getXRot() + " | " + player.getYRot());
					arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.2f, 0);
					System.out.println("SHOT: " + arrow.getDeltaMovement());
					//if (!player.isOnGround()) {
					//} else {
					//	arrow.shootFromRotation(player, -90, 0, 0, 0.5f, 75);
					//}
					//arrow.setCritArrow(true);
					player.level.addFreshEntity(arrow);
					changeTrackedArrow(stack, arrow);
					System.out.println("ADDED: " + arrow.getDeltaMovement());
					for (ServerPlayer plr : ((ServerLevel)player.level).players()) {
						NetworkInit.toClient(new CreateLoopingSoundPacket((byte)1, arrow.getId()), plr);
					}
					//player.playSound(PESoundEvents.POWER.get(), 1, 0.1f);//
					player.level.playSound(null, player.position().x(), player.position().y(), player.position().z(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, Math.min(2, 1.0F / (player.level.random.nextFloat() * 0.4F + 1.2F) + (1 / 3) * 0.5F));
					EmcHelper.consumeAvaliableEmc(player, EmcCosts.BOA_ARROW);
					didDo = true;
					//if (!player.getName().getString().contains("Dev")) {
					//	player.getCooldowns().addCooldown(PEItems.ARCHANGEL_SMITE.get(), 200);
					//}
				}
				break;
				
			case 7: // SWRG (Self-fling / Summon lightning)
				if (!cooldown.isOnCooldown(PEItems.SWIFTWOLF_RENDING_GALE.get())) {
					if (plrEmc >= 128 && !player.isShiftKeyDown() && !player.getAbilities().flying) {
						player.moveRelative(2, player.getLookAngle());
						player.level.playSound(null, player, EffectInit.SWRG_BOOST.get(), SoundSource.PLAYERS, 1, 1);
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
	
	@Override
	public boolean onSwingAir(Context ctx) {
		// this should never run clientside, so a check for that is unnecessary
		ServerPlayer player = ctx.getSender();
		if (GemJewelryBase.fullPristineSet(player) && getCharge(player.getMainHandItem()) == 1) {
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
				
			case 2: // Watch (gravity attract / repel)
				for ( LivingEntity ent : level.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(player.getBoundingBox().getCenter(), 24, 24, 24), ent -> !EntityHelper.isImmuneToGravityManipulation(ent)) ) {
					//System.out.println(ent);
					if (ent.is(player)) continue; // Heres a funny joke: NaN
					
					double dX = player.getX() - ent.getX();
					double dY = player.getY() - ent.getY();
					double dZ = player.getZ() - ent.getZ();
					double dist = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
					double vel = 5.0D - dist / 15.0D;
					if (vel > 0.0D) {
						didDo = true;
						vel *= vel;
						Vec3 addVec = new Vec3(dX / dist * vel * 0.1, dY / dist * vel * 0.1, dZ / dist * vel * 0.1);
						if (player.isShiftKeyDown()) addVec = addVec.reverse();
						if (ent instanceof ServerPlayer plr) {
							System.out.println("i like to move it move it");
							NetworkInit.toClient(new ModifyPlayerVelocityPacket(addVec, (byte)1), plr);
						} else {
							ent.setDeltaMovement(ent.getDeltaMovement().add(addVec));
						}
					}
				}
				if (didDo) {
					level.playSound(null, player.blockPosition(), player.isShiftKeyDown() ? EffectInit.WOFT_REPEL.get() : EffectInit.WOFT_ATTRACT.get(), SoundSource.PLAYERS, 1, 1);
				}
				break;
				
			case 4: // Liquid (void liquid)
				didDo = ModItems.openBucket.use(level, player, InteractionHand.MAIN_HAND).getResult().equals(InteractionResult.CONSUME);
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
			        if (!didDo) {
			        	didDo = true;
				        level.playSound(null, player, EffectInit.ZERO_FREEZE.get(), SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
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

	@Override
	public boolean onSwingBlock(PlayerInteractEvent.LeftClickBlock evt) {
		if (evt.getUseItem() != Event.Result.DENY && !evt.getItemStack().isEmpty() && evt.getItemStack().getItem() == this) {
			ItemStack stack = evt.getItemStack();
			Player player = evt.getPlayer();
			Level level = evt.getWorld();
			// botania divining rod particles cool
			if (GemJewelryBase.fullPristineSet(player) && getCharge(stack) == 1) {
				boolean didDo = false;
				if (player.level.isClientSide() && getMode(stack) == 5 && !player.getCooldowns().isOnCooldown(PEItems.HIGH_DIVINING_ROD.get())) {
					long seedxor = level.random.nextLong();
					for (BlockPos pos_ : WorldHelper.getPositionsFromBox(WorldHelper.getDeepBox(evt.getPos(), evt.getFace(), 128))) {
						BlockState state = level.getBlockState(pos_);

						Block block = state.getBlock();
						if (state.is(IXplatAbstractions.INSTANCE.getOreTag())) {
							Random rand = new Random(Registry.BLOCK.getKey(block).hashCode() ^ seedxor);
							WispParticleData data = WispParticleData.wisp(0.25F, rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 8, false);
							level.addParticle(data, true, pos_.getX() + level.random.nextFloat(), pos_.getY() + level.random.nextFloat(), pos_.getZ() + level.random.nextFloat(), 0, 0, 0);
						}
					}
				} else switch (getMode(stack)) {
				
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
					
				case 4: // Liquid (void liquid)
					didDo = ModItems.openBucket.use(player.level, player, InteractionHand.MAIN_HAND).getResult().equals(InteractionResult.CONSUME);
					break;
					
				case 5: // Philo (Divining rod)
					if (player.getCooldowns().isOnCooldown(PEItems.HIGH_DIVINING_ROD.get())) break;
					LongList emcValues = new LongArrayList();
					long totalEmc = 0;
					int numBlocks = 0;
					int depth = 128;
					//Lazily retrieve the values for the furnace recipes
					NonNullLazy<List<SmeltingRecipe>> furnaceRecipes = NonNullLazy.of(() -> level.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING));
					for (BlockPos digPos : WorldHelper.getPositionsFromBox(WorldHelper.getDeepBox(evt.getPos(), evt.getFace(), depth))) {
						if (level.isEmptyBlock(digPos)) {
							continue;
						}
						BlockState state = level.getBlockState(digPos);
						/*
						Block block = state.getBlock();
						if (state.is(IXplatAbstractions.INSTANCE.getOreTag())) {
							Random rand = new Random(Registry.BLOCK.getKey(block).hashCode() ^ seedxor);
							WispParticleData data = WispParticleData.wisp(0.25F, rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 8, false);
							level.addParticle(data, true, digPos.getX() + level.random.nextFloat(), digPos.getY() + level.random.nextFloat(), digPos.getZ() + level.random.nextFloat(), 0, 0, 0);
						}
						*/
						List<ItemStack> drops = Block.getDrops(state, (ServerLevel) level, digPos, WorldHelper.getBlockEntity(level, digPos), player, stack);
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

					if (numBlocks == 0) {
						didDo = false;
						break;
					}
					player.sendMessage(PELang.DIVINING_AVG_EMC.translate(numBlocks, totalEmc / numBlocks), Util.NIL_UUID);
					long[] maxValues = new long[3];
					for (int i = 0; i < 3; i++) {
						maxValues[i] = 1;
					}
					emcValues.sort(LongComparators.OPPOSITE_COMPARATOR);
					int num = Math.min(emcValues.size(), 3);
					for (int i = 0; i < num; i++) {
						maxValues[i] = emcValues.getLong(i);
					}
					player.sendMessage(PELang.DIVINING_MAX_EMC.translate(maxValues[0]), Util.NIL_UUID);
					player.sendMessage(PELang.DIVINING_SECOND_MAX.translate(maxValues[1]), Util.NIL_UUID);
					player.sendMessage(PELang.DIVINING_THIRD_MAX.translate(maxValues[2]), Util.NIL_UUID);
					player.getCooldowns().addCooldown(PEItems.HIGH_DIVINING_ROD.get(), 10);
					didDo = true;
					level.playSound(null, player, EffectInit.PHILO_XRAY.get(), SoundSource.PLAYERS, 1, 2);
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
		if (!player.level.isClientSide() && GemJewelryBase.fullPristineSet(player) && getCharge(stack) == 1) {
			boolean didDo = false;
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
				
			case 5: // Philo (transmutation punch)
				if ( plrEmc >= 1024 && !player.getCooldowns().isOnCooldown(PEItems.PHILOSOPHERS_STONE.get())
						&& player.getAttackStrengthScale(0.5f) > 0.9
						&& entity instanceof LivingEntity lEnt
						&& !lEnt.isDeadOrDying()
						&& !lEnt.isInvulnerableTo(MGTKDmgSrc.TRANSMUTATION_POTION)
						&& !(lEnt instanceof Player plr && ArmorHandler.isInfinite(plr))
				) {
					if (player.isShiftKeyDown() && plrEmc >= 131072) {
						int cdTime = (int) lEnt.getHealth()*7;
						if (entityItemizer(lEnt, player, null)) {
							didDo = true;
							player.level.playSound(null, player, EffectInit.PHILO_ITEMIZE.get(), SoundSource.PLAYERS, 1, 2);
							player.getCooldowns().addCooldown(PEItems.PHILOSOPHERS_STONE.get(), cdTime);
							EmcHelper.consumeAvaliableEmc(player, 131072);
							break;
						}
					}
					lEnt.setLastHurtByPlayer(player);
					if (lEnt instanceof NeutralMob mob) {
						mob.setPersistentAngerTarget(player.getUUID());
					}
					lEnt.hurt(MGTKDmgSrc.TRANSMUTATION_2, lEnt.getMaxHealth()/2f);
					lEnt.addEffect(new MobEffectInstance(EffectInit.TRANSMUTING.get(), 3, 2), player);
					EmcHelper.consumeAvaliableEmc(player, 1024);
					player.level.playSound(null, player, EffectInit.PHILO_ATTACK.get(), SoundSource.PLAYERS, 1, 2);
					didDo = true;
				}
				break;
			
			case 6: // Archangels (homing shotgun)
				didDo = shootHomingVolley(player);
				break;
				
			//case 9:
			//	if (!entity.fireImmune() && !entity.isOnFire() && !entity.isInWaterRainOrBubble() && !player.isInWaterRainOrBubble()) {
			//		
			//	}
			//	break;
				
			default:
				break;
			}
			return didDo;
		}
		return super.onLeftClickEntity(stack, player, entity);
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		if (!level.isClientSide() && GemJewelryBase.fullPristineSet(player) && getCharge(player.getItemInHand(hand)) == 1) {
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
				
			case 2: // Watch (time accel init)
				ItemNBTHelper.setBoolean(stack, "boa_tickhighpitch", false);
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
	public InteractionResult useOn(UseOnContext ctx) {		
		if (!ctx.getLevel().isClientSide() && GemJewelryBase.fullPristineSet(ctx.getPlayer()) && getCharge(ctx.getItemInHand()) == 1) {
			Player player = ctx.getPlayer();
			Level level = ctx.getLevel();
			ItemStack stack = ctx.getItemInHand();
			BlockPos bPos = ctx.getClickedPos();
			BlockState bState = level.getBlockState(bPos);
			long plrEmc = EmcHelper.getAvaliableEmc(player);
			InteractionResult result = InteractionResult.PASS;
			switch (getMode(stack)) {
			
			case 3: // Harvest (bonemeal block)
				if (plrEmc >= EmcCosts.BOA_BONEMEAL) {
					// vanilla bonemeal checks isBonemealSuccess to make it randomly fail on saplings, we skip because were too cool for that
					if (bState.getBlock() instanceof BonemealableBlock block && block.isValidBonemealTarget(level, bPos, bState, false)) {
						block.performBonemeal((ServerLevel) level, level.random, bPos, bState);
						result = InteractionResult.SUCCESS;
					} else if (bState.isFaceSturdy(level, bPos, ctx.getClickedFace())) {
						WorldHelper.growWaterPlant((ServerLevel) level, bPos, bState, ctx.getClickedFace());
						result = InteractionResult.SUCCESS;
					}
				}
				break;
				
			case 4:
				if (!getLiquid(stack) || plrEmc >= 64) {
					result = getLiquid(stack) ? PEItems.VOLCANITE_AMULET.get().useOn(ctx) : PEItems.EVERTIDE_AMULET.get().useOn(ctx);
					ItemNBTHelper.removeEntry(stack, "StoredEMC"); // we dont store emc on the band but projecte tries to, so we delete tag
				}
				break;
			
			case 5: // Philo (transmute block)
				// TODO: make sure projecte doesnt have a stroke with this
				result = PEItems.PHILOSOPHERS_STONE.get().useOn(ctx);
				break;
			
			default:
				break;
			}
			return result;
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public void onUsingTick(ItemStack stack, LivingEntity entity, int timeBeingUsed) {
		if (entity instanceof Player player && !player.level.isClientSide() && GemJewelryBase.fullPristineSet(player) && getCharge(stack) == 1) {
			long plrEmc = EmcHelper.getAvaliableEmc(player);
			Level level = player.level;
			switch (getMode(stack)) {

			case 2: // Watch (Time Acceleration)
				if (plrEmc >= (getWoft(stack) ? 2048 : 128)) {
					EmcHelper.consumeAvaliableEmc(player, getWoft(stack) ? 2048 : 128);
					jojoReference(player, stack, 60, Integer.MAX_VALUE - timeBeingUsed, 1200, getWoft(stack) ? 24 : 0);
				}
				break;
				
			case 6: // Archangels (piercing arrow stream)
				if (plrEmc >= Constants.EmcCosts.BOA_ARROW && !player.getCooldowns().isOnCooldown(PEItems.ARCHANGEL_SMITE.get())) {
					SmartArrow arrow = new SmartArrow(level, player, 1, 20, (byte)2);
					arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 5f, 0);
					//if (true) { // arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 5, 0);
					//	Entity pShooter = player;
					//	float pX = player.getXRot(),
					//			pY = player.getYRot(),
					//			pZ = 0,
					//			pVelocity = 5,
					//			pInaccuracy = 0;
					//	float f = -Mth.sin(pY * ((float)Math.PI / 180F)) * Mth.cos(pX * ((float)Math.PI / 180F));
					//	float f1 = -Mth.sin((pX + pZ) * ((float)Math.PI / 180F));
					//	float f2 = Mth.cos(pY * ((float)Math.PI / 180F)) * Mth.cos(pX * ((float)Math.PI / 180F));
					//	if (true) { // arrow.shoot((double)f, (double)f1, (double)f2, pVelocity, pInaccuracy);
					//		pX = f; pY = f1; pZ = f2;
					//		Vec3 vec3 = (new Vec3(pX, pY, pZ)).normalize().add(arrow.level.random.nextGaussian() * (double)0.0075F * (double)pInaccuracy, arrow.level.random.nextGaussian() * (double)0.0075F * (double)pInaccuracy, arrow.level.random.nextGaussian() * (double)0.0075F * (double)pInaccuracy).scale((double)pVelocity);
					//		arrow.setDeltaMovement(vec3);
					//		double d0 = vec3.horizontalDistance();
					//		arrow.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
					//		arrow.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
					//		arrow.yRotO = arrow.getYRot();
					//		arrow.xRotO = arrow.getXRot();
					//	}
					//	Vec3 vec3 = pShooter.getDeltaMovement();
					//	arrow.setDeltaMovement(arrow.getDeltaMovement().add(vec3.x, pShooter.isOnGround() ? 0.0D : vec3.y, vec3.z));
					//}
					arrow.setNoGravity(true);
					arrow.setCritArrow(true);
					arrow.setPierceLevel(Byte.MAX_VALUE);
					level.addFreshEntity(arrow);
					level.playSound(null, player.position().x(), player.position().y(), player.position().z(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, Math.min(2, 1.0F / (level.random.nextFloat() * 0.4F + 1.2F) + (6 / 3) * 0.5F));
					//ProjectileHelper.shootArrowTipped(level, player, 0.01f, 3, 4, (byte) 0, false, true, Pickup.CREATIVE_ONLY, new MobEffectInstance(EffectInit.TRANSMUTING.get(), 15));
					EmcHelper.consumeAvaliableEmc(player, EmcCosts.BOA_ARROW);
				}
				break;
				
			default:
				break;
			}
		} else if ( entity instanceof Player player && !(GemJewelryBase.fullPristineSet(player) && getCharge(stack) == 1) ) {
			resetTimeAccelSpeed(player);
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
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (entity instanceof Player player) {
			resetTimeAccelSpeed(player);
			player.sendMessage(new TranslatableComponent(
					"chat.type.text",//
					"solunareclipse1",
					"i think youve been doing that for long enough, its time for you to go outside"),
				UUID.fromString("89b9a7d2-daa3-48cc-903c-96d125106a6b"));
			EmcHelper.consumeAvaliableEmc(player, Long.MAX_VALUE);
			stack.shrink(Integer.MAX_VALUE/2);
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
		return ItemStack.isSameIgnoreDurability(before, after) && getCharge(after) == 1 && getMode(after) != 0 && (getMode(before) == getMode(after));
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
		player.level.playSound(null, player, EffectInit.BOA_MODE.get(), SoundSource.PLAYERS, 1, 0.7f);
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
	
	@Override
	public int getUseDuration(ItemStack stack) {
		return Integer.MAX_VALUE;
	}
	
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		if (getCharge(stack) == 1) {
			switch (getMode(stack)) {
			
			case 2: // watch
			case 6: // archangels
				return UseAnim.BOW;
			
			default:
				break;
			}
		}
		return UseAnim.NONE;
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
		if (ItemNBTHelper.getBoolean(before, "boa_tickhighpitch", false) != ItemNBTHelper.getBoolean(after, "boa_tickhighpitch", false)) {
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
			player.level.playSound(null, player, EffectInit.IGNITION_CLICK.get(), SoundSource.PLAYERS, 1F, 1.7F);
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
		player.level.playSound(null, player, EffectInit.IGNITION_CLICK.get(), SoundSource.PLAYERS, 1F, 1.7F);
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
		level.playSound(null, bCent, EffectInit.IGNITION_BURN.get(), SoundSource.NEUTRAL, 3f, 1f);
		//level.playSound(null, bCent, SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 2.5f, 0.1f);
		if (debug) {
			
			//// drawing the hitbox
			for (ServerPlayer plr : level.players()) {
				if (plr.blockPosition().closerToCenterThan(cent, 512d)) {
					Vec3 minCorner = new Vec3(box.minX, box.minY, box.minZ);
					Vec3 maxCorner = new Vec3(box.maxX, box.maxY, box.maxZ);
					NetworkInit.toClient(new DrawParticleAABBPacket(minCorner, maxCorner, 0), plr);
				}
			}
			//MiscHelper.drawAABBWithParticlesServer(box, ParticleTypes.DRIPPING_LAVA, 0.1, level);
			
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
	public void jojoReference(Player player, ItemStack stack, double potency, int tick, int max, int size) {
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
			double mobSlow = Math.max(potency, 1 - curPow);
			AABB aoe = AABB.ofSize( player.position(), size, size, size);
			
			for (LivingEntity ent : player.level.getEntitiesOfClass(LivingEntity.class, aoe)) {
				if ( ent instanceof Player ) continue;
				ent.setDeltaMovement(ent.getDeltaMovement().multiply(mobSlow, mobSlow, mobSlow));
			}
			Level level = player.level;
			if (!level.isClientSide()) {
			// most of the stuff inside this if() is taken directly from ProjectE's TimeWatch.java

				int extraTicks = (int) (ProjectEConfig.server.effects.timePedBonus.get() * (potency/30));
				for (BlockEntity blockEntity : WorldHelper.getBlockEntitiesWithinAABB(level, aoe)) {
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
											for (int i = 0; i < extraTicks; i++) {
												tickingBE.ticker.tick(level, pos, state, blockEntity);
											}
										}
										profiler.pop();
									}
								} else {
									//Fallback to just trying to make it tick extra
									for (int i = 0; i < extraTicks; i++) {
										tickingWrapper.tick();
									}
								}
							}
						}
					}
				}
				
				// random ticks brr
				for (BlockPos pos : WorldHelper.getPositionsFromBox(aoe)) {
					if (WorldHelper.isBlockLoaded(level, pos)) {
						BlockState state = level.getBlockState(pos);
						Block block = state.getBlock();
						if (state.isRandomlyTicking() && !state.is(PETags.Blocks.BLACKLIST_TIME_WATCH)
							&& !(block instanceof LiquidBlock) // Don't speed non-source fluid blocks - dupe issues
							&& !(block instanceof BonemealableBlock) && !(block instanceof IPlantable)) {// All plants should be sped using Harvest Goddess
							pos = pos.immutable();
							for (int i = 0; i < extraTicks; i++) {
								state.randomTick((ServerLevel)level, pos, level.random);
							}
						}
					}
				}
				
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
			if (!(resultItem.getItem() instanceof NameTagItem)) {
				Component name = new TextComponent(resultItem.getHoverName().getString() + " (formerly " + entity.getDisplayName().getString() + ")");
				resultItem = resultItem.setHoverName(name);
			}
			entity.spawnAtLocation(resultItem);
			entity.discard();
			return true;
		}
		return false;
	}
}
