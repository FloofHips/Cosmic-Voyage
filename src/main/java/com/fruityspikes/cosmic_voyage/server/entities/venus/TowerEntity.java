package com.fruityspikes.cosmic_voyage.server.entities.venus;

import com.fruityspikes.cosmic_voyage.server.util.legs.LegSolverQuadruped;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class TowerEntity extends PathfinderMob {

    //public LegSolverQuadruped legSolver = new LegSolverQuadruped(-2F, 2F, 2F, 2F, 1);
    private Vec3 bodyOffset = Vec3.ZERO;
    private float bodyTilt;

    public Leg[] legs = new Leg[4];
    public TowerEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        initLegs();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 35.0D);
    }

    private void initLegs() {
        legs[0] = new Leg(this, new Vec3(-1.5, 0, 1.5), 0);  // Front left
        legs[1] = new Leg(this, new Vec3(1.5, 0, 1.5), 1);  // Front right

        legs[2] = new Leg(this, new Vec3(1.5, 0, -1.5), 2); // Back right
        legs[3] = new Leg(this, new Vec3(-1.5, 0, -1.5), 3);  // Back left
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult interactAt(Player pPlayer, Vec3 pVec, InteractionHand pHand) {
        setTargetLegPos();
        return super.interactAt(pPlayer, pVec, pHand);
    }

    @Override
    public void tick() {
        super.tick();
        setYBodyRot(0);
        setTargetLegPos();
        updateLegPos();
    }

    private void updateLegPos() {
        float movementThreshold = isMoving() ? 2.0f : 0.2f;

        for (Leg leg : legs) {
            if (leg.currentPos == null) {
                leg.currentPos = leg.updatePosition();
            }

            double distance = leg.getTargetPos().distanceTo(leg.currentPos);

            if (distance > movementThreshold) {
                leg.currentPos = leg.updatePosition();
            }

            if (!isMoving() && distance > 0.05) {
                Vec3 direction = leg.currentPos.subtract(leg.targetPos).normalize();
                leg.targetPos = leg.targetPos.add(direction.scale(0.05 * distance));
            }
        }
    }

    @Override
    public void setYBodyRot(float pOffset) {
        super.setYBodyRot(0);
    }

    private boolean isMoving() {
        double d0 = this.getX() - this.xo;
        double d1 = this.getZ() - this.zo;

        double r0 = this.yBodyRot - this.yBodyRotO;
        return d0 * d0 + d1 * d1 > 2.500000277905201E-7 || r0 != 0;
    }

    public void setTargetLegPos(){
        for (Leg leg : legs) {
            leg.targetPos = leg.updatePosition();
        }
    }
    public void setCurrentLegPos(){
        for (Leg leg : legs) {
            leg.currentPos = leg.updatePosition();
        }
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

    public class Leg {
        Vec3 restingOffset;
        Vec3 currentPos;
        Vec3 targetPos;
        TowerEntity entity;
        public Leg(TowerEntity towerEntity, Vec3 restingOffset, int i) {
            this.entity = towerEntity;
            this.restingOffset = restingOffset;
        }

        public Vec3 updatePosition() {
            Vec3 rotatedOffset = restingOffset.yRot(-entity.getYRot() * ((float)Math.PI / 180F));
            Vec3 mobCenter = entity.position();
            Vec3 startPos = mobCenter
                    .add(rotatedOffset)
                    .add(0, 2.0, 0);
            BlockHitResult hit = entity.level().clip(new ClipContext(
                    startPos,
                    startPos.subtract(0, 6.0, 0),
                    ClipContext.Block.OUTLINE,
                    ClipContext.Fluid.NONE,
                    entity
            ));

            if (hit.getType() == HitResult.Type.BLOCK) {
                return hit.getLocation();
            }

            Vec3 pos = mobCenter.add(rotatedOffset);
            BlockPos groundPos = BlockPos.containing(pos).below();
            //if (entity.level().getBlockState(groundPos).isSolid()) {
            return Vec3.atBottomCenterOf(groundPos.above());
            //}
        }

        public Vec3 getTargetPos() {
            return targetPos;
        }

        public Vec3 getCurrentPos() {
            return currentPos;
        }

        public float getProgress() {
        }
    }
}