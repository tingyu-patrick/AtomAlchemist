package com.tingyu.alchemist.registry;

import com.tingyu.alchemist.Alchemist;
import com.tingyu.alchemist.recipe.DecomposingRecipe;
import com.tingyu.alchemist.recipe.SynthesizingRecipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Alchemist.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<DecomposingRecipe>> DECOMPOSING =
            RECIPE_TYPES.register("decomposing", () -> new RecipeType<DecomposingRecipe>() {
                @Override
                public String toString() {
                    return "alchemist:decomposing";
                }
            });

    public static final DeferredHolder<RecipeType<?>, RecipeType<SynthesizingRecipe>> SYNTHESIZING =
            RECIPE_TYPES.register("synthesizing", () -> new RecipeType<SynthesizingRecipe>() {
                @Override
                public String toString() {
                    return "alchemist:synthesizing";
                }
            });
}
