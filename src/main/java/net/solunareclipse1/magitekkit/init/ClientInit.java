package net.solunareclipse1.magitekkit.init;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.solunareclipse1.magitekkit.MagiTekkit;

@Mod.EventBusSubscriber(modid = MagiTekkit.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientInit {
    public static void init(final FMLClientSetupEvent event) {
    }
}