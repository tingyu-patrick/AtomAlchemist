package com.tingyu.alchemist.menu;

import com.tingyu.alchemist.block.entity.DecomposerBlockEntity;
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

public class DecomposerMenu extends AbstractContainerMenu {
    private static final int[] OUTPUT_SLOT_X = {80, 98, 116, 134};
    private static final int SLOT_Y = 35;

    private static final int PLAYER_INV_X = 8;
    private static final int PLAYER_INV_Y = 84;
    private static final int PLAYER_HOTBAR_Y = 142;

    private final DecomposerBlockEntity blockEntity;

    public DecomposerMenu(int containerId, Inventory playerInventory, DecomposerBlockEntity blockEntity) {
        super(ModMenuTypes.DECOMPOSER.get(), containerId);
        this.blockEntity = blockEntity;

        var itemHandler = blockEntity.getItemHandler();
        addSlot(new SlotItemHandler(itemHandler, DecomposerBlockEntity.INPUT_SLOT, 26, SLOT_Y));
        for (int i = 0; i < DecomposerBlockEntity.OUTPUT_SLOT_COUNT; i++) {
            int outputSlot = DecomposerBlockEntity.INPUT_SLOT + 1 + i;
            addSlot(new SlotItemHandler(itemHandler, outputSlot, OUTPUT_SLOT_X[i], SLOT_Y) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    // Output-only: results land here via tryDecompose(), never via player insertion.
                    return false;
                }
            });
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, PLAYER_INV_X + col * 18, PLAYER_INV_Y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, PLAYER_INV_X + col * 18, PLAYER_HOTBAR_Y));
        }
    }

    public static DecomposerMenu create(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity entity = playerInventory.player.level().getBlockEntity(pos);
        if (entity instanceof DecomposerBlockEntity decomposer) {
            return new DecomposerMenu(containerId, playerInventory, decomposer);
        }
        throw new IllegalStateException("No DecomposerBlockEntity at " + pos);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack original = slot.getItem();
        ItemStack result = original.copy();
        int machineSlots = DecomposerBlockEntity.SLOT_COUNT;

        if (index < machineSlots) {
            // Machine -> player inventory
            if (!moveItemStackTo(original, machineSlots, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Player inventory -> machine input slot only
            if (!moveItemStackTo(original, DecomposerBlockEntity.INPUT_SLOT, DecomposerBlockEntity.INPUT_SLOT + 1, false)) {
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
