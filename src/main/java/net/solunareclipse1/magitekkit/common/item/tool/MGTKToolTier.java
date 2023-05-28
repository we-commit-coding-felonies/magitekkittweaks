package net.solunareclipse1.magitekkit.common.item.tool;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.common.TierSortingRegistry;

import moze_intel.projecte.gameObjs.PETags;

import net.solunareclipse1.magitekkit.MagiTekkit;

public enum MGTKToolTier implements Tier {
	VOID("void", 0, 13, 9, 5, 30, () -> Ingredient.EMPTY, PETags.Blocks.NEEDS_DARK_MATTER_TOOL, Tiers.NETHERITE, new ResourceLocation(MagiTekkit.MODID, "crimson")),
	CRIMSON("crimson", 0, 13, 18, 6, 0, () -> Ingredient.EMPTY, PETags.Blocks.NEEDS_RED_MATTER_TOOL, VOID, null);

	private final String name;
	private final int durability;
	private final float speed;
	private final float damage;
	private final int harvest;
	private final int ench;
	private final Supplier<Ingredient> repair;
	private final TagKey<Block> tag;

	MGTKToolTier(String name, int durability, float speed, float damage, int harvest, int ench, Supplier<Ingredient> repair, TagKey<Block> tag, Tier prev, @Nullable ResourceLocation next) {
		this.name = name;
		this.durability = durability;
		this.speed = speed;
		this.damage = damage;
		this.harvest = harvest;
		this.ench = ench;
		this.repair = repair;
		this.tag = tag;
		ResourceLocation rl = new ResourceLocation(MagiTekkit.MODID, name);
		TierSortingRegistry.registerTier(this, rl, List.of(prev), next == null ? Collections.emptyList() : List.of(next));
	}

	@Override
	public String toString() {
		return name;
	}
	
	

	@Override
	public int getUses() {
		return durability;
	}

	@Override
	public float getSpeed() {
		return speed;
	}

	@Override
	public float getAttackDamageBonus() {
		return damage;
	}

	@Override
	public int getLevel() {
		return harvest;
	}

	@Override
	public int getEnchantmentValue() {
		return ench;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return repair.get();
	}

	@NotNull
	@Override
	public TagKey<Block> getTag() {
		return tag;
	}

}
