package com.yourname.pathfinder;

import com.mojang.blaze3d.systems.RenderSystem;
import com.yourname.pathfinder.path.PathResult;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PathRenderer {
    public static void render(WorldRenderContext context, PathResult pathResult) {
        if (pathResult == null || pathResult.isFinished()) return;

        MatrixStack matrixStack = context.matrixStack();
        Camera camera = context.camera();
        Vec3d cameraPos = camera.getPos();

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        
        matrixStack.push();
        matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.lineWidth(3.0f);

        bufferBuilder.begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.POSITION_COLOR);

        for (BlockPos pos : pathResult.getPath()) {
            float x = pos.getX() + 0.5f;
            float y = pos.getY() + 0.5f;
            float z = pos.getZ() + 0.5f;
            bufferBuilder.vertex(matrixStack.peek().getPositionMatrix(), x, y, z).color(0.2f, 0.4f, 1.0f, 0.8f).next();
        }

        tessellator.draw();
        
        matrixStack.pop();

        RenderSystem.lineWidth(1.0F);
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }
}
