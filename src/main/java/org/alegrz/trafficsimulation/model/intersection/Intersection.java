package org.alegrz.trafficsimulation.model.intersection;

import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.Direction;
import org.alegrz.trafficsimulation.model.RoadType;
import org.alegrz.trafficsimulation.model.controller.IntersectionController;
import org.alegrz.trafficsimulation.model.controller.LightState;

import java.util.*;

public class Intersection {
    protected final EnumMap<RoadType, LinkedList<Car>> roads = new EnumMap<>(RoadType.class);
    protected final EnumMap<RoadType, List<LightState>> lightStatus = new EnumMap<>(RoadType.class);
    protected final IntersectionController controller;

    protected int timeStep = 0;

    public Intersection(IntersectionController controller) {
        this.controller = controller;
        init();
    }

    protected void init() {
        for (RoadType roadType : RoadType.values()) {
            roads.put(roadType, new LinkedList<>());
            lightStatus.put(roadType, List.of(LightState.RED));
        }
    }

    public void putCar(Car car) {
        roads.get(car.origin()).addLast(car);
    }

    public List<Car> step() {
        controller.processLightStates(lightStatus, roads, timeStep);
        List<Car> leavingCars = new ArrayList<>();
        for (RoadType roadType : RoadType.values()) {
            LinkedList<Car> road = roads.get(roadType);
            List<LightState> lightState = lightStatus.get(roadType);
            if (!road.isEmpty() && lightState.get(0) == LightState.GREEN) {
                Car car = road.get(0);
                RoadType opposite = RoadType.fromDirection(car.origin(), Direction.FORWARD);
                LinkedList<Car> oppositeRoad = roads.get(opposite);
                if (oppositeRoad.isEmpty()) {
                    leavingCars.add(car);
                    continue;
                }
                Direction oppositeCarDirection = oppositeRoad.get(0).direction();
                if (car.direction() != Direction.LEFT || oppositeCarDirection == Direction.LEFT) {
                    leavingCars.add(car);
                }
            }
        }
        for (Car car : leavingCars) {
            RoadType roadType = car.origin();
            LinkedList<Car> road = roads.get(roadType);
            road.remove(car);
        }
        timeStep++;
        return leavingCars;
    }

    public EnumMap<RoadType, List<LightState>> getLightStatus() {
        return lightStatus;
    }

    public IntersectionController getController() {
        return controller;
    }

    public EnumMap<RoadType, LinkedList<Car>> getRoads() {
        return roads;
    }
}
