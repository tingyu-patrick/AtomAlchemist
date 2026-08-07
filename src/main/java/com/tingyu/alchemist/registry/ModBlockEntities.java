package com.tingyu.alchemist.registry;

import com.tingyu.alchemist.Alchemist;
import com.tingyu.alchemist.block.entity.DecomposerBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Alchemist.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DecomposerBlockEntity>> DECOMPOSER =
            BLOCK_ENTITY_TYPES.register("decomposer", () -> BlockEntityType.Builder.of(
                    DecomposerBlockEntity::new, ModBlocks.DECOMPOSER.get()).build(null));
}
