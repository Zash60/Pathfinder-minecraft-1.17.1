package com.yourname.pathfinder;

import com.yourname.pathfinder.path.Node;
import com.yourname.pathfinder.path.PathResult;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.*;

public class Pathfinder {
    private final BlockPos start;
    private final BlockPos end;
    private final World world = MinecraftClient.getInstance().world;
    private static final int MAX_ITERATIONS = 5000;

    public Pathfinder(BlockPos start, BlockPos end) {
        this.start = start;
        this.end = end;
    }

    public PathResult findPath() {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::getFCost));
        Set<BlockPos> closedSet = new HashSet<>();

        Node startNode = new Node(start);
        startNode.gCost = 0;
        startNode.hCost = getHeuristic(start, end);
        openSet.add(startNode);

        int iterations = 0;
        while (!openSet.isEmpty() && iterations < MAX_ITERATIONS) {
            iterations++;
            Node currentNode = openSet.poll();

            if (currentNode.pos.equals(end)) {
                return new PathResult(true, reconstructPath(currentNode));
            }

            closedSet.add(currentNode.pos);

            for (Node neighbor : getNeighbors(currentNode)) {
                if (closedSet.contains(neighbor.pos)) continue;

                double tentativeGCost = currentNode.gCost + getDistance(currentNode.pos, neighbor.pos);

                if (tentativeGCost < neighbor.gCost || !openSet.stream().anyMatch(n -> n.pos.equals(neighbor.pos))) {
                    neighbor.parent = currentNode;
                    neighbor.gCost = tentativeGCost;
                    neighbor.hCost = getHeuristic(neighbor.pos, end);
                    
                    if (!openSet.stream().anyMatch(n -> n.pos.equals(neighbor.pos))) {
                         openSet.add(neighbor);
                    }
                }
            }
        }
        return new PathResult(false, new ArrayList<>());
    }

    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        BlockPos p = node.pos;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    BlockPos neighborPos = p.add(x, y, z);
                    if (isWalkable(neighborPos)) {
                        neighbors.add(new Node(neighborPos));
                    }
                }
            }
        }
        return neighbors;
    }

    private boolean isWalkable(BlockPos pos) {
        BlockState floorState = world.getBlockState(pos.down());
        if (floorState.isAir()) return false;
        
        VoxelShape selfShape = world.getBlockState(pos).getCollisionShape(world, pos);
        VoxelShape headShape = world.getBlockState(pos.up()).getCollisionShape(world, pos);

        return selfShape.isEmpty() && headShape.isEmpty();
    }
    
    private List<BlockPos> reconstructPath(Node endNode) {
        List<BlockPos> path = new ArrayList<>();
        Node current = endNode;
        while (current != null) {
            path.add(current.pos);
            current = current.parent;
        }
        return path;
    }
    
    private double getHeuristic(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) + Math.abs(a.getZ() - b.getZ());
    }

    private double getDistance(BlockPos a, BlockPos b) {
        return a.getSquaredDistance(b);
    }
}
