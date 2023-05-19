package net.solunareclipse1.magitekkit.network.packet.client;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.network.NetworkEvent;

/**
 * because changing velocity serverside doesnt seem to do anything <br>
 * values for operation:
 * 	<li> -1: subtract
 *  <li> 0: override
 *  <li> 1: add
 *  <li> 2: multiply
 *  <li> 3: cross
 *  <br><br>
 * @author solunareclipse1
 */
public record ModifyPlayerVelocityPacket(Vec3 vector, byte operation) {
	
	public void enc(FriendlyByteBuf buffer) {
		buffer.writeDouble(vector.x);
		buffer.writeDouble(vector.y);
		buffer.writeDouble(vector.z);
		buffer.writeByte(operation);
	}

	public static ModifyPlayerVelocityPacket dec(FriendlyByteBuf buffer) {
		return new ModifyPlayerVelocityPacket(
			new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
			buffer.readByte()
		);
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> {
        	@SuppressWarnings("resource")
			LocalPlayer player = Minecraft.getInstance().player;
        	Vec3 newVel = player.getDeltaMovement();
        	switch (operation) {
        	
        	case -1:
        		newVel.subtract(vector);
        		break;
        	
        	case 1:
        		newVel = newVel.add(vector);
        		break;
        	
        	case 2:
        		newVel = newVel.multiply(vector);
        		break;
        		
        	case 3:
        		newVel = newVel.cross(vector);
        		break;
        	
        	default:
        		newVel = vector;
        		break;
        	}
        	
        	player.setDeltaMovement(newVel);
        });
        return true;
    }
}
