package com.tingyu.alchemist.menu;

import com.tingyu.alchemist.block.entity.SynthesizerBlockEntity;
import com.tingyu.alchemist.registry.ModMenuTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class SynthesizerMenu extends AbstractContainerMenu {
    private static final int GRID_X = 30;
    private static final int GRID_Y = 17;
    private static final int FUEL_X = 98;
    private static final int FUEL_Y = 53;
    private static final int OUTPUT_X = 124;
    private static final int OUTPUT_Y = 35;

    public static final int ARROW_X = 94;
    public static final int ARROW_Y = 35;

    private static final int PLAYER_INV_X = 8;
    private static final int PLAYER_INV_Y = 84;
    private static final int PLAYER_HOTBAR_Y = 142;

    private final SynthesizerBlockEntity blockEntity;

    public SynthesizerMenu(int containerId, Inventory playerInventory, SynthesizerBlockEntity blockEntity) {
        super(ModMenuTypes.SYNTHESIZER.get(), containerId);
        this.blockEntity = blockEntity;

        var itemHandler = blockEntity.getItemHandler();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int slot = row * 3 + col;
                addSlot(new SlotItemHandler(itemHandler, slot, GRID_X + col * 18, GRID_Y + row * 18));
            }
        }
        addSlot(new SlotItemHandler(itemHandler, SynthesizerBlockEntity.FUEL_SLOT, FUEL_X, FUEL_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getBurnTime(null) > 0;
            }
        });
        addSlot(new SlotItemHandler(itemHandler, SynthesizerBlockEntity.OUTPUT_SLOT, OUTPUT_X, OUTPUT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                // Output-only: results land here via the machine's own tick logic.
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, PLAYER_INV_X + col * 18, PLAYER_INV_Y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, PLAYER_INV_X + col * 18, PLAYER_HOTBAR_Y));
        }

        addDataSlots(blockEntity.getDataAccess());
    }

    public SynthesizerBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public static SynthesizerMenu create(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity entity = playerInventory.player.level().getBlockEntity(pos);
        if (entity instanceof SynthesizerBlockEntity synthesizer) {
            return new SynthesizerMenu(containerId, playerInventory, synthesizer);
        }
        throw new IllegalStateException("No SynthesizerBlockEntity at " + pos);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack original = slot.getItem();
        ItemStack result = original.copy();
        int machineSlots = SynthesizerBlockEntity.SLOT_COUNT;

        if (index < machineSlots) {
            // Machine -> player inventory
            if (!moveItemStackTo(original, machineSlots, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Player inventory -> fuel slot if it's fuel, otherwise the input grid
            boolean isFuel = original.getBurnTime(null) > 0;
            boolean moved = isFuel
                    && moveItemStackTo(original, SynthesizerBlockEntity.FUEL_SLOT, SynthesizerBlockEntity.FUEL_SLOT + 1, false);
            if (!moved) {
                moved = moveItemStackTo(original, 0, SynthesizerBlockEntity.INPUT_SLOT_COUNT, false);
            }
            if (!moved) {
                return ItemStack.EMPTY;
            }
        }

        if (original.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity.getLevel() != null
                && blockEntity.getLevel().getBlockEntity(blockEntity.getBlockPos()) == blockEntity
                && player.distanceToSqr(
                        blockEntity.getBlockPos().getX() + 0.5,
                        blockEntity.getBlockPos().getY() + 0.5,
                        blockEntity.getBlockPos().getZ() + 0.5) <= 64.0;
    }
}
