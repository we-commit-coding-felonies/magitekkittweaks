package net.solunareclipse1.magitekkit.network.packet.server;

import net.solunareclipse1.magitekkit.api.item.ISwingItem;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

/**
 * Sent by anything that needs to do something when swung at the air <br>
 * (IE: not mining or attacking an entity)
 * 
 * @author solunareclipse1
 */
public class LeftClickAirPacket {
	
	public void enc(FriendlyByteBuf buffer) {}

	public static LeftClickAirPacket dec(FriendlyByteBuf buffer) {
		return new LeftClickAirPacket();
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> {
    		ServerPlayer player = ctx.getSender();
    		if (player != null) {
    			ItemStack stack = player.getMainHandItem();
    			if (stack.getItem() instanceof ISwingItem item) {
    				item.onSwingAir(ctx);
    			}
    		}
        });
        return true;
    }
}



