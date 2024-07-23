package com.fruityspikes.cosmic_voyage.client.renderers;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.client.client_registries.CVModelLayers;
import com.fruityspikes.cosmic_voyage.client.models.ShipModel;
import com.fruityspikes.cosmic_voyage.server.entities.ShipEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ShipRenderer extends EntityRenderer<ShipEntity> {
    private static final ResourceLocation SHIP_LOCATION = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "textures/entity/ship.png");
    private final ShipModel model;
    public ShipRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ShipModel(context.bakeLayer(CVModelLayers.SHIP));
    }

    @Override
    public ResourceLocation getTextureLocation(ShipEntity entity) {
        return SHIP_LOCATION;
    }

    @Override
    public void render(ShipEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.5D, 0.0D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.model.renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity))), packedLight, OverlayTexture.NO_OVERLAY, -1);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
