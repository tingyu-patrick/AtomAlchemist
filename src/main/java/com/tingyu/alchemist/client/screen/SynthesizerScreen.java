package com.tingyu.alchemist.client.screen;

import com.tingyu.alchemist.Alchemist;
import com.tingyu.alchemist.block.entity.SynthesizerBlockEntity;
import com.tingyu.alchemist.menu.SynthesizerMenu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SynthesizerScreen extends AbstractContainerScreen<SynthesizerMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Alchemist.MODID, "textures/gui/synthesizer.png");

    // Matches the sprite regions baked into synthesizer.png by the texture generator script.
    private static final int ARROW_BG_U = 176;
    private static final int ARROW_BG_V = 0;
    private static final int ARROW_FG_U = 176;
    private static final int ARROW_FG_V = 17;
    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_HEIGHT = 17;

    public SynthesizerScreen(SynthesizerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int arrowX = leftPos + SynthesizerMenu.ARROW_X;
        int arrowY = topPos + SynthesizerMenu.ARROW_Y;
        graphics.blit(TEXTURE, arrowX, arrowY, ARROW_BG_U, ARROW_BG_V, ARROW_WIDTH, ARROW_HEIGHT);

        SynthesizerBlockEntity blockEntity = getMenu().getBlockEntity();
        if (blockEntity.isLit()) {
            int filled = Math.min(ARROW_WIDTH,
                    blockEntity.getProcessingProgress() * ARROW_WIDTH / SynthesizerBlockEntity.PROCESS_TIME_TICKS);
            if (filled > 0) {
                graphics.blit(TEXTURE, arrowX, arrowY, ARROW_FG_U, ARROW_FG_V, filled, ARROW_HEIGHT);
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
