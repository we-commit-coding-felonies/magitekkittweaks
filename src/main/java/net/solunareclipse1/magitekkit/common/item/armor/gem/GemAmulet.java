package net.solunareclipse1.magitekkit.common.item.armor.gem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jline.utils.Colors;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage.EmcAction;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.capability.EmcHolderItemCapabilityWrapper;
import moze_intel.projecte.capability.ItemCapability;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.utils.WorldHelper;

import net.solunareclipse1.magitekkit.common.item.armor.CrimsonArmor;
import net.solunareclipse1.magitekkit.common.misc.NukeDamageCalculator;
import net.solunareclipse1.magitekkit.common.misc.damage.MGTKDmgSrc;
import net.solunareclipse1.magitekkit.config.EmcCfg;
import net.solunareclipse1.magitekkit.config.EmcCfg.Gem.Chest;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.init.NetworkInit;
import net.solunareclipse1.magitekkit.network.packet.client.ModifyPlayerVelocityPacket;
import net.solunareclipse1.magitekkit.util.ColorsHelper;
import net.solunareclipse1.magitekkit.util.EmcHelper;
import net.solunareclipse1.magitekkit.util.EntityHelper;
import net.solunareclipse1.magitekkit.util.LoggerHelper;
import net.solunareclipse1.magitekkit.util.MiscHelper;
import net.solunareclipse1.magitekkit.util.PlrHelper;
import net.solunareclipse1.magitekkit.util.ProjectileHelper;
import net.solunareclipse1.magitekkit.util.ColorsHelper.Color;

import codechicken.lib.vec.Vector3;
import mekanism.api.Coord4D;
import mekanism.api.MekanismAPI;
import morph.avaritia.entity.GapingVoidEntity;
import vazkii.botania.common.entity.EntityManaStorm;
import vazkii.botania.common.entity.ModEntities;
import vazkii.botania.common.helper.ItemNBTHelper;

/**
 * Chestplate
 * 
 * @author solunareclipse1
 */
public class GemAmulet extends GemJewelryBase implements IItemEmcHolder {
	
	public static final long CAPACITY = 384000;
	
	public GemAmulet(Properties props, float baseDr) {
		super(EquipmentSlot.CHEST, props, baseDr);
		MinecraftForge.EVENT_BUS.addListener(this::checkExplosion);
		addItemCapability(EmcHolderItemCapabilityWrapper::new);
		//addItemCapability(ManaItemCapabilityWrapper::new);
	}

	// durability bar stuff
	@Override
	public boolean isBarVisible(ItemStack stack) {
		return stack.isDamaged() || getStoredEmc(stack) > 0;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		if (stack.isDamaged()) return super.getBarWidth(stack);
		return Math.round(13 * ((float)getStoredEmc(stack) / (float)CAPACITY));
	}

	@Override
	public int getBarColor(ItemStack stack) {
		if (stack.isDamaged()) return super.getBarColor(stack);
		
		// i hope this isnt bad?
		Minecraft mc = Minecraft.getInstance();
		long worldTime = mc.level.getGameTime();
		//high 0.605555555555
		//low 0.303055555556, changed to 0.3911 cuz otherwise too much green
		return Mth.hsvToRgb(ColorsHelper.fadingValue(worldTime, 200, 0, 0.3911f, 0.6056f), 1.0f, 0.824f);
		//return ColorsHelper.fadingColorInt(worldTime, 200, 0, 0.3911f, 1.0f, 0.824f, 0.6056f, 1.0f, 0.824f);
	}
	
	
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag isAdvanced) {
		superAppendHoverText(stack, level, tips, isAdvanced);
		tips.add(new TranslatableComponent("tip.mgtk.gem.ref.2").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
	}
	
	/**
	 * catastrophic klein star failure
	 * @param event
	 */
	public void checkExplosion(LivingEquipmentChangeEvent event) {
		ItemStack from = event.getFrom();
		if (from.getItem() instanceof GemAmulet amulet && amulet.getStoredEmc(from) > 0 && from.isDamaged()) {
			int damage = from.getDamageValue();
			int max = from.getMaxDamage();
			ItemStack to = event.getTo();
			if (damage+1 >= max && to.isEmpty()) {
				LivingEntity wearer = event.getEntityLiving();
				DamageSource dmgSrc = MGTKDmgSrc.emcNuke(wearer);
				float power = (float)amulet.getStoredEmc(from)/(float)amulet.getMaximumEmc(from);
				NukeDamageCalculator nukeCalc = new NukeDamageCalculator(0f);
				Vec3 cent = wearer.getBoundingBox().getCenter();
				wearer.level.explode(wearer, dmgSrc, nukeCalc, cent.x, cent.y, cent.z, 20f*power, false, Explosion.BlockInteraction.DESTROY);
				if (power > 0.9) {
					if (!EntityHelper.isInvincible(wearer)) {
						wearer.hurt(dmgSrc, Float.MAX_VALUE);
					}
					EntityManaStorm singularity = ModEntities.MANA_STORM.create(wearer.level);
					singularity.burstColor = Color.COVALENCE_BLUE.I;
					singularity.setPos(cent.x, cent.y, cent.z);
					singularity.setYRot(wearer.getYRot());
					singularity.liveTime = 1350;
					singularity.deathTime = 195;
					wearer.level.addFreshEntity(singularity);
				}
				wearer.level.playSound(null, wearer.blockPosition(), EffectInit.ARMOR_BREAK.get(), wearer.getSoundSource(), 10f*power, 0.75f);
			}
		}
	}
	
	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		if (level.isClientSide) {
			// Client
			if (!stack.isDamaged()) {
				GemJewelrySetInfo set = jewelryTick(stack, level, player);
				long plrEmc = set.plrEmc();
				
				// This should be last, so that plrEmc is accurate!
				if (plrEmc > 0 && level.getGameTime() % 160 == 0) {
					shieldHum(player, stack);
				}
			}
		} else {
			// Server
			GemJewelrySetInfo set = jewelryTick(stack, level, player);
			long plrEmc = set.plrEmc();
			if (!stack.isDamaged()) {
				// gem of density
				long amount = Math.min(CAPACITY/10, CAPACITY - getStoredEmc(stack));
				if (amount > 0 && getInfo(player, EquipmentSlot.LEGS).pristine()) {
					condenserRefill(stack, player, amount);
					// recalculate plrEmc, since it could be desynced
					plrEmc = EmcHelper.getAvaliableEmc(player);
				}
				
				// life stone
				if (!player.hasEffect(EffectInit.TRANSMUTING.get()) && plrEmc >= Chest.REJUVENATE.get() && level.getGameTime() % 7 == 0) {
					if (tryHeal(player)) {
						plrEmc -= EmcHelper.consumeAvaliableEmc(player, Chest.REJUVENATE.get());
					}
					// plrEmc might have changed, need to re-check
					if (plrEmc >= Chest.REJUVENATE.get() && tryFeed(player)) {
						plrEmc -= EmcHelper.consumeAvaliableEmc(player, Chest.REJUVENATE.get());
					}
				}
				
				
				// This should be last, so that plrEmc is accurate!
				if (plrEmc > 0 && level.getGameTime() % 160 == 0) {
					shieldHum(player, stack);
				}
			} else {
				// leaky when damaged
				if (getDamage(stack) >= getMaxDamage(stack)/2) {
					plrEmc -= tryLeak(stack, level, player, plrEmc);
				}
			}
		}
	}
	
	
	private void shieldHum(Player player, ItemStack stack) {
		if (shieldCondition(player, 1f, DamageSource.GENERIC, stack)) {
			player.level.playSound(player, player, EffectInit.SHIELD_AMBIENT.get(), SoundSource.PLAYERS, 1, 1);
		}
	}
	
	private void condenserRefill(ItemStack stack, Player player, long requested) {
		long consumed = EmcHelper.consumeAvaliableEmcExcludeSelf(player, requested, stack);
		long needed = getNeededEmc(stack);
		if (needed < consumed) {
			player.level.playSound(null, player.blockPosition(), EffectInit.EMC_WASTE.get(), SoundSource.PLAYERS, 1, 1);
			consumed = needed;
		}
		insertEmc(stack, consumed, EmcAction.EXECUTE);
	}
	
	private boolean tryHeal(Player player) {
		if (player.getHealth() < player.getMaxHealth()) {
			player.heal(1);
			return true;
		}
		return false;
	}
	
	private boolean tryFeed(Player player) {
		if (player.getFoodData().needsFood()) {
			player.getFoodData().eat(1, 10);
			return true;
		}
		return false;
	}
	
	private long tryLeak(ItemStack stack, Level level, Player player, long plrEmc) {
		if (plrEmc > 0) {
			int remaining = getMaxDamage(stack)-getDamage(stack);
			if (Math.round(level.getGameTime() % remaining) == 0) {
				return EmcHelper.consumeAvaliableEmc(player, doLeak(stack, level, player, Math.max(remaining, 16))-1);
			}
		}
		return 0;
	}
	
	public long doLeak(ItemStack stack, Level level, Player player, int shenanigansChance) {
		long consumed = extractEmc(stack, 1, EmcAction.EXECUTE);
		if (consumed > 0) {
			level.playSound(null, player.blockPosition(), EffectInit.EMC_LEAK.get(), SoundSource.PLAYERS, 1, 1);
			if (player.getRandom().nextInt(shenanigansChance) == 0) {
				consumed += doShenanigans(stack, level, player);
			}
		}
		return consumed;
	}
	
	private long doShenanigans(ItemStack stack, Level level, Player player) {
		Random rand = player.getRandom();
		long consumed = 0;
		AABB area = AABB.ofSize(player.getBoundingBox().getCenter(), rand.nextInt(1,14), rand.nextInt(1,14), rand.nextInt(1,14));
		boolean noRad = Chest.RADLEAK.get() == 0;
		switch (rand.nextInt(1,noRad ? 13 : 14)) {
		case 1:
			MiscHelper.funnySound(rand, level, player.blockPosition());
			consumed++;
			break;
		case 2:
			WorldHelper.freezeInBoundingBox(level, area, player, true);
			consumed += EmcCfg.Arcana.Zero.FREEZE.get();
			break;
		case 3:
			WorldHelper.extinguishNearby(level, player);
			consumed += EmcCfg.Arcana.Zero.EXTINGUISH.get();
			break;
		case 4:
			MiscHelper.burnInBoundingBox(level, area, player, false);
			consumed += EmcCfg.Arcana.Ignition.BURN.get();
			break;
		case 5:
			WorldHelper.growNearbyRandomly(rand.nextBoolean(), level, player.blockPosition(), player);
			consumed += EmcCfg.Arcana.Harvest.AOEGROW.get();
			break;
		case 6:
			for (int i = 0; i < rand.nextInt(1,14); i++) {
				WorldHelper.repelEntitiesSWRG(level, area, player);
				consumed += i;
			}
			break;
		case 7:
			ModifyPlayerVelocityPacket pkt = new ModifyPlayerVelocityPacket(new Vec3(rand.nextInt(-1, 2),rand.nextInt(-1, 2),rand.nextInt(-1, 2)), (byte)rand.nextInt(-1, 4));
			NetworkInit.toClient(pkt, (ServerPlayer)player);
			break;
		case 8:
			consumed += ProjectileHelper.fiftyTwoCardPickup(rand, level, player, true)*EmcCfg.Arcana.Archangel.ARROW.get();
			break;
		case 9:
			MiscHelper.pokeNearby(level, player, stack);
			break;
		case 10:
			if (player instanceof ServerPlayer) { // TODO: this check might be unneccessary
				MiscHelper.smiteSelf(level, (ServerPlayer) player);
				consumed += EmcCfg.Arcana.SWRG.SMITE.get();
			} break;
		case 11:
			long oldXp = PlrHelper.getXp(player);
			player.giveExperienceLevels(rand.nextInt(-1, 2));
			long diff = PlrHelper.getXp(player) - oldXp;
			consumed += Math.max(0, diff);
			break;
		case 12:
			player.addEffect(new MobEffectInstance(EffectInit.TRANSMUTING.get(), 60, 0));
			break;
		case 13:
			double maxRad = Chest.RADLEAK.get();
			if (maxRad > 0) {
				consumed += rand.nextLong(1, (long)(maxRad+1));
				double sv = Math.pow(2d, (9d*consumed)/maxRad );
				MekanismAPI.getRadiationManager().radiate(new Coord4D(player), sv);
			}
			break;
		}
		
		//if (consumed < 0) consumed = Long.MAX_VALUE;
		
		return consumed;
	}
	
	/*
	 * OLD
	/**
	 * Fills an ItemStack to max EMC capacity, pulling from the players inventory
	 * @param stack
	 * @param player
	public void autoRefill(ItemStack stack, Player player) {
		long needed = CAPACITY - getStoredEmc(stack);
		long consumed = EmcHelper.consumeAvaliableEmcExcludeSelf(player, needed, stack);
		insertEmc(stack, consumed, EmcAction.EXECUTE); 
	}
	
	// body / soul / life stone
	public long rejuvenatePlayer(Level level, Player player, long plrEmc) {
		if (!level.isClientSide && plrEmc >= 72) {
			if (level.getGameTime() % 16 == 0) {
				byte used = 0;
				// prioritize direct healing
				if (player.getHealth() < player.getMaxHealth()) {
					used++;
					player.heal(2.0f);
				}
				
				if (player.getFoodData().needsFood() && (plrEmc >= 144 || used == 0) ) {
					used++;
					player.getFoodData().eat(2, 10);
				}
				
				if (used != 0) level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.HEAL.get(), SoundSource.PLAYERS, 1, 1);
				plrEmc -= EmcHelper.consumeAvaliableEmc(player, 72*used);
			}
		}
		
		return plrEmc;
	}
	
	
	public long leakEmc(ItemStack stack, Level level, Player player, long plrEmc) {
		int remaining = getMaxDamage(stack)-getDamage(stack);
		if (Math.round(level.getGameTime() % remaining) == 0) {
			if (extractEmc(stack, 1, EmcAction.EXECUTE) > 0) {
				plrEmc--;
				level.playSound(null, player, EffectInit.EMC_LEAK.get(), SoundSource.PLAYERS, 1, 1);
				if (player.getRandom().nextInt(Math.max(remaining, 16)) == 0 && !level.isClientSide) plrEmc -= performShenanigans(stack, level, player, plrEmc);
			}
		}
		
		return plrEmc;
	}

	/**
	 * Does random stuff when called, most of which isnt pleasant
	 * 
	 * @return emc consumed by shenanigans
	public long performShenanigans(ItemStack stack, Level level, Player player, long plrEmc) {
		Random rand = player.getRandom();
		long consumed = 0;
		switch (rand.nextInt(12)) {
		case 0:
			break;
		case 1:
			MiscHelper.funnySound(rand, level, player.blockPosition());
			consumed++;
			break;
		case 2:
			WorldHelper.freezeInBoundingBox(level, player.getBoundingBox().inflate(10), player, true);
			consumed += 16;
			break;
		case 3:
			WorldHelper.extinguishNearby(level, player);
			consumed += 4;
			break;
		case 4:
			WorldHelper.igniteNearby(level, player);
			consumed += 24;
			break;
		case 5:
			WorldHelper.growNearbyRandomly(rand.nextBoolean(), level, player.blockPosition(), player);
			consumed += 64;
			break;
		case 6:
			WorldHelper.repelEntitiesInterdiction(level, player.getBoundingBox().inflate(10), player.position().x, player.position().y, player.position().z);
			consumed++;
			break;
		case 7:
			int oldXp = player.totalExperience;
			player.giveExperienceLevels(rand.nextInt(-1, 2));
			int diff = player.totalExperience - oldXp;
			consumed += Math.max(diff, 1);
			break;
		case 8:
			//consumed += 32*MiscHelper.fiftyTwoCardPickup(rand, level, player, true); TODO fix
			break;
		case 9:
			MiscHelper.pokeNearby(level, player, stack);
			break;
		case 10:
			if (player instanceof ServerPlayer) { // TODO: this check might be unneccessary
				MiscHelper.smiteSelf(level, (ServerPlayer) player);
				consumed += 1024;
			} break;
		case 11:
			consumed += rand.nextInt(1, 8193);
			break;
		}
		
		if (consumed < 0) consumed = Long.MAX_VALUE;
		
		return EmcHelper.consumeAvaliableEmc(player, consumed);
	}*/
	
	// Built-in klein star stuff
	// Modified from KleinStar.java
	// https://github.com/sinkillerj/ProjectE/blob/mc1.18.x/src/main/java/moze_intel/projecte/gameObjs/items/KleinStar.java
	@Override
	public long insertEmc(@NotNull ItemStack stack, long toInsert, EmcAction action) {
		if (toInsert < 0) {
			return extractEmc(stack, -toInsert, action);
		}
		long toAdd = Math.min(getNeededEmc(stack), toInsert);
		if (action.execute()) {
			ItemPE.addEmcToStack(stack, toAdd);
		}
		return toAdd;
	}
	
	@Override
	public long extractEmc(@NotNull ItemStack stack, long toExtract, EmcAction action) {
		if (toExtract < 0) {
			return insertEmc(stack, -toExtract, action);
		}
		long storedEmc = getStoredEmc(stack);
		long toRemove = Math.min(storedEmc, toExtract);
		if (action.execute()) {
			ItemPE.setEmc(stack, storedEmc - toRemove);
		}
		return toRemove;
	}

	@Override
	public @Range(from = 1, to = Long.MAX_VALUE) long getMaximumEmc(@NotNull ItemStack stack) {return CAPACITY;}

	@Override
	public @Range(from = 0, to = Long.MAX_VALUE) long getStoredEmc(@NotNull ItemStack stack) {
		return ItemPE.getEmc(stack);
	}
	@Override
	public @NotNull List<Supplier<ItemCapability<?>>> getSupportedCaps() {
		return supportedCapabilities;
	}
	private static final List<Supplier<ItemCapability<?>>> supportedCapabilities = new ArrayList<>();
	
	
	
	// hex casting media compat
	// emc should be converted as necessary
}
