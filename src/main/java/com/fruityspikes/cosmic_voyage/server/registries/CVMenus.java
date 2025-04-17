package com.fruityspikes.cosmic_voyage.server.registries;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.client.gui.HelmGui;
import com.fruityspikes.cosmic_voyage.server.menus.HelmMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class CVMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, CosmicVoyage.MODID);

//    public static final DeferredHolder<MenuType<?>, MenuType<HelmMenu>> HELM = MENUS.register("helm",
//            () -> new MenuType<>(HelmMenu::new, FeatureFlags.REGISTRY.allFlags()));

    public static final Supplier<MenuType<HelmMenu>> HELM = MENUS.register("helm", () -> new MenuType<>(HelmMenu::new, FeatureFlags.DEFAULT_FLAGS));

}
