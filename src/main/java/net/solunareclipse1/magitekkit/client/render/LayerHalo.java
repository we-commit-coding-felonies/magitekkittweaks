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
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.common.item.armor.gem.GemAmulet;
import net.solunareclipse1.magitekkit.common.item.armor.gem.GemJewelryBase;
import net.solunareclipse1.magitekkit.util.ColorsHelper;
import net.solunareclipse1.magitekkit.util.ColorsHelper.Color;

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
		float emcLevel = 0;
		for (ItemStack stack : player.getArmorSlots()) {
			if (!(stack.getItem() instanceof GemJewelryBase) || stack.isDamaged()) return;
			if (stack.getItem() instanceof GemAmulet) {
				GemAmulet amulet = (GemAmulet) stack.getItem();
				emcLevel = (float) amulet.getStoredEmc(stack) / amulet.getMaximumEmc(stack);
			}
		}
		if (emcLevel <= 0) return;
		int timer = Math.round(ageInTicks);
		if (timer % 20 == 0) System.out.println("EMC Percentage: "+emcLevel);
		poseStack.pushPose();
		renderer.getModel().jacket.translateAndRotate(poseStack);
		poseStack.mulPose(Vector3f.XP.rotationDegrees(270)); // rotate upright
		poseStack.scale(1.5f, 1.5f, 1.5f); // bigger!
		poseStack.mulPose(Vector3f.YP.rotationDegrees(ageInTicks*0.6f % 360)); // spinny
		poseStack.translate(-0.5, -0.25, -0.5); // positioning behind the head
		ResourceLocation texture = HALO_TEXTURE;
		// nerd
		if (NERD_UUID.equals(player.getUUID())) texture = NERD_TEXTURE;
		VertexConsumer poly = renderBuffer.getBuffer(MGTKRenderType.HALO_RENDERER.apply(texture));
		Matrix4f matrix4f = poseStack.last().pose();
		Color c1 = Color.COVALENCE_GREEN_TRUE;
		Color c2 = Color.COVALENCE_BLUE;
		if (player.getUUID().equals(SOL_UUID)) {
			c1 = Color.MIDGREEN;
			c2 = Color.MIDGREEN;
		}

		int alpha = Math.round(ColorsHelper.fadingValue(timer, Math.round(Math.max(280*emcLevel, 10)), 0, 96, 192));
		int[] color = ColorsHelper.rgbFromInt(Mth.hsvToRgb(ColorsHelper.fadingValue(timer, 2048, 0, c1.H/360f, c2.H/360f), 1.0f, 0.824f)); 
		poly.vertex(matrix4f, 0, 0, 0).color(color[0], color[1], color[2], alpha).uv(0, 0).endVertex();
		color = ColorsHelper.rgbFromInt(Mth.hsvToRgb(ColorsHelper.fadingValue(timer, 2048, 50, c1.H/360f, c2.H/360f), 1.0f, 0.824f));
		poly.vertex(matrix4f, 0, 0, 1).color(color[0], color[1], color[2], alpha).uv(0, 1).endVertex();
		color = ColorsHelper.rgbFromInt(Mth.hsvToRgb(ColorsHelper.fadingValue(timer, 2048, 100, c1.H/360f, c2.H/360f), 1.0f, 0.824f));
		poly.vertex(matrix4f, 1, 0, 1).color(color[0], color[1], color[2], alpha).uv(1, 1).endVertex();
		color = ColorsHelper.rgbFromInt(Mth.hsvToRgb(ColorsHelper.fadingValue(timer, 2048, 150, c1.H/360f, c2.H/360f), 1.0f, 0.824f));
		poly.vertex(matrix4f, 1, 0, 0).color(color[0], color[1], color[2], alpha).uv(1, 0).endVertex();
		poseStack.popPose();
	}
}
