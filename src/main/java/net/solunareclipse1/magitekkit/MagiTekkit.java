package net.solunareclipse1.magitekkit;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.solunareclipse1.magitekkit.init.ClientInit;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.init.ModInit;
import net.solunareclipse1.magitekkit.init.ObjectInit;
import net.solunareclipse1.magitekkit.util.LoggerHelper;

@Mod(MagiTekkit.MODID)
public class MagiTekkit {
    public static final String MODID = "magitekkit";
    public static final String DISPLAYNAME = "MagiTekkit Tweaks";
    public static final boolean DEBUG = true;

    public MagiTekkit() {
    	LoggerHelper.printInfo("Main", "IsLoading", "Hello from MagiTekkit Tweaks!");
        ObjectInit.init();
        EffectInit.init();
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(ModInit::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modbus.addListener(ClientInit::init));
    }
}
