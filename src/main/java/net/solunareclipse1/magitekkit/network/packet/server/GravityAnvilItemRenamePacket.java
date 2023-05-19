package net.solunareclipse1.magitekkit.network.packet.server;

import java.util.function.Supplier;

import net.minecraft.SharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import net.solunareclipse1.magitekkit.common.inventory.container.GravityAnvilMenu;

public record GravityAnvilItemRenamePacket(String rename) {
	public void enc(FriendlyByteBuf buffer) {
		buffer.writeUtf(rename);
	}

	public static GravityAnvilItemRenamePacket dec(FriendlyByteBuf buffer) {
		return new GravityAnvilItemRenamePacket(
				buffer.readUtf()
		);
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> sup) {
		NetworkEvent.Context ctx = sup.get();
		ctx.enqueueWork(() -> {
			ServerPlayer player = ctx.getSender();
			//PacketUtils.ensureRunningOnSameThread(this, this, player.getLevel());
			if (player.containerMenu instanceof GravityAnvilMenu menu) {
				String s = SharedConstants.filterText(this.rename());
				if (s.length() <= 50) {
					menu.setItemName(s);
				}
			}
		});
		return true;
	}

}
