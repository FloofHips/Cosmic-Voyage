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
    private final ModelPart head;
    private final ModelPart leftFrontLeg;
    private final ModelPart leftBackLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart rightBackLeg;

    public TowerModel(ModelPart root) {
        this.center = root.getChild("center");
        this.body = center.getChild("body");
        this.head = body.getChild("head");
        this.leftFrontLeg = center.getChild("left_front_leg");
        this.leftBackLeg = center.getChild("left_back_leg");
        this.rightFrontLeg = center.getChild("right_front_leg");
        this.rightBackLeg = center.getChild("right_back_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition center = partdefinition.addOrReplaceChild("center", CubeListBuilder.create(), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition body = center.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 123).addBox(-24.0F, -60.0F, -24.0F, 48.0F, 64.0F, 48.0F, new CubeDeformation(0.0F))
                .texOffs(192, 123).addBox(-8.0F, 4.0F, -24.0F, 16.0F, 16.0F, 48.0F, new CubeDeformation(0.0F))
                .texOffs(192, 187).addBox(-24.0F, 4.0F, -8.0F, 48.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(40, 167).addBox(6.0F, -46.0F, -25.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_front_leg = center.addOrReplaceChild("left_front_leg", CubeListBuilder.create(), PartPose.offset(24.0F, 4.0F, -24.0F));

        PartDefinition cube_r1 = left_front_leg.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(320, 0).addBox(-15.0F, -32.0F, -1.0F, 16.0F, 32.0F, 16.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(15.0F, 32.0F, -1.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r2 = left_front_leg.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(320, 48).addBox(-7.0F, -8.0F, -1.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(7.0F, 24.0F, -1.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition left_back_leg = center.addOrReplaceChild("left_back_leg", CubeListBuilder.create(), PartPose.offset(24.0F, 4.0F, 24.0F));

        PartDefinition cube_r3 = left_back_leg.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(320, 48).addBox(-7.0F, -8.0F, -1.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(1.0F, 24.0F, 7.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition cube_r4 = left_back_leg.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(320, 0).addBox(-15.0F, -32.0F, -1.0F, 16.0F, 32.0F, 16.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(1.0F, 32.0F, 15.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition right_front_leg = center.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(320, 48).addBox(-8.0F, 16.0F, -8.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.1F))
                .texOffs(320, 0).addBox(-16.0F, 0.0F, -16.0F, 16.0F, 32.0F, 16.0F, new CubeDeformation(0.1F)), PartPose.offset(-24.0F, 4.0F, -24.0F));

        PartDefinition right_back_leg = center.addOrReplaceChild("right_back_leg", CubeListBuilder.create(), PartPose.offset(-24.0F, 4.0F, 24.0F));

        PartDefinition cube_r5 = right_back_leg.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(320, 0).addBox(-15.0F, -32.0F, -1.0F, 16.0F, 32.0F, 16.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-15.0F, 32.0F, 1.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition cube_r6 = right_back_leg.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(320, 48).addBox(-7.0F, -8.0F, -1.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-7.0F, 24.0F, 1.0F, 0.0F, 1.5708F, 0.0F));

        return LayerDefinition.create(meshdefinition, 512, 256);
    }
    @Override
    public void setupAnim(TowerEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
//        if ((int)ageInTicks % 10 != 0) {  // Using modulus on ageInTicks
//            return;
//        }

        if (entity.legs == null || entity.legs.length < 4) return;

        center.resetPose();
        body.resetPose();

        //body.y = (float) entity.getBodyPosition().subtract(entityPos).y;
        body.setPos((float) entity.getAverageLegPosition().x * 16, (float) entity.getAverageLegPosition().y - 16, (float) -entity.getAverageLegPosition().z * 16);
        //body.x = (float) entity.getAverageLegPosition().x;
        //body.z = (float) entity.getAverageLegPosition().z;

        body.xRot = entity.getBodyPitch() * 0.1f;
        body.zRot = -entity.getBodyRoll() * 0.1f;

        if (limbSwingAmount > 0.5f){
            this.body.y = Mth.sin(ageInTicks * 0.1F) * 0.005F;
            this.body.x = Mth.cos(ageInTicks * 0.13F) * 0.005F;
        }

        if (limbSwingAmount < 0.1f) {
            this.body.yRot += Mth.sin(ageInTicks * 0.1F) * 0.005F;
            this.body.xRot += Mth.cos(ageInTicks * 0.13F) * 0.005F;
        }

//        for (TowerEntity.Leg leg : entity.legs) {
//
//            if (leg == null) continue;
//            ModelPart legPart = getLegPart(leg.getIndex());
//            legPart.resetPose();
//            //legPart.setPos(0,0,0);
//
//            float arcHeight = 0.5F;
//            float arc = (float) Math.sin(leg.getProgress() * Math.PI) * arcHeight;
//
//            Vec3 renderPos = leg.getCurrentPos();
//
//            //legPart.x += (float) (leg.getRestingOffset().x / -4);
//            //legPart.z += (float) (leg.getRestingOffset().z / 4);
//
//            legPart.x += (float) (renderPos.x - entity.position().x) * 16;
//            legPart.y += (float) (- (renderPos.y - entity.position().y + 2) - arc) * 16;
//            legPart.z += (float) -(renderPos.z - entity.position().z) * 16;
//
//
//        }
    }

    public ModelPart getLegPart(int index) {
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
//        leftFrontLeg.render(poseStack, vertexConsumer, i, i1);
//        rightFrontLeg.render(poseStack, vertexConsumer, i, i1);
//        leftBackLeg.render(poseStack, vertexConsumer, i, i1);
//        rightBackLeg.render(poseStack, vertexConsumer, i, i1);
    }
}