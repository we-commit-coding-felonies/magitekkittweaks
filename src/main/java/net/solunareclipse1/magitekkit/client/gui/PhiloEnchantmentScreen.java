package net.solunareclipse1.magitekkit.client.gui;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import net.solunareclipse1.magitekkit.common.inventory.container.PhiloEnchantmentMenu;

public class PhiloEnchantmentScreen extends AbstractContainerScreen<PhiloEnchantmentMenu> {
	/** The ResourceLocation containing the Enchantment GUI texture location */
	private static final ResourceLocation ENCHANTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/enchanting_table.png");
	/** The ResourceLocation containing the texture for the Book rendered above the enchantment table */
	private static final ResourceLocation ENCHANTING_BOOK_LOCATION = new ResourceLocation("textures/entity/enchanting_table_book.png");
	/** A Random instance for use with the enchantment gui */
	private final Random random = new Random();
	private BookModel bookModel;
	public int time;
	public float flip;
	public float oFlip;
	public float flipT;
	public float flipA;
	public float open;
	public float oOpen;
	private ItemStack last = ItemStack.EMPTY;

	public PhiloEnchantmentScreen(PhiloEnchantmentMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
	}

	protected void init() {
		super.init();
		this.bookModel = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
	}

	public void containerTick() {
		super.containerTick();
		this.tickBook();
	}

	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;

		for (int k = 0; k < 3; ++k) {
			double d0 = pMouseX - (double)(i + 60);
			double d1 = pMouseY - (double)(j + 14 + 19 * k);
			if (d0 >= 0.0D && d1 >= 0.0D && d0 < 108.0D && d1 < 19.0D && this.menu.clickMenuButton(this.minecraft.player, k)) {
				this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, k);
				return true;
			}
		}

		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}

	protected void renderBg(PoseStack poseStack, float partialTick, int x, int y) {
		Lighting.setupForFlatItems();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, ENCHANTING_TABLE_LOCATION);
		int w = (this.width - this.imageWidth) / 2;
		int h = (this.height - this.imageHeight) / 2;
		this.blit(poseStack, w, h, 0, 0, this.imageWidth, this.imageHeight);
		int guiScale = (int)this.minecraft.getWindow().getGuiScale();
		RenderSystem.viewport((this.width - 320) / 2 * guiScale, (this.height - 240) / 2 * guiScale, 320 * guiScale, 240 * guiScale);
		Matrix4f matrix4f = Matrix4f.createTranslateMatrix(-0.34F, 0.23F, 0.0F);
		matrix4f.multiply(Matrix4f.perspective(90.0D, 1.3333334F, 9.0F, 80.0F));
		RenderSystem.backupProjectionMatrix();
		RenderSystem.setProjectionMatrix(matrix4f);
		poseStack.pushPose();
		PoseStack.Pose posestack$pose = poseStack.last();
		posestack$pose.pose().setIdentity();
		posestack$pose.normal().setIdentity();
		poseStack.translate(0.0D, (double)3.3F, 1984.0D);
		poseStack.scale(5.0F, 5.0F, 5.0F);
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
		poseStack.mulPose(Vector3f.XP.rotationDegrees(20.0F));
		float bookOpenness = Mth.lerp(partialTick, this.oOpen, this.open);
		poseStack.translate((double)((1.0F - bookOpenness) * 0.2F), (double)((1.0F - bookOpenness) * 0.1F), (double)((1.0F - bookOpenness) * 0.25F));
		float ypRot = -(1.0F - bookOpenness) * 90.0F - 90.0F;
		poseStack.mulPose(Vector3f.YP.rotationDegrees(ypRot));
		poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
		float bookRightFlipAmt = Mth.lerp(partialTick, this.oFlip, this.flip) + 0.25F;
		float bookLeftFlipAmt = Mth.lerp(partialTick, this.oFlip, this.flip) + 0.75F;
		bookRightFlipAmt = (bookRightFlipAmt - (float)Mth.fastFloor((double)bookRightFlipAmt)) * 1.6F - 0.3F;
		bookLeftFlipAmt = (bookLeftFlipAmt - (float)Mth.fastFloor((double)bookLeftFlipAmt)) * 1.6F - 0.3F;
		if (bookRightFlipAmt < 0.0F) {
			bookRightFlipAmt = 0.0F;
		}

		if (bookLeftFlipAmt < 0.0F) {
			bookLeftFlipAmt = 0.0F;
		}

		if (bookRightFlipAmt > 1.0F) {
			bookRightFlipAmt = 1.0F;
		}

		if (bookLeftFlipAmt > 1.0F) {
			bookLeftFlipAmt = 1.0F;
		}

		this.bookModel.setupAnim(0.0F, bookRightFlipAmt, bookLeftFlipAmt, bookOpenness);
		MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		VertexConsumer vertexconsumer = multibuffersource$buffersource.getBuffer(this.bookModel.renderType(ENCHANTING_BOOK_LOCATION));
		this.bookModel.renderToBuffer(poseStack, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		multibuffersource$buffersource.endBatch();
		poseStack.popPose();
		RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
		RenderSystem.restoreProjectionMatrix();
		Lighting.setupFor3DItems();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		EnchantmentNames.getInstance().initSeed((long)this.menu.getEnchantmentSeed());
		int fuel = this.menu.getGoldCount();

		for (int costIdx = 0; costIdx < 3; ++costIdx) {
			int j = w + 60;
			int k = j + 20;
			this.setBlitOffset(0);
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, ENCHANTING_TABLE_LOCATION);
			int cost = (this.menu).costs[costIdx];
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			if (cost == 0) {
				this.blit(poseStack, j, h + 14 + 19 * costIdx, 0, 185, 108, 19);
			} else {
				String s = "" + cost;
				int maxFontWidth = 86 - this.font.width(s);
				FormattedText text = EnchantmentNames.getInstance().getRandomName(this.font, maxFontWidth);
				int colorInt = 6839882;
				if (((fuel < costIdx + 1 || this.minecraft.player.experienceLevel < cost) && !this.minecraft.player.getAbilities().instabuild) || this.menu.enchantClue[costIdx] == -1) { // Forge: render buttons as disabled when enchantable but enchantability not met on lower levels
					this.blit(poseStack, j, h + 14 + 19 * costIdx, 0, 185, 108, 19);
					this.blit(poseStack, j + 1, h + 15 + 19 * costIdx, 16 * costIdx, 239, 16, 16);
					this.font.drawWordWrap(text, k, h + 16 + 19 * costIdx, maxFontWidth, (colorInt & 16711422) >> 1);
					colorInt = 4226832;
				} else {
					int k2 = x - (w + 60);
					int l2 = y - (h + 14 + 19 * costIdx);
					if (k2 >= 0 && l2 >= 0 && k2 < 108 && l2 < 19) {
						this.blit(poseStack, j, h + 14 + 19 * costIdx, 0, 204, 108, 19);
						colorInt = 16777088;
					} else {
						this.blit(poseStack, j, h + 14 + 19 * costIdx, 0, 166, 108, 19);
					}

					this.blit(poseStack, j + 1, h + 15 + 19 * costIdx, 16 * costIdx, 223, 16, 16);
					this.font.drawWordWrap(text, k, h + 16 + 19 * costIdx, maxFontWidth, colorInt);
					colorInt = 8453920;
				}
				this.font.drawShadow(poseStack, s, (float)(k + 86 - this.font.width(s)), (float)(h + 16 + 19 * costIdx + 7), colorInt);
			}
		}
	}

	public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		pPartialTick = this.minecraft.getFrameTime();
		this.renderBackground(pPoseStack);
		super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
		this.renderTooltip(pPoseStack, pMouseX, pMouseY);
		boolean flag = this.minecraft.player.getAbilities().instabuild;
		int i = this.menu.getGoldCount();

		for (int j = 0; j < 3; ++j) {
			int k = (this.menu).costs[j];
			Enchantment enchantment = Enchantment.byId((this.menu).enchantClue[j]);
			int l = (this.menu).levelClue[j];
			int i1 = j + 1;
			if (this.isHovering(60, 14 + 19 * j, 108, 17, (double)pMouseX, (double)pMouseY) && k > 0) {
				List<Component> list = Lists.newArrayList();
				list.add((new TranslatableComponent("container.enchant.clue", enchantment == null ? "" : enchantment.getFullname(l))).withStyle(ChatFormatting.WHITE));
				if (enchantment == null) {
					list.add(new TextComponent(""));
					list.add(new TranslatableComponent("forge.container.enchant.limitedEnchantability").withStyle(ChatFormatting.RED));
				} else if (!flag) {
					list.add(TextComponent.EMPTY);
					if (this.minecraft.player.experienceLevel < k) {
						list.add((new TranslatableComponent("container.enchant.level.requirement", (this.menu).costs[j])).withStyle(ChatFormatting.RED));
					} else {
						MutableComponent mutablecomponent;
						if (i1 == 1) {
							mutablecomponent = new TranslatableComponent("container.enchant.lapis.one");
						} else {
							mutablecomponent = new TranslatableComponent("container.enchant.lapis.many", i1);
						}
						list.add(mutablecomponent.withStyle(i >= i1 ? ChatFormatting.GRAY : ChatFormatting.RED));
						MutableComponent mutablecomponent1;
						if (i1 == 1) {
							mutablecomponent1 = new TranslatableComponent("container.enchant.level.one");
						} else {
							mutablecomponent1 = new TranslatableComponent("container.enchant.level.many", i1);
						}
						list.add(mutablecomponent1.withStyle(ChatFormatting.GRAY));
					}
				}
				this.renderComponentTooltip(pPoseStack, list, pMouseX, pMouseY);
				break;
			}
		}
	}

	public void tickBook() {
		ItemStack itemstack = this.menu.getSlot(0).getItem();
		if (!ItemStack.matches(itemstack, this.last)) {
			this.last = itemstack;

			do {
				this.flipT += (float)(this.random.nextInt(4) - this.random.nextInt(4));
			} while(this.flip <= this.flipT + 1.0F && this.flip >= this.flipT - 1.0F);
		}

		++this.time;
		this.oFlip = this.flip;
		this.oOpen = this.open;
		boolean flag = false;

		for(int i = 0; i < 3; ++i) {
			if ((this.menu).costs[i] != 0) {
				flag = true;
			}
		}

		if (flag) {
			this.open += 0.2F;
		} else {
			this.open -= 0.2F;
		}

		this.open = Mth.clamp(this.open, 0.0F, 1.0F);
		float f1 = (this.flipT - this.flip) * 0.4F;
		float f = 0.2F;
		f1 = Mth.clamp(f1, -0.2F, 0.2F);
		this.flipA += (f1 - this.flipA) * 0.9F;
		this.flip += this.flipA;
	}
}
