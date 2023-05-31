package net.solunareclipse1.magitekkit.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.network.packet.client.CreateLoopingSoundPacket;
import net.solunareclipse1.magitekkit.network.packet.client.CutParticlePacket;
import net.solunareclipse1.magitekkit.network.packet.client.DrawParticleAABBPacket;
import net.solunareclipse1.magitekkit.network.packet.client.DrawParticleLinePacket;
import net.solunareclipse1.magitekkit.network.packet.client.GustParticlePacket;
import net.solunareclipse1.magitekkit.network.packet.client.ModifyPlayerVelocityPacket;
import net.solunareclipse1.magitekkit.network.packet.client.MustangExplosionPacket;
import net.solunareclipse1.magitekkit.network.packet.server.GravityAnvilItemRenamePacket;
import net.solunareclipse1.magitekkit.network.packet.server.LeftClickAirPacket;

public class NetworkInit {

	private static final String VERSION = "1";
    private static SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MagiTekkit.MODID, "packets"))
            .networkProtocolVersion(() -> VERSION)
            .clientAcceptedVersions(VERSION::equals)
            .serverAcceptedVersions(VERSION::equals)
            .simpleChannel();

    private static int id = 0;

    public static void register() {

    	// client -> server
        CHANNEL.messageBuilder(LeftClickAirPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
        		.encoder(LeftClickAirPacket::enc)
                .decoder(LeftClickAirPacket::dec)
                .consumer(LeftClickAirPacket::handle)
                .add();
        
        CHANNEL.messageBuilder(GravityAnvilItemRenamePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(GravityAnvilItemRenamePacket::enc)
        		.decoder(GravityAnvilItemRenamePacket::dec)
        		.consumer(GravityAnvilItemRenamePacket::handle)
        		.add();
        
        
        
        // server -> client
        CHANNEL.messageBuilder(MustangExplosionPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(MustangExplosionPacket::enc)
        		.decoder(MustangExplosionPacket::dec)
        		.consumer(MustangExplosionPacket::handle)
        		.add();
        
        CHANNEL.messageBuilder(DrawParticleLinePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(DrawParticleLinePacket::enc)
        		.decoder(DrawParticleLinePacket::dec)
        		.consumer(DrawParticleLinePacket::handle)
        		.add();
        
        CHANNEL.messageBuilder(DrawParticleAABBPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(DrawParticleAABBPacket::enc)
        		.decoder(DrawParticleAABBPacket::dec)
        		.consumer(DrawParticleAABBPacket::handle)
        		.add();
        
        CHANNEL.messageBuilder(CreateLoopingSoundPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(CreateLoopingSoundPacket::enc)
        		.decoder(CreateLoopingSoundPacket::dec)
        		.consumer(CreateLoopingSoundPacket::handle)
        		.add();
        
        CHANNEL.messageBuilder(ModifyPlayerVelocityPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ModifyPlayerVelocityPacket::enc)
        		.decoder(ModifyPlayerVelocityPacket::dec)
        		.consumer(ModifyPlayerVelocityPacket::handle)
        		.add();
        
        CHANNEL.messageBuilder(GustParticlePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(GustParticlePacket::enc)
        		.decoder(GustParticlePacket::dec)
        		.consumer(GustParticlePacket::handle)
        		.add();
        
        CHANNEL.messageBuilder(CutParticlePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(CutParticlePacket::enc)
        		.decoder(CutParticlePacket::dec)
        		.consumer(CutParticlePacket::handle)
        		.add();
    }
    
    public static <PKT> void toServer(PKT packet) {
    	CHANNEL.sendToServer(packet);
    }

    public static <PKT> void toClient(PKT message, ServerPlayer player) {
    	CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

}