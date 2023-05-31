package net.solunareclipse1.magitekkit.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.common.inventory.container.GravityAnvilMenu;
import net.solunareclipse1.magitekkit.init.NetworkInit;
import net.solunareclipse1.magitekkit.network.packet.server.GravityAnvilItemRenamePacket;

public class GravityAnvilScreen extends ItemCombinerScreen<GravityAnvilMenu> {
	private static final ResourceLocation ANVIL_LOCATION = new ResourceLocation(MagiTekkit.MODID, "textures/gui/gravity_anvil.png");
	private EditBox name;
	private final Player player;

	public GravityAnvilScreen(GravityAnvilMenu menu, Inventory inv, Component title) {
		super(menu, inv, title, ANVIL_LOCATION);
		this.player = inv.player;
		this.titleLabelX = 60;
	}

	public void containerTick() {
		super.containerTick();
		this.name.tick();
	}

	protected void subInit() {
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		this.name = new EditBox(this.font, i + 62, j + 24, 103, 12, new TranslatableComponent("container.repair"));
		this.name.setCanLoseFocus(false);
		this.name.setTextColor(-1);
		this.name.setTextColorUneditable(-1);
		this.name.setBordered(false);
		this.name.setMaxLength(50);
		this.name.setResponder(this::onNameChanged);
		this.name.setValue("");
		this.addWidget(this.name);
		this.setInitialFocus(this.name);
		this.name.setEditable(false);
	}

	public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
		String s = this.name.getValue();
		this.init(pMinecraft, pWidth, pHeight);
		this.name.setValue(s);
	}

	public void removed() {
		super.removed();
		this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
		if (pKeyCode == 256) {
			this.minecraft.player.closeContainer();
		}

		return !this.name.keyPressed(pKeyCode, pScanCode, pModifiers) && !this.name.canConsumeInput() ? super.keyPressed(pKeyCode, pScanCode, pModifiers) : true;
	}

	private void onNameChanged(String newName) {
		if (!newName.isEmpty()) {
			String s = newName;
			Slot slot = this.menu.getSlot(0);
			if (slot != null && slot.hasItem() && !slot.getItem().hasCustomHoverName() && newName.equals(slot.getItem().getHoverName().getString())) {
				s = "";
			}

			this.menu.setItemName(s);
			NetworkInit.toServer(new GravityAnvilItemRenamePacket(s));
		}
	}

	protected void renderLabels(PoseStack pPoseStack, int pX, int pY) {
		RenderSystem.disableBlend();
		super.renderLabels(pPoseStack, pX, pY);
		int xpCost = this.menu.getCost();
		if (xpCost > 0) {
			int j = 8453920;
			Component component;
			/*if (i >= 40 && !this.minecraft.player.getAbilities().instabuild) {
				component = TOO_EXPENSIVE_TEXT;
				j = 16736352;
			} else*/ if (!this.menu.getSlot(2).hasItem()) {
				component = null;
			} else {
				component = new TranslatableComponent("container.repair.cost", xpCost);
				if (!this.menu.getSlot(2).mayPickup(this.player)) {
					j = 16736352;
				}
			}

			if (component != null) {
				int k = this.imageWidth - 8 - this.font.width(component) - 2;
				fill(pPoseStack, k - 2, 67, this.imageWidth - 8, 79, 1325400064);
				this.font.drawShadow(pPoseStack, component, (float)k, 69.0F, j);
			}
		}

	}

	public void renderFg(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		this.name.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
	}

	/**
	 * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
	 * contents of that slot.
	 */
	public void slotChanged(AbstractContainerMenu pContainerToSend, int pSlotInd, ItemStack pStack) {
		if (pSlotInd == 0) {
			this.name.setValue(pStack.isEmpty() ? "" : pStack.getHoverName().getString());
			this.name.setEditable(!pStack.isEmpty());
			this.setFocused(this.name);
		}

	}
}
