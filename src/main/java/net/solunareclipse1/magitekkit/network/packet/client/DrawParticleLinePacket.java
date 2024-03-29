package net.solunareclipse1.magitekkit.network.packet.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.network.NetworkEvent;

import net.solunareclipse1.magitekkit.util.ColorsHelper.Color;
import net.solunareclipse1.magitekkit.util.LoggerHelper;
import net.solunareclipse1.magitekkit.util.MiscHelper;

import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.client.fx.WispParticleData;

public record DrawParticleLinePacket(Vec3 start, Vec3 end, LineParticlePreset preset) {
	
	public enum LineParticlePreset {
		DEBUG,
		DEBUG_2,
		DEBUG_3,
		ARROW_TARGET_LOCK,
		SENTIENT_RETARGET,
		SENTIENT_COMMUNICATE,
		SENTIENT_TRACER,
		VINE,
		SMITE
	}
	
	public void enc(FriendlyByteBuf buffer) {
		buffer.writeDouble(start.x); //
		buffer.writeDouble(start.y); // start point
		buffer.writeDouble(start.z); //
		
		buffer.writeDouble(end.x); //
		buffer.writeDouble(end.y); // end point
		buffer.writeDouble(end.z); //
		
		buffer.writeEnum(preset); // particle preset
	}

	public static DrawParticleLinePacket dec(FriendlyByteBuf buffer) {
		return new DrawParticleLinePacket(
				new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), // start point
				new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), // end point
				buffer.readEnum(LineParticlePreset.class) // particle preset
		);
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> {
        	@SuppressWarnings("resource")
			ClientLevel level = Minecraft.getInstance().level;
        	/** particle, stepSize */
        	Map<ParticleOptions, Double> particles = new HashMap<>();
        	switch (preset) {
        	
        	case DEBUG: // debug
        		particles.put(ParticleTypes.FALLING_SPORE_BLOSSOM, 0.1);
        		break;
        	
        	case DEBUG_2: // debug
        		particles.put(ParticleTypes.DRIPPING_HONEY, 0.1);
        		break;
        	
        	case DEBUG_3: // debug
        		particles.put(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, 0.1);
        		break;
        		
        	case ARROW_TARGET_LOCK: // smart arrow target-lock
        		particles.put(SparkleParticleData.noClip(1, Color.PHILOSOPHERS.R/255f, Color.PHILOSOPHERS.G/255f, Color.PHILOSOPHERS.B/255f, 1), 0.1);
        		break;
        		
        	case SENTIENT_RETARGET: // sentient arrow retarget
        		particles.put(SparkleParticleData.corrupt(2, Color.PHILOSOPHERS.R/255f, Color.PHILOSOPHERS.G/255f, Color.PHILOSOPHERS.B/255f, 20), 0.1);
        		break;
        		
        	case SENTIENT_COMMUNICATE: // sentient arrow communicate
        		particles.put(ParticleTypes.ENCHANT, 0.1);
        		break;
        		
        	case SENTIENT_TRACER: // sentient arrow tracer
        		particles.put(WispParticleData.wisp(0.5f, Color.PHILOSOPHERS.R/255f, Color.PHILOSOPHERS.G/255f, Color.PHILOSOPHERS.B/255f, 1), 0.1);
        		break;
        		
        	case VINE: // vine
        		particles.put(WispParticleData.wisp(0.5f, 0.35f, 0.5f, 0, 1.5f), 0.1);
        		break;
        		
        	case SMITE:
        		particles.put(ParticleTypes.ELECTRIC_SPARK, 0.1);
        		particles.put(ParticleTypes.ENCHANTED_HIT, 0.1);
        		break;
        	
        	default: // invalid
				LoggerHelper.printWarn("DrawParticleLinePacket", "InvalidPreset", "Line particles preset " + preset + " is undefined!");
        		level.playSound(null, start.x, start.y, start.z, SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.MASTER, 100, 2);
        		level.addAlwaysVisibleParticle(ParticleTypes.ELDER_GUARDIAN, start.x, start.y, start.z, 0, 0, 0);
        		level.addAlwaysVisibleParticle(ParticleTypes.DRAGON_BREATH, start.x, start.y, start.z, 0, 0, 0);
        		level.addAlwaysVisibleParticle(ParticleTypes.DRAGON_BREATH, end.x, end.y, end.z, 0, 0, 0);
        		particles.put(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, 0.1);
        		break;
        	}
        	for (Map.Entry<ParticleOptions, Double> particle : particles.entrySet()) {
            	MiscHelper.drawVectorWithParticles(start, end, particle.getKey(), particle.getValue(), level);
        	}
        });
        return true;
    }
}
