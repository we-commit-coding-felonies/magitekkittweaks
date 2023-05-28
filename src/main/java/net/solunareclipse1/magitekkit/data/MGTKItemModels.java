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
import net.solunareclipse1.magitekkit.api.item.IEmpowerItem;
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
		
		tool(ObjectInit.VOID_SWORD, PECore.rl("item/dm_tools/sword"));
		tool(ObjectInit.VOID_PICKAXE, PECore.rl("item/dm_tools/pickaxe"));
		tool(ObjectInit.VOID_SHOVEL, PECore.rl("item/dm_tools/shovel"));
		tool(ObjectInit.VOID_AXE, PECore.rl("item/dm_tools/axe"));
		tool(ObjectInit.VOID_HOE, PECore.rl("item/dm_tools/hoe"));
		normal(ObjectInit.VOID_HELM, PECore.rl("item/dm_armor/head"));
		normal(ObjectInit.VOID_CHEST, PECore.rl("item/dm_armor/chest"));
		normal(ObjectInit.VOID_LEGS, PECore.rl("item/dm_armor/legs"));
		normal(ObjectInit.VOID_BOOTS, PECore.rl("item/dm_armor/feet"));

		empowerItem(ObjectInit.CRIMSON_SWORD, "item/tool/crimson/sword/", "item/handheld");
		empowerItem(ObjectInit.CRIMSON_PICKAXE, "item/tool/crimson/pickaxe/", "item/handheld");
		empowerItem(ObjectInit.CRIMSON_SHOVEL, "item/tool/crimson/shovel/", "item/handheld");
		empowerItem(ObjectInit.CRIMSON_AXE, "item/tool/crimson/axe/", "item/handheld");
		empowerItem(ObjectInit.CRIMSON_HOE, "item/tool/crimson/hoe/", "item/handheld");
		normal(ObjectInit.CRIMSON_HELM, PECore.rl("item/rm_armor/head"));
		normal(ObjectInit.CRIMSON_CHEST, PECore.rl("item/rm_armor/chest"));
		normal(ObjectInit.CRIMSON_LEGS, PECore.rl("item/rm_armor/legs"));
		normal(ObjectInit.CRIMSON_BOOTS, PECore.rl("item/rm_armor/feet"));
		
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
        				String name = String.format("item/tool/band_of_arcana/mode%1$s_cov%2$s_liquid%3$s_woft%4$s", i, j, k, l);
        				builder.override()
        				.predicate(ClientInit.BOA_MODE, i)
        				.predicate(ClientInit.BOA_COVALENCE, j)
        				.predicate(ClientInit.BOA_LIQUID, k)
                   		.predicate(ClientInit.BOA_WOFT, l)
                   		.model(withExistingParent(name, "item/generated")
                   				.texture("layer0", modLoc(name)))
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
	
	protected ItemModelBuilder empowerItem(RegistryObject<? extends IEmpowerItem> reg, String folder, String parent) {
		ItemModelBuilder builder = getBuilder(reg.getId().getPath());
		IEmpowerItem item = reg.get();
		for (int i = 0; i <= item.getMaxStages(); i++) {
			String name = String.format(folder+"stage%1$s", i);
			builder.override()
			.predicate(ClientInit.EMPOWER_CHARGE, i)
			.model(withExistingParent(name, parent)
					.texture("layer0", modLoc(name)))
			.end();
		}
		return builder;
	}
}
