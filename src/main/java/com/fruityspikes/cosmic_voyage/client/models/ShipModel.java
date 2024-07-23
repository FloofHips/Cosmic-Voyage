package com.fruityspikes.cosmic_voyage.client.models;

import com.fruityspikes.cosmic_voyage.server.entities.ShipEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class ShipModel<T extends ShipEntity> extends EntityModel<T> {
    private final ModelPart bb_main;

    public ShipModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(110, 175).addBox(-6.0F, -16.0F, -35.0F, 12.0F, 16.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(111, 212).mirror().addBox(-6.0F, -7.0F, -30.0F, 12.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 91).addBox(-24.0F, -41.0F, -24.0F, 48.0F, 36.0F, 48.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-24.0F, -96.0F, -24.0F, 48.0F, 43.0F, 48.0F, new CubeDeformation(0.0F))
                .texOffs(144, 0).addBox(-13.0F, -53.0F, -13.0F, 26.0F, 12.0F, 26.0F, new CubeDeformation(0.0F))
                .texOffs(0, 175).addBox(-2.0F, -41.0F, -35.0F, 4.0F, 25.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(144, 91).addBox(-2.0F, -50.0F, -29.0F, 4.0F, 9.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, 27).addBox(-5.0F, -99.0F, -5.0F, 10.0F, 3.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(30, 91).addBox(-1.0F, -128.0F, -1.0F, 2.0F, 29.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(184, 91).mirror().addBox(-2.0F, -50.0F, 13.0F, 4.0F, 9.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 91).addBox(-2.0F, -41.0F, 24.0F, 4.0F, 25.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(156, 175).addBox(-6.0F, -16.0F, 25.0F, 12.0F, 16.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(111, 202).addBox(-6.0F, -7.0F, 25.0F, 12.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(144, 116).mirror().addBox(-29.0F, -50.0F, -2.0F, 16.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(144, 116).addBox(13.0F, -50.0F, -2.0F, 16.0F, 9.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(34, 175).mirror().addBox(-35.0F, -41.0F, -2.0F, 11.0F, 25.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(34, 175).addBox(24.0F, -41.0F, -2.0F, 11.0F, 25.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(64, 175).mirror().addBox(-35.0F, -16.0F, -6.0F, 11.0F, 16.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(64, 175).addBox(24.0F, -16.0F, -6.0F, 11.0F, 16.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(0, 212).addBox(24.0F, -7.0F, -6.0F, 6.0F, 4.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(0, 212).mirror().addBox(-30.0F, -7.0F, -6.0F, 6.0F, 4.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }
    @Override
    public void setupAnim(T t, float v, float v1, float v2, float v3, float v4) {
        this.bb_main.yRot = v+1;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, int i2) {
        bb_main.render(poseStack, vertexConsumer, i, i1, i2);
    }
}
