package net.solunareclipse1.magitekkit;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.solunareclipse1.magitekkit.init.ClientInit;
import net.solunareclipse1.magitekkit.init.ModInit;
import net.solunareclipse1.magitekkit.init.ObjectInit;

@Mod(MagiTekkit.MODID)
public class MagiTekkit {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "magitekkit";
    public static final String DISPLAYNAME = "MagiTekkit";

    public MagiTekkit() {
        ObjectInit.init();
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(ModInit::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modbus.addListener(ClientInit::init));
    }
}
