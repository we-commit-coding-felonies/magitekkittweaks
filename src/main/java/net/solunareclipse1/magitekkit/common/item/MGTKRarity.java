package net.solunareclipse1.magitekkit.common.item;

import java.util.function.UnaryOperator;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;

public enum MGTKRarity {
	ALCHEMICAL("ALCHEMICAL", ChatFormatting.AQUA),
	VOID("VOID", ChatFormatting.DARK_PURPLE),
	CRIMSON("CRIMSON", ChatFormatting.DARK_RED),
	MAGNUMOPUS("MAGNUMOPUS", ChatFormatting.GOLD)
	;
	
	private final Rarity rarity;

	private MGTKRarity(String name, ChatFormatting color) {
		rarity = Rarity.create(name, color);
	}
	private MGTKRarity(String name, UnaryOperator<Style> styleMod) {
		rarity = Rarity.create(name, styleMod);
	}
	
	public Rarity get() {
		return this.rarity;
	}
}
