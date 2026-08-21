package com.tingyu.alchemist.recipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tingyu.alchemist.registry.ModRecipeSerializers;
import com.tingyu.alchemist.registry.ModRecipeTypes;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record SynthesizingRecipe(List<ItemStack> inputs, ItemStack output) implements Recipe<AtomRecipeInput> {
    @Override
    public boolean matches(AtomRecipeInput recipeInput, Level level) {
        Map<Item, Integer> provided = tally(recipeInput.items());
        for (Map.Entry<Item, Integer> required : tally(inputs).entrySet()) {
            if (provided.getOrDefault(required.getKey(), 0) < required.getValue()) {
                return false;
            }
        }
        return true;
    }

    private static Map<Item, Integer> tally(List<ItemStack> stacks) {
        Map<Item, Integer> counts = new HashMap<>();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                counts.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }
        }
        return counts;
    }

    @Override
    public ItemStack assemble(AtomRecipeInput recipeInput, HolderLookup.Provider registries) {
        return getResultItem(registries);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        for (ItemStack stack : inputs) {
            list.add(Ingredient.of(stack));
        }
        return list;
    }

    @Override
    public RecipeSerializer<? extends Recipe<AtomRecipeInput>> getSerializer() {
        return ModRecipeSerializers.SYNTHESIZING.get();
    }

    @Override
    public RecipeType<? extends Recipe<AtomRecipeInput>> getType() {
        return ModRecipeTypes.SYNTHESIZING.get();
    }
}
