package com.yourname.pathfinder;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class GotoCommand {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("goto")
            .then(argument("x", IntegerArgumentType.integer())
            .then(argument("y", IntegerArgumentType.integer())
            .then(argument("z", IntegerArgumentType.integer())
                .executes(context -> {
                    int x = IntegerArgumentType.getInteger(context, "x");
                    int y = IntegerArgumentType.getInteger(context, "y");
                    int z = IntegerArgumentType.getInteger(context, "z");

                    BlockPos start = client.player.getBlockPos();
                    BlockPos end = new BlockPos(x, y, z);
                    
                    sendFeedback("§aCalculando caminho para " + x + ", " + y + ", " + z + "...");
                    PathfinderMod.startPathfinding(start, end);
                    return 1;
                })
            )))
        );
        
        dispatcher.register(literal("stop")
            .executes(context -> {
                sendFeedback("§eAutowalk cancelado.");
                PathfinderMod.stopPathfinding();
                return 1;
            })
        );
    }
    
    public static void sendFeedback(String message) {
        if (client.player != null) {
            client.player.sendMessage(new LiteralText(message), false);
        }
    }
}
