package net.solunareclipse1.magitekkit.api.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.network.NetworkEvent.Context;

import net.solunareclipse1.magitekkit.init.NetworkInit;
import net.solunareclipse1.magitekkit.network.packet.server.LeftClickAirPacket;

/**
 * implement to make an item compatible with LeftClickAirPacket <br>
 * also used for things that do special stuff on swing in general
 * @author solunareclipse1
 */
public interface ISwingItem {
	


	/**
	 * should be added as a listener, called when item is swung at the air
	 * by default it will just send a LeftClickAirPacket to the server when called <br>
	 * <p>
	 * Overriding this should only be necessary if you need to do things clientside
	 * 
	 * @param evt
	 */
	default void sendEmptySwingToServer(PlayerInteractEvent.LeftClickEmpty event) {
		NetworkInit.toServer(new LeftClickAirPacket());
	}
	
	/**
	 * called directly by LeftClickAirPacket serverside <br>
	 * does nothing by default, override to do something
	 * 
	 * @param ctx the NetworkEvent.Context for this swing
	 * @return if the swing did anything
	 * 
	 * @see #sendEmptySwingToServer(LeftClickEmpty event)
	 */
	default boolean onSwingAir(Context ctx) {return false;}
	
	/**
	 * should be added as a listener, called when this item is swung at a block <br>
	 * does nothing by default, override if you want something to happen
	 * 
	 * @param event the event
	 * @return if the swing did anything
	 */
	default boolean onSwingBlock(PlayerInteractEvent.LeftClickBlock event) {return false;}
	
	/**
	 * should be called from onLeftClickEntity() <br>
	 * does nothing by default, override if you want to do something
	 * 
	 * @param stack
	 * @param player
	 * @param target
	 * @return
	 */
	default boolean onSwingEntity(ItemStack stack, Player player, Entity target) {return false;}
}
