package net.solunareclipse1.magitekkit;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.solunareclipse1.magitekkit.init.ClientInit;
import net.solunareclipse1.magitekkit.init.ConfigInit;
import net.solunareclipse1.magitekkit.init.EffectInit;
import net.solunareclipse1.magitekkit.init.ModInit;
import net.solunareclipse1.magitekkit.init.ObjectInit;
import net.solunareclipse1.magitekkit.util.LoggerHelper;

@Mod(MagiTekkit.MODID)
public class MagiTekkit {
    public static final String MODID = "magitekkit";
    public static final String DISPLAYNAME = "MagiTekkit Tweaks";

    public MagiTekkit() {
    	LoggerHelper.printInfo("Main", "IsLoading", "Hello from MagiTekkit Tweaks!");
        ObjectInit.init();
        EffectInit.init();
        ConfigInit.init();
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(ModInit::init);
        //modbus.addListener(this::registerCaps);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modbus.addListener(ClientInit::init));
    }
    
    
    private void registerCaps(RegisterCapabilitiesEvent evt) {
    	// TODO: possibly migrate some stuff over to this (such as emc shield)
    }
}
