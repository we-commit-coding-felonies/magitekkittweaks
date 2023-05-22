package net.solunareclipse1.magitekkit.network.packet.client;

import java.util.function.Supplier;

import com.simibubi.create.foundation.particle.AirParticleData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.network.NetworkEvent;

/**
 * swrg wind gust particles
 * @author solunareclipse1
 */
public record GustParticlePacket(byte size, Vec3 pos, Vec3 dir) {	
	
	public void enc(FriendlyByteBuf buffer) {
		buffer.writeByte(size);
		buffer.writeDouble(pos.x);
		buffer.writeDouble(pos.y);
		buffer.writeDouble(pos.z);
		buffer.writeDouble(dir.x);
		buffer.writeDouble(dir.y);
		buffer.writeDouble(dir.z);
	}

	public static GustParticlePacket dec(FriendlyByteBuf buffer) {
		return new GustParticlePacket(buffer.readByte(),
			new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
			new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble())
		);
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> {
        	@SuppressWarnings("resource")
			ClientLevel level = Minecraft.getInstance().level;
        	
    		//Vec3 f = new Vec3(-Math.abs(dir.x/dir.lengthSqr()), -Math.abs(dir.y/dir.lengthSqr()), -Math.abs(dir.z/dir.lengthSqr())).scale(size);
    		float v = 1*size;//0*size;
        	for (int i = 0; i < 1000*size; i++) {
        		double x = pos.x + size*level.random.nextGaussian();
        		double y = pos.y + size*level.random.nextGaussian();
        		double z = pos.z + size*level.random.nextGaussian();
            	//level.addParticle(level.isWaterAt(new BlockPos(x,y,z)) ? ParticleTypes.BUBBLE_POP : ParticleTypes.CLOUD, x, y, z, dir.x*v, dir.y*v, dir.z*v);
        		level.addParticle(new AirParticleData(0.5f, size), x, y, z, dir.x*v, dir.y*v, dir.z*v);
            	//level.addParticle(new AirFlowParticleData(1,1,1), x, y, z, dir.x*v, dir.y*v, dir.z*v);
        	}
    		// addParticle(pParticleData, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        });
        return true;
    }
}



