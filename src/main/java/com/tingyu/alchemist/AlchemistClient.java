package com.tingyu.alchemist;

import com.tingyu.alchemist.client.screen.DecomposerScreen;
import com.tingyu.alchemist.item.AtomItem;
import com.tingyu.alchemist.registry.ModItems;
import com.tingyu.alchemist.registry.ModMenuTypes;

import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Alchemist.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = Alchemist.MODID, value = Dist.CLIENT)
public class AlchemistClient {
    public AlchemistClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.DECOMPOSER.get(), DecomposerScreen::new);
    }

    @SubscribeEvent
    static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (stack.getItem() instanceof AtomItem atomItem) {
                // Item tint is multiplied against the texture including alpha;
                // omitting 0xFF here renders the icon fully transparent.
                return 0xFF000000 | atomItem.getElement().color();
            }
            return 0xFFFFFFFF;
        }, ModItems.ITEMS.getEntries().stream()
                .map(entry -> entry.get())
                .toArray(Item[]::new));
    }
}
