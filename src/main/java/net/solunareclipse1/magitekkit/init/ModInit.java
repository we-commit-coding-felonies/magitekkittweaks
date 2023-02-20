package net.solunareclipse1.magitekkit.init;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.solunareclipse1.magitekkit.MagiTekkit;

public class ModInit {
    public static void init(final FMLCommonSetupEvent event) {
    }
    
    public static final String TAB_NAME = MagiTekkit.MODID;
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(TAB_NAME) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ObjectInit.GANTIUM_BLOCK.get());
        }
    };
}