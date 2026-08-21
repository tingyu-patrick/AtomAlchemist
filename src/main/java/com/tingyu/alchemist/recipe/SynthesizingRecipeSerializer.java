package com.tingyu.alchemist.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class SynthesizingRecipeSerializer implements RecipeSerializer<SynthesizingRecipe> {
    private static final MapCodec<SynthesizingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.STRICT_CODEC.listOf().fieldOf("inputs").forGetter(SynthesizingRecipe::inputs),
            ItemStack.STRICT_CODEC.fieldOf("output").forGetter(SynthesizingRecipe::output))
            .apply(instance, SynthesizingRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, SynthesizingRecipe> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), SynthesizingRecipe::inputs,
            ItemStack.STREAM_CODEC, SynthesizingRecipe::output,
            SynthesizingRecipe::new);

    @Override
    public MapCodec<SynthesizingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SynthesizingRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
