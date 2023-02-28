package net.solunareclipse1.magitekkit.client.render;

import java.util.UUID;

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
import net.minecraft.world.item.ItemStack;

import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.common.item.armor.gem.GemJewelryBase;

public class LayerHalo extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

	public LayerHalo(PlayerRenderer renderer) {
		super(renderer);
		this.renderer = renderer;
	}
	
	private final PlayerRenderer renderer;
	private static final ResourceLocation HALO_TEXTURE = new ResourceLocation(MagiTekkit.MODID, "textures/models/halo.png");
	private static final UUID SOL_UUID = UUID.fromString("89b9a7d2-daa3-48cc-903c-96d125106a6b");
	private static final UUID NERD_UUID = UUID.fromString("b9d0673f-51af-446e-a4d0-512eab478561");
	private static final ResourceLocation NERD_TEXTURE = new ResourceLocation(MagiTekkit.MODID, "textures/models/halo_doofus.png");

	@Override
	public void render(PoseStack poseStack, MultiBufferSource renderBuffer, int bakedLight,
			AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick,
			float ageInTicks, float netHeadYaw, float headPitch)
	{
		for (ItemStack stack : player.getArmorSlots()) {
			if (!(stack.getItem() instanceof GemJewelryBase) || stack.isDamaged()) return;
		}
		poseStack.pushPose();
		renderer.getModel().jacket.translateAndRotate(poseStack);
		poseStack.mulPose(Vector3f.XP.rotationDegrees(270)); // rotate upright
		poseStack.scale(1.5f, 1.5f, 1.5f);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(ageInTicks*0.6f % 360)); // spinny
		poseStack.translate(-0.5, -0.25, -0.5); // positioning behind the head
		ResourceLocation texture = HALO_TEXTURE;
		if (NERD_UUID.equals(player.getUUID())) texture = NERD_TEXTURE;
		VertexConsumer builder = renderBuffer.getBuffer(MGTKRenderType.HALO_RENDERER.apply(texture));
		Matrix4f matrix4f = poseStack.last().pose();
		
		// fancy "breathing" effect
		// modified from DuraBarHelper.fadingBarColor
		float aVal, a1 = 192, a2 = 96, aDiff = a1-a2;
		int timer = Math.round(ageInTicks);
		int cycle = 280;
		int swap = (cycle / 2);
		float fade = (timer % cycle) % cycle;

		if (fade < swap) {
			aVal = a2 + ((aDiff * fade) / swap);
			//return Mth.hsvToRgb(hVal, sVal, vVal);
		} else {
			aVal = a1 - ((aDiff * (fade - swap)) / swap);
			//return Mth.hsvToRgb(hVal, sVal, vVal);
		}
		//System.out.println(aVal);
		int a = Math.round(aVal);
		int r = 179, g = 47, b = 103;
		if (SOL_UUID.equals(player.getUUID())) {r = 0; g = 128; b = 0;}
		builder.vertex(matrix4f, 0, 0, 0).color(r, g, b, a).uv(0, 0).endVertex();
		builder.vertex(matrix4f, 0, 0, 1).color(r, g, b, a).uv(0, 1).endVertex();
		builder.vertex(matrix4f, 1, 0, 1).color(r, g, b, a).uv(1, 1).endVertex();
		builder.vertex(matrix4f, 1, 0, 0).color(r, g, b, a).uv(1, 0).endVertex();
		poseStack.popPose();
	}
}
