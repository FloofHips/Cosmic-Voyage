package com.fruityspikes.cosmic_voyage.client.renderers;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.client.client_registries.CVModelLayers;
import com.fruityspikes.cosmic_voyage.client.models.TowerModel;
import com.fruityspikes.cosmic_voyage.server.entities.venus.TowerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
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
        pEntityYaw = 1;

        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBufferSource, pPackedLight);

//        for (TowerEntity.Leg leg : pEntity.getLegs()) {
//            pPoseStack.pushPose();
//            Vec3 pos = leg.getWorldPos().subtract(pEntity.position());
//            pPoseStack.translate(pos.x, pos.y, pos.z);
//            LevelRenderer.renderLineBox(pPoseStack, pBufferSource.getBuffer(RenderType.lines()),
//                    new AABB(-0.1, -0.1, -0.1, 0.1, 0.1, 0.1), 1, 0, 0, 1);
//
//            pPoseStack.translate(-pos.x, -pos.y, -pos.z);
//            pos = leg.getTargetPos().subtract(pEntity.position());
//            pPoseStack.translate(pos.x, pos.y, pos.z);
//            LevelRenderer.renderLineBox(pPoseStack, pBufferSource.getBuffer(RenderType.lines()),
//                    new AABB(-0.1, -0.1, -0.1, 0.1, 0.1, 0.1), 0, 0, 1, 1);
//            pPoseStack.popPose();
//        }
    }

    @Override
    public ResourceLocation getTextureLocation(TowerEntity explorerEntity) {
        return TEXTURE;
    }
}
