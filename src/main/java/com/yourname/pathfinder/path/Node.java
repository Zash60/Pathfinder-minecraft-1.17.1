package com.yourname.pathfinder.path;

import net.minecraft.util.math.BlockPos;

public class Node {
    public final BlockPos pos;
    public Node parent;
    public double gCost;
    public double hCost;

    public Node(BlockPos pos) { this.pos = pos; }
    public double getFCost() { return gCost + hCost; }
}
