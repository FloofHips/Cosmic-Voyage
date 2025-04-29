package com.fruityspikes.cosmic_voyage.client.renderers;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.client.client_registries.CVModelLayers;
import com.fruityspikes.cosmic_voyage.client.models.ExplorerModel;
import com.fruityspikes.cosmic_voyage.client.models.ShipModel;
import com.fruityspikes.cosmic_voyage.server.entities.venus.ExplorerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;

public class ExplorerRenderer extends MobRenderer<ExplorerEntity, ExplorerModel<ExplorerEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "textures/entity/explorer_automata.png");
    public ExplorerRenderer(EntityRendererProvider.Context context) {
        super(context, new ExplorerModel(context.bakeLayer(CVModelLayers.EXPLORER)), 0.7F);
    }

    @Override
    public void render(ExplorerEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBufferSource, pPackedLight);
        //this.model.renderToBuffer(pPoseStack, pBufferSource.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(pEntity))), pPackedLight, OverlayTexture.NO_OVERLAY, -1);
    }

    @Override
    public ResourceLocation getTextureLocation(ExplorerEntity explorerEntity) {
        return TEXTURE;
    }
}
