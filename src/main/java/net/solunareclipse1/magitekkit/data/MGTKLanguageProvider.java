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
        add("itemGroup." + MagiTekkit.MODID, "ProjectTweaks");
        
        add(ObjectInit.GANTIUM_BLOCK.get(), "Block of Gantium");
        
        add(ObjectInit.VOID_HELM.get(), "Helmet of the Void");
        add(ObjectInit.VOID_CHEST.get(), "Chestplate of the Void");
        add(ObjectInit.VOID_LEGS.get(), "Leggings of the Void");
        add(ObjectInit.VOID_BOOTS.get(), "Boots of the Void");
        
        add(ObjectInit.PHIL_HELM.get(), "Philosophical Helmet");
        add(ObjectInit.PHIL_CHEST.get(), "Philosophical Chestplate");
        add(ObjectInit.PHIL_LEGS.get(), "Philosophical Leggings");
        add(ObjectInit.PHIL_BOOTS.get(), "Philosophical Boots");
        
        add("toolTip.pt.burnout", "Burnout: %1$s / %2$s");
    }
}
