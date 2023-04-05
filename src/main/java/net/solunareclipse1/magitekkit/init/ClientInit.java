package net.solunareclipse1.magitekkit.init;

import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import moze_intel.projecte.PECore;
import moze_intel.projecte.rendering.EntitySpriteRenderer;

import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.client.render.LayerHalo;
import net.solunareclipse1.magitekkit.client.render.SentientArrowRenderer;
import net.solunareclipse1.magitekkit.common.item.tool.BandOfArcana;

import morph.avaritia.init.AvaritiaModContent;
import vazkii.botania.common.helper.ItemNBTHelper;

@Mod.EventBusSubscriber(modid = MagiTekkit.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientInit {

	public static final ResourceLocation ARC_MODE = new ResourceLocation(MagiTekkit.MODID, "arc_mode");
	public static final ResourceLocation ARC_OFFENSIVE = new ResourceLocation(MagiTekkit.MODID, "arc_offensive");
	public static final ResourceLocation ARC_LIQUID = new ResourceLocation(MagiTekkit.MODID, "arc_liquid");
	public static final ResourceLocation ARC_WOFT = new ResourceLocation(MagiTekkit.MODID, "arc_woft");
	
    public static void init(final FMLClientSetupEvent event) {
    	event.enqueueWork(() -> {
			//Property Overrides
    		BandOfArcana bracelet = ObjectInit.GEM_BRACELET.get();																													
    		ItemProperties.register(bracelet.asItem(), ARC_MODE, (stack, level, entity, seed) -> ItemNBTHelper.getByte(stack, BandOfArcana.TAG_MODE, (byte) 0));
    		ItemProperties.register(bracelet.asItem(), ARC_OFFENSIVE, (stack, level, entity, seed) -> ItemNBTHelper.getBoolean(stack, BandOfArcana.TAG_OFFENSIVE, false) ? 1 : 0);
    		ItemProperties.register(bracelet.asItem(), ARC_LIQUID, (stack, level, entity, seed) -> ItemNBTHelper.getBoolean(stack, BandOfArcana.TAG_LIQUID, false) ? 1 : 0);
    		ItemProperties.register(bracelet.asItem(), ARC_WOFT, (stack, level, entity, seed) -> ItemNBTHelper.getBoolean(stack, BandOfArcana.TAG_WOFT, false) ? 1 : 0);
		});
    }

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		//Entities
		event.registerEntityRenderer(ObjectInit.FREE_LAVA_PROJECTILE.get(), context -> new EntitySpriteRenderer<>(context, PECore.rl("textures/entity/lava_orb.png")));
		event.registerEntityRenderer(ObjectInit.SENTIENT_ARROW.get(), context -> new SentientArrowRenderer(context));
	}
    


	@SubscribeEvent
	public static void addLayers(EntityRenderersEvent.AddLayers event) {
		for (String skinName : event.getSkins()) {
			PlayerRenderer skin = event.getSkin(skinName);
			if (skin != null) {
				skin.addLayer(new LayerHalo(skin));
			}
		}
	}
}