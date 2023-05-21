package net.solunareclipse1.magitekkit.config;

import net.minecraftforge.common.ForgeConfigSpec.*;

/**
 * EMC values for things
 * @author solunareclipse1
 */
public class EmcCfg {
	// Client
	
	// Server
	public class Gem {
		public static LongValue
			SHIELD_MIN;
		public static DoubleValue
			SHIELD_EXP;
		public class Head {
			public static LongValue
				BREATH,
				CLAIRVOYANCE;
		}
		public class Chest {
			public static LongValue
				REJUVENATE,
				CAPACITY,
				AUTOCHARGE,
				TRICKLE,
				RADLEAK;
		}
		public class Legs {
			public static LongValue
				FASTDROP,
				MAGNET;
		}
		public class Feet {
			public static LongValue
				FLIGHT,
				JESUS;
		}
	}
	public class Arcana {
		// Left, Right, Proj, Extra
		//public class Mind {}
		public class WOFT {
			public static LongValue
				GRAVITY,
				JOJO,
				JOJO_STRONG,
				TICKACCEL,
				TELEPORT;
		}
		public class Harvest {
			public static LongValue
				WITHERVINE,
				INSTAGROW,
				AOEGROW,
				HARVEST;
		}
		public class Liquid {
			public static LongValue
				DESTROY,
				CREATE,
				ORB;
		}
		public class Philo {
			public static LongValue
				TRANSMUTE,
				ITEMIZE,
				DIVINING,
				ORB;
		}
		public class Archangel {
			public static LongValue
				ARROW,
				SMART,
				HOMING;
		}
		public class SWRG {
			public static LongValue
				GUST,
				SMITE,
				STORM;
		}
		public class Zero {
			public static LongValue
				EXTINGUISH,
				FREEZE,
				ICESHIELD;
		}
		public class Ignition {
			public static LongValue
				FIREBALL,
				TNT,
				BURN,
				MUSTANG;
		}
	}
	
	public static void registerServerConfig(Builder cfg) {
		cfg.comment("Here you can change the EMC values of various things");
		
		cfg.push("Gem Jewellery");
		Gem.SHIELD_MIN = cfg
				.comment("Minimum EMC cost per hit of the Alchemical Barrier")
				.defineInRange("gemShieldMin", 64, 0, Long.MAX_VALUE);
		Gem.SHIELD_EXP = cfg
				.comment("The exponent used in EMC cost calculation. Heres a graph: https://www.desmos.com/calculator/isf4jzlo4d",
						"P corresponds to this config setting, and M is damage-type specific modifier (for most things its just 1)")
				.defineInRange("gemShieldExp", 2.0, 0.1, 5.0);
		Gem.Head.BREATH = cfg
				.comment("EMC used by the Circlet to breathe underwater")
				.defineInRange("gemHeadBreath", 24, 0, Long.MAX_VALUE);
		Gem.Head.CLAIRVOYANCE = cfg
				.comment("Cost of the Circlet night vision & mob xray")
				.defineInRange("gemHeadClairvoyance", 42, 0, Long.MAX_VALUE);
		Gem.Chest.REJUVENATE = cfg
				.comment("Cost per feed / heal operation of the Amulet")
				.defineInRange("gemChestRejuvenate", 72, 0, Long.MAX_VALUE);
		Gem.Chest.CAPACITY = cfg
				.comment("EMC storage capacity of the Amulet")
				.defineInRange("gemChestCapacity", 384000, 1, Long.MAX_VALUE);
		Gem.Chest.AUTOCHARGE = cfg
				.comment("Rate of Amulet autorefill, in EMC/tick. 0 to disable")
				.defineInRange("gemChestAutocharge", 38400, 0, Long.MAX_VALUE);
		Gem.Chest.TRICKLE = cfg
				.comment("Amount of EMC regenerated each time the Amulet blocks incoming radiation. 0 to disable.")
				.defineInRange("gemChestTrickle", 1, 0, 96l);
		Gem.Chest.RADLEAK = cfg
				.comment("max emc that can get converted to radiation when leaking. 0 to disable radiation leaking.")
				.defineInRange("gemChestRadleak", 8192, 0, 8192l);
		Gem.Legs.FASTDROP = cfg
				.comment("Cost per tick when fast-falling with the Timepiece")
				.defineInRange("gemLegsFastfall", 1, 0, Long.MAX_VALUE);
		Gem.Legs.MAGNET = cfg
				.comment("Cost per tick to magnet a single item with the Timepiece")
				.defineInRange("gemLegsMagnet", 1, 0, Long.MAX_VALUE);
		Gem.Feet.FLIGHT = cfg
				.comment("Cost per tick to fly with the Anklets")
				.defineInRange("gemFeetFlight", 1, 0, Long.MAX_VALUE);
		Gem.Feet.JESUS = cfg
				.comment("EMC consumed per tick while walking on liquids with the Anklets")
				.defineInRange("gemFeetJesus", 3, 0, Long.MAX_VALUE);
		cfg.pop();
		
		
		
		cfg.push("Band of Arcana");
		// WOFT
		Arcana.WOFT.GRAVITY = cfg
				.comment("EMC cost of the gravity manipulation")
				.defineInRange("arcanaWoftGravity", 98, 0, Long.MAX_VALUE);
		Arcana.WOFT.JOJO = cfg
				.comment("Cost per tick of the self time acceleration (speed boost)")
				.defineInRange("arcanaWoftJojo", 128, 0, Long.MAX_VALUE);
		Arcana.WOFT.JOJO_STRONG = cfg
				.comment("Cost per tick of the 'global' time acceleration (daytime speedup)")
				.defineInRange("arcanaWoftJojoStrong", 2048, 0, Long.MAX_VALUE);
		Arcana.WOFT.TICKACCEL = cfg
				.comment("Cost per extra tick when time accelerating blocks")
				.defineInRange("arcanaWoftTickaccel", 128, 0, Long.MAX_VALUE);
		Arcana.WOFT.TELEPORT = cfg
				.comment("EMC consumed when teleporting")
				.defineInRange("arcanaWoftTeleport", 144, 0, Long.MAX_VALUE);
		
		// Harvest
		Arcana.Harvest.WITHERVINE = cfg
				.comment("EMC cost of the 'wither vine' attack")
				.defineInRange("arcanaHarvestWithervine", 860, 0, Long.MAX_VALUE);
		Arcana.Harvest.INSTAGROW = cfg
				.comment("Amount of EMC used to instantly fully grow a plant")
				.defineInRange("arcanaHarvestInstagrow", 512, 0, Long.MAX_VALUE);
		Arcana.Harvest.AOEGROW = cfg
				.comment("Cost to perform the AOE plant growth")
				.defineInRange("arcanaHarvestAoegrow", 768, 0, Long.MAX_VALUE);
		Arcana.Harvest.HARVEST = cfg
				.comment("EMC used to harvest nearby fully-grown plants")
				.defineInRange("arcanaHarvestHarvest", 384, 0, Long.MAX_VALUE);
		
		// Liquid
		Arcana.Liquid.DESTROY = cfg
				.comment("EMC used to annihilate a liquid")
				.defineInRange("arcanaLiquidDestroy", 0, 0, Long.MAX_VALUE);
		Arcana.Liquid.CREATE = cfg
				.comment("EMC used to place a liquid")
				.defineInRange("arcanaLiquidCreate", 64, 0, Long.MAX_VALUE);
		Arcana.Liquid.ORB = cfg
				.comment("Cost to fire a liquid orb")
				.defineInRange("arcanaLiquidOrb", 256, 0, Long.MAX_VALUE);
		
		// Philo
		Arcana.Philo.TRANSMUTE = cfg
				.comment("EMC used by the transmutation punch attack")
				.defineInRange("arcanaPhiloTransmute", 1, 0, Long.MAX_VALUE);
		Arcana.Philo.ITEMIZE = cfg
				.comment("EMC cost of the 'itemizer' effect")
				.defineInRange("arcanaPhiloItemize", 131072, 0, Long.MAX_VALUE);
		Arcana.Philo.DIVINING = cfg
				.comment("EMC per block scanned by the divining rod")
				.defineInRange("arcanaPhiloItemize", 10, 0, Long.MAX_VALUE);
		Arcana.Philo.ORB = cfg
				.comment("Cost of the projectile")
				.defineInRange("arcanaPhiloOrb", 1024, 0, Long.MAX_VALUE);
		
		// Archangel
		Arcana.Archangel.ARROW = cfg
				.comment("EMC used to shoot a basic arrow")
				.defineInRange("arcanaArchangelArrow", 64, 0, Long.MAX_VALUE);
		Arcana.Archangel.SMART = cfg
				.comment("EMC to conjure a smart/aimbot arrow")
				.defineInRange("arcanaArchangelSmart", 384, 0, Long.MAX_VALUE);
		Arcana.Archangel.HOMING = cfg
				.comment("Amount of EMC to summon a Sentient Arrow")
				.defineInRange("arcanaArchangelHoming", 4096, 0, Long.MAX_VALUE);
		
		// SWRG
		Arcana.SWRG.GUST = cfg
				.comment("EMC used when manipulating wind")
				.defineInRange("arcanaSwrgGust", 28, 0, Long.MAX_VALUE);
		Arcana.SWRG.SMITE = cfg
				.comment("Cost of smiting something")
				.defineInRange("arcanaSwrgSmite", 2304, 0, Long.MAX_VALUE);
		Arcana.SWRG.STORM = cfg
				.comment("EMC used to start a thunderstorm")
				.defineInRange("arcanaSwrgStorm", 65536, 0, Long.MAX_VALUE);
		
		// Zero
		Arcana.Zero.EXTINGUISH = cfg
				.comment("EMC to extinguish fire / defuse tnt")
				.defineInRange("arcanaZeroExtinguish", 1, 1, Long.MAX_VALUE);
		Arcana.Zero.FREEZE = cfg
				.comment("EMC used to flash-freeze things")
				.defineInRange("arcanaZeroFreeze", 273, 0, Long.MAX_VALUE);
		Arcana.Zero.ICESHIELD = cfg
				.comment("ice shield cost")
				.defineInRange("arcanaZeroIceshield", 460, 0, Long.MAX_VALUE);
		
		// Ignition
		Arcana.Ignition.FIREBALL = cfg
				.comment("fireball cost")
				.defineInRange("arcanaIgnitionFireball", 768, 0, Long.MAX_VALUE);
		Arcana.Ignition.TNT = cfg
				.comment("tnt throw cost")
				.defineInRange("arcanaIgnitionTnt", 1536, 0, Long.MAX_VALUE);
		Arcana.Ignition.BURN = cfg
				.comment("EMC used to flash-burn things")
				.defineInRange("arcanaIgnitionBurn", 451, 0, Long.MAX_VALUE);
		Arcana.Ignition.MUSTANG = cfg
				.comment("Cost of the Alchemical Flameburst projectile")
				.defineInRange("arcanaIgnitionMustang", 20340, 0, Long.MAX_VALUE);
	}
	
	// Common
}
