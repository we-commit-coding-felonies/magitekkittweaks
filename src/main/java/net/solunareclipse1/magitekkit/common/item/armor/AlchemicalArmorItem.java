package net.solunareclipse1.magitekkit.common.item.armor;

import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import net.solunareclipse1.magitekkit.util.DuraBarHelper;

public class AlchemicalArmorItem extends ArmorItem {
	DuraBarHelper duraBarHelper = new DuraBarHelper();
	private int maxBurnOut;
	private float drAmount;
	/**
	 * PTArmorItems are unbreakable armor pieces that reduce damage by a percentage amount,
	 * but provide reduced protection from consecutive attacks
	 * 
	 * As damage is blocked, the item will accumulate "burnout"
	 * The percent reduction changes based on the properties of the DamageType reduces,
	 * as well as how "burnt out" the item is
	 * 
	 * Burnout is stored in an integer NBT tag pe_burnout on the item
	 * 
	 * @param slot The EquipmentSlot this item belongs in
	 * @param drAmount The base amount of Damage Reduction this item provides
	 * @param maxBurnOut The maximum amount of burnout this item can accumulate
	 * @param props The properties of the item
	 */
	public AlchemicalArmorItem(ArmorMaterial mat, EquipmentSlot slot, float drAmount, int maxBurnOut, Properties props) {
		super(mat, slot, props);
		this.drAmount = drAmount;
		this.maxBurnOut = maxBurnOut;
	}
	
	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		if (isBarVisible(stack) && level.getGameTime() % Math.max(3, 13*getBurnOutPercentage(stack)) == 0) {
			setBurnOut(stack, getBurnOut(stack) - 1);
		}
	}
	
	@Override
	public boolean isBarVisible(@NotNull ItemStack stack) {
		return stack.getOrCreateTag().getInt("pe_burnout") > 0;
	}
	
	@Override
	public int getBarWidth(@NotNull ItemStack stack) {return duraBarHelper.barLevelFromCurMax(getBurnOut(stack), getMaxBurnOut());}

	@Override
	public int getBarColor(@NotNull ItemStack stack) {return duraBarHelper.covBarColor((float) getBarWidth(stack) / (float) 13);}
	
	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull Level level, @NotNull List<Component> tips, @NotNull TooltipFlag flag) {
		super.appendHoverText(stack, level, tips, flag);
		int stepAmt = Math.round(getMaxBurnOut() / 3);
		int burnOutAmt = getBurnOut(stack);
		ChatFormatting numeratorColor = ChatFormatting.GRAY;
		if (burnOutAmt > 0 && burnOutAmt < stepAmt) numeratorColor = ChatFormatting.GREEN;
		if (burnOutAmt >= stepAmt && burnOutAmt < stepAmt*2) numeratorColor = ChatFormatting.YELLOW;
		if (burnOutAmt >= stepAmt*2 && burnOutAmt < 9*(getMaxBurnOut()/10)) numeratorColor = ChatFormatting.RED;
		if (burnOutAmt >= 9*(getMaxBurnOut()/10)) numeratorColor = ChatFormatting.DARK_RED;
		MutableComponent numerator = new TextComponent(getBurnOut(stack)+"").withStyle(numeratorColor);
		MutableComponent denominator = new TextComponent(getMaxBurnOut()+"").withStyle(ChatFormatting.AQUA);
		tips.add(new TranslatableComponent("toolTip.pt.burnout", numerator, denominator));
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {return 0;}

	/**
	 * Gets the base damage reduction value of this item
	 * This value changes based on how burnt out the item is, and what kind of damage is being reduced
	 * 1 is a 100% reduction, 0 is no reduction
	 * @return Damage Reduction percentage
	 */
	public float getDrAmount() {return this.drAmount;}
	
	/**
	 * Gets the maximum amount of burnout for this item
	 * @return Maximum burnout value
	 */
	public int getMaxBurnOut() {return this.maxBurnOut;}
	
	/**
	 * Gets the percentage burnout for the given itemstack
	 * 1.0 = 100% burnout
	 * @param stack the stack to check
	 * @return
	 */
	public double getBurnOutPercentage(ItemStack stack) {return isBarVisible(stack) ? (double) stack.getOrCreateTag().getInt("pe_burnout") / this.maxBurnOut : 0.0;} 
	
	/**
	 * Gets the amount of burnout on the itemstack
	 * @param stack The itemstack to query
	 * @return
	 */
	public int getBurnOut(ItemStack stack) {return stack.getOrCreateTag().getInt("pe_burnout");}
	
	/**
	 * Sets the items burnout to the given amount
	 * @param stack The stack to set
	 * @param amount The amount to set to
	 */
	public void setBurnOut(ItemStack stack, int amount) {
		if (amount < 0) amount = 0;
		stack.getOrCreateTag().putInt("pe_burnout", Math.min(this.maxBurnOut, amount));
	}	
	
	@Override
	public boolean isEnchantable(@NotNull ItemStack stack) {return false;}
	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {return false;}
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {return false;}
}
