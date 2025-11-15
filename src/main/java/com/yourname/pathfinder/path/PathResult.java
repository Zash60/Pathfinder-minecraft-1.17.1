package com.yourname.pathfinder.path;

import net.minecraft.util.math.BlockPos;
import java.util.Collections;
import java.util.List;

public class PathResult {
    private final boolean found;
    private final List<BlockPos> path;
    private int currentIndex = 0;

    public PathResult(boolean found, List<BlockPos> path) {
        this.found = found;
        this.path = path;
        Collections.reverse(this.path);
    }

    public boolean isFound() { return found; }
    public List<BlockPos> getPath() { return path; }
    public boolean isFinished() { return currentIndex >= path.size(); }
    
    public BlockPos getCurrentTarget() {
        return isFinished() ? null : path.get(currentIndex);
    }

    public void advance() { currentIndex++; }
}
