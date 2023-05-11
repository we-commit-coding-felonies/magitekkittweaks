package net.solunareclipse1.magitekkit.client.render;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.solunareclipse1.magitekkit.common.entity.projectile.SentientArrow;

public class SentientArrowRenderer extends ArrowRenderer<SentientArrow> {
	public static final ResourceLocation ACTIVE_TEXTURE = new ResourceLocation("magitekkit", "textures/entity/projectile/sentient_arrow.png");
	public static final ResourceLocation INERT_TEXTURE = new ResourceLocation("magitekkit", "textures/entity/projectile/sentient_arrow_inert.png");

	public SentientArrowRenderer(Context ctx) {
		super(ctx);
	}
	
	@Override
	public ResourceLocation getTextureLocation(SentientArrow arrow) {
		return !arrow.isInert() ? ACTIVE_TEXTURE : INERT_TEXTURE;
	}
	
	@Override
	protected int getBlockLightLevel(SentientArrow arrow, BlockPos pos) {
		return !arrow.isInert() ? 15 : super.getBlockLightLevel(arrow, pos);
	}

}
