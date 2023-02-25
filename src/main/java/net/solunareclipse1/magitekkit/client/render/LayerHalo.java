package net.solunareclipse1.magitekkit.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.common.item.armor.gem.GemJewelryItemBase;

public class LayerHalo extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

	public LayerHalo(PlayerRenderer renderer) {
		super(renderer);
		this.renderer = renderer;
	}
	
	private final PlayerRenderer renderer;
	private float currentRotation = 0;
	private static final ResourceLocation HALO_TEXTURE = new ResourceLocation(MagiTekkit.MODID, "textures/models/halo.png");

	@Override
	public void render(PoseStack poseStack, MultiBufferSource renderBuffer, int bakedLight,
			AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick,
			float ageInTicks, float netHeadYaw, float headPitch)
	{
		//if (player.isInvisible()) return;
		for (ItemStack stack : player.getArmorSlots()) {
			if (!(stack.getItem() instanceof GemJewelryItemBase) || stack.isDamaged()) return;
		}
		
		poseStack.pushPose();
		renderer.getModel().head.translateAndRotate(poseStack);
		//if (player.isCrouching()) {
			// Only modify where it renders if the player's pose is crouching
			//poseStack.mulPose(Vector3f.XP.rotationDegrees(-28.64789F));
			//yShift = 1; //-0.44; // difference of 58
		//}
		poseStack.mulPose(Vector3f.XP.rotationDegrees(270)); // rotate upright
		//currentRotation++;
		//if (currentRotation >= 360) currentRotation = 0;
		poseStack.scale(1.5f, 1.5f, 1.5f);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(ageInTicks % 360)); // spinny
		poseStack.translate(-0.5, -0.25, -0.5); // positioning behind the head
		VertexConsumer builder = renderBuffer.getBuffer(MGTKRenderType.HALO_RENDERER.apply(HALO_TEXTURE));
		Matrix4f matrix4f = poseStack.last().pose();
		
		// fancy "breathing" effect
		// modified from DuraBarHelper.fadingBarColor
		float aVal, a1 = 192, a2 = 96, aDiff = a1-a2;
		int timer = Math.round(ageInTicks);
		int cycle = 250;
		int swap = (byte) (cycle / 2);
		float fade = (timer % cycle) % cycle;

		if (fade < swap) {
			aVal = a2 + ((aDiff * fade) / swap);
			//return Mth.hsvToRgb(hVal, sVal, vVal);
		} else {
			aVal = a1 - ((aDiff * (fade - swap)) / swap);
			//return Mth.hsvToRgb(hVal, sVal, vVal);
		}
		//System.out.println(aVal);
		int aFin = Math.round(aVal);
		builder.vertex(matrix4f, 0, 0, 0).color(179, 47, 103, aFin).uv(0, 0).endVertex();
		builder.vertex(matrix4f, 0, 0, 1).color(179, 47, 103, aFin).uv(0, 1).endVertex();
		builder.vertex(matrix4f, 1, 0, 1).color(179, 47, 103, aFin).uv(1, 1).endVertex();
		builder.vertex(matrix4f, 1, 0, 0).color(179, 47, 103, aFin).uv(1, 0).endVertex();
		poseStack.popPose();
	}
}
