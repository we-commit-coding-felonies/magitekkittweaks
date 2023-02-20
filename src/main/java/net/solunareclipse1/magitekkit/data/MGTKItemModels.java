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
        
        singleTexture(ObjectInit.VOID_HELM.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/armor/void/helm"));
        singleTexture(ObjectInit.VOID_CHEST.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/armor/void/chest"));
        singleTexture(ObjectInit.VOID_LEGS.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/armor/void/legs"));
        singleTexture(ObjectInit.VOID_BOOTS.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/armor/void/boots"));
        
        singleTexture(ObjectInit.PHIL_HELM.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/armor/phil/helm"));
        singleTexture(ObjectInit.PHIL_CHEST.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/armor/phil/chest"));
        singleTexture(ObjectInit.PHIL_LEGS.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/armor/phil/legs"));
        singleTexture(ObjectInit.PHIL_BOOTS.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/armor/phil/boots"));
    }
}
