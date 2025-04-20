package com.fruityspikes.cosmic_voyage.client.gui;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.server.data.CelestialObject;
import com.fruityspikes.cosmic_voyage.server.menus.HelmMenu;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class HelmGui extends AbstractContainerScreen<HelmMenu> {
    ResourceLocation HELM_SHELL = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "textures/gui/helm/helm_shell.png");
    ResourceLocation SCREEN = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "textures/gui/helm/helm_screen.png");
    ResourceLocation BALL_OVERLAY = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "textures/gui/helm/helm_ball_overlay.png");
    //public int leftPos;
    //public int topPos;
    public int imageWidth;
    public int imageHeight;
    protected float velocity;
    protected float rotation;
    protected int posX;
    protected int posY;
    ThrottleSlider slider;
    BallMouse ball;
    public boolean hasPower = true;
    public int shipTick = 0;
    private Map<CelestialObject, Vec2> currentCelestialPositions;

    public HelmGui(HelmMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    public void init() {
        this.velocity = menu.getVelocity();
        this.rotation = menu.getRotation();
        this.posX = menu.getPosX();
        this.posY = menu.getPosY();
        //this.currentCelestialPositions = menu.getCurrentCelestialPositions();
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
        //background
        guiGraphics.blit(HELM_SHELL, this.leftPos + 360, this.topPos + 151, 96, 256, 96, 96, 512, 512);
        //overlay
        guiGraphics.blit(BALL_OVERLAY, this.leftPos + 360, this.topPos + 151, (int) (-(rotation / 360f) * 160), 0, 96, 96, 160, 96);
        //shading
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        guiGraphics.blit(HELM_SHELL, this.leftPos + 360, this.topPos + 151, 0, 256+96, 96, 96, 512, 512);
        //Screen
        renderScreen(guiGraphics);
        //Cover
        guiGraphics.blit(HELM_SHELL, this.leftPos, this.topPos, 0, 0, imageWidth, imageHeight, 512, 512);

        //renderStarSystem(guiGraphics);
        guiGraphics.pose().pushPose();
        {
            int leverX = this.leftPos + 341;
            int leverY = this.topPos + 200;
            float perspectiveFactor = (velocity - 50) / 50f;
            guiGraphics.pose().translate(leverX, leverY , 0);

            float scaleY = 0.8f + Math.abs(perspectiveFactor);

            if (perspectiveFactor > 0.1) {
                guiGraphics.pose().scale(1f, scaleY, 1f);
                guiGraphics.blit(HELM_SHELL,0, -15,16*15+6, 256,6, 15,512, 512);
            } else if(perspectiveFactor < -0.2) {
                guiGraphics.pose().scale(1f, scaleY, 1f);
                guiGraphics.blit(HELM_SHELL,0, 0,16*15, 256,6, 15,512, 512);
            }
        }
        guiGraphics.pose().popPose();
        int posX = menu.getPosX();
        int posY = menu.getPosY();

        guiGraphics.drawString(
                font,
                String.format("Position: %d, %d", posX, posY),
                leftPos + 20, topPos + 20,
                0xFFFFFF,
                false
        );

        guiGraphics.drawString(
                font,
                String.format("Rotation: %d°", menu.getRotation()),
                leftPos + 20, topPos + 40,
                0xFFFFFF,
                false
        );

        guiGraphics.drawString(
                font,
                String.format("Velocity: %d%%", menu.getVelocity()),
                leftPos + 20, topPos + 60,
                0xFFFFFF,
                false
        );
    }
    public void renderScreen(GuiGraphics guiGraphics) {
        guiGraphics.enableScissor(this.leftPos + 19,this.topPos + 23,this.leftPos + 19 + 298,this.topPos + 23 + 210);

        if(hasPower){
            RenderSystem.enableBlend();
            guiGraphics.fill(this.leftPos + 19,this.topPos + 23,this.leftPos + 19 + 298,this.topPos + 23 + 210, -2779381);
            guiGraphics.blit(SCREEN, this.leftPos + 19, this.topPos + 23, 0, 210, 298, 210, 1024, 512);
            RenderSystem.disableBlend();
            renderStarSystem(guiGraphics);
            renderInvertedQuad(guiGraphics, SCREEN, this.leftPos + 19, this.topPos + 23, 298, 0, 298, 210, 1024, 512, -2779381);
            renderMultiplicativeQuad(guiGraphics, SCREEN, this.leftPos + 19, this.topPos + 23, 298, 0, 298, 210, 1024, 512, -2779381);
        }

        float alpha = velocity / 100f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        guiGraphics.setColor(1f, 1f, 1f, 0);
        guiGraphics.blit(SCREEN, this.leftPos + 19, this.topPos + 23, 0, 0, 298, 210, 1024, 512);
        guiGraphics.setColor(1f, 1f, 1f, 1f);
        guiGraphics.disableScissor();
    }

    private void renderStarSystem(GuiGraphics guiGraphics) {
        Collection<CelestialObject> objects = CosmicVoyage.getCelestialObjectManager().getAll();
        Map<ResourceLocation, CelestialObject> allObjects = CosmicVoyage.getCelestialObjectManager().getObjectMap();
        Map<CelestialObject, Vec2> positions = new HashMap<>();

        float time = (float) shipTick / 50;

        for (CelestialObject obj : objects) {
            positions.put(obj, obj.calculatePosition(time, allObjects));
        }

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        float scale = velocity + 50;

        for (Map.Entry<CelestialObject, Vec2> entry : positions.entrySet()) {
            CelestialObject obj = entry.getKey();
            Vec2 position = entry.getValue();

            int x = centerX + (int)(position.x * scale);
            int y = centerY + (int)(position.y * scale);

            // this is all dubious...
            int size = (int)(Math.log(obj.getDiameter()) * 2 );
            int scaledSize = (int) (size * scale/1000);

            //do texture instead
            guiGraphics.fill(x - scaledSize/2, y - scaledSize/2, x + scaledSize/2, y + scaledSize/2, -14467573);

            //FOR DEBUGGING!!!!!!!!!!
            guiGraphics.drawString(
                        this.font,
                        obj.getName().getPath(),
                        x,
                        y - this.font.lineHeight/2,
                        0xFFFFFF
                );
        }

        for (CelestialObject obj : positions.keySet()) {
            if (obj.getParent().isPresent()) {
                drawOrbit(guiGraphics, obj, centerX, centerY, scale);
            }
        }
    }

    private int getObjectColor(CelestialObject obj) {
        if (obj.getName().getPath().contains("star")) {
            return 0xFFFF00;
        } else if (obj.getDiameter() > 10000) {
            return 0x8888FF;
        } else {
            return 0xAAAAAA;
        }
    }

    private void drawOrbit(GuiGraphics guiGraphics, CelestialObject obj, int centerX, int centerY, float scale) {
        // FOR DEBUGGING ?? MAYBE ??
        float a = obj.getAverageDistance() * scale;
        float e = obj.getEccentricity();
        float b = a * (float)Math.sqrt(1 - e*e);

        int segments = 100;
        int orbitColor = 0x44FFFFFF;

        for (int i = 0; i < segments; i++) {
            float angle1 = (float)(2 * Math.PI * i / segments);
            float angle2 = (float)(2 * Math.PI * (i+1) / segments);

            int x1 = centerX + (int)(a * Math.cos(angle1));
            int y1 = centerY + (int)(b * Math.sin(angle1));
            int x2 = centerX + (int)(a * Math.cos(angle2));
            int y2 = centerY + (int)(b * Math.sin(angle2));

            guiGraphics.hLine(x1, x2, y2, orbitColor);
        }
    }

//    private void simulateSolarSystem(int shipTick) {
//        double universeTimeDays = shipTick / 24000.0;
//
//        Collection<CelestialObject> objects = CosmicVoyage.getCelestialObjectManager().getAll();
//        //System.out.println(objects.isEmpty());
//
//        Map<CelestialObject, Vec2> positions = new HashMap<>();
//        for (CelestialObject obj : objects) {
//            positions.put(obj, obj.calculatePosition(universeTimeDays));
//        }
//
//        this.currentCelestialPositions = positions;
//    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    public void containerTick() {
        super.containerTick();
        this.helmTick();
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {

    }

    public void helmTick() {
        shipTick++;

        slider.tick();
        ball.tick();

        float newRotation = ball.getCurrentRotation();
        float newVelocity = slider.getCurrentVelocity();
        //this.simulateSolarSystem(shipTick);

        if (newRotation != rotation || newVelocity != velocity) {
            rotation = newRotation;
            velocity = newVelocity;

//            NetworkHandler.CHANNEL.sendToServer(
//                    new UpdateShipControlsPacket(
//                            menu.getShipId(),
//                            (int)rotation,
//                            (int)velocity
//                    )
//            );
        }

//        currentCelestialPositions = menu.getCurrentCelestialPositions();
        //this.simulateSolarSystem(shipTick);
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

    public void renderInvertedQuad(GuiGraphics gui, ResourceLocation texture, int startx, int starty, int Uoffset, int Voffset, int width, int height, int texwidth, int texheight, int tintColor) {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
                GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

//        float a = ((tintColor >> 24) & 0xFF) / 255f;
//        float r = ((tintColor >> 16) & 0xFF) / 255f;
//        float g = ((tintColor >> 8) & 0xFF) / 255f;
//        float b = (tintColor & 0xFF) / 255f;
//        RenderSystem.setShaderColor(r, g, b, a);

        gui.blit(texture, startx, starty, Uoffset, Voffset, width, height, texwidth, texheight);

        //RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.defaultBlendFunc();
    }

    public void renderMultiplicativeQuad(GuiGraphics gui, ResourceLocation texture, int startx, int starty, int Uoffset, int Voffset, int width, int height, int texwidth, int texheight, int tintColor) {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.DST_COLOR,
                GlStateManager.DestFactor.ZERO,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        float a = ((tintColor >> 24) & 0xFF) / 255f;
        float r = ((tintColor >> 16) & 0xFF) / 255f;
        float g = ((tintColor >> 8) & 0xFF) / 255f;
        float b = (tintColor & 0xFF) / 255f;

        RenderSystem.setShaderColor(r, g, b, a);

        gui.blit(texture, startx, starty, Uoffset, Voffset, width, height, texwidth, texheight);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.defaultBlendFunc();
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