package com.tingyu.alchemist.registry;

import com.tingyu.alchemist.Alchemist;
import com.tingyu.alchemist.chemistry.Element;
import com.tingyu.alchemist.chemistry.Elements;
import com.tingyu.alchemist.item.AtomItem;

import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Alchemist.MODID);

    public static final DeferredItem<AtomItem> ATOM_HYDROGEN = registerAtom(Elements.HYDROGEN);
    public static final DeferredItem<AtomItem> ATOM_CARBON = registerAtom(Elements.CARBON);
    public static final DeferredItem<AtomItem> ATOM_OXYGEN = registerAtom(Elements.OXYGEN);
    public static final DeferredItem<AtomItem> ATOM_IRON = registerAtom(Elements.IRON);

    public static final DeferredItem<BlockItem> DECOMPOSER =
            ITEMS.registerSimpleBlockItem(ModBlocks.DECOMPOSER);

    private static DeferredItem<AtomItem> registerAtom(Element element) {
        return ITEMS.registerItem("atom_" + element.id(), props -> new AtomItem(props, element));
    }
}
