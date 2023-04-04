package net.solunareclipse1.magitekkit.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.common.item.tool.BandOfArcana;
import net.solunareclipse1.magitekkit.init.ClientInit;
import net.solunareclipse1.magitekkit.init.ObjectInit;

public class MGTKItemModels extends ItemModelProvider {
	public MGTKItemModels(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, MagiTekkit.MODID, helper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(ObjectInit.GANTIUM_BLOCK_ITEM.getId().getPath(), modLoc("block/gantium_block"));
        
        singleTexture(ObjectInit.VOID_HELM.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/armor/void/helm"));
        singleTexture(ObjectInit.VOID_CHEST.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/armor/void/chest"));
        singleTexture(ObjectInit.VOID_LEGS.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/armor/void/legs"));
        singleTexture(ObjectInit.VOID_BOOTS.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/armor/void/boots"));
        
        singleTexture(ObjectInit.GEM_CIRCLET.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/armor/gem_jewelry/helm"));
        singleTexture(ObjectInit.GEM_AMULET.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/armor/gem_jewelry/chest"));
        singleTexture(ObjectInit.GEM_TIMEPIECE.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/armor/gem_jewelry/legs"));
        singleTexture(ObjectInit.GEM_ANKLET.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/armor/gem_jewelry/boots"));
        
        BandOfArcana bracelet = ObjectInit.GEM_BRACELET.get();
        ItemModelBuilder builder = getBuilder(bracelet.getRegistryName().getPath());
        // 80 texture files for 1 item
        // https://www.youtube.com/watch?v=KnhXwlFeRP8
        for (int i = 0; i < 10; i++) {
        	for (int j = 0; j < 2; j++) {
        		for (int k = 0; k < 2; k++) {
        			for (int l = 0; l < 2; l++) {
                       	builder.override()
                       	.predicate(ClientInit.ARC_MODE, i)
                   		.predicate(ClientInit.ARC_OFFENSIVE, j)
                   		.predicate(ClientInit.ARC_LIQUID, k)
                   		.predicate(ClientInit.ARC_WOFT, l)
                       	.model(withExistingParent(String.format("item/gem_bracelet/mode%1$s_offensive%2$s_liquid%3$s_woft%4$s", i, j, k, l), "item/generated")
                       	.texture("layer0", modLoc(String.format("item/curio/band_of_arcana/mode%1$s_offensive%2$s_liquid%3$s_woft%4$s", i, j, k, l))))
                       	.end();
        			}
        		}
        	}
        }
    }

	protected ItemModelBuilder generated(String name, ResourceLocation texture) {
		return withExistingParent(name, "item/generated").texture("layer0", texture);
	}
}
