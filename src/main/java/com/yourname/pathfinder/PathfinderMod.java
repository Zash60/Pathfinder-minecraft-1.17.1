package com.yourname.pathfinder;

import com.yourname.pathfinder.path.PathResult;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class PathfinderMod implements ClientModInitializer {

    public static final MinecraftClient client = MinecraftClient.getInstance();
    private static PathResult currentPath = null;

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> GotoCommand.register(dispatcher));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (currentPath != null && !currentPath.isFinished()) {
                AutoWalker.tick(currentPath);
            }
        });

        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            if (currentPath != null) {
                PathRenderer.render(context, currentPath);
            }
        });
    }
    
    public static void startPathfinding(BlockPos start, BlockPos end) {
        stopPathfinding();
        new Thread(() -> {
            Pathfinder pathfinder = new Pathfinder(start, end);
            PathResult result = pathfinder.findPath();
            if (result.isFound()) {
                currentPath = result;
                AutoWalker.start(currentPath);
            } else {
                GotoCommand.sendFeedback("§cCaminho não encontrado!");
            }
        }).start();
    }

    public static void stopPathfinding() {
        AutoWalker.stop();
        currentPath = null;
    }
}
