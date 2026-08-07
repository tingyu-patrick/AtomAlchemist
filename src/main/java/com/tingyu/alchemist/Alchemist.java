package com.tingyu.alchemist;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.tingyu.alchemist.registry.ModBlockEntities;
import com.tingyu.alchemist.registry.ModBlocks;
import com.tingyu.alchemist.registry.ModCreativeTabs;
import com.tingyu.alchemist.registry.ModItems;
import com.tingyu.alchemist.registry.ModMenuTypes;
import com.tingyu.alchemist.registry.ModRecipeSerializers;
import com.tingyu.alchemist.registry.ModRecipeTypes;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(Alchemist.MODID)
public class Alchemist {
    public static final String MODID = "alchemist";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Alchemist(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
