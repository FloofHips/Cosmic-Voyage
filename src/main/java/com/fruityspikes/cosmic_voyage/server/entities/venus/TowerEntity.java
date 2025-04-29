package com.fruityspikes.cosmic_voyage.server.entities.venus;

import com.fruityspikes.cosmic_voyage.server.util.legs.LegSolverQuadruped;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TowerEntity extends PathfinderMob {

    public LegSolverQuadruped legSolver = new LegSolverQuadruped(-2F, 2F, 2F, 2F, 1);
    private Vec3 bodyOffset = Vec3.ZERO;
    private float bodyTilt;

    public TowerEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 35.0D);
    }

//    private void initLegs() {
//        legs[0] = new Leg(this, new Vec3(1.5, 0, 1.5), 0);  // Front left
//        legs[2] = new Leg(this, new Vec3(1.5, 0, -1.5), 1);  // Front right
//        legs[1] = new Leg(this, new Vec3(-1.5, 0, -1.5), 2); // Back right
//        legs[3] = new Leg(this, new Vec3(-1.5, 0, 1.5), 3);  // Back left
//    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        //this.setYBodyRot(0);
        this.legSolver.update(this, this.yBodyRot, this.getScale());
    }

    public Vec3 getBodyOffset() {
        return bodyOffset;
    }

    public float getBodyTilt() {
        return bodyTilt;
    }

    public boolean isOnGround() {
        return this.level().getBlockStates(this.getBoundingBox().move(0, -0.05f, 0))
                .anyMatch(state -> !state.isAir() && state.blocksMotion());
    }
}