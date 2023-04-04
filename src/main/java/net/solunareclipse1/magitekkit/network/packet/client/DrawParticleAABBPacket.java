package net.solunareclipse1.magitekkit.network.packet.client;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.network.NetworkEvent;

import net.solunareclipse1.magitekkit.util.MiscHelper;

import vazkii.botania.client.fx.WispParticleData;

public record DrawParticleAABBPacket(Vec3 cMin, Vec3 cMax, int preset) {
	
	public void enc(FriendlyByteBuf buffer) {
		buffer.writeDouble(cMin.x); //
		buffer.writeDouble(cMin.y); // min corner
		buffer.writeDouble(cMin.z); //
		
		buffer.writeDouble(cMax.x); //
		buffer.writeDouble(cMax.y); // max corner
		buffer.writeDouble(cMax.z); //
		
		buffer.writeInt(preset); // particle preset
	}

	public static DrawParticleAABBPacket dec(FriendlyByteBuf buffer) {
		return new DrawParticleAABBPacket(
				new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), // min corner
				new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), // max corner
				buffer.readInt() // particle preset
		);
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> {
        	ClientLevel level = Minecraft.getInstance().level;
        	AABB box = new AABB(cMin, cMax);
        	Vec3 cent = box.getCenter();
        	ParticleOptions particle;
        	double stepSize = 0.1;
        	switch (preset) {
        	
        	case -1: // debug fill
        		MiscHelper.drawAABBWithParticles(box.deflate(0.05), ParticleTypes.DRIPPING_WATER, stepSize, level, true);
        	case 0: // debug outline
        		particle = ParticleTypes.DRIPPING_LAVA;
        		break;
        		
        	case 1: // smart arrow lost target
        		stepSize = 1;
        		particle = ParticleTypes.ENCHANT;
        		break;
        	
        	default: // invalid
        		level.playSound(null, cent.x, cent.y, cent.z, SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.MASTER, 100, 1);
        		level.addAlwaysVisibleParticle(ParticleTypes.ELDER_GUARDIAN, cent.x, cent.y, cent.z, 0, 0, 0);
        		MiscHelper.drawAABBWithParticles(box.deflate(0.05), ParticleTypes.DRAGON_BREATH, stepSize, level, true);
        		particle = ParticleTypes.CAMPFIRE_SIGNAL_SMOKE;
        		break;
        	}
        	MiscHelper.drawAABBWithParticles(box, particle, stepSize, level, false);
        });
        return true;
    }
}
