package com.fruityspikes.cosmic_voyage.client.renderers;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.client.client_registries.CVModelLayers;
import com.fruityspikes.cosmic_voyage.client.models.TowerModel;
import com.fruityspikes.cosmic_voyage.server.entities.venus.TowerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TowerRenderer extends MobRenderer<TowerEntity, TowerModel<TowerEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "textures/entity/tower_automata.png");
    public TowerRenderer(EntityRendererProvider.Context context) {
        super(context, new TowerModel(context.bakeLayer(CVModelLayers.TOWER)), 3F);
    }

    @Override
    public void render(TowerEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight) {

        //pPoseStack.mulPose(Axis.YP.rotationDegrees(pEntity.yBodyRot));

        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBufferSource, pPackedLight);

        Vec3 entityPos = pEntity.position();

        for (TowerEntity.Leg leg : pEntity.legs) {
            if (leg == null) continue;

            if (leg.getTargetPos() != null) {
                renderLegBox(
                        pPoseStack, pBufferSource,
                        leg.getTargetPos(), entityPos,
                        1.0f, 0.0f, 0.0f, 1.0f
                );
            }

            if (leg.getCurrentPos() != null) {
                renderLegBox(
                        pPoseStack, pBufferSource,
                        leg.getCurrentPos(), entityPos,
                        0.0f, 0.0f, 1.0f, 1.0f
                );
            }
        }
    }

    private void renderLegBox(PoseStack poseStack, MultiBufferSource buffer,
                              Vec3 worldPos, Vec3 entityPos,
                              float r, float g, float b, float a) {
        poseStack.pushPose();
        poseStack.translate(
                worldPos.x - entityPos.x,
                worldPos.y - entityPos.y,
                worldPos.z - entityPos.z
        );

        LevelRenderer.renderLineBox(
                poseStack,
                buffer.getBuffer(RenderType.lines()),
                new AABB(-0.1, -0.1, -0.1, 0.1, 0.1, 0.1),
                r, g, b, a
        );

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(TowerEntity explorerEntity) {
        return TEXTURE;
    }
}
