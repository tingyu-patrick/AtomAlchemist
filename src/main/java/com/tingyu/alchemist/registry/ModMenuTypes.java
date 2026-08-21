package com.tingyu.alchemist.registry;

import com.tingyu.alchemist.Alchemist;
import com.tingyu.alchemist.menu.DecomposerMenu;
import com.tingyu.alchemist.menu.SynthesizerMenu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, Alchemist.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<DecomposerMenu>> DECOMPOSER =
            MENU_TYPES.register("decomposer", () -> IMenuTypeExtension.create(DecomposerMenu::create));

    public static final DeferredHolder<MenuType<?>, MenuType<SynthesizerMenu>> SYNTHESIZER =
            MENU_TYPES.register("synthesizer", () -> IMenuTypeExtension.create(SynthesizerMenu::create));
}
