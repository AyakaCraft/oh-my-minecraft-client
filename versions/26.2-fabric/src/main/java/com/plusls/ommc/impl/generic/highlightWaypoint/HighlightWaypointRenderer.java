package com.plusls.ommc.impl.generic.highlightWaypoint;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.plusls.ommc.mixin.accessor.AccessorRenderPipelines;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.blockentity.state.BeaconRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import top.hendrixshen.magiclib.MagicLib;
import top.hendrixshen.magiclib.api.compat.minecraft.resources.ResourceLocationCompat;
import top.hendrixshen.magiclib.api.event.minecraft.render.RenderLevelListener;
import top.hendrixshen.magiclib.api.render.context.LevelRenderContext;
import top.hendrixshen.magiclib.impl.render.TextRenderer;
import top.hendrixshen.magiclib.impl.render.context.RenderGlobal;
import top.hendrixshen.magiclib.util.minecraft.PositionUtil;
import top.hendrixshen.magiclib.util.minecraft.render.RenderUtil;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HighlightWaypointRenderer implements RenderLevelListener {
    @Getter
    private static final HighlightWaypointRenderer instance = new HighlightWaypointRenderer();
    private static final Identifier BEAM_LOCATION = ResourceLocationCompat.withDefaultNamespace("textures/entity/beacon/beacon_beam.png");

    private static final   RenderPipeline WAYPOINT_ICON      = RenderPipeline.builder(AccessorRenderPipelines.getGuiTexturedSnipped())
            .withLocation(ResourceLocationCompat.fromNamespaceAndPath("ommc", "pipeline/waypoint_icon"))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .build();

    private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();

    private BlockPos waypointPos;
    private Vec3 target;
    private double distance;
    private float scale;

    private Matrix4f iconMatrix;

    long lastBeamTime = 0;

    public static void init() {
        MagicLib.getInstance().getEventManager().register(RenderLevelListener.class, HighlightWaypointRenderer.instance);
    }

    @Override
    public void preRenderLevel(ClientLevel level, LevelRenderContext context) {
        float partialTicks = RenderUtil.getPartialTick();

        this.waypointPos = HighlightWaypointHandler.getInstance().getHighlightPos();
        if (waypointPos == null) return;

        this.target = PositionUtil.centerOf(waypointPos);

        Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
        Vec3 cameraPos = camera.entity().getEyePosition(partialTicks);

        this.distance = target.distanceTo(cameraPos);
        double renderDistance = distance;

        double maxDistance = Minecraft.getInstance().options.renderDistance().get() * 16;
        if (distance > maxDistance) {
            Vec3 direction = target.subtract(cameraPos);
            target = cameraPos.add(direction.normalize().multiply(maxDistance, maxDistance, maxDistance));
            renderDistance = maxDistance;
        }
        this.scale = (float) ((renderDistance > 8 ? renderDistance - 8 : 0) * 0.2 + 1) * 0.0265F;

        PoseStack stack = new PoseStack();
        stack.pushPose();
        stack.translate(target.subtract(cameraPos));

        if (this.lastBeamTime >= System.currentTimeMillis()) {
            stack.pushPose();
            stack.translate(-0.5, -0.5, -0.5);
            // TODO: 1.16+ RenderType hook to support beam seeThrough
            this.prepareBeam(level, partialTicks, new BlockPos(waypointPos.getX(), -64, waypointPos.getZ()));
            stack.popPose();
        }

        stack.pushPose();
        stack.mulPose(new Matrix4f().rotation(camera.rotation()));
        stack.scale(RenderUtil.getSizeScalingXSign() * scale, -scale, -scale);
        RenderGlobal.disableDepthTest();
        this.iconMatrix = stack.last().pose();
        RenderGlobal.enableDepthTest();
        stack.popPose();

        stack.popPose();
    }

    @Override
    public void postRenderLevel(ClientLevel level, LevelRenderContext context) {
        if (waypointPos == null) return;

        this.renderText(
                String.format("x:%d, y:%d, z:%d (%dm)", waypointPos.getX(), waypointPos.getY(), waypointPos.getZ(), (int) distance),
                target,
                scale
        );

        RenderGlobal.disableDepthTest();
        this.renderIcon(iconMatrix);
        RenderGlobal.enableDepthTest();
    }

    private void prepareBeam(@NotNull ClientLevel level, float partialTicks, BlockPos bottomPos) {
        var state = new BeaconRenderState();
        state.animationTime = Math.floorMod(level.getGameTime(), 40) + partialTicks;
        state.beamRadiusScale = 1F;
        state.sections.add(new BeaconRenderState.Section(0xFF0000, 2048));
        state.blockPos = bottomPos;
        state.blockEntityType = BlockEntityTypes.BEACON;
        Minecraft.getInstance().gameRenderer.gameRenderState().levelRenderState.blockEntityRenderStates.add(state);
    }

    private void renderText(String text, Vec3 pos, double scale) {
        var renderer = TextRenderer.create()
                .text(text)
                .at(pos)
                .seeThrough()
                .fontScale(scale)
                .color(0xFFFFFFFF)
                .bgColor(0x80000000);
        renderer.shift(0D, 0.5D + renderer.getLineHeight());
        renderer.render();
    }

    private void renderIcon(@NotNull Matrix4f matrix4f) {
        TextureAtlasSprite icon = HighlightWaypointResourceLoader.targetIdSprite;
        RenderGlobal.enableBlend();

        try (var buffer = new StagedVertexBuffer(() -> "OMMC Waypoint Icon Renderer", 65536)) {
            var draw = buffer.appendDraw(
                    Objects.requireNonNull(WAYPOINT_ICON.getVertexFormatBinding(0)), WAYPOINT_ICON.getPrimitiveTopology(),
                    RenderSystem.getProjectionType().vertexSorting()
            );
            VertexConsumer bufferBuilder = buffer.getVertexBuilder(draw);

            float xWidth = 10.0f;
            float yWidth = 10.0f;
            float iconR  = 1.0f;
            float iconG  = 0.0f;
            float iconB  = 0.0f;

            bufferBuilder.addVertex(matrix4f, -xWidth, -yWidth, 0.0F).setUv(icon.getU0(), icon.getV0()).setColor(iconR, iconG, iconB, 0.5F);
            bufferBuilder.addVertex(matrix4f, -xWidth, yWidth, 0.0F).setUv(icon.getU0(), icon.getV1()).setColor(iconR, iconG, iconB, 0.5F);
            bufferBuilder.addVertex(matrix4f, xWidth, yWidth, 0.0F).setUv(icon.getU1(), icon.getV1()).setColor(iconR, iconG, iconB, 0.5F);
            bufferBuilder.addVertex(matrix4f, xWidth, -yWidth, 0.0F).setUv(icon.getU1(), icon.getV0()).setColor(iconR, iconG, iconB, 0.5F);

            buffer.upload();
            var info = buffer.getExecuteInfo(draw);
            if (info != null) {
                GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                        .writeTransform(RenderSystem.getModelViewMatrixCopy(), COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX);
                RenderTarget mainTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget();
                try (RenderPass renderPass = RenderSystem.getDevice()
                        .createCommandEncoder()
                        .createRenderPass(
                                () -> "ommc waypoint icon rendering",
                                Objects.requireNonNull(mainTarget.getColorTextureView()),
                                Optional.empty(),
                                mainTarget.getDepthTextureView(),
                                OptionalDouble.empty())
                ) {
                    renderPass.setPipeline(WAYPOINT_ICON);
                    RenderSystem.bindDefaultUniforms(renderPass);
                    renderPass.setUniform("DynamicTransforms", dynamicTransforms);
                    var atlas = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(net.minecraft.data.AtlasIds.BLOCKS);
                    renderPass.bindTexture("Sampler0", atlas.getTextureView(), atlas.getSampler());
                    renderPass.setVertexBuffer(0, info.vertexBuffer().slice());
                    renderPass.setIndexBuffer(info.indexBuffer(), info.indexType());
                    renderPass.drawIndexed(info.indexCount(), 1, info.firstIndex(), info.baseVertex(), 0);
                }
            }
            buffer.endFrame();
        }
    }

}
