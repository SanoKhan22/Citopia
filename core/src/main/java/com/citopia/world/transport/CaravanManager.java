package com.citopia.world.transport;

import com.citopia.world.CitySite;
import com.citopia.world.TileMap;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class CaravanManager {
    private final RouteNetwork routeNetwork;
    private final TileMap tileMap;
    private final List<Caravan> activeCaravans;
    
    private float spawnTimer = 0f;
    private static final float SPAWN_INTERVAL = 4.0f; 
    private static final int MAX_CARAVANS = 20;

    public CaravanManager(RouteNetwork routeNetwork, TileMap tileMap) {
        this.routeNetwork = routeNetwork;
        this.tileMap = tileMap;
        this.activeCaravans = new ArrayList<>();
    }

    public void update(float delta) {
        spawnTimer += delta;
        if (spawnTimer >= SPAWN_INTERVAL && activeCaravans.size() < MAX_CARAVANS) {
            spawnTimer = 0f;
            spawnRandomCaravan();
        }

        for (int i = activeCaravans.size() - 1; i >= 0; i--) {
            Caravan caravan = activeCaravans.get(i);
            boolean arrived = caravan.update(delta);
            if (arrived) {
                activeCaravans.remove(i);
            }
        }
    }

    public void seedInitialCaravans(int count) {
        for (int i = 0; i < count; i++) {
            Caravan spawned = spawnRandomCaravan();
            if (spawned != null) {
                spawned.update(MathUtils.random(2f, 15f));
            }
        }
    }

    private Caravan spawnRandomCaravan() {
        List<CitySite> cities = tileMap.cities();
        if (cities.size() < 2) return null;

        CitySite origin = cities.get(MathUtils.random(cities.size() - 1));
        CitySite destination;
        do {
            destination = cities.get(MathUtils.random(cities.size() - 1));
        } while (destination == origin);

        Route route = routeNetwork.getCachedRoute(origin, destination);
        
        if (route == null) {
            route = routeNetwork.computeShortestRoute(origin, destination);
        }

        if (route != null && route.getPath().getLength() > 1) {
            Caravan.Type type = MathUtils.randomBoolean() ? Caravan.Type.WAGON : Caravan.Type.LAND_SHIP;
            float speed = type == Caravan.Type.WAGON ? 3.5f : 6.0f; 

            Caravan caravan = new Caravan(route, type, speed);
            activeCaravans.add(caravan);
            return caravan;
        }

        return null;
    }

    public List<Caravan> getActiveCaravans() {
        return activeCaravans;
    }
}
