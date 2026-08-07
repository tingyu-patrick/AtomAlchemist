package com.tingyu.alchemist.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class DecomposingRecipeSerializer implements RecipeSerializer<DecomposingRecipe> {
    private static final MapCodec<DecomposingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(DecomposingRecipe::input),
            ItemStack.STRICT_CODEC.listOf().fieldOf("outputs").forGetter(DecomposingRecipe::outputs))
            .apply(instance, DecomposingRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, DecomposingRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, DecomposingRecipe::input,
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), DecomposingRecipe::outputs,
            DecomposingRecipe::new);

    @Override
    public MapCodec<DecomposingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, DecomposingRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
