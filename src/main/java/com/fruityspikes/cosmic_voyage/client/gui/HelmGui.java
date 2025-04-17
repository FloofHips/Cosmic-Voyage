package com.fruityspikes.cosmic_voyage.client.gui;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.server.menus.HelmMenu;
import com.fruityspikes.cosmic_voyage.server.ships.Ship;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

import java.text.DecimalFormat;
import java.util.Iterator;

@OnlyIn(Dist.CLIENT)
public class HelmGui extends AbstractContainerScreen<HelmMenu> {
    ResourceLocation HELM_SHELL = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "textures/gui/helm/helm_shell.png");
    //public int leftPos;
    //public int topPos;
    public int imageWidth;
    public int imageHeight;
    protected float velocity = 0;
    protected float rotation = 0;
    ThrottleSlider slider;
    BallMouse ball;

    public HelmGui(HelmMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    public void init() {
        imageWidth = 464;
        imageHeight = 256;
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        slider = (new ThrottleSlider(leftPos + 327, topPos + 226, 34, 18, Component.empty(), velocity));
        ball = (new BallMouse(leftPos + 360, topPos + 151, 96, 96, Component.empty(), rotation));
        addRenderableWidget(slider);
        addRenderableWidget(ball);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        this.minecraft.gameRenderer.processBlurEffect(i1);
        this.minecraft.getMainRenderTarget().bindWrite(false);
        //Orange Ball
        guiGraphics.blit(HELM_SHELL, this.leftPos + 360, this.topPos + 151, 16*6, 256, 96, 96, 512, 512);
        //Cover
        guiGraphics.blit(HELM_SHELL, this.leftPos, this.topPos, 0, 0, imageWidth, imageHeight, 512, 512);

        guiGraphics.pose().pushPose();
        {
            int leverX = this.leftPos + 341;
            int leverY = this.topPos + 200;
            float perspectiveFactor = (velocity - 50) / 50f;
            guiGraphics.pose().translate(leverX, leverY , 0);

            float scaleY = 0.8f + Math.abs(perspectiveFactor);

            if (perspectiveFactor > 0.1) {
                guiGraphics.pose().scale(1f, scaleY, 1f);
                guiGraphics.blit(HELM_SHELL,
                        0, -15,
                        16*15+6, 256,
                        6, 15,
                        512, 512);
            } else if(perspectiveFactor < -0.2) {
                guiGraphics.pose().scale(1f, scaleY, 1f);
                guiGraphics.blit(HELM_SHELL,
                        0, 0,
                        16*15, 256,
                        6, 15,
                        512, 512);
            }
        }
        guiGraphics.pose().popPose();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    public void containerTick() {
        super.containerTick();
        this.helmTick();
    }

    public void helmTick() {
        slider.tick();
        ball.tick();
        setRotation(ball.getCurrentRotation());
        setVelocity(slider.getCurrentVelocity());
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == GLFW.GLFW_KEY_A || pKeyCode == GLFW.GLFW_KEY_LEFT) {
            ball.leftHeld = true;
            return true;
        }
        if (pKeyCode == GLFW.GLFW_KEY_D || pKeyCode == GLFW.GLFW_KEY_RIGHT) {
            ball.rightHeld = true;
            return true;
        }
        if (pKeyCode == GLFW.GLFW_KEY_W || pKeyCode == GLFW.GLFW_KEY_UP) {
            slider.forwardHeld = true;
            return true;
        }
        if (pKeyCode == GLFW.GLFW_KEY_S || pKeyCode == GLFW.GLFW_KEY_DOWN) {
            slider.backwardHeld = true;
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == GLFW.GLFW_KEY_A || pKeyCode == GLFW.GLFW_KEY_LEFT) {
            ball.leftHeld = false;
            return true;
        }
        if (pKeyCode == GLFW.GLFW_KEY_D || pKeyCode == GLFW.GLFW_KEY_RIGHT) {
            ball.rightHeld = false;
            return true;
        }
        if (pKeyCode == GLFW.GLFW_KEY_W || pKeyCode == GLFW.GLFW_KEY_UP) {
            slider.forwardHeld = false;
            return true;
        }
        if (pKeyCode == GLFW.GLFW_KEY_S || pKeyCode == GLFW.GLFW_KEY_DOWN) {
            slider.backwardHeld = false;
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    //    public void renderShipInfo(GuiGraphics pGuiGraphics) {
//        int textX = leftPos + 355;
//        int textY = topPos + 34;
//        int textSpacing = 24;
//
//        String X = formatString(shipx);
//        String Y = formatString(shipy);
//        String Z = formatString(shipz);
//
//        //for (int i = 0; i < 3; i++) {
//        pGuiGraphics.drawString(this.font, X, textX, textY + (2 * textSpacing) + 4, 0xe6dc36, false);
//        pGuiGraphics.drawString(this.font, Y, textX, textY + (3 * textSpacing) + 4, 0xe6dc36, false);
//        pGuiGraphics.drawString(this.font, Z, textX, textY + (4 * textSpacing) + 4, 0xe6dc36, false);
//        //}
//    }

    public String formatString(float num) {
        num = Math.max(0, Math.min(100, num));

        DecimalFormat df = new DecimalFormat("000.0 %");
        return df.format(num);
    }

    public boolean isPauseScreen() {
        return false;
    }

    class BallMouse extends AbstractWidget {
        private static final float ROTATION_SPEED = 1f;
        private static final float FRICTION = 0.5f;
        private static final int SOUND_INTERVAL = 10;
        public boolean leftHeld = false;
        private float rotationMomentum = 0;
        public boolean rightHeld = false;
        private float currentRotation = 0;
        private final int centerX;
        private final int centerY;
        private final int radius;
        private int soundCooldown = 0;

        public BallMouse(int pX, int pY, int pWidth, int pHeight, Component pMessage, float rotation) {
            super(pX, pY, pWidth, pHeight, pMessage);
            this.radius = pWidth / 2;
            this.centerX = pX + radius;
            this.centerY = pY + radius;
            this.currentRotation = rotation;
        }

        public float getCurrentRotation() {
            return currentRotation;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(centerX, centerY, 0);
            guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(currentRotation));
            guiGraphics.blit(HELM_SHELL,
                    -radius, -radius,
                    0, 256,
                    96, 96,
                    512, 512);
            guiGraphics.pose().popPose();
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }

        public void tick() {
            float prevMomentum = rotationMomentum;
            if (leftHeld) {
                rotationMomentum = Math.clamp(rotationMomentum - ROTATION_SPEED, -25, 25);
            }
            if (rightHeld) {
                rotationMomentum = Math.clamp(rotationMomentum + ROTATION_SPEED, -25, 25);
            }

            if ((leftHeld || rightHeld) && (prevMomentum == 0 || Math.signum(prevMomentum) != Math.signum(rotationMomentum))) {
                playTickSound();
                soundCooldown = 0;
            }

            if (!leftHeld && !rightHeld) {
                rotationMomentum *= FRICTION;
                if (Math.abs(rotationMomentum) < 0.1f) rotationMomentum = 0;
            }

            currentRotation += rotationMomentum;
            currentRotation %= 360;

            if (rotationMomentum != 0) {
                if (soundCooldown <= 0) {
                    playTickSound();
                    soundCooldown = (int) (SOUND_INTERVAL + rotationMomentum);
                } else {
                    soundCooldown--;
                }
            }
        }

        private void playTickSound() {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.playSound(
                        SoundEvents.TRIPWIRE_ATTACH,
                        0.5f, // Volume
                        0.8f + 0.4f * Minecraft.getInstance().player.getRandom().nextFloat()
                );
            }
        }
    }
    class ThrottleSlider extends AbstractWidget {
        private static final float ACCELERATION = 0.75f;
        private static final float DECELERATION = 0.25f;
        private static final float FRICTION = 0.5f;
        private static final float MAX_VELOCITY = 100f;
        private float currentVelocity = 0;
        public boolean forwardHeld = false;
        public boolean backwardHeld = false;
        private float velocityMomentum = 0;


        public ThrottleSlider(int pX, int pY, int pWidth, int pHeight, Component pMessage, float velocity) {
            super(pX, pY, pWidth, pHeight, pMessage);
            this.currentVelocity = velocity;
        }
        public float getCurrentVelocity() {
            return currentVelocity;
        }

        public void tick() {
            if(currentVelocity>=100)
                currentVelocity = 100;
            if(currentVelocity<=0)
                currentVelocity = 0;

            if (backwardHeld) velocityMomentum -=DECELERATION;

            if (forwardHeld) velocityMomentum += ACCELERATION;


            if(currentVelocity!=0 && currentVelocity % 10 == 0)
                playDownSound(Minecraft.getInstance().getSoundManager());

            if (!backwardHeld && !forwardHeld) {
                velocityMomentum *= FRICTION;
                if (Math.abs(velocityMomentum) < 0.1f) velocityMomentum = 0;
            }

            currentVelocity = Math.clamp(currentVelocity + velocityMomentum, -15, 115);

            currentVelocity -= DECELERATION;
        }

        @Override
        protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();

            int handleY = topPos+16 + (int)((1 - (currentVelocity/MAX_VELOCITY)) * (topPos+15*4 - (topPos)));

            gui.blit(HELM_SHELL,
                    this.getX(),
                    handleY+16*9,
                    192,
                    imageHeight + (isHovered ? 18 : 0),
                    34, 18,
                    512, 512);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narration) {
            this.defaultButtonNarrationText(narration);
        }
    }
}