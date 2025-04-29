package com.fruityspikes.cosmic_voyage.client.models;

import com.fruityspikes.cosmic_voyage.server.entities.venus.TowerEntity;
import com.fruityspikes.cosmic_voyage.server.util.legs.LegSolverQuadruped;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class TowerModel<T extends TowerEntity> extends EntityModel<T> {
    private final ModelPart center;
    private final ModelPart body;
    private final ModelPart leftFrontLeg;
    private final ModelPart leftBackLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart rightBackLeg;

    public TowerModel(ModelPart root) {
        this.center = root.getChild("center");
        this.body = center.getChild("body");
        this.leftFrontLeg = center.getChild("left_front_leg");
        this.leftBackLeg = center.getChild("left_back_leg");
        this.rightFrontLeg = center.getChild("right_front_leg");
        this.rightBackLeg = center.getChild("right_back_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition center = partdefinition.addOrReplaceChild("center", CubeListBuilder.create(), PartPose.offset(0.0F, -12.0F, 0.0F));
        PartDefinition body = center.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-24.0F, -60.0F, -24.0F, 48.0F, 64.0F, 48.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_front_leg = center.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 112).addBox(0.0F, 0.0F, -16.0F, 16.0F, 32.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(24.0F, 4.0F, -24.0F));
        PartDefinition left_back_leg = center.addOrReplaceChild("left_back_leg", CubeListBuilder.create().texOffs(0, 160).addBox(0.0F, 0.0F, 0.0F, 16.0F, 32.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(24.0F, 4.0F, 24.0F));
        PartDefinition right_front_leg = center.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(64, 112).addBox(-16.0F, 0.0F, -16.0F, 16.0F, 32.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(-24.0F, 4.0F, -24.0F));
        PartDefinition right_back_leg = center.addOrReplaceChild("right_back_leg", CubeListBuilder.create().texOffs(128, 112).addBox(-16.0F, 0.0F, 0.0F, 16.0F, 32.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(-24.0F, 4.0F, 24.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }
    @Override
    public void setupAnim(TowerEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        if ((int)ageInTicks % 2 != 0) {  // Using modulus on ageInTicks
            return;
        }

        if (entity.legs == null || entity.legs.length < 4) return;

        center.resetPose();
        body.resetPose();
//        leftFrontLeg.resetPose();
//        rightFrontLeg.resetPose();
//        leftBackLeg.resetPose();
//        rightBackLeg.resetPose();

        Vec3 entityPos = entity.position();

        //body.y = (float) entity.getBodyPosition().subtract(entityPos).y;
        body.x = (float) entity.getBodyPosition().subtract(entityPos).x;
        body.z = (float) entity.getBodyPosition().subtract(entityPos).z;

        //body.xRot = entity.getBodyPitch() * 0.1f;
        //body.zRot = -entity.getBodyRoll() * 0.1f;

        if (limbSwingAmount > 0.5f){
            this.body.y = Mth.sin(ageInTicks * 0.1F) * 0.005F;
            this.body.x = Mth.cos(ageInTicks * 0.13F) * 0.005F;
        }

        if (limbSwingAmount < 0.1f) {
            this.body.yRot += Mth.sin(ageInTicks * 0.1F) * 0.005F;
            this.body.xRot += Mth.cos(ageInTicks * 0.13F) * 0.005F;
        }

        applyLegOffset(entity.legs[0], leftFrontLeg, entityPos);
        applyLegOffset(entity.legs[1], rightFrontLeg, entityPos);
        applyLegOffset(entity.legs[2], leftBackLeg, entityPos);
        applyLegOffset(entity.legs[3], rightBackLeg, entityPos);
    }

    private void applyLegOffset(TowerEntity.Leg leg, ModelPart modelPart, Vec3 entityPos) {
        if (leg.getCurrentPos() == null) return;

        float progress = leg.getProgress();
        Vec3 currentOffset = leg.getCurrentPos().subtract(entityPos);
       // Vec3 restingOffset = leg.getRestingOffset().scale(10); // keep this if 10 is your layout scale

        // Compute stepping arc
        float arcHeight = 10.0F;
        float arc = (float) Math.sin(progress * Math.PI) * arcHeight;

        // Combine resting position and movement offset
//        modelPart.x = (float) (restingOffset.x + currentOffset.x);
//        modelPart.y = -8.0F - (float) (restingOffset.y + currentOffset.y) * 16 - arc;
//        modelPart.z = (float) -(restingOffset.z + currentOffset.z);

        modelPart.x = (float) (currentOffset.x);
        modelPart.y = -8.0F - (float) (currentOffset.y) * 16 - arc;
        modelPart.z = (float) -(currentOffset.z);

    }

    public void setupAnimOld(TowerEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {

        body.resetPose();
        leftFrontLeg.resetPose();
        rightFrontLeg.resetPose();
        leftBackLeg.resetPose();
        rightBackLeg.resetPose();

        for (int i = 0; i < 4; i++) {
            TowerEntity.Leg leg = entity.legs[i];

            if (leg == null || leg.getTargetPos() == null || leg.getCurrentPos() == null) continue;

            Vec3 current = leg.getCurrentPos();
            Vec3 target = leg.getTargetPos();
            float t = leg.getProgress(); // should be 0.0 to 1.0

            // Linear interpolation
            double x = Mth.lerp(t, current.x, target.x);
            double z = Mth.lerp(t, current.z, target.z);

            // Arc in Y
            double yLinear = Mth.lerp(t, current.y, target.y);
            double arc = 4 * t * (1 - t); // 0 at t=0 and t=1, peak at t=0.5
            double arcHeight = 1.0; // adjust this for jump height
            double y = yLinear + arc * arcHeight;

            // Apply to leg model part
            switch (i) {
                case 0 -> leftFrontLeg.setPos((float)x, (float)y, (float)z);
                case 1 -> rightFrontLeg.setPos((float)x, (float)y, (float)z);
                case 2 -> leftBackLeg.setPos((float)x, (float)y, (float)z);
                case 3 -> rightBackLeg.setPos((float)x, (float)y, (float)z);
            }
        }

    }
    private ModelPart getLegPart(int index) {
        return switch (index) {
            case 0 -> leftFrontLeg;
            case 1 -> rightFrontLeg;
            case 2 -> leftBackLeg;
            case 3 -> rightBackLeg;
            default -> null;
        };
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, int i2) {
        body.render(poseStack, vertexConsumer, i, i1);
        leftFrontLeg.render(poseStack, vertexConsumer, i, i1);
        rightFrontLeg.render(poseStack, vertexConsumer, i, i1);
        leftBackLeg.render(poseStack, vertexConsumer, i, i1);
        rightBackLeg.render(poseStack, vertexConsumer, i, i1);
    }
}