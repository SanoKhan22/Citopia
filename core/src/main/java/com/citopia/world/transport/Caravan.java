package com.citopia.world.transport;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.GridPoint2;
import com.citopia.world.pathfinding.Path;

import java.util.List;

public class Caravan {
    public enum Type {
        WAGON,
        LAND_SHIP
    }

    private final Route route;
    private final Type type;
    
    // Position on the map in tiles
    private final Vector2 position;
    // Current index in the route's path
    private int currentPathIndex;
    // Interpolation progress between currentPathIndex and currentPathIndex + 1
    private float progress;
    private final float speed; // tiles per second

    public Caravan(Route route, Type type, float speed) {
        this.route = route;
        this.type = type;
        this.speed = speed;
        
        this.currentPathIndex = 0;
        this.progress = 0f;
        this.position = new Vector2();
        
        List<GridPoint2> steps = route.getPath().getSteps();
        if (!steps.isEmpty()) {
            GridPoint2 startNode = steps.get(0);
            this.position.set(startNode.x, startNode.y);
        }
    }

    // Returns true if reached destination
    public boolean update(float delta) {
        List<GridPoint2> steps = route.getPath().getSteps();
        if (steps.size() < 2 || currentPathIndex >= steps.size() - 1) {
            return true; // Finished
        }

        progress += speed * delta;

        while (progress >= 1.0f) {
            progress -= 1.0f;
            currentPathIndex++;
            if (currentPathIndex >= steps.size() - 1) {
                // We reached the end
                progress = 0f;
                // Snap to final location
                GridPoint2 finalNode = steps.get(steps.size() - 1);
                position.set(finalNode.x, finalNode.y);
                return true;
            }
        }

        // Interpolate position
        GridPoint2 currentStep = steps.get(currentPathIndex);
        GridPoint2 nextStep = steps.get(currentPathIndex + 1);
        
        position.x = currentStep.x + (nextStep.x - currentStep.x) * progress;
        position.y = currentStep.y + (nextStep.y - currentStep.y) * progress;

        return false;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Type getType() {
        return type;
    }
    
    public Route getRoute() {
        return route;
    }

    // Facing direction in degrees
    public float getRotation() {
        List<GridPoint2> steps = route.getPath().getSteps();
        if (steps.size() < 2 || currentPathIndex >= steps.size() - 1) {
            return 0f;
        }
        GridPoint2 currentStep = steps.get(currentPathIndex);
        GridPoint2 nextStep = steps.get(currentPathIndex + 1);
        float dx = nextStep.x - currentStep.x;
        float dy = nextStep.y - currentStep.y;
        
        // LibGDX 0 degrees is facing right.
        return (float) Math.toDegrees(Math.atan2(dy, dx));
    }
}
