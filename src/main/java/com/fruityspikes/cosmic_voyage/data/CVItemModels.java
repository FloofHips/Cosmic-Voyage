package com.fruityspikes.cosmic_voyage.data;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.server.registries.CVItemRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.HashSet;
import java.util.Set;

import static com.fruityspikes.cosmic_voyage.server.registries.CVItemRegistry.ITEMS;

public class CVItemModels extends ItemModelProvider {
    public CVItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CosmicVoyage.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(CVItemRegistry.COSMIC_TEST.getId().getPath());
        Set<DeferredItem<Item>> items = new HashSet(ITEMS.getEntries());
        DataHelper.takeAll(items, i -> i.get() instanceof BlockItem).forEach(this::blockItem);
    }
    private ItemModelBuilder simpleItem(String path) {
        return withExistingParent(path,
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.withDefaultNamespace("item/" + path));
    }
    private ItemModelBuilder handheldItem(String path) {
        return withExistingParent(path,
                ResourceLocation.withDefaultNamespace("item/handheld")).texture("layer0",
                ResourceLocation.withDefaultNamespace("item/" + path));
    }

    private void blockItem(DeferredItem<Item> i) {
        String name = i.getId().getPath();
        getBuilder(name).parent(new ModelFile.UncheckedModelFile(prefix("block/" + name)));
    }

    private ResourceLocation prefix(String s) {
        return ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, s);
    }

}
