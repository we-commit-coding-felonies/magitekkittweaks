package net.solunareclipse1.magitekkit.data;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.init.ObjectInit;

public class MGTKBlockTags extends BlockTagsProvider {
	public MGTKBlockTags(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, MagiTekkit.MODID, helper);
    }

	public static final TagKey<Block> ARROW_NOCLIP = makeTag("sentient_arrow_pathfind_noclip");
	public static final TagKey<Block> ARROW_ANNIHILATE = makeTag("sentient_arrow_pathfind_annihilate");

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
        
        tag(ARROW_NOCLIP)
    		.add(Blocks.AIR)
    		.add(Blocks.WATER)
        	.add(Blocks.LAVA)
        	.add(Blocks.IRON_BARS)
        	.addTag(BlockTags.LEAVES);
        
        /** help */
        tag(ARROW_ANNIHILATE)
        	.addTag(net.minecraftforge.common.Tags.Blocks.GLASS)
        	.addTag(net.minecraftforge.common.Tags.Blocks.GLASS_PANES);
    }
    
    private static TagKey<Block> makeTag(String name) {
    	return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("magitekkit", name));
    }

    @Override
    public String getName() {
        return "MagiTekkit Block Tags";
    }
}
