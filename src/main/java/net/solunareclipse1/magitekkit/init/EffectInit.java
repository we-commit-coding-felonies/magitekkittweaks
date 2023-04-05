package net.solunareclipse1.magitekkit.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.common.effect.TransmutingEffect;

public class EffectInit {
	private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MagiTekkit.MODID);
    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MagiTekkit.MODID);

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        SOUNDS.register(bus);
        MOB_EFFECTS.register(bus);
    }

    ////// Sounds
    // God
    public static final RegistryObject<SoundEvent> GOD_CHAT = registerSound("god.chat");
    public static final RegistryObject<SoundEvent> GOD_ATTACK = registerSound("god.attack");
    // AlchShield
    public static final RegistryObject<SoundEvent> SHIELD_AMBIENT = registerSound("alchshield.ambient");
    public static final RegistryObject<SoundEvent> SHIELD_PROTECT = registerSound("alchshield.protect");
    public static final RegistryObject<SoundEvent> SHIELD_FAIL = registerSound("alchshield.fail");
    public static final RegistryObject<SoundEvent> SHIELD_IGNORED = registerSound("alchshield.ignored");
    // EMC
    public static final RegistryObject<SoundEvent> EMC_WASTE = registerSound("emc.waste");
    public static final RegistryObject<SoundEvent> EMC_LEAK = registerSound("emc.leak");
    //// Items
    // Armor
    public static final RegistryObject<SoundEvent> ARMOR_ABSORB = registerSound("item.matterarmor.absorb");
    public static final RegistryObject<SoundEvent> ARMOR_BREAK = registerSound("item.matterarmor.degrade");
    public static final RegistryObject<SoundEvent> JEWELRY_BREAK = registerSound("item.matterarmor.shatter");
    // Band of Arcana
    public static final RegistryObject<SoundEvent> BOA_MODE = registerSound("item.boa.mode");
    // WOFT
    public static final RegistryObject<SoundEvent> WOFT_TICK = registerSound("item.woft.tick");
    public static final RegistryObject<SoundEvent> WOFT_MODE = registerSound("item.woft.mode");
    public static final RegistryObject<SoundEvent> WOFT_ATTRACT = registerSound("item.woft.attract");
    public static final RegistryObject<SoundEvent> WOFT_REPEL = registerSound("item.woft.repel");
    public static final RegistryObject<SoundEvent> WOFT_BLINK = registerSound("item.woft.blink");
    // Liquid
    public static final RegistryObject<SoundEvent> LIQUID_DESTROY = registerSound("item.boa.liquid.destroy");
    public static final RegistryObject<SoundEvent> LIQUID_WATER_SWITCH = registerSound("item.boa.liquid.water.switch");
    public static final RegistryObject<SoundEvent> LIQUID_WATER_CREATE = registerSound("item.boa.liquid.water.create");
    public static final RegistryObject<SoundEvent> LIQUID_WATER_WEATHER = registerSound("item.boa.liquid.water.weather");
    public static final RegistryObject<SoundEvent> LIQUID_LAVA_SWITCH = registerSound("item.boa.liquid.lava.switch");
    public static final RegistryObject<SoundEvent> LIQUID_LAVA_CREATE = registerSound("item.boa.liquid.lava.create");
    public static final RegistryObject<SoundEvent> LIQUID_LAVA_WEATHER = registerSound("item.boa.liquid.lava.weather");
    // Philo
    public static final RegistryObject<SoundEvent> PHILO_TRANSMUTE = registerSound("item.philo.transmute");
    public static final RegistryObject<SoundEvent> PHILO_ATTACK = registerSound("item.philo.attack");
    public static final RegistryObject<SoundEvent> PHILO_ITEMIZE = registerSound("item.philo.itemize");
    public static final RegistryObject<SoundEvent> PHILO_XRAY = registerSound("item.philo.xray");
    public static final RegistryObject<SoundEvent> PHILO_3X3GUI = registerSound("item.philo.3x3gui");
    // Archangels
    public static final RegistryObject<SoundEvent> ARCHANGELS_REDIRECT = registerSound("item.archangels.redirect");
    public static final RegistryObject<SoundEvent> ARCHANGELS_SENTIENT_AMBIENT = registerSound("item.archangels.sentient.ambient");
    public static final RegistryObject<SoundEvent> ARCHANGELS_SENTIENT_HIT = registerSound("item.archangels.sentient.hit");
    public static final RegistryObject<SoundEvent> ARCHANGELS_EXPIRE = registerSound("item.archangels.expire");
    // SWRG
    public static final RegistryObject<SoundEvent> SWRG_SMITE = registerSound("item.swrg.smite");
    public static final RegistryObject<SoundEvent> SWRG_WEATHER = registerSound("item.swrg.weather");
    public static final RegistryObject<SoundEvent> SWRG_BOOST = registerSound("item.swrg.boost");
    // Zero
    public static final RegistryObject<SoundEvent> ZERO_FREEZE = registerSound("item.zero.freeze");
    // Ignition
    public static final RegistryObject<SoundEvent> IGNITION_CLICK = registerSound("item.ignition.click");
    public static final RegistryObject<SoundEvent> IGNITION_BURN = registerSound("item.ignition.burn");
    
    
    
    
    
    
    
    //// MobEffects
    public static final RegistryObject<MobEffect> TRANSMUTING = MOB_EFFECTS.register("transmuting", () -> new TransmutingEffect());
    
    
    private static RegistryObject<SoundEvent> registerSound(String name) {
    	return SOUNDS.register(name, () -> new SoundEvent(new ResourceLocation(MagiTekkit.MODID, name)));
    }
}