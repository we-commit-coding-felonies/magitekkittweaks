package net.solunareclipse1.magitekkit.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
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
import net.solunareclipse1.magitekkit.common.item.armor.BarrierArmorItem;
import net.solunareclipse1.magitekkit.common.item.armor.AlchemicalArmorItem;
import net.solunareclipse1.magitekkit.common.item.armor.MGTKArmorMaterials.*;

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
    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(ModInit.ITEM_GROUP);

    // Blocks
    public static final RegistryObject<Block> GANTIUM_BLOCK = BLOCKS.register("gantium_block", () -> new Block(BLOCK_PROPERTIES));
    
    // BlockItems
    public static final RegistryObject<Item> GANTIUM_BLOCK_ITEM = fromBlock(GANTIUM_BLOCK);
    
    // Items
    public static final RegistryObject<AlchemicalArmorItem> VOID_HELM = ITEMS.register("void_helm", () -> new AlchemicalArmorItem(VoidArmorMaterial.MAT, EquipmentSlot.HEAD, 0.15f, 16384, ITEM_PROPERTIES));
    public static final RegistryObject<AlchemicalArmorItem> VOID_CHEST = ITEMS.register("void_chest", () -> new AlchemicalArmorItem(VoidArmorMaterial.MAT, EquipmentSlot.CHEST, 0.36f, 16384, ITEM_PROPERTIES));
    public static final RegistryObject<AlchemicalArmorItem> VOID_LEGS = ITEMS.register("void_legs", () -> new AlchemicalArmorItem(VoidArmorMaterial.MAT, EquipmentSlot.LEGS, 0.27f, 16384,  ITEM_PROPERTIES));
    public static final RegistryObject<AlchemicalArmorItem> VOID_BOOTS = ITEMS.register("void_boots", () -> new AlchemicalArmorItem(VoidArmorMaterial.MAT, EquipmentSlot.FEET, 0.12f, 16384, ITEM_PROPERTIES));
    
    public static final RegistryObject<BarrierArmorItem> PHIL_HELM = ITEMS.register("phil_helm", () -> new BarrierArmorItem(PhilArmorMaterial.MAT, EquipmentSlot.HEAD, 0.17f, 1024, ITEM_PROPERTIES));
    public static final RegistryObject<BarrierArmorItem> PHIL_CHEST = ITEMS.register("phil_chest", () -> new BarrierArmorItem(PhilArmorMaterial.MAT, EquipmentSlot.CHEST, 0.4f, 1024, ITEM_PROPERTIES));
    public static final RegistryObject<BarrierArmorItem> PHIL_LEGS = ITEMS.register("phil_legs", () -> new BarrierArmorItem(PhilArmorMaterial.MAT, EquipmentSlot.LEGS, 0.3f, 1024,  ITEM_PROPERTIES));
    public static final RegistryObject<BarrierArmorItem> PHIL_BOOTS = ITEMS.register("phil_boots", () -> new BarrierArmorItem(PhilArmorMaterial.MAT, EquipmentSlot.FEET, 0.13f, 1024, ITEM_PROPERTIES));

    public static final TagKey<Item> VOID_ARMOR = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(MagiTekkit.MODID, "void_armor"));
    public static final TagKey<Item> PHIL_ARMOR = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(MagiTekkit.MODID, "phil_armor"));

    // Conveniance function: Take a RegistryObject<Block> and make a corresponding RegistryObject<Item> from it
    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }
}