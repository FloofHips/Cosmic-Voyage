package com.fruityspikes.cosmic_voyage.client.models;

import com.fruityspikes.cosmic_voyage.server.entities.venus.ExplorerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class ExplorerModel<T extends ExplorerEntity> extends EntityModel<T> {

    public final ModelPart body_top;
    public final ModelPart body;
    public final ModelPart rightArm;
    public final ModelPart leftArm;
    public final ModelPart rightLeg;
    public final ModelPart leftLeg;
    public ExplorerModel(ModelPart root) {
        this.body = root.getChild("body");
        this.body_top = this.body.getChild("body_top");
        this.rightArm = body_top.getChild("right_arm");
        this.leftArm = body_top.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(52, 30).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.0F, 14.0F, 0.0F));

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(52, 30).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 14.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 30).addBox(-7.0F, -2.0F, -6.0F, 14.0F, 4.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(0, 46).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition body_top = body.addOrReplaceChild("body_top", CubeListBuilder.create().texOffs(16, 56).addBox(-6.0F, -5.0F, -7.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-8.0F, -15.0F, -7.0F, 16.0F, 16.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

        PartDefinition left_arm = body_top.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(60, 0).mirror().addBox(-0.5F, -2.5F, -2.0F, 4.0F, 21.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(32, 46).mirror().addBox(-0.5F, -3.5F, -3.0F, 6.0F, 11.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(8.5F, -8.5F, 0.0F));

        PartDefinition right_arm = body_top.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(60, 0).addBox(-3.5F, -2.5F, -2.0F, 4.0F, 21.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(32, 46).addBox(-5.5F, -3.5F, -3.0F, 6.0F, 11.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.5F, -8.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if ((int)ageInTicks % 4 != 0) {
            return;
        }
        body.resetPose();
        body_top.resetPose();
        leftArm.resetPose();
        rightArm.resetPose();
        leftLeg.resetPose();
        rightLeg.resetPose();

        this.body_top.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.body_top.xRot = headPitch * ((float)Math.PI / 180F);

        this.body.yRot = Mth.sin(ageInTicks * 0.05F) * 0.1F;
        this.body.xRot = Mth.sin(ageInTicks * 0.1F) * 0.05F;

        float legAngle = limbSwing * 0.6662F;
        this.rightLeg.xRot = Mth.cos(legAngle + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(legAngle) * 1.4F * limbSwingAmount;

        float armAngle = limbSwing * 0.6662F;
        this.leftArm.xRot = Mth.cos(armAngle + (float)Math.PI) * 0.8F * limbSwingAmount;
        this.rightArm.xRot = Mth.cos(armAngle) * 0.8F * limbSwingAmount;

//        if (limbSwingAmount > 0.5f){
//            this.body_top.y = (ageInTicks * 0.1F) * 0.05F;
//            this.body_top.x = (ageInTicks * 0.13F) * 0.05F;
//        }

//        if (limbSwingAmount < 0.1f) {
//            this.body_top.yRot += quantizeAngle(Mth.sin(ageInTicks * 0.1F), 4) * 0.05F;
//            this.body_top.xRot += quantizeAngle(Mth.cos(ageInTicks * 0.13F), 4) * 0.05F;
//        }
    }

    private float quantizeAngle(float angle, int divisions) {
        float step = (float)Math.PI * 2 / divisions;
        return Mth.floor(angle / step) * step;
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, int pColor) {
        leftLeg.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor);
        rightLeg.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor);
        body.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor);
    }
}
