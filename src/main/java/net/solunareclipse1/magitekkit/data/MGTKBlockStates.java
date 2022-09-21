package net.solunareclipse1.magitekkit.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.init.ObjectInit;

public class MGTKBlockStates extends BlockStateProvider {

    public MGTKBlockStates(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, MagiTekkit.MODID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(ObjectInit.GANTIUM_BLOCK.get());
    }
}