package com.plusls.ommc.impl.generic.highlightWaypoint;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.*;
import com.plusls.ommc.mixin.accessor.AccessorRenderPipelines;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import top.hendrixshen.magiclib.MagicLib;
import top.hendrixshen.magiclib.api.compat.minecraft.client.gui.FontCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.resources.ResourceLocationCompat;
import top.hendrixshen.magiclib.api.compat.mojang.blaze3d.vertex.VertexFormatCompat;
import top.hendrixshen.magiclib.api.event.minecraft.render.RenderLevelListener;
import top.hendrixshen.magiclib.api.render.context.LevelRenderContext;
import top.hendrixshen.magiclib.impl.render.context.RenderGlobal;
import top.hendrixshen.magiclib.util.minecraft.PositionUtil;
import top.hendrixshen.magiclib.util.minecraft.render.RenderUtil;

import java.util.OptionalDouble;
import java.util.OptionalInt;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HighlightWaypointRenderer implements RenderLevelListener {
    @Getter
    private static final HighlightWaypointRenderer instance = new HighlightWaypointRenderer();
    private static final ResourceLocation BEAM_LOCATION = ResourceLocationCompat.withDefaultNamespace("textures/entity/beacon_beam.png");

    private static final RenderPipeline WAYPOINT_ICON = RenderPipeline.builder(AccessorRenderPipelines.getGuiTexturedSnipped())
            .withLocation(ResourceLocationCompat.fromNamespaceAndPath("ommc", "pipeline/waypoint_icon"))
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .build();

    public TextureAtlasSprite targetIdSprite;
    protected long lastBeamTime = 0;

    public static void init() {
        MagicLib.getInstance().getEventManager().register(RenderLevelListener.class, HighlightWaypointRenderer.instance);
    }

    @Override
    public void preRenderLevel(ClientLevel level, LevelRenderContext context) {
        // NO-OP
    }

    @Override
    public void postRenderLevel(ClientLevel level, LevelRenderContext context) {
        float partialTicks = RenderUtil.getPartialTick();
        BlockPos waypointPos = HighlightWaypointHandler.getInstance().getHighlightPos();

        if (waypointPos == null) {
            return;
        }

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getEntity().getEyePosition(partialTicks);
        double maxDistance = Minecraft.getInstance().options.renderDistance().get() * 16;
        Vec3 target = PositionUtil.centerOf(waypointPos);
        double distance = target.distanceTo(cameraPos);
        double renderDistance = distance;

        if (distance > maxDistance) {
            Vec3 direction = target.subtract(cameraPos);
            target = cameraPos.add(direction.normalize().multiply(maxDistance, maxDistance, maxDistance));
            renderDistance = maxDistance;
        }

        Vec3      vec3  = target.subtract(cameraPos);
        PoseStack stack = new PoseStack();
        stack.pushPose();
        stack.translate(vec3.x(), vec3.y(), vec3.z());
        RenderGlobal.disableDepthTest();

        if (this.lastBeamTime >= System.currentTimeMillis()) {
            stack.pushPose();
            stack.translate(-0.5, -0.5, -0.5);
            // TODO: 1.16+ RenderType hook to support beam seeThrough
            this.renderBeam(level, stack, partialTicks);
            stack.popPose();
        }

        stack.pushPose();
        stack.mulPose(new Matrix4f().rotation(camera.rotation()));

        float scale = (float) ((renderDistance > 8 ? renderDistance - 8 : 0) * 0.2 + 1) * 0.0265F;
        stack.scale(RenderUtil.getSizeScalingXSign() * scale, -scale, -scale);

        stack.pushPose();
        stack.translate(0.0, 5.0, 0.0);
        this.renderText(stack, String.format("x:%d, y:%d, z:%d (%dm)",
                waypointPos.getX(), waypointPos.getY(), waypointPos.getZ(), (int) distance));
        stack.popPose();

        RenderGlobal.disableDepthTest();
        this.renderIcon(stack);
        RenderGlobal.enableDepthTest();
        stack.popPose();
        stack.popPose();
    }

    private void renderBeam(@NotNull ClientLevel level, @NotNull PoseStack stack, float partialTicks) {
        MultiBufferSource.BufferSource bufferBuilder = RenderUtil.getBufferSource();

        BeaconRenderer.renderBeaconBeam(
                stack,
                bufferBuilder,
                HighlightWaypointRenderer.BEAM_LOCATION,
                partialTicks,
                1.0F,
                level.getGameTime(),
                -128,
                256,
                0xFF0000,
                0.2F,
                0.25F
        );
        bufferBuilder.endBatch();

        RenderGlobal.enableBlend();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderText(@NotNull PoseStack stack, String text) {
        FontCompat fontCompat = FontCompat.of(Minecraft.getInstance().font);
        int halfTextWidth = fontCompat.get().width(text) / 2;
        int bgColor = 0x80000000;

        while (true) {
            MultiBufferSource.BufferSource immediate = RenderUtil.getBufferSource();

            fontCompat.drawInBatch(
                    text,
                    (float) -halfTextWidth,
                    0.0F,
                    0xFFFFFF,
                    false,
                    stack.last().pose(),
                    immediate,
                    FontCompat.DisplayMode.SEE_THROUGH,
                    bgColor,
                    0xF000F0
            );

            immediate.endBatch();

            if (bgColor == 0) {
                break;
            } else {
                bgColor = 0;
            }
        }
    }

    private void renderIcon(@NotNull PoseStack stack) {
        TextureAtlasSprite icon = HighlightWaypointResourceLoader.targetIdSprite;
        RenderGlobal.enableBlend();

        RenderSystem.setShaderTexture(0, Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getTexture());

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormatCompat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        Matrix4f matrix4f = stack.last().pose();

        float xWidth = 10.0f;
        float yWidth = 10.0f;
        float iconR = 1.0f;
        float iconG = 0.0f;
        float iconB = 0.0f;

        bufferBuilder.addVertex(matrix4f, -xWidth, -yWidth, 0.0F).setUv(icon.getU0(), icon.getV0()).setColor(iconR, iconG, iconB, 0.5F);
        bufferBuilder.addVertex(matrix4f, -xWidth, yWidth, 0.0F).setUv(icon.getU0(), icon.getV1()).setColor(iconR, iconG, iconB, 0.5F);
        bufferBuilder.addVertex(matrix4f, xWidth, yWidth, 0.0F).setUv(icon.getU1(), icon.getV1()).setColor(iconR, iconG, iconB, 0.5F);
        bufferBuilder.addVertex(matrix4f, xWidth, -yWidth, 0.0F).setUv(icon.getU1(), icon.getV0()).setColor(iconR, iconG, iconB, 0.5F);
        HighlightWaypointRenderer.end(bufferBuilder);
    }

    private static void end(BufferBuilder builder) {
        try (MeshData meshData = builder.buildOrThrow()) {
            RenderPipeline renderPipeline = WAYPOINT_ICON;
            try {
                GpuBuffer gpuBuffer = renderPipeline.getVertexFormat().uploadImmediateVertexBuffer(meshData.vertexBuffer());
                GpuBuffer gpuBuffer2;
                VertexFormat.IndexType indexType;
                if (meshData.indexBuffer() == null) {
                    RenderSystem.AutoStorageIndexBuffer autoStorageIndexBuffer = RenderSystem.getSequentialBuffer(meshData.drawState().mode());
                    gpuBuffer2 = autoStorageIndexBuffer.getBuffer(meshData.drawState().indexCount());
                    indexType = autoStorageIndexBuffer.type();
                } else {
                    gpuBuffer2 = renderPipeline.getVertexFormat().uploadImmediateIndexBuffer(meshData.indexBuffer());
                    indexType = meshData.drawState().indexType();
                }

                RenderTarget renderTarget = Minecraft.getInstance().getMainRenderTarget();

                try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                        renderTarget.getColorTexture(),
                        OptionalInt.empty(),
                        renderTarget.useDepth ? renderTarget.getDepthTexture() : null,
                        OptionalDouble.empty()
                )) {
                    renderPass.setPipeline(renderPipeline);
                    renderPass.setVertexBuffer(0, gpuBuffer);
                    if (RenderSystem.SCISSOR_STATE.isEnabled()) {
                        renderPass.enableScissor(RenderSystem.SCISSOR_STATE);
                    }

                    for(int i = 0; i < 12; ++i) {
                        GpuTexture gpuTexture = RenderSystem.getShaderTexture(i);
                        if (gpuTexture != null) {
                            renderPass.bindSampler("Sampler" + i, gpuTexture);
                        }
                    }

                    renderPass.setIndexBuffer(gpuBuffer2, indexType);
                    renderPass.drawIndexed(0, meshData.drawState().indexCount());
                }
            } catch (Throwable t) {
                try {
                    meshData.close();
                } catch (Throwable t1) {
                    t.addSuppressed(t1);
                }
                throw t;
            }
        } catch (Exception ignore) {
        }
    }
}
