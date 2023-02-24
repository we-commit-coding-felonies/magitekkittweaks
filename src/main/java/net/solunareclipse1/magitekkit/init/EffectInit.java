package net.solunareclipse1.magitekkit.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.solunareclipse1.magitekkit.MagiTekkit;

public class EffectInit {
	private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MagiTekkit.MODID);

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        SOUNDS.register(bus);
    }

    // Sounds
    public static final RegistryObject<SoundEvent> SHIELD_AMBIENT = registerSound("item.alchshield.ambient");
    public static final RegistryObject<SoundEvent> SHIELD_PROTECT = registerSound("item.alchshield.protect");
    public static final RegistryObject<SoundEvent> SHIELD_FAIL = registerSound("item.alchshield.fail");
    public static final RegistryObject<SoundEvent> EMC_WASTE = registerSound("item.emc.waste");
    public static final RegistryObject<SoundEvent> ARMOR_ABSORB = registerSound("item.matterarmor.absorb");
    public static final RegistryObject<SoundEvent> ARMOR_BREAK = registerSound("item.matterarmor.break");
    public static final RegistryObject<SoundEvent> JEWELRY_BREAK = registerSound("item.jewelry.break");
    
    
    private static RegistryObject<SoundEvent> registerSound(String name) {
    	return SOUNDS.register(name, () -> new SoundEvent(new ResourceLocation(MagiTekkit.MODID, name)));
    }
}