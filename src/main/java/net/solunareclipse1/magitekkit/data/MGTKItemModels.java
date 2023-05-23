package net.solunareclipse1.magitekkit.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import moze_intel.projecte.PECore;

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
		
		// TODO: figure out how to reference other mods textures
		tool(ObjectInit.VOID_SWORD, PECore.rl("item/dm_tools/sword"));
		tool(ObjectInit.VOID_PICKAXE, PECore.rl("item/dm_tools/pickaxe"));
		tool(ObjectInit.VOID_SHOVEL, PECore.rl("item/dm_tools/shovel"));
		tool(ObjectInit.VOID_AXE, PECore.rl("item/dm_tools/axe"));
		tool(ObjectInit.VOID_HOE, PECore.rl("item/dm_tools/hoe"));
		normal(ObjectInit.VOID_HELM, PECore.rl("item/dm_armor/head"));
		normal(ObjectInit.VOID_CHEST, PECore.rl("item/dm_armor/chest"));
		normal(ObjectInit.VOID_LEGS, PECore.rl("item/dm_armor/legs"));
		normal(ObjectInit.VOID_BOOTS, PECore.rl("item/dm_armor/feet"));
		
		normal(ObjectInit.GEM_CIRCLET, modLoc("item/armor/gem_jewelry/helm"));
		normal(ObjectInit.GEM_AMULET, modLoc("item/armor/gem_jewelry/chest"));
		normal(ObjectInit.GEM_TIMEPIECE, modLoc("item/armor/gem_jewelry/legs"));
		normal(ObjectInit.GEM_ANKLET, modLoc("item/armor/gem_jewelry/boots"));
		
		BandOfArcana bracelet = ObjectInit.GEM_BRACELET.get();
		ItemModelBuilder builder = getBuilder(bracelet.getRegistryName().getPath());
		// 80 auto-generated texture files for 1 item
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 2; j++) {
        		for (int k = 0; k < 2; k++) {
        			for (int l = 0; l < 2; l++) {
        				builder.override()
        				.predicate(ClientInit.BOA_MODE, i)
        				.predicate(ClientInit.BOA_COVALENCE, j)
        				.predicate(ClientInit.BOA_LIQUID, k)
                   		.predicate(ClientInit.BOA_WOFT, l)
                   		.model(withExistingParent(String.format("item/tool/band_of_arcana/mode%1$s_cov%2$s_liquid%3$s_woft%4$s", i, j, k, l), "item/generated")
                   				.texture("layer0", modLoc(String.format("item/tool/band_of_arcana/mode%1$s_cov%2$s_liquid%3$s_woft%4$s", i, j, k, l))))
                   		.end();
        			}
        		}
        	}
		}
	}

	protected ItemModelBuilder normal(RegistryObject<? extends Item> item, ResourceLocation texture) {
		return withExistingParent(item.getId().getPath(), "item/generated").texture("layer0", texture);
	}

	protected ItemModelBuilder tool(RegistryObject<? extends Item> item, ResourceLocation texture) {
		return withExistingParent(item.getId().getPath(), "item/handheld").texture("layer0", texture);
	}
}
