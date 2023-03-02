package net.solunareclipse1.magitekkit.init;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.common.item.armor.CrimsonArmor;
import net.solunareclipse1.magitekkit.common.item.armor.CrimsonArmor.CrimsonArmorMaterial;
import net.solunareclipse1.magitekkit.common.item.armor.VoidArmorBase;
import net.solunareclipse1.magitekkit.common.item.armor.VoidArmorBase.VoidArmorMaterial;
import net.solunareclipse1.magitekkit.common.item.armor.gem.GemAmulet;
import net.solunareclipse1.magitekkit.common.item.armor.gem.GemAnklet;
import net.solunareclipse1.magitekkit.common.item.armor.gem.GemCirclet;
import net.solunareclipse1.magitekkit.common.item.armor.gem.GemTimepiece;
import net.solunareclipse1.magitekkit.common.item.curio.CovalenceBracelet;
import net.solunareclipse1.magitekkit.common.item.curio.GemBracelet;

public class ObjectInit {
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MagiTekkit.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MagiTekkit.MODID);

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }

    // Some common properties for our blocks and items
    public static final BlockBehaviour.Properties BLOCK_PROPERTIES = BlockBehaviour.Properties.of(Material.STONE).strength(2f).requiresCorrectToolForDrops();
    public static final Item.Properties ITEM_PROPERTIES_GENERIC = new Item.Properties().tab(ModInit.ITEM_GROUP);
    public static final Item.Properties ITEM_PROPERTIES_UNBREAKABLE = new Item.Properties().tab(ModInit.ITEM_GROUP).durability(0);
    public static final Item.Properties ITEM_PROPERTIES_JEWELRY = new Item.Properties().tab(ModInit.ITEM_GROUP).defaultDurability(96).durability(96);

    // Blocks
    public static final RegistryObject<Block> GANTIUM_BLOCK = BLOCKS.register("gantium_block", () -> new Block(BLOCK_PROPERTIES));
    
    // BlockItems
    public static final RegistryObject<Item> GANTIUM_BLOCK_ITEM = fromBlock(GANTIUM_BLOCK);
    
    // Items
    public static final RegistryObject<CovalenceBracelet> COVALENCE_BRACELET = ITEMS.register("covalence_bracelet", () -> new CovalenceBracelet(ITEM_PROPERTIES_GENERIC.stacksTo(1)));
    
    public static final RegistryObject<VoidArmorBase> VOID_HELM = ITEMS.register("void_helm", () -> new VoidArmorBase(VoidArmorMaterial.MAT, EquipmentSlot.HEAD, ITEM_PROPERTIES_UNBREAKABLE, 0.15f));
    public static final RegistryObject<VoidArmorBase> VOID_CHEST = ITEMS.register("void_chest", () -> new VoidArmorBase(VoidArmorMaterial.MAT, EquipmentSlot.CHEST, ITEM_PROPERTIES_UNBREAKABLE, 0.36f));
    public static final RegistryObject<VoidArmorBase> VOID_LEGS = ITEMS.register("void_legs", () -> new VoidArmorBase(VoidArmorMaterial.MAT, EquipmentSlot.LEGS, ITEM_PROPERTIES_UNBREAKABLE, 0.27f));
    public static final RegistryObject<VoidArmorBase> VOID_BOOTS = ITEMS.register("void_boots", () -> new VoidArmorBase(VoidArmorMaterial.MAT, EquipmentSlot.FEET, ITEM_PROPERTIES_UNBREAKABLE, 0.12f));
    
    public static final RegistryObject<CrimsonArmor> CRIMSON_HELM = ITEMS.register("crimson_helm", () -> new CrimsonArmor(CrimsonArmorMaterial.MAT, EquipmentSlot.HEAD, ITEM_PROPERTIES_UNBREAKABLE, 0.17f));
    public static final RegistryObject<CrimsonArmor> CRIMSON_CHEST = ITEMS.register("crimson_chest", () -> new CrimsonArmor(CrimsonArmorMaterial.MAT, EquipmentSlot.CHEST, ITEM_PROPERTIES_UNBREAKABLE, 0.40f));
    public static final RegistryObject<CrimsonArmor> CRIMSON_LEGS = ITEMS.register("crimson_legs", () -> new CrimsonArmor(CrimsonArmorMaterial.MAT, EquipmentSlot.LEGS, ITEM_PROPERTIES_UNBREAKABLE, 0.30f));
    public static final RegistryObject<CrimsonArmor> CRIMSON_BOOTS = ITEMS.register("crimson_boots", () -> new CrimsonArmor(CrimsonArmorMaterial.MAT, EquipmentSlot.FEET, ITEM_PROPERTIES_UNBREAKABLE, 0.13f));
    
    public static final RegistryObject<GemCirclet> GEM_CIRCLET = ITEMS.register("gem_circlet", () -> new GemCirclet(ITEM_PROPERTIES_JEWELRY, 0.25f));
    public static final RegistryObject<GemAmulet> GEM_AMULET = ITEMS.register("gem_amulet", () -> new GemAmulet(ITEM_PROPERTIES_JEWELRY, 0.25f));
    public static final RegistryObject<GemTimepiece> GEM_TIMEPIECE = ITEMS.register("gem_timepiece", () -> new GemTimepiece(ITEM_PROPERTIES_JEWELRY, 0.25f));
    public static final RegistryObject<GemAnklet> GEM_ANKLET = ITEMS.register("gem_anklet", () -> new GemAnklet(ITEM_PROPERTIES_JEWELRY, 0.25f));
    public static final RegistryObject<GemBracelet> GEM_BRACELET = ITEMS.register("gem_bracelet", () -> new GemBracelet(ITEM_PROPERTIES_GENERIC.stacksTo(1)));

    // Conveniance function: Take a RegistryObject<Block> and make a corresponding RegistryObject<Item> from it
    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES_GENERIC));
    }
}