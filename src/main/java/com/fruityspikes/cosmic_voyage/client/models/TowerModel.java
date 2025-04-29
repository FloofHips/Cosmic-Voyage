package com.fruityspikes.cosmic_voyage.client.models;

import com.fruityspikes.cosmic_voyage.server.entities.venus.TowerEntity;
import com.fruityspikes.cosmic_voyage.server.util.legs.LegSolverQuadruped;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class TowerModel<T extends TowerEntity> extends EntityModel<T> {
    private final ModelPart body;
    private final ModelPart leftFrontLeg;
    private final ModelPart leftBackLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart rightBackLeg;

    public TowerModel(ModelPart root) {
        this.body = root.getChild("body");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.leftBackLeg = root.getChild("left_back_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.rightBackLeg = root.getChild("right_back_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(-92, -46).addBox(-24.0F, -60.0F, -24.0F, 48.0F, 64.0F, 48.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition left_front_leg = partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(-28, -14).addBox(0.0F, 0.0F, -16.0F, 16.0F, 32.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(24.0F, -8.0F, -24.0F));
        PartDefinition left_back_leg = partdefinition.addOrReplaceChild("left_back_leg", CubeListBuilder.create().texOffs(-28, -14).addBox(0.0F, 0.0F, 0.0F, 16.0F, 32.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(24.0F, -8.0F, 24.0F));
        PartDefinition right_front_leg = partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(-28, -14).addBox(-16.0F, 0.0F, -16.0F, 16.0F, 32.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(-24.0F, -8.0F, -24.0F));
        PartDefinition right_back_leg = partdefinition.addOrReplaceChild("right_back_leg", CubeListBuilder.create().texOffs(-28, -14).addBox(-16.0F, 0.0F, 0.0F, 16.0F, 32.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(-24.0F, -8.0F, 24.0F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void setupAnim(TowerEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        body.resetPose();
        leftFrontLeg.resetPose();
        rightFrontLeg.resetPose();
        leftBackLeg.resetPose();
        rightBackLeg.resetPose();

        float partialTick = ageInTicks - entity.tickCount;
        articulateLegs(entity.legSolver, partialTick);
    }

    private void articulateLegs(LegSolverQuadruped legs, float partialTick) {

        float heightBackLeft = legs.backLeft.getHeight(partialTick);
        float heightBackRight = legs.backRight.getHeight(partialTick);
        float heightFrontLeft = legs.frontLeft.getHeight(partialTick);
        float heightFrontRight = legs.frontRight.getHeight(partialTick);

        float max = Math.max(Math.max(heightBackLeft, heightBackRight), Math.max(heightFrontLeft, heightFrontRight)) * 0.8F;

        body.yRot += max * 16;

        rightFrontLeg.y += (heightFrontRight - max) * 16;
        leftFrontLeg.y += (heightFrontLeft - max) * 16;
        rightBackLeg.y += (heightBackRight - max) * 16;
        leftBackLeg.y += (heightBackLeft - max) * 16;
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