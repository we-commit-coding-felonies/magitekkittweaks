package net.solunareclipse1.magitekkit.config;

import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

public class DebugCfg {
	// Client
	
	// Server
	public static BooleanValue
					ARROW_PATHFIND,
					MUSTANG_HITBOX,
					XRAY_HITBOX,
					GUST_HITBOX;
	public static void registerServerConfig(Builder cfg) {
		cfg.comment("Settings used for debugging. Enable these at your own risk.").push("debug");
		ARROW_PATHFIND = cfg
				.comment("Creates particles along Sentient Arrow pathfinds")
				.define("debugArrowPathfind", false);
		MUSTANG_HITBOX = cfg
				.comment("Outlines the mustang explosion hitbox")
				.define("debugMustangHitbox", false);
		XRAY_HITBOX = cfg
				.comment("Outlines the 'mob xray' effect of the circlet")
				.define("debugXrayHitbox", false);
		GUST_HITBOX = cfg
				.comment("swrg gust effect")
				.define("debugGustHitbox", false);
		cfg.pop();
	}
	
	// Common
	public static BooleanValue
					ARROW_LOG;
	public static void registerCommonConfig(Builder cfg) {
		cfg.comment("Settings used for debugging. Enable these at your own risk.").push("debug");
		ARROW_LOG = cfg
				.comment("Enable sentient arrow debug logs")
				.define("debugArrowLogs", false);
		cfg.pop();
	}
}
