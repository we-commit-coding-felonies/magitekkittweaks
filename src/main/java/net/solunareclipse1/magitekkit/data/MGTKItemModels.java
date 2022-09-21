package net.solunareclipse1.magitekkit.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.init.ObjectInit;

public class MGTKItemModels extends ItemModelProvider {
	public MGTKItemModels(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, MagiTekkit.MODID, helper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(ObjectInit.GANTIUM_BLOCK_ITEM.getId().getPath(), modLoc("block/gantium_block"));
    }
}
