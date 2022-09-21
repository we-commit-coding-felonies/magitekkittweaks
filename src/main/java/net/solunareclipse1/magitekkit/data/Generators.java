package net.solunareclipse1.magitekkit.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.solunareclipse1.magitekkit.MagiTekkit;

@Mod.EventBusSubscriber(modid = MagiTekkit.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Generators {
	@SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(new MGTKRecipes(generator));
            generator.addProvider(new MGTKLootTables(generator));
            MGTKBlockTags blockTags = new MGTKBlockTags(generator, event.getExistingFileHelper());
            generator.addProvider(blockTags);
            generator.addProvider(new MGTKItemTags(generator, blockTags, event.getExistingFileHelper()));
        }
        if (event.includeClient()) {
            generator.addProvider(new MGTKBlockStates(generator, event.getExistingFileHelper()));
            generator.addProvider(new MGTKItemModels(generator, event.getExistingFileHelper()));
            generator.addProvider(new MGTKLanguageProvider(generator, "en_us"));
        }
    }	
}
