package org.alegrz.trafficsimulation.model.intersection;

import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.Direction;
import org.alegrz.trafficsimulation.model.RoadType;
import org.alegrz.trafficsimulation.model.controller.LightState;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class NoCollisionIntersectionTest {
    private NoCollisionIntersection intersection;

    @BeforeEach
    void setUp() {
        intersection = new NoCollisionIntersection();
    }

    @Test
    void testStep_GreenLightForward() {
        LinkedList<Car> road = new LinkedList<>();
        Car car = new Car("a", RoadType.NORTH, Direction.FORWARD, 0);
        road.add(car);
        intersection.getRoads().put(RoadType.NORTH, road);

        intersection.getLightStatus().put(RoadType.NORTH, List.of(LightState.GREEN, LightState.RED));

        List<Car> leavingCars = intersection.step();

        assertFalse(leavingCars.contains(car));
        assertTrue(road.contains(car));
    }

    @Test
    void testStep_GreenLightLeft() {
        LinkedList<Car> road = new LinkedList<>();
        Car car = new Car("b", RoadType.WEST, Direction.LEFT, 0);
        road.add(car);
        intersection.getRoads().put(RoadType.WEST, road);

        intersection.getLightStatus().put(RoadType.WEST, List.of(LightState.RED, LightState.GREEN));

        List<Car> leavingCars = intersection.step();

        assertFalse(leavingCars.contains(car));
        assertTrue(road.contains(car));
    }

    @Test
    void testStep_NoCarOnRoad() {
        intersection.getLightStatus().put(RoadType.EAST, List.of(LightState.GREEN, LightState.RED));

        List<Car> leavingCars = intersection.step();

        assertTrue(leavingCars.isEmpty());
    }

    @Test
    void testStep_CarNotLeavingWhenLightIsRed() {
        LinkedList<Car> road = new LinkedList<>();
        Car car = new Car("c", RoadType.SOUTH, Direction.FORWARD, 0);
        road.add(car);
        intersection.getRoads().put(RoadType.SOUTH, road);

        intersection.getLightStatus().put(RoadType.SOUTH, List.of(LightState.RED, LightState.RED));

        List<Car> leavingCars = intersection.step();

        assertFalse(leavingCars.contains(car));
        assertTrue(road.contains(car));
    }

    @Test
    void testStep_CarInBothLanes() {
        LinkedList<Car> forwardRoad = new LinkedList<>();
        Car forwardCar = new Car("d", RoadType.NORTH, Direction.FORWARD, 0);
        forwardRoad.add(forwardCar);
        intersection.getRoads().put(RoadType.NORTH, forwardRoad);

        LinkedList<Car> leftRoad = new LinkedList<>();
        Car leftCar = new Car("e", RoadType.NORTH, Direction.LEFT, 0);
        leftRoad.add(leftCar);
        intersection.getRoads().put(RoadType.NORTH, leftRoad);

        intersection.getLightStatus().put(RoadType.NORTH, List.of(LightState.GREEN, LightState.GREEN));

        List<Car> leavingCars = intersection.step();

        assertFalse(leavingCars.contains(forwardCar));
        assertFalse(leavingCars.contains(leftCar));
    }
}
