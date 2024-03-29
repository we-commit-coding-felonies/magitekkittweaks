package net.solunareclipse1.magitekkit.data;

import net.minecraft.data.DataGenerator;

import net.solunareclipse1.magitekkit.init.ObjectInit;

public class MGTKLootTables extends MGTKLootTableProvider {
	public MGTKLootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
    	blockLootTables.put(ObjectInit.GANTIUM_BLOCK.get(), createSimpleBlockTable("gantium_block", ObjectInit.GANTIUM_BLOCK.get()));
    }
}
