package net.solunareclipse1.magitekkit.init;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import moze_intel.projecte.PECore;
import moze_intel.projecte.rendering.EntitySpriteRenderer;

import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.api.item.IEmpowerItem;
import net.solunareclipse1.magitekkit.client.gui.GravityAnvilScreen;
import net.solunareclipse1.magitekkit.client.gui.PhiloEnchantmentScreen;
import net.solunareclipse1.magitekkit.client.render.LayerHalo;
import net.solunareclipse1.magitekkit.client.render.SentientArrowRenderer;
import net.solunareclipse1.magitekkit.common.entity.projectile.WitherVineProjectile;
import net.solunareclipse1.magitekkit.common.item.tool.BandOfArcana;

import vazkii.botania.common.helper.ItemNBTHelper;

@Mod.EventBusSubscriber(modid = MagiTekkit.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientInit {

	public static final ResourceLocation BOA_MODE = new ResourceLocation(MagiTekkit.MODID, "boa_mode");
	public static final ResourceLocation BOA_COVALENCE = new ResourceLocation(MagiTekkit.MODID, "boa_covalence");
	public static final ResourceLocation BOA_LIQUID = new ResourceLocation(MagiTekkit.MODID, "boa_liquid");
	public static final ResourceLocation BOA_WOFT = new ResourceLocation(MagiTekkit.MODID, "boa_woft");
	
	public static final ResourceLocation EMPOWER_CHARGE = new ResourceLocation(MagiTekkit.MODID, "empowerment_charge");
	
    public static void init(final FMLClientSetupEvent event) {
    	event.enqueueWork(() -> {
			//Property Overrides
    		BandOfArcana bracelet = ObjectInit.GEM_BRACELET.get();																													
    		ItemProperties.register(bracelet.asItem(), BOA_MODE, (stack, level, entity, seed) -> ItemNBTHelper.getByte(stack, BandOfArcana.TAG_MODE, (byte) 0));
    		ItemProperties.register(bracelet.asItem(), BOA_COVALENCE, (stack, level, entity, seed) -> ItemNBTHelper.getBoolean(stack, BandOfArcana.TAG_COVALENCE, false) ? 1 : 0);
    		ItemProperties.register(bracelet.asItem(), BOA_LIQUID, (stack, level, entity, seed) -> ItemNBTHelper.getBoolean(stack, BandOfArcana.TAG_LIQUID, false) ? 1 : 0);
    		ItemProperties.register(bracelet.asItem(), BOA_WOFT, (stack, level, entity, seed) -> ItemNBTHelper.getBoolean(stack, BandOfArcana.TAG_WOFT, false) ? 1 : 0);
    		
    		ItemProperties.registerGeneric(EMPOWER_CHARGE, ClientInit::getEmpowerCharge);
    		
    		// Screens
    		MenuScreens.register(ObjectInit.PHILO_ENCHANTER.get(), PhiloEnchantmentScreen::new);
    		MenuScreens.register(ObjectInit.GRAVITY_ANVIL.get(), GravityAnvilScreen::new);
		});
    }

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		//Entities
		event.registerEntityRenderer(ObjectInit.FREE_LAVA_PROJECTILE.get(), context -> new EntitySpriteRenderer<>(context, PECore.rl("textures/entity/lava_orb.png")));
		event.registerEntityRenderer(ObjectInit.SENTIENT_ARROW.get(), context -> new SentientArrowRenderer(context));
		event.registerEntityRenderer(ObjectInit.WITHER_VINE.get(), context -> new NoopRenderer<WitherVineProjectile>(context));
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
	
	private static float getEmpowerCharge(ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
		if (stack.getItem() instanceof IEmpowerItem item) {
			return item.getStage(stack);
		}
		return 0;
	}
}