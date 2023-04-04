package net.solunareclipse1.magitekkit.data;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags;

import net.solunareclipse1.magitekkit.MagiTekkit;
import net.solunareclipse1.magitekkit.init.ObjectInit;

import vazkii.botania.common.entity.ModEntities;

public class MGTKEntityTags extends EntityTypeTagsProvider {
	public MGTKEntityTags(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, MagiTekkit.MODID, helper);
    }

	public static final TagKey<EntityType<?>> ITEMIZER_ENTITY_BLACKLIST = makeTag("itemizer_entity_blacklist");
	public static final TagKey<EntityType<?>> PHILO_HOMING_ARROW_BLACKLIST = makeTag("philo_homing_arrow_blacklist");

    @Override
    protected void addTags() {
        tag(ITEMIZER_ENTITY_BLACKLIST)
    		.add(EntityType.PLAYER)
    		.add(EntityType.ENDER_DRAGON)
        ;
        
        tag(PHILO_HOMING_ARROW_BLACKLIST)
    		.add(EntityType.ARMOR_STAND)
			.add(EntityType.ENDERMAN);
        
        tag(PETags.Entities.BLACKLIST_SWRG)
        	.add(ModEntities.DOPPLEGANGER)
        ;
        
        tag(PETags.Entities.BLACKLIST_INTERDICTION)
        	.add(ModEntities.DOPPLEGANGER)
        ;
    }
    
    private static TagKey<EntityType<?>> makeTag(String name) {
    	return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("magitekkit", name));
    }

    @Override
    public String getName() {
        return "MagiTekkit Block Tags";
    }
}
