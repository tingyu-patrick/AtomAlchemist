package com.tingyu.alchemist.item;

import java.util.List;

import com.tingyu.alchemist.chemistry.Element;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class AtomItem extends Item {
    private final Element element;

    public AtomItem(Properties properties, Element element) {
        super(properties);
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(element.symbol() + " · #" + element.atomicNumber())
                .withStyle(ChatFormatting.GRAY));
    }
}
