package com.tingyu.alchemist.block.entity;

import java.util.List;
import java.util.Optional;

import com.tingyu.alchemist.menu.DecomposerMenu;
import com.tingyu.alchemist.recipe.DecomposingRecipe;
import com.tingyu.alchemist.registry.ModBlockEntities;
import com.tingyu.alchemist.registry.ModRecipeTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.MenuProvider;
import net.neoforged.neoforge.items.ItemStackHandler;

public class DecomposerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT_COUNT = 4;
    public static final int SLOT_COUNT = 1 + OUTPUT_SLOT_COUNT;

    private final ItemStackHandler itemHandler = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (slot == INPUT_SLOT && level != null && !level.isClientSide) {
                tryDecompose();
            }
        }

        // Deliberately not restricted to the input slot here: insertItem() (used internally by
        // tryDecompose() to push results into the output slots) checks isItemValid() itself, so
        // restricting it here would also block the machine from filling its own output slots.
        // Player-facing insertion is blocked per-slot instead, via Slot#mayPlace in the menu.
    };

    public DecomposerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DECOMPOSER.get(), pos, state);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    private void tryDecompose() {
        ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);
        if (input.isEmpty() || level == null) {
            return;
        }

        Optional<RecipeHolder<DecomposingRecipe>> match = level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.DECOMPOSING.get(), new SingleRecipeInput(input), level);
        if (match.isEmpty()) {
            return;
        }

        List<ItemStack> outputs = match.get().value().outputs();
        if (!canFitOutputs(outputs)) {
            return;
        }

        input.shrink(1);
        for (ItemStack output : outputs) {
            insertIntoOutputs(output.copy());
        }
        setChanged();
    }

    // A recipe can yield several distinct atoms (e.g. sugar -> C/H/O); this checks them all
    // against a scratch copy of the output slots at once so a multi-output recipe never
    // partially reserves slots before discovering a later output doesn't fit.
    private boolean canFitOutputs(List<ItemStack> outputs) {
        ItemStackHandler scratch = new ItemStackHandler(SLOT_COUNT);
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            scratch.setStackInSlot(slot, itemHandler.getStackInSlot(slot).copy());
        }
        for (ItemStack output : outputs) {
            ItemStack remainder = output.copy();
            for (int slot = INPUT_SLOT + 1; slot < SLOT_COUNT && !remainder.isEmpty(); slot++) {
                remainder = scratch.insertItem(slot, remainder, false);
            }
            if (!remainder.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void insertIntoOutputs(ItemStack stack) {
        for (int slot = INPUT_SLOT + 1; slot < SLOT_COUNT && !stack.isEmpty(); slot++) {
            stack = itemHandler.insertItem(slot, stack, false);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.alchemist.decomposer");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new DecomposerMenu(containerId, playerInventory, this);
    }
}
