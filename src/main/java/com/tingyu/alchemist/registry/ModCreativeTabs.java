package com.tingyu.alchemist.registry;

import com.tingyu.alchemist.Alchemist;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Alchemist.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ALCHEMY_TAB = CREATIVE_MODE_TABS.register("alchemy_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.alchemist"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.ATOM_IRON.get().getDefaultInstance())
                    .displayItems((parameters, output) -> ModItems.ITEMS.getEntries()
                            .forEach(entry -> output.accept(entry.get())))
                    .build());
}
