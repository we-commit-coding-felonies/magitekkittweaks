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
        add("itemGroup." + MagiTekkit.MODID, "MagiTekkit");
        
        add(ObjectInit.GANTIUM_BLOCK.get(), "Block of Gantium");
        
        add(ObjectInit.COVALENCE_BRACELET.get(), "Covalence Bracelet");
        
        add(ObjectInit.VOID_HELM.get(), "Helmet of the Void");
        add(ObjectInit.VOID_CHEST.get(), "Chestplate of the Void");
        add(ObjectInit.VOID_LEGS.get(), "Leggings of the Void");
        add(ObjectInit.VOID_BOOTS.get(), "Boots of the Void");
        
        add(ObjectInit.CRIMSON_HELM.get(), "Crimson Helmet");
        add(ObjectInit.CRIMSON_CHEST.get(), "Crimson Chestplate");
        add(ObjectInit.CRIMSON_LEGS.get(), "Crimson Leggings");
        add(ObjectInit.CRIMSON_BOOTS.get(), "Crimson Boots");
        
        add(ObjectInit.GEM_CIRCLET.get(), "Abyssal Circlet");
        add(ObjectInit.GEM_AMULET.get(), "Infernal Amulet");
        add(ObjectInit.GEM_TIMEPIECE.get(), "Gravity's Timepiece");
        add(ObjectInit.GEM_ANKLET.get(), "Hurricane Anklet");
        add(ObjectInit.GEM_BRACELET.get(), "Band of Arcana");
        
        add("tip.mgtk.burnout", "Burnout: %1$s / %2$s");
        add("tip.mgtk.gem_circlet", "PLACEHOLDER");
        add("tip.mgtk.gem_amulet", "PLACEHOLDER: Perhaps wearing this around your neck isn't the best idea...");
        add("tip.mgtk.gem_timepiece", "2... 3... 5... 7... 11... 13...");
        add("tip.mgtk.gem_anklet", "Not to be confused with Anklet of the Wind");
        
        add("subtitles.mgtk.jewelry.break", "Jewellery shatters");
        add("subtitles.mgtk.alchshield.ambient", "Alchemical barrier hums");
        add("subtitles.mgtk.alchshield.protect", "Alchemical barrier deflects");
        add("subtitles.mgtk.alchshield.fail", "Alchemical barrier shatters");
        add("subtitles.mgtk.emc.waste", "EMC disperses");
        add("subtitles.mgtk.emc.leak", "Amulet leaks");
        add("subtitles.mgtk.matterarmor.absorb", "Damage absorbed");
        add("subtitles.mgtk.matterarmor.break", "Armor degrades");
    }
}
