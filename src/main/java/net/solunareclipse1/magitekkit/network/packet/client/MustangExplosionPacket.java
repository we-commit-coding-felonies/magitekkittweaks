package net.solunareclipse1.magitekkit.network.packet.client;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import vazkii.botania.client.fx.WispParticleData;

/**
 * Does fire particle effect for Band of Arcana ignition projectile
 * @author solunareclipse1
 */
public record MustangExplosionPacket(double x, double y, double z) {	
	
	public void enc(FriendlyByteBuf buffer) {
		buffer.writeDouble(x);
		buffer.writeDouble(y);
		buffer.writeDouble(z);
	}

	public static MustangExplosionPacket dec(FriendlyByteBuf buffer) {
		return new MustangExplosionPacket(
			buffer.readDouble(), // X
			buffer.readDouble(), // Y
			buffer.readDouble()  // Z
		);
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> {
        	// ide warns me about this but im pretty sure its fine?
        	ClientLevel level = Minecraft.getInstance().level;
        	Random rand = level.getRandom();
    		level.addParticle(ParticleTypes.FLASH, true, x, y, z, 0, 0, 0);
    		for (int i = 0; i < 24; i++) {
        		//level.addParticle(ParticleTypes.FLASH, true, rand.nextGaussian() + x, rand.nextGaussian() + y, rand.nextGaussian() + z, 0, 0, 0);
        		level.addParticle(WispParticleData.wisp(4, 1, 0.1f, 0, 0.25f), true, rand.nextGaussian() + x, rand.nextGaussian() + y, rand.nextGaussian() + z, 0, Math.abs(rand.nextGaussian()/10), 0);
    		}
    		
    		for (int i = 0; i < 256; i++) {
        		level.addParticle(ParticleTypes.FLAME, true, rand.nextGaussian() * 2 + x, rand.nextGaussian() * 2 + y, rand.nextGaussian() * 2 + z, 0, Math.abs(rand.nextGaussian()/10), 0);
        		level.addParticle(ParticleTypes.SMALL_FLAME, true, rand.nextGaussian() * 2 + x, rand.nextGaussian() * 2 + y, rand.nextGaussian() * 2 + z, 0, Math.abs(rand.nextGaussian()/10), 0);
    		}
    		
    		for (int i=0; i<128; i++) {
        		level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true, rand.nextGaussian() + x, rand.nextGaussian() + y, rand.nextGaussian() + z, 0, Math.abs(rand.nextGaussian()/10), 0);
    		}
    		// addParticle(pParticleData, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        });
        return true;
    }
}



