package com.yourname.pathfinder;

import com.yourname.pathfinder.path.PathResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AutoWalker {
    private static boolean walking = false;
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void start(PathResult path) {
        if (path != null && path.isFound()) walking = true;
    }

    public static void stop() {
        if (!walking) return;
        walking = false;
        client.options.keyForward.setPressed(false);
        client.options.keyJump.setPressed(false);
    }

    public static void tick(PathResult path) {
        if (!walking || path.isFinished()) {
            stop();
            return;
        }

        ClientPlayerEntity player = client.player;
        BlockPos targetPos = path.getCurrentTarget();
        Vec3d playerFeet = new Vec3d(player.getX(), player.getY(), player.getZ());
        Vec3d targetCenter = new Vec3d(targetPos.getX() + 0.5, player.getY(), targetPos.getZ() + 0.5);

        if (playerFeet.distanceTo(targetCenter) < 0.7) {
            path.advance();
            if (path.isFinished()) {
                PathfinderMod.stopPathfinding();
                return;
            }
            targetPos = path.getCurrentTarget();
        }

        lookAt(targetPos);
        client.options.keyForward.setPressed(true);

        if (targetPos.getY() > player.getBlockPos().getY() && player.isOnGround()) {
            player.jump();
        } else {
            client.options.keyJump.setPressed(false);
        }

        if (client.options.keyBack.isPressed() || client.options.keyLeft.isPressed() || client.options.keyRight.isPressed()) {
            PathfinderMod.stopPathfinding();
        }
    }

    private static void lookAt(BlockPos pos) {
        ClientPlayerEntity player = client.player;
        double dx = (pos.getX() + 0.5) - player.getX();
        double dy = (pos.getY() + 0.5) - (player.getY() + player.getEyeHeight(player.getPose()));
        double dz = (pos.getZ() + 0.5) - player.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, distance));
        player.setYaw(MathHelper.wrapDegrees(yaw));
        player.setPitch(MathHelper.wrapDegrees(pitch));
    }
}
