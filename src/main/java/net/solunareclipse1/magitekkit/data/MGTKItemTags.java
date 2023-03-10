package net.solunareclipse1.magitekkit.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;

import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import moze_intel.projecte.gameObjs.PETags;

import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.init.ObjectInit;

import vazkii.botania.common.lib.ModTags;

public class MGTKItemTags extends ItemTagsProvider {
	public MGTKItemTags(DataGenerator generator, BlockTagsProvider blockTags, ExistingFileHelper helper) {
        super(generator, blockTags, MagiTekkit.MODID, helper);
    }

    @Override
    protected void addTags() {
        tag(Tags.Items.STORAGE_BLOCKS)
        	.add(ObjectInit.GANTIUM_BLOCK_ITEM.get())
        ;
        
        tag(ItemTags.FREEZE_IMMUNE_WEARABLES)
        	.add(ObjectInit.GEM_AMULET.get())
        ;
        
        tag(ModTags.Items.BURST_VIEWERS)
        	.add(ObjectInit.GEM_CIRCLET.get())
        ;
        
        tag(PETags.Items.CURIOS_RING)
        	.add(ObjectInit.COVALENCE_BRACELET.get())
        	//.add(ObjectInit.GEM_BRACELET.get())
        ;
    }

    @Override
    public String getName() {
        return "MagiTekkit Item Tags";
    }
}
