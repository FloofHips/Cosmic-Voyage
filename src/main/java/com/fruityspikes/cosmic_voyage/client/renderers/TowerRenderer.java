package com.fruityspikes.cosmic_voyage.client.renderers;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.client.client_registries.CVModelLayers;
import com.fruityspikes.cosmic_voyage.client.models.TowerModel;
import com.fruityspikes.cosmic_voyage.server.entities.venus.TowerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Quaternionf;

public class TowerRenderer extends MobRenderer<TowerEntity, TowerModel<TowerEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "textures/entity/tower_automata.png");
    public TowerRenderer(EntityRendererProvider.Context context) {
        super(context, new TowerModel(context.bakeLayer(CVModelLayers.TOWER)), 3F);
    }

    @Override
    public void render(TowerEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBufferSource, pPackedLight);

        Vec3 entityPos = pEntity.position();
        float bodyRot = pEntity.yBodyRot;
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        
        for (TowerEntity.Leg leg : pEntity.legs) {
            if (leg == null) continue;

            renderLegModel(pPoseStack, pBufferSource, pPackedLight, entityPos, leg, pPartialTick, bodyRot);
            if(entityRenderDispatcher.shouldRenderHitBoxes())
                renderDebugMarkers(pPoseStack, pBufferSource, entityPos, leg);
        }
    }

    private void renderDebugMarkers(PoseStack pPoseStack, MultiBufferSource pBufferSource, Vec3 cameraPos, TowerEntity.Leg leg) {
        renderDebugBox(pPoseStack, pBufferSource, leg.getTargetPos(), cameraPos,
                1.0f, 0.0f, 0.0f, 1.0f);

        renderDebugBox(pPoseStack, pBufferSource, leg.getCurrentPos(), cameraPos,
                0.0f, 0.0f, 1.0f, 1.0f);
    }

    private void renderLegModel(PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, Vec3 entityPos, TowerEntity.Leg leg, float pPartialTick, float bodyRot) {

        if(pPartialTick%1000==0)
            return;

        pPoseStack.pushPose();
        ModelPart legPart = getModel().getLegPart(leg.getIndex());
        legPart.resetPose();
        legPart.setPos(0,0,0);

        float arcHeight = 0.5F;
        float arc = (float) Math.sin(leg.getProgress() * Math.PI) * arcHeight;

        Vec3 renderPos = leg.getCurrentPos();

        pPoseStack.mulPose(Axis.XP.rotationDegrees(180));
        pPoseStack.translate(leg.getRestingOffset().x / -4, 0, leg.getRestingOffset().z / 4);
        pPoseStack.translate(renderPos.x - entityPos.x, - (renderPos.y - entityPos.y + 2) - arc, -(renderPos.z - entityPos.z));

        double angle = leg.getCurrentPos().distanceTo(leg.getTargetPos());

        pPoseStack.mulPose(Axis.YP.rotationDegrees((float) (angle * 50 + bodyRot)));

        legPart.render(pPoseStack, pBufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),
                pPackedLight, OverlayTexture.NO_OVERLAY);

        pPoseStack.popPose();
    }
    private void renderDebugBox(PoseStack poseStack, MultiBufferSource buffer,
                              Vec3 worldPos, Vec3 entityPos,
                              float r, float g, float b, float a) {
        poseStack.pushPose();
        poseStack.translate(
                worldPos.x - entityPos.x,
                worldPos.y - entityPos.y,
                worldPos.z - entityPos.z
        );

        // Draw the box
        LevelRenderer.renderLineBox(
                poseStack,
                buffer.getBuffer(RenderType.lines()),
                new AABB(-0.1, -0.1, -0.1, 0.1, 0.1, 0.1),
                r, g, b, a
        );
        poseStack.popPose();
    }
    public String getLabel(int index){
        return switch (index) {
            case 0 -> "Front Left";
            case 1 -> "Front Right";
            case 2 -> "Back Left";
            case 3 -> "Back Right";
            default -> "Unknown";
        };
    }
    @Override
    public ResourceLocation getTextureLocation(TowerEntity explorerEntity) {
        return TEXTURE;
    }
}
