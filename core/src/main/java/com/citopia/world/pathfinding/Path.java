package com.citopia.world.pathfinding;

import com.badlogic.gdx.math.GridPoint2;
import java.util.Collections;
import java.util.List;

/**
 * Represents a sequence of discrete contiguous tile coordinates 
 * forming a traversable path in the world grid.
 */
public class Path {
    private final List<GridPoint2> steps;
    private final int totalLength; // usually steps.size() - 1, as steps include origin

    public Path(List<GridPoint2> steps) {
        this.steps = Collections.unmodifiableList(steps);
        this.totalLength = Math.max(0, steps.size() - 1);
    }

    public List<GridPoint2> getSteps() {
        return steps;
    }

    public int getLength() {
        return totalLength;
    }

    public boolean isEmpty() {
        return steps.isEmpty();
    }
}
