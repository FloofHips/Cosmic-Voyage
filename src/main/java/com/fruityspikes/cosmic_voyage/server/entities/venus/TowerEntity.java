package com.fruityspikes.cosmic_voyage.server.entities.venus;

import com.fruityspikes.cosmic_voyage.server.util.legs.LegSolverQuadruped;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
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
        legs[1] = new Leg(this, new Vec3(-2, 0, 2), 1);
        legs[2] = new Leg(this, new Vec3(2, 0, -2), 2);

        legs[3] = new Leg(this, new Vec3(-2, 0, -2), 3);
        legs[0] = new Leg(this, new Vec3(2, 0, 2), 0);
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
        //setYBodyRot(0);
        setTargetLegPos();
        updateLegPos();
    }

    private void updateLegPos() {
        for (int i = 0; i < legs.length; i++) {
            Leg leg = legs[i];
            leg.tick();

            if (leg.moving) continue;

            boolean adj1Moving = legs[(i + 1) % 4].moving;
            boolean adj2Moving = legs[(i + 2) % 4].moving;

            if (adj1Moving || adj2Moving) continue;

            float threshold = this.isMoving() ? 1.0F : 0.1F;
            leg.tryMoveToNewTarget(threshold);
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

    public Vec3 getAverageLegPosition() {
        Vec3 momentum = this.getDeltaMovement();
        Vec3 balancePoint = Vec3.ZERO;

        // Weight front legs differently than rear legs
        float[] legWeights = {0.3f, 0.3f, 0.2f, 0.2f}; // Front-heavy

        for (int i = 0; i < 4; i++) {
            Vec3 legOffset = legs[i].getCurrentPos().subtract(this.position());
            balancePoint = balancePoint.add(legOffset.scale(legWeights[i]));
        }

        // Apply momentum influence
        return balancePoint.add(momentum.scale(0.5f));
    }

    public Vec3 getBodyPosition() {
        return getAverageLegPosition();
    }

    public float getBodyRoll() {
        double leftY = 0, rightY = 0;
        int leftCount = 0, rightCount = 0;

        for (Leg leg : legs) {
            if (leg != null && leg.getCurrentPos() != null) {
                double y = leg.getCurrentPos().y;
                if (leg.getIndex() % 2 == 0) {
                    leftY += y;
                    leftCount++;
                } else if (leg.getIndex() % 2 != 0) {
                    rightY += y;
                    rightCount++;
                }
            }
        }

        if (leftCount == 0 || rightCount == 0) return 0;

        double avgLeftY = leftY / leftCount;
        double avgRightY = rightY / rightCount;

        return (float) (avgLeftY - avgRightY);
    }

    public float getBodyPitch() {
        double frontY = 0, backY = 0;
        int frontCount = 0, backCount = 0;

        for (Leg leg : legs) {
            if (leg != null && leg.getCurrentPos() != null) {
                double y = leg.getCurrentPos().y;
                if (leg.getIndex() < 2) {
                    frontY += y;
                    frontCount++;
                } else if (leg.getIndex() > 1) {
                    backY += y;
                    backCount++;
                }
            }
        }

        if (frontCount == 0 || backCount == 0) return 0;

        double avgFrontY = frontY / frontCount;
        double avgBackY = backY / backCount;

        return (float) (avgBackY - avgFrontY);
    }

    public class Leg {
        Vec3 restingOffset;
        Vec3 currentPos;
        Vec3 startPos;
        Vec3 targetPos;
        TowerEntity entity;
        int moveDuration = 10;
        int moveTicks = 0;
        boolean moving = false;
        int index;

        public Leg(TowerEntity towerEntity, Vec3 restingOffset, int i) {
            this.entity = towerEntity;
            this.restingOffset = restingOffset;
            this.currentPos = updatePosition(); // Initial position
            this.targetPos = this.currentPos;
            this.startPos = this.currentPos;
            this.index = i;
        }

        public Vec3 updatePosition() {
            Vec3 rotatedOffset = restingOffset.yRot(-entity.yBodyRot * ((float)Math.PI / 180F));
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
            return Vec3.atBottomCenterOf(groundPos.above());
        }

        public void tick() {
            if (moving) {
                moveTicks++;
                if (moveTicks >= moveDuration) {
                    moving = false;
                    currentPos = targetPos;
                } else {
                    float progress = getProgress();
                    currentPos = startPos.lerp(targetPos, progress);
                }
            }
        }

        public void tryMoveToNewTarget(float threshold) {
            Vec3 newTarget = updatePosition();
            if (currentPos.distanceTo(newTarget) > threshold) {
                this.startPos = currentPos;
                this.targetPos = newTarget;
                this.moveTicks = 0;
                this.moving = true;
            }
        }

        public Vec3 getTargetPos() {
            return targetPos;
        }

        public Vec3 getCurrentPos() {
            return currentPos;
        }

        public float getProgress() {
            if (!moving || moveDuration <= 0) return 1.0f;
            return Mth.clamp((float) moveTicks / moveDuration, 0.0f, 1.0f);
        }

        public int getIndex() {
            return index;
        }

        public Vec3 getRestingOffset() {
            return restingOffset;
        }
    }
}