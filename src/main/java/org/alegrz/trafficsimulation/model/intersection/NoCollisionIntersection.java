package org.alegrz.trafficsimulation.model.intersection;

import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.RoadType;
import org.alegrz.trafficsimulation.model.controller.LightState;
import org.alegrz.trafficsimulation.model.controller.TwoLaneFixedController;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import static org.alegrz.trafficsimulation.model.Direction.LEFT;

public class NoCollisionIntersection extends Intersection {

    private final EnumMap<RoadType, LinkedList<Car>> roadsForwardRight = new EnumMap<>(RoadType.class);
    private final EnumMap<RoadType, LinkedList<Car>> roadsLeft = new EnumMap<>(RoadType.class);

    protected int timeStep = 0;
    public NoCollisionIntersection() {
        super(new TwoLaneFixedController());
        for (RoadType roadType : RoadType.values()) {
            roadsForwardRight.put(roadType, new LinkedList<>());
            roadsLeft.put(roadType, new LinkedList<>());
        }
    }

    public EnumMap<RoadType, LinkedList<Car>> getRoads() {
        EnumMap<RoadType, LinkedList<Car>> mergedRoads = new EnumMap<>(RoadType.class);
        for (RoadType roadType : RoadType.values()) {
            LinkedList<Car> leftRoad = roadsLeft.get(roadType);
            LinkedList<Car> forwardRightRoad = roadsForwardRight.get(roadType);
            mergedRoads.computeIfAbsent(roadType, k -> new LinkedList<>());
            mergedRoads.get(roadType).addAll(leftRoad);
            mergedRoads.get(roadType).addAll(forwardRightRoad);
        }
        return mergedRoads;
    }

    public void putCar(Car car) {
        if (car.direction() == LEFT) {
            roadsLeft.get(car.origin()).addLast(car);
        } else {
            roadsForwardRight.get(car.origin()).addLast(car);
        }
    }

    public List<Car> step() {
        controller.processLightStates(lightStatus, roads, timeStep);
        List<Car> leavingCars = new ArrayList<>();
        for (RoadType roadType : RoadType.values()) {
            List<LightState> lightState = lightStatus.get(roadType);
            if (lightState.get(0) == LightState.GREEN && !roadsForwardRight.get(roadType).isEmpty()) {
                leavingCars.add(roadsForwardRight.get(roadType).getFirst());
            }
            if (lightState.get(1) == LightState.GREEN && !roadsLeft.get(roadType).isEmpty()) {
                leavingCars.add(roadsLeft.get(roadType).getFirst());
            }
        }
        for (Car car : leavingCars) {
            RoadType roadType = car.origin();
            LinkedList<Car> road;
            if (car.direction() == LEFT) {
                road = roadsLeft.get(roadType);
            } else {
                road = roadsForwardRight.get(roadType);
            }
            road.remove(car);
        }
        timeStep++;
        return leavingCars;
    }

}
