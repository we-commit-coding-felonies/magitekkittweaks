package net.solunareclipse1.magitekkit.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.init.ObjectInit;

public class MGTKLanguageProvider extends LanguageProvider {
	public MGTKLanguageProvider(DataGenerator gen, String locale) {
        super(gen, MagiTekkit.MODID, locale);
    }

    @Override
    protected void addTranslations() {
    	// blocks & items
        add("itemGroup." + MagiTekkit.MODID, "MagiTekkit");
        
        add(ObjectInit.GANTIUM_BLOCK.get(), "Block of Gantium");
        
        add(ObjectInit.COVALENCE_BRACELET.get(), "Covalence Bracelet");

        add(ObjectInit.VOID_SWORD.get(), "Void Sword");
        add(ObjectInit.VOID_PICKAXE.get(), "Void Pickaxe");
        add(ObjectInit.VOID_SHOVEL.get(), "Void Shovel");
        add(ObjectInit.VOID_AXE.get(), "Void Axe");
        add(ObjectInit.VOID_HOE.get(), "Void Hoe");
        add(ObjectInit.VOID_HELM.get(), "Helmet of the Void");
        add(ObjectInit.VOID_CHEST.get(), "Chestplate of the Void");
        add(ObjectInit.VOID_LEGS.get(), "Leggings of the Void");
        add(ObjectInit.VOID_BOOTS.get(), "Boots of the Void");
        
        add(ObjectInit.CRIMSON_SWORD.get(), "Crimson Sword");
        add(ObjectInit.CRIMSON_HELM.get(), "Crimson Helmet");
        add(ObjectInit.CRIMSON_CHEST.get(), "Crimson Chestplate");
        add(ObjectInit.CRIMSON_LEGS.get(), "Crimson Leggings");
        add(ObjectInit.CRIMSON_BOOTS.get(), "Crimson Boots");
        
        add(ObjectInit.GEM_CIRCLET.get(), "Abyssal Circlet");
        add(ObjectInit.GEM_AMULET.get(), "Infernal Amulet");
        add(ObjectInit.GEM_TIMEPIECE.get(), "Gravity's Timepiece");
        add(ObjectInit.GEM_ANKLET.get(), "Hurricane Anklet");
        add(ObjectInit.GEM_BRACELET.get(), "Band of Arcana");

        add("effect.magitekkit.transmuting", "Transmuting");
        add("effect.magitekkit.ice_shield", "Frozen Solid");
        
        // tooltips
        add("tip.mgtk.arcana.1", "Channels the latent power of Gem Jewellery");
        add("tip.mgtk.arcana.2", "Press %s to change mode, %s to toggle Covalence");
        add("tip.mgtk.arcana.3", "Hold %s to modify certain abilities");
        add("tip.mgtk.arcana.4", "Current mode: %s");
        add("tip.mgtk.arcana.guide", "%s: %s");
        add("tip.mgtk.arcana.guide.alt", "%s+%s: %s");
        add("tip.mgtk.arcana.guide.1.1", "Withdraw 1 Level");
        add("tip.mgtk.arcana.guide.1.1.alt", "Withdraw 10 Levels");
        add("tip.mgtk.arcana.guide.1.2", "Deposit 1 Level");
        add("tip.mgtk.arcana.guide.1.2.alt", "Deposit 10 Levels");
        add("tip.mgtk.arcana.guide.1.3", "Deposit All XP");
        add("tip.mgtk.arcana.guide.1.3.alt", "Deposit All XP");
        add("tip.mgtk.arcana.guide.1.4", "Withdraw All XP");
        add("tip.mgtk.arcana.guide.1.4.alt", "Withdraw All XP");
        add("tip.mgtk.arcana.guide.2.1", "Gravity well (attract)");
        add("tip.mgtk.arcana.guide.2.1.alt", "Gravity well (repel)");
        add("tip.mgtk.arcana.guide.2.2", "Time acceleration");
        add("tip.mgtk.arcana.guide.2.2.alt", "Toggle global time acceleration");
        add("tip.mgtk.arcana.guide.2.3", "Blink");
        add("tip.mgtk.arcana.guide.2.3.alt", "Blink");
        add("tip.mgtk.arcana.guide.2.4", "Ender Chest");
        add("tip.mgtk.arcana.guide.2.4.alt", "Anvil");
        add("tip.mgtk.arcana.guide.3.1", "Withering vine");
        add("tip.mgtk.arcana.guide.3.1.alt", "Withering vine");
        add("tip.mgtk.arcana.guide.3.2", "Fertilize");
        add("tip.mgtk.arcana.guide.3.2.alt", "Fertilize");
        add("tip.mgtk.arcana.guide.3.3", "Clear potion effects");
        add("tip.mgtk.arcana.guide.3.3.alt", "Clear potion effects");
        add("tip.mgtk.arcana.guide.3.4", "Grow nearby");
        add("tip.mgtk.arcana.guide.3.4.alt", "Harvest nearby");
        add("tip.mgtk.arcana.guide.4.1", "Void liquid");
        add("tip.mgtk.arcana.guide.4.1.alt", "Void liquid");
        add("tip.mgtk.arcana.guide.4.2", "Place liquid");
        add("tip.mgtk.arcana.guide.4.2.alt", "Place liquid");
        add("tip.mgtk.arcana.guide.4.3", "Shoot liquid orb");
        add("tip.mgtk.arcana.guide.4.3.alt", "Shoot liquid orb");
        add("tip.mgtk.arcana.guide.4.4", "Change liquid type");
        add("tip.mgtk.arcana.guide.4.4.alt", "Change liquid type");
        add("tip.mgtk.arcana.guide.5.1", "Transmute");
        add("tip.mgtk.arcana.guide.5.1.alt", "Transmute");
        add("tip.mgtk.arcana.guide.5.2", "Divining Rod");
        add("tip.mgtk.arcana.guide.5.2.alt", "Divining Rod");
        add("tip.mgtk.arcana.guide.5.3", "Shoot transmutation orb");
        add("tip.mgtk.arcana.guide.5.3.alt", "Shoot transmutation orb");
        add("tip.mgtk.arcana.guide.5.4", "Craft");
        add("tip.mgtk.arcana.guide.5.4.alt", "Enchant");
        add("tip.mgtk.arcana.guide.6.1", "Shotgun");
        add("tip.mgtk.arcana.guide.6.1.alt", "Shotgun");
        add("tip.mgtk.arcana.guide.6.2", "Arrow stream");
        add("tip.mgtk.arcana.guide.6.2.alt", "Arrow stream");
        add("tip.mgtk.arcana.guide.6.3", "Summon / control Sentient Arrow");
        add("tip.mgtk.arcana.guide.6.3.alt", "Summon / control Sentient Arrow");
        add("tip.mgtk.arcana.guide.6.4", "Smart arrows");
        add("tip.mgtk.arcana.guide.6.4.alt", "Smart arrows");
        add("tip.mgtk.arcana.guide.7.1", "Gust");
        add("tip.mgtk.arcana.guide.7.1.alt", "Gust");
        add("tip.mgtk.arcana.guide.7.2", "Smite");
        add("tip.mgtk.arcana.guide.7.2.alt", "Smite");
        add("tip.mgtk.arcana.guide.7.3", "Smite nearby");
        add("tip.mgtk.arcana.guide.7.3.alt", "Smite nearby");
        add("tip.mgtk.arcana.guide.7.4", "Create thunderstorm");
        add("tip.mgtk.arcana.guide.7.4.alt", "Create thunderstorm");
        add("tip.mgtk.arcana.guide.8.1", "Extinguish nearby");
        add("tip.mgtk.arcana.guide.8.1.alt", "Extinguish nearby");
        add("tip.mgtk.arcana.guide.8.2", "Freeze");
        add("tip.mgtk.arcana.guide.8.2.alt", "Freeze");
        add("tip.mgtk.arcana.guide.8.3", "Ice shield");
        add("tip.mgtk.arcana.guide.8.3.alt", "Ice shield");
        add("tip.mgtk.arcana.guide.8.4", "Freeze nearby");
        add("tip.mgtk.arcana.guide.8.4.alt", "Freeze nearby");
        add("tip.mgtk.arcana.guide.9.1", "Fireballs");
        add("tip.mgtk.arcana.guide.9.1.alt", "Throw TNT");
        add("tip.mgtk.arcana.guide.9.2", "Ignite");
        add("tip.mgtk.arcana.guide.9.2.alt", "Ignite");
        add("tip.mgtk.arcana.guide.9.3", "Alchemical Flameburst");
        add("tip.mgtk.arcana.guide.9.3.alt", "Alchemical Flameburst");
        add("tip.mgtk.arcana.guide.9.4", "Ignite nearby");
        add("tip.mgtk.arcana.guide.9.4.alt", "Ignite nearby");
        add("tip.mgtk.arcana.mode.0", "Disabled");
        add("tip.mgtk.arcana.mode.1", "Mind");
        add("tip.mgtk.arcana.mode.2", "Gravity");
        add("tip.mgtk.arcana.mode.3", "Harvest");
        add("tip.mgtk.arcana.mode.4", "Liquid");
        add("tip.mgtk.arcana.mode.5", "Magnum Opus");
        add("tip.mgtk.arcana.mode.6", "Archangel");
        add("tip.mgtk.arcana.mode.7", "Rending Gale");
        add("tip.mgtk.arcana.mode.8", "Zero");
        add("tip.mgtk.arcana.mode.9", "Ignition");
        add("tip.mgtk.arcana.charge.on", "Covalence enabled");
        add("tip.mgtk.arcana.charge.off", "Covalence disabled");
        add("tip.mgtk.arcana.liquid.water", "Aqua");
        add("tip.mgtk.arcana.liquid.lava", "Magma");
        add("tip.mgtk.enchsynergy", "Grows stronger when enchanted");
        add("tip.mgtk.enchbonus", "Current bonus: %s%s");
        add("tip.mgtk.enchbonus.armor", "% Damage Reduction");
        add("tip.mgtk.enchbonus.weapon", " Attack Strength");
        add("tip.mgtk.enchbonus.tool", "% Faster Speed");
        add("tip.mgtk.enchbonus.unknown", "Current bonus: %s Unknown. This is a bug!");
        add("tip.mgtk.dyndr", "Currently providing %s damage reduction");
        add("tip.mgtk.burnout", "Burnout: %1$s / %2$s");
        add("tip.mgtk.gem.ref.1", "One shudders to imagine what inhuman thoughts lie within");
        add("tip.mgtk.gem.ref.2", "I am become death, the destroyer of worlds");
        add("tip.mgtk.gem.ref.3", "2... 3... 5... 7... 11... 13...");
        add("tip.mgtk.gem.ref.4", "Not to be confused with Anklet of the Wind");
        add("tip.mgtk.crimson.empower.1", "Abilities require empowerment.");
        add("tip.mgtk.crimson.empower.2", "Press %s to empower with EMC");
        add("tip.mgtk.crimson.armor", "Absorbs almost all damage, but don't overuse it...");
        add("tip.mgtk.crimson.armor.1", "Provides enormous amounts of protection by absorbing damage");
        add("tip.mgtk.crimson.armor.2", "Protection decreases with more damage absorbed, regenerates over time");
        add("tip.mgtk.crimson.armor.3", "Will violently release all stored energy at once if it absorbs to much");
        add("tip.mgtk.crimson.sword.autoslash", "Decimates foes with the powerful Autoslash");
        add("tip.mgtk.crimson.sword.autoslash.1", "Press %s to rend nearby creatures, %s to change mode");
        add("tip.mgtk.crimson.sword.autoslash.2", "Currently targeting: %s");
        add("tip.mgtk.crimson.sword.autoslash.hud", "Autoslash: %s");
        add("tip.mgtk.crimson.sword.killall.0", "Hostile only");
        add("tip.mgtk.crimson.sword.killall.1", "Hostile + Players");
        add("tip.mgtk.crimson.sword.killall.2", "All except Players");
        add("tip.mgtk.crimson.sword.killall.3", "Everything");
        

        add("gui.mgtk.philo.enchanter.name", "Arcanum");
        add("gui.mgtk.philo.enchanter.dust", "%s Covalence Dust");
        add("gui.mgtk.philo.enchanter.bonus", "Up to %s bonus enchantment levels");
        add("gui.mgtk.philo.enchanter.bonusamount", "Up to %s bonuses applied");
        
        add("gui.mgtk.woft.anvil.name", "Gravitational Anomaly");

        
        
        // subtitles
        add("subtitles.mgtk.alchshield.ambient", "Alchemical Barrier hums");
        add("subtitles.mgtk.alchshield.fail", "Barrier disintegrates");
        add("subtitles.mgtk.alchshield.ignored", "Barrier is pierced");
        add("subtitles.mgtk.alchshield.protect", "Barrier deflects");
        
        add("subtitles.mgtk.emc.leak", "EMC leaks");
        add("subtitles.mgtk.emc.waste", "EMC disperses");
        
        add("subtitles.mgtk.god.attack", "Deity attacks");
        add("subtitles.mgtk.god.chat", "Deity speaks");

        add("subtitles.mgtk.item.archangels.expire", "Magic arrow vanishes");
        add("subtitles.mgtk.item.archangels.redirect", "Magic arrow aims");
        add("subtitles.mgtk.item.archangels.sentient.ambient", "Whispers of sentience");
        add("subtitles.mgtk.item.archangels.sentient.hit", "Magic arrow transmutes something");
        add("subtitles.mgtk.item.archangels.sentient.yondu", "Alchemist retargets");
        
        add("subtitles.mgtk.item.boa.mode", "Band of Arcana changes mode");
        add("subtitles.mgtk.item.boa.liquid.destroy", "Liquid annihilated");
        add("subtitles.mgtk.item.boa.liquid.water.create", "Water transmuted");
        add("subtitles.mgtk.item.boa.liquid.water.switch", "Band of Arcana changes liquid");
        add("subtitles.mgtk.item.boa.liquid.lava.create", "Lava transmuted");
        add("subtitles.mgtk.item.boa.liquid.lava.switch", "Band of Arcana changes liquid");

        add("subtitles.mgtk.item.ignition.burn", "Atmosphere ignites");
        add("subtitles.mgtk.item.ignition.click", "Alchemical spark");

        add("subtitles.mgtk.item.matterarmor.equip", "Matter armor equipped");
        add("subtitles.mgtk.item.matterarmor.absorb", "Damage absorbed");
        add("subtitles.mgtk.item.matterarmor.degrade", "Armor degrades");
        add("subtitles.mgtk.item.matterarmor.shatter", "Jewellery shatters");

        add("subtitles.mgtk.item.philo.3x3gui", "Incorporeal block accessed");
        add("subtitles.mgtk.item.philo.attack", "Magnum Opus transmutes entity");
        add("subtitles.mgtk.item.philo.itemize", "Something becomes item");
        add("subtitles.mgtk.item.philo.transmute", "Minium transmutation");
        add("subtitles.mgtk.item.philo.xray", "Divining Rod activates");

        add("subtitles.mgtk.item.swrg.boost", "Alchemy-induced gust");
        add("subtitles.mgtk.item.swrg.smite", "Rending Gale smites");

        add("subtitles.mgtk.item.woft.attract", "Gravity pulls");
        add("subtitles.mgtk.item.woft.blink", "Alchemist teleports");
        add("subtitles.mgtk.item.woft.mode", "Pocketwatch clicks");
        add("subtitles.mgtk.item.woft.repel", "Gravity pushes");
        add("subtitles.mgtk.item.woft.tick", "Time accelerates");
        
        add("subtitles.mgtk.item.zero.freeze", "Frost magic");

        
        
        // death messages
        add("death.attack.matter_aoe", "%s was decimated by %s");
        add("death.attack.matter_aoe.item", "%s was butchered by %s's %s");
        add("death.attack.transmutation", "%s turned into an unspeakable horror");
        add("death.attack.transmutation.player", "%s was horrifically disfigured by %s");
        add("death.attack.transmutation.strong", "%s experienced the law of equivalent exchange");
        add("death.attack.transmutation.strong.player", "%s's carbon was transmuted by %s");
        add("death.attack.transmutation.potion", "%s was converted into EMC");
        add("death.attack.transmutation.potion.player", "%s is now a mere statistic thanks to %s");
        add("death.attack.mustang", "%s got phlogistonated by %s");
        add("death.attack.mustang.item", "%s was immolated by %s using %s");
        add("death.attack.emcnuke", "%s was atomized by a catastrophic energy release");
        add("death.attack.emcnuke.player", "%s underwent fission in an attempt to destroy %s");
        add("death.attack.god", "%s is a work of fiction");
        add("death.attack.god.player", "%s was retconned by %s");
        // since avaritia cant do it themselves, apparently
        add("death.attack.infinity", "%s stopped existing");
        add("death.attack.infinity.1", "%s was divided by zero");
        add("death.attack.infinity.2", "%s's entropy was maximized");
        add("death.attack.infinity.3", "%s is gone");
        add("death.attack.infinity.4", "%s--;");
    }
}
