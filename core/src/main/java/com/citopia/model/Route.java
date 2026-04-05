package com.citopia.model;

public class Route {
    private final City source;
    private final City target;
    private final float distance;
    private final int costPerTick;

    public Route(City source, City target, int costPerTick) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source and target cities cannot be null.");
        }
        if (source.equals(target)) {
            throw new IllegalArgumentException("Source and target cities cannot be the same.");
        }
        this.source = source;
        this.target = target;
        this.distance = source.distanceTo(target);
        this.costPerTick = costPerTick;
    }

    public City getSource() { return source; }
    public City getTarget() { return target; }
    public float getDistance() { return distance; }
    public int getCostPerTick() { return costPerTick; }

    @Override
    public String toString() {
        return String.format("Route{source=%s, target=%s, dist=%.1f, cost=%d}",
                source.getName(), target.getName(), distance, costPerTick);
    }
}
