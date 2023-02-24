//package net.solunareclipse1.magitekkit.common.item.armor;
//
//import net.minecraft.world.entity.EquipmentSlot;
//import net.minecraft.world.item.ArmorMaterial;
//import net.minecraft.world.item.ItemStack;
//
//import moze_intel.projecte.gameObjs.items.armor.PEArmor;
//
//import net.solunareclipse1.magitekkit.api.item.IBurnoutItem;
//
//public class RedArmorItem extends VoidArmorItem implements IBurnoutItem {
//	
//	private float maxDR;
//	
//	/**
//	 * AlchArmorItems are {@link PEArmor}s that accumulate "burnout", making them less effective when taking consecutive hits.
//	 * 
//	 * @param mat The material of the armor
//	 * @param slot The slot the item goes in
//	 * @param props The item's properties
//	 * @param maxDR The maximum amount of damage reduction this item can provide
//	 */
//	protected RedArmorItem(ArmorMaterial mat, EquipmentSlot slot, Properties props, float maxDR) {
//		super(mat, slot, props);
//		this.maxDR = maxDR;
//	}
//	
//	/**
//	 * Change this 
//	 * @return
//	 */
//	public float getDamageReductionMin() {return 0;}
//	
//	/**
//	 * Override to change the default damage reduction calculation
//	 * By default, it scales linearly with burnout level
//	 * 
//	 * @return The current reduction this item provides
//	 */
//	public float getDamageReduction(ItemStack stack) {
//		return getBurnoutPercent(stack)*maxDR;
//	};
//	
//	
//	/**
//	 * Gets the maximum damage reduction this item can provide
//	 * 
//	 * @return the maxDR
//	 */
//	public float getDamageReductionMax() {return maxDR;}
//	
//	/**
//	 * <b>Overriding this is unnecessary,</b>
//	 * please 
//	 */
//	@Override
//	public float getFullSetBaseReduction() {
//		return 0;
//	}
//	
//	
//}
