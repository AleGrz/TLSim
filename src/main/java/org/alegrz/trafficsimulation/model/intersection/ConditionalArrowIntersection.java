package org.alegrz.trafficsimulation.model.intersection;

import org.alegrz.trafficsimulation.model.controller.IntersectionController;
import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.Direction;
import org.alegrz.trafficsimulation.model.RoadType;
import org.alegrz.trafficsimulation.model.controller.LightState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConditionalArrowIntersection extends Intersection {
    public ConditionalArrowIntersection(IntersectionController controller) {
        super(controller);
    }

    @Override
    public List<Car> step() {
        controller.processLightStates(lightStatus, roads, timeStep);
        List<Car> leavingCars = new ArrayList<>();
        for (RoadType roadType : RoadType.values()) {
            LinkedList<Car> road = roads.get(roadType);
            LightState lightState = lightStatus.get(roadType).get(0);
            if (!road.isEmpty() && lightState == LightState.GREEN) {
                Car car = road.get(0);
                RoadType opposite = RoadType.fromDirection(car.origin(), Direction.FORWARD);
                LinkedList<Car> oppositeRoad = roads.get(opposite);
                if (oppositeRoad.isEmpty()) {
                    leavingCars.add(car);
                    continue;
                }
                Direction oppositeCarDirection = oppositeRoad.get(0).direction();
                if (car.direction() != Direction.LEFT || (oppositeCarDirection == Direction.LEFT)) {
                    leavingCars.add(car);
                }
            } else if(!road.isEmpty() && lightState == LightState.RED) {
                Car car = road.get(0);
                if(car == null || car.direction() != Direction.RIGHT) {
                    continue;
                }
                RoadType left = RoadType.fromDirection(car.origin(), Direction.LEFT);
                LinkedList<Car> leftRoad = roads.get(left);
                if (leftRoad.isEmpty()) {
                    leavingCars.add(car);
                    continue;
                }
                Direction leftCarDirection = leftRoad.get(0).direction();
                if (leftCarDirection == Direction.RIGHT || leftCarDirection == Direction.LEFT) {
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
}
