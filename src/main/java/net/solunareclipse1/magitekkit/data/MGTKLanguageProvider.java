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
        add("itemGroup." + MagiTekkit.DISPLAYNAME, "MagiTekkit");
        add(ObjectInit.GANTIUM_BLOCK.get(), "Block of Gantium");
    }
}
