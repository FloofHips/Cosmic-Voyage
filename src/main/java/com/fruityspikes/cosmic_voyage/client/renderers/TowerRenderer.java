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
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
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

        for (TowerEntity.Leg leg : pEntity.legs) {
            if (leg == null) continue;

            if (leg.getTargetPos() != null) {
                renderLegBox(
                        pPoseStack, pBufferSource,
                        leg.getTargetPos(), entityPos,
                        1.0f, 0.0f, 0.0f, 1.0f, leg.getIndex()
                );
            }

            if (leg.getCurrentPos() != null) {
                renderLegBox(
                        pPoseStack, pBufferSource,
                        leg.getCurrentPos(), entityPos,
                        0.0f, 0.0f, 1.0f, 1.0f, leg.getIndex()
                );
            }
        }
    }

    private void renderLegBox(PoseStack poseStack, MultiBufferSource buffer,
                              Vec3 worldPos, Vec3 entityPos,
                              float r, float g, float b, float a,
                              int index) {
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

        // Draw label text
        Font font = Minecraft.getInstance().font;
        String text = "#" + index + " " + getLabel(index);

        //poseStack.pushPose();
        poseStack.translate(0, 0.3, 0); // raise label above the leg
        //poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation().invert());
        poseStack.scale(-0.025f, -0.025f, 0.025f);

        float xOffset = -font.width(text) / 2f;
        font.drawInBatch(text, xOffset, 0, 0xFFFFFF, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, 255);

        //poseStack.popPose();
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
