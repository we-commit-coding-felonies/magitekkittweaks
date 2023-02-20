package net.solunareclipse1.magitekkit.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.init.ObjectInit;

public class MGTKItemTags extends ItemTagsProvider {
	public MGTKItemTags(DataGenerator generator, BlockTagsProvider blockTags, ExistingFileHelper helper) {
        super(generator, blockTags, MagiTekkit.MODID, helper);
    }

    @Override
    protected void addTags() {
        tag(Tags.Items.STORAGE_BLOCKS)
        	.add(ObjectInit.GANTIUM_BLOCK_ITEM.get())
        ;
        
        tag(ObjectInit.VOID_ARMOR)
    		.add(ObjectInit.VOID_HELM.get())
    		.add(ObjectInit.VOID_CHEST.get())
    		.add(ObjectInit.VOID_LEGS.get())
    		.add(ObjectInit.VOID_BOOTS.get())
    	;
        
        tag(ObjectInit.PHIL_ARMOR)
			.add(ObjectInit.PHIL_HELM.get())
			.add(ObjectInit.PHIL_CHEST.get())
			.add(ObjectInit.PHIL_LEGS.get())
			.add(ObjectInit.PHIL_BOOTS.get())
		;
    }

    @Override
    public String getName() {
        return "ProjectTweaks Item Tags";
    }
}
