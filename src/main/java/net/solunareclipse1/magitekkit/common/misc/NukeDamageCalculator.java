package net.solunareclipse1.magitekkit.common.misc;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import net.solunareclipse1.magitekkit.data.MGTKBlockTags;

public class NukeDamageCalculator extends ExplosionDamageCalculator {
	final float blockResist;
	
	public NukeDamageCalculator(float blockResist) {
		this.blockResist = blockResist;
	}
	
	public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, FluidState fluid) {
		if (blockResist < 0 || state.is(MGTKBlockTags.NUKE_RESIST)) {
			return super.getBlockExplosionResistance(explosion, reader, pos, state, fluid);
		}
		return Optional.of(blockResist);
	}
	
	public boolean shouldBlockExplode(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, float power) {
		return !state.is(MGTKBlockTags.NUKE_IMMUNE);
	}
}
