package com.tingyu.alchemist.recipe;

import java.util.List;

import com.tingyu.alchemist.registry.ModRecipeSerializers;
import com.tingyu.alchemist.registry.ModRecipeTypes;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public record DecomposingRecipe(Ingredient input, List<ItemStack> outputs) implements Recipe<SingleRecipeInput> {
    @Override
    public boolean matches(SingleRecipeInput recipeInput, Level level) {
        return input.test(recipeInput.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput recipeInput, HolderLookup.Provider registries) {
        return getResultItem(registries);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        // Display/inventory-result purposes only; the real multi-atom output list is `outputs()`.
        return outputs.isEmpty() ? ItemStack.EMPTY : outputs.get(0).copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input);
        return list;
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return ModRecipeSerializers.DECOMPOSING.get();
    }

    @Override
    public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return ModRecipeTypes.DECOMPOSING.get();
    }
}
