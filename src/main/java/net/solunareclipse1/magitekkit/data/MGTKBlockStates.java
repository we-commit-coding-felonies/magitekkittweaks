package net.solunareclipse1.magitekkit.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.common.block.AirIceBlock;
import net.solunareclipse1.magitekkit.init.ObjectInit;

public class MGTKBlockStates extends BlockStateProvider {

	public MGTKBlockStates(DataGenerator gen, ExistingFileHelper helper) {
		super(gen, MagiTekkit.MODID, helper);
	}

	@Override
	protected void registerStatesAndModels() {
		simpleBlock(ObjectInit.GANTIUM_BLOCK.get());
		getVariantBuilder(ObjectInit.AIR_ICE.get())
			.partialState().with(AirIceBlock.AGE, 0).addModels(modelOf(Blocks.FROSTED_ICE, "_0"))
			.partialState().with(AirIceBlock.AGE, 1).addModels(modelOf(Blocks.FROSTED_ICE, "_1"))
			.partialState().with(AirIceBlock.AGE, 2).addModels(modelOf(Blocks.FROSTED_ICE, "_2"))
			.partialState().with(AirIceBlock.AGE, 3).addModels(modelOf(Blocks.FROSTED_ICE, "_3"))
		;
	}
	
	private ConfiguredModel modelOf(Block block, String suffix) {
		return new ConfiguredModel(models().getExistingFile(ModelLocationUtils.getModelLocation(block, suffix)));
	}
}