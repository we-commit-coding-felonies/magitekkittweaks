package net.solunareclipse1.magitekkit.util;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;

import net.solunareclipse1.magitekkit.util.ColorsHelper.Color;

/**
 * contains stuff for working with Strings, Components and other similar stuff
 * @author solunareclipse1
 */
public class TextHelper {

	public static void appendSpeedTooltip(byte mode, List<Component> tips, Component keyText) {
		Style speedStyle = Style.EMPTY;
		switch (mode) {
		case 0:
			speedStyle = speedStyle.withColor(Color.RED_MATTER.I);
			break;
		case 1:
			speedStyle = speedStyle.withColor(Color.COVALENCE_TEAL.I);
			break;
		case 2:
			speedStyle = speedStyle.withColor(Color.COVALENCE_GREEN_TRUE.I);
			break;
		}
		tips.add(new TranslatableComponent("tip.mgtk.crimson.tool.speed"));
		tips.add(new TranslatableComponent("tip.mgtk.crimson.tool.speed.1", keyText));
		tips.add(new TranslatableComponent("tip.mgtk.crimson.tool.speed.2",
				new TranslatableComponent("tip.mgtk.crimson.tool.speed.mode."+mode).withStyle(speedStyle)));
	}

	public static void appendSafetyTooltip(boolean safety, List<Component> tips, Component keyText) {
		tips.add(new TranslatableComponent("tip.mgtk.crimson.tool", keyText));
		String modeLang = "tip.mgtk.crimson.tool.mode."+safety;
		ChatFormatting modeStyle = safety ? ChatFormatting.GREEN : ChatFormatting.RED;
		Component stateText = new TranslatableComponent(modeLang).withStyle(modeStyle);
		Component descText = new TranslatableComponent(modeLang+".full");
		tips.add(new TranslatableComponent("tip.mgtk.crimson.tool.1", stateText, descText));
	}
}
