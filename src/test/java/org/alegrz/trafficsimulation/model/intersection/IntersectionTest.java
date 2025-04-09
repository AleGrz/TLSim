package org.alegrz.trafficsimulation.model.intersection;

import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.Direction;
import org.alegrz.trafficsimulation.model.RoadType;
import org.alegrz.trafficsimulation.model.controller.LightState;
import org.alegrz.trafficsimulation.model.controller.TwoLaneFixedController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntersectionTest {
    private Intersection intersection;

    @BeforeEach
    void setUp() {
        intersection = new Intersection(new TwoLaneFixedController());
    }

    @Test
    void testPutCar() {
        Car car = new Car("a", RoadType.NORTH, Direction.FORWARD, 0);
        intersection.putCar(car);

        assertEquals(1, intersection.getRoads().get(RoadType.NORTH).size());
        assertTrue(intersection.getRoads().get(RoadType.NORTH).contains(car));
    }

    @Test
    void testStep_GreenLightForward() {
        LinkedList<Car> road = new LinkedList<>();
        Car car = new Car("a", RoadType.NORTH, Direction.FORWARD, 0);
        road.add(car);
        intersection.getRoads().put(RoadType.NORTH, road);

        intersection.getLightStatus().put(RoadType.NORTH, List.of(LightState.GREEN));

        List<Car> leavingCars = intersection.step();

        assertFalse(leavingCars.contains(car));
        assertTrue(road.contains(car));
    }

    @Test
    void testStep_RedLightNoMovement() {
        LinkedList<Car> road = new LinkedList<>();
        Car car = new Car("b", RoadType.SOUTH, Direction.FORWARD, 0);
        road.add(car);
        intersection.getRoads().put(RoadType.SOUTH, road);

        intersection.getLightStatus().put(RoadType.SOUTH, List.of(LightState.RED));

        List<Car> leavingCars = intersection.step();

        assertFalse(leavingCars.contains(car));
        assertTrue(road.contains(car));
    }

    @Test
    void testStep_CarWithOppositeDirection() {
        LinkedList<Car> road = new LinkedList<>();
        Car car = new Car("c", RoadType.NORTH, Direction.FORWARD, 0);
        road.add(car);
        intersection.getRoads().put(RoadType.NORTH, road);

        LinkedList<Car> oppositeRoad = new LinkedList<>();
        Car oppositeCar = new Car("d", RoadType.SOUTH, Direction.FORWARD, 0);
        oppositeRoad.add(oppositeCar);
        intersection.getRoads().put(RoadType.SOUTH, oppositeRoad);

        intersection.getLightStatus().put(RoadType.NORTH, List.of(LightState.GREEN));
        intersection.getLightStatus().put(RoadType.SOUTH, List.of(LightState.RED));

        List<Car> leavingCars = intersection.step();

        assertFalse(leavingCars.contains(car));
        assertTrue(road.contains(car));
    }

    @Test
    void testStep_LeaningOnOppositeRoad() {
        LinkedList<Car> road = new LinkedList<>();
        Car car = new Car("e", RoadType.NORTH, Direction.FORWARD, 0);
        road.add(car);
        intersection.getRoads().put(RoadType.NORTH, road);

        LinkedList<Car> oppositeRoad = new LinkedList<>();
        Car oppositeCar = new Car("f", RoadType.SOUTH, Direction.LEFT, 0);
        oppositeRoad.add(oppositeCar);
        intersection.getRoads().put(RoadType.SOUTH, oppositeRoad);

        intersection.getLightStatus().put(RoadType.NORTH, List.of(LightState.GREEN));
        intersection.getLightStatus().put(RoadType.SOUTH, List.of(LightState.RED));

        List<Car> leavingCars = intersection.step();

        assertFalse(leavingCars.contains(car));
        assertTrue(road.contains(car));
    }
}
