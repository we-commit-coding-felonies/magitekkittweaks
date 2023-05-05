package net.solunareclipse1.magitekkit.init;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import net.solunareclipse1.magitekkit.config.DebugCfg;
import net.solunareclipse1.magitekkit.config.EmcCfg;

public class ConfigInit {

	public static void init() {
		registerClientConfigs();
		registerServerConfigs();
		registerCommonConfigs();
	}

    private static void registerClientConfigs() {
        ForgeConfigSpec.Builder CLIENT_CFG = new ForgeConfigSpec.Builder();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CFG.build());
    }

    private static void registerServerConfigs() {
        ForgeConfigSpec.Builder SERVER_CFG = new ForgeConfigSpec.Builder();
        DebugCfg.registerServerConfig(SERVER_CFG);
        EmcCfg.registerServerConfig(SERVER_CFG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CFG.build());
    }

    private static void registerCommonConfigs() {
        ForgeConfigSpec.Builder COMMON_CFG = new ForgeConfigSpec.Builder();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CFG.build());
    }
}
