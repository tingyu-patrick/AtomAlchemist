package com.tingyu.alchemist.registry;

import com.tingyu.alchemist.Alchemist;
import com.tingyu.alchemist.block.DecomposerBlock;
import com.tingyu.alchemist.block.SynthesizerBlock;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Alchemist.MODID);

    public static final DeferredBlock<DecomposerBlock> DECOMPOSER = BLOCKS.register("decomposer",
            () -> new DecomposerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5f)
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<SynthesizerBlock> SYNTHESIZER = BLOCKS.register("synthesizer",
            () -> new SynthesizerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5f)
                    .requiresCorrectToolForDrops()));
}
