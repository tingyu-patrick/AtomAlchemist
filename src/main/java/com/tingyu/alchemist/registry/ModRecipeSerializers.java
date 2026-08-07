package com.tingyu.alchemist.registry;

import com.tingyu.alchemist.Alchemist;
import com.tingyu.alchemist.recipe.DecomposingRecipeSerializer;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Alchemist.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, DecomposingRecipeSerializer> DECOMPOSING =
            RECIPE_SERIALIZERS.register("decomposing", DecomposingRecipeSerializer::new);
}
