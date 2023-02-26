package net.solunareclipse1.magitekkit.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.init.ObjectInit;

public class MGTKBlockTags extends BlockTagsProvider {
	public MGTKBlockTags(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, MagiTekkit.MODID, helper);
    }

    @Override
    protected void addTags() {
        tag(BlockTags.BEACON_BASE_BLOCKS)
            .add(ObjectInit.GANTIUM_BLOCK.get());
        tag(BlockTags.NEEDS_DIAMOND_TOOL)
        	.add(ObjectInit.GANTIUM_BLOCK.get());
        tag(Tags.Blocks.STORAGE_BLOCKS)
        	.add(ObjectInit.GANTIUM_BLOCK.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
        	.add(ObjectInit.GANTIUM_BLOCK.get());
    }

    @Override
    public String getName() {
        return "MagiTekkit Block Tags";
    }
}
