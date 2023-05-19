package net.solunareclipse1.magitekkit.api.capability.wrapper.converter;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Multimap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

import net.minecraftforge.common.capabilities.Capability;

import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import net.solunareclipse1.magitekkit.api.capability.wrapper.CovalentCapabilityWrapper;
import net.solunareclipse1.magitekkit.util.EmcHelper;

/**
 * Makes any converter item work with curios <br>
 * The item will only convert when equipped <br>
 * Also attempts to completely refill itself when worn, using EMC from the players inventory
 * 
 * @author solunareclipse1
 */
public class CurioCovalentCapabilityWrapper extends CovalentCapabilityWrapper<ICurio> implements ICurio {
	ItemStack oldStack = ItemStack.EMPTY;
	
	@Override
	public Capability<ICurio> getCapability() {
		return CuriosCapability.ITEM;
	}

	@Override
	public ItemStack getStack() {
		return super.getStack();
	}
	
	// auto-refill from inventory emc while worn
	@Override
	public void curioTick(SlotContext ctx) {
		if (ctx.cosmetic()) return;
		if (ctx.entity() instanceof Player && getState()) {
			//if (!getState()) setState(true);
			if (getPoolNeeded() > 0) {
				long consumed = EmcHelper.consumeAvaliableEmc((Player) ctx.entity(), getPoolNeeded());
				setPool(Mth.clamp(getPool() + consumed, 0, getPoolMax()));
			}
		}
		getItem().curioTick(ctx);
	}
	
	@Override
	public void onEquip(SlotContext ctx, ItemStack prevStack) {
		if (ctx.entity() instanceof Player) {
			setState(true);
		}
		getItem().onEquip(ctx, prevStack);
	}
	
	@Override
	public void onUnequip(SlotContext ctx, ItemStack newStack) {
		setState(false);
		getItem().onUnequip(ctx, newStack);
	}
	
	@Override
	public boolean canEquip(SlotContext ctx) {
		return getItem().canEquip(ctx);
	}
	
	@Override
	public boolean canUnequip(SlotContext ctx) {
		return getItem().canUnequip(ctx);
	}
	
	@Override
	public List<Component> getSlotsTooltip(List<Component> tips) {
		return getItem().getSlotsTooltip(tips);
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext ctx, UUID uuid) {
		return getItem().getAttributeModifiers(ctx, uuid);
	}
	
	@Override
	public void onEquipFromUse(SlotContext ctx) {
		if (ctx.entity() instanceof Player) {
			setState(true);
		}
		getItem().onEquipFromUse(ctx);
	}
	
	@Override
	public SoundInfo getEquipSound(SlotContext ctx) {
		return getItem().getEquipSound(ctx);
	}
	
	@Override
	public boolean canEquipFromUse(SlotContext ctx) {
		return getItem().canEquipFromUse(ctx);
	}
	
	@Override
	public void curioBreak(SlotContext ctx) {
		getItem().curioBreak(ctx);
	}
	
	@Override
	public boolean canSync(SlotContext ctx) {
		return getItem().canSync(ctx);
	}
	
	@Override
	public CompoundTag writeSyncData(SlotContext ctx) {
		return getItem().writeSyncData(ctx);
	}
	
	@Override
	public void readSyncData(SlotContext ctx, CompoundTag tag) {
		getItem().readSyncData(ctx, tag);
	}
	
	@Override
	public DropRule getDropRule(SlotContext ctx, DamageSource src, int looting, boolean recentlyHit) {
		return getItem().getDropRule(ctx, src, looting, recentlyHit);
	}
	
	@Override
	public List<Component> getAttributesTooltip(List<Component> tips) {
		return getItem().getAttributesTooltip(tips);
	}
	
	@Override
	public int getFortuneLevel(SlotContext ctx, @Nullable LootContext lootCtx) {
		return getItem().getFortuneLevel(ctx, lootCtx);
	}
	
	@Override
	public int getLootingLevel(SlotContext ctx, DamageSource src, LivingEntity victim, int oldLooting) {
		return getItem().getLootingLevel(ctx, src, victim, oldLooting);
	}
	
	@Override
	public boolean makesPiglinsNeutral(SlotContext ctx) {
		return getItem().makesPiglinsNeutral(ctx);
	}
	
	@Override
	public boolean isEnderMask(SlotContext ctx, EnderMan man) {
		return getItem().isEnderMask(ctx, man);
	}
}
