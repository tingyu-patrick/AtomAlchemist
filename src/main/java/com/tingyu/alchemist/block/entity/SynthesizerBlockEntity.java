package com.tingyu.alchemist.block.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tingyu.alchemist.item.AtomItem;
import com.tingyu.alchemist.menu.SynthesizerMenu;
import com.tingyu.alchemist.recipe.AtomRecipeInput;
import com.tingyu.alchemist.recipe.SynthesizingRecipe;
import com.tingyu.alchemist.registry.ModBlockEntities;
import com.tingyu.alchemist.registry.ModRecipeTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class SynthesizerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int PROCESS_TIME_TICKS = 100;

    public static final int INPUT_SLOT_COUNT = 9;
    public static final int FUEL_SLOT = INPUT_SLOT_COUNT;
    public static final int OUTPUT_SLOT = INPUT_SLOT_COUNT + 1;
    public static final int SLOT_COUNT = INPUT_SLOT_COUNT + 2;

    private final ItemStackHandler itemHandler = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            // Only atoms belong in the input slots; fuel/output stay open at this layer
            // (fuel is validated by burn time, output is filled by the machine itself via
            // insertItem() -- player-facing restriction happens via Slot#mayPlace in the menu).
            return slot >= INPUT_SLOT_COUNT || stack.getItem() instanceof AtomItem;
        }
    };

    private int litTimeRemaining;
    private int litDuration;
    private int processingProgress;

    // Mirrors the vanilla furnace pattern: wraps the three fields above so the menu can
    // sync their current values to the client each tick for the progress bar/fuel display.
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> litTimeRemaining;
                case 1 -> litDuration;
                case 2 -> processingProgress;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> litTimeRemaining = value;
                case 1 -> litDuration = value;
                case 2 -> processingProgress = value;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    public SynthesizerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SYNTHESIZER.get(), pos, state);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public boolean isLit() {
        return litTimeRemaining > 0;
    }

    public int getProcessingProgress() {
        return processingProgress;
    }

    public int getLitTimeRemaining() {
        return litTimeRemaining;
    }

    public int getLitDuration() {
        return litDuration;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SynthesizerBlockEntity entity) {
        boolean changed = false;

        if (entity.litTimeRemaining > 0) {
            entity.litTimeRemaining--;
            changed = true;
        }

        List<ItemStack> inputs = new ArrayList<>(INPUT_SLOT_COUNT);
        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
            inputs.add(entity.itemHandler.getStackInSlot(slot));
        }

        Optional<RecipeHolder<SynthesizingRecipe>> match = level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.SYNTHESIZING.get(), new AtomRecipeInput(inputs), level);

        boolean canProcess = match.isPresent() && entity.canFitOutput(match.get().value().getResultItem(level.registryAccess()));

        if (canProcess) {
            if (!entity.isLit()) {
                ItemStack fuel = entity.itemHandler.getStackInSlot(FUEL_SLOT);
                int fuelBurnTime = fuel.getBurnTime(ModRecipeTypes.SYNTHESIZING.get());
                if (fuelBurnTime > 0) {
                    entity.itemHandler.extractItem(FUEL_SLOT, 1, false);
                    entity.litTimeRemaining = fuelBurnTime;
                    entity.litDuration = fuelBurnTime;
                    changed = true;
                }
            }

            if (entity.isLit()) {
                entity.processingProgress++;
                changed = true;
                if (entity.processingProgress >= PROCESS_TIME_TICKS) {
                    entity.processingProgress = 0;
                    entity.synthesize(match.get().value());
                }
            } else if (entity.processingProgress != 0) {
                entity.processingProgress = 0;
                changed = true;
            }
        } else if (entity.processingProgress != 0) {
            entity.processingProgress = 0;
            changed = true;
        }

        if (changed) {
            entity.setChanged();
        }
    }

    private void synthesize(SynthesizingRecipe recipe) {
        // Consume only what the recipe needs; any surplus of a required atom (or
        // unrelated atoms) stays untouched in the input slots.
        for (ItemStack required : recipe.inputs()) {
            consumeFromInputSlots(required.getItem(), required.getCount());
        }
        itemHandler.insertItem(OUTPUT_SLOT, recipe.getResultItem(level.registryAccess()), false);
    }

    private void consumeFromInputSlots(Item item, int amount) {
        int remaining = amount;
        for (int slot = 0; slot < INPUT_SLOT_COUNT && remaining > 0; slot++) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (stack.getItem() != item) {
                continue;
            }
            int extract = Math.min(remaining, stack.getCount());
            itemHandler.extractItem(slot, extract, false);
            remaining -= extract;
        }
    }

    private boolean canFitOutput(ItemStack result) {
        ItemStack remainder = itemHandler.insertItem(OUTPUT_SLOT, result, true);
        return remainder.isEmpty();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("LitTime", litTimeRemaining);
        tag.putInt("LitDuration", litDuration);
        tag.putInt("ProcessingProgress", processingProgress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        litTimeRemaining = tag.getInt("LitTime");
        litDuration = tag.getInt("LitDuration");
        processingProgress = tag.getInt("ProcessingProgress");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.alchemist.synthesizer");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SynthesizerMenu(containerId, playerInventory, this);
    }
}
