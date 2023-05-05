package net.solunareclipse1.magitekkit.config;

import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

public class DebugCfg {
	// Client
	
	// Server
	public static BooleanValue ARROW_PATHFIND;
	public static BooleanValue MUSTANG_HITBOX;
	public static BooleanValue XRAY_HITBOX;
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
		cfg.pop();
	}
	
	// Common
}
