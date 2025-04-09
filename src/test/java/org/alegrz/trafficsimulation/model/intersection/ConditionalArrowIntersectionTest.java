package org.alegrz.trafficsimulation.model.intersection;

import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.Direction;
import org.alegrz.trafficsimulation.model.RoadType;
import org.alegrz.trafficsimulation.model.controller.LightState;
import org.alegrz.trafficsimulation.model.controller.TwoLaneFixedController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConditionalArrowIntersectionTest {

    private ConditionalArrowIntersection intersection;

    @BeforeEach
    void setUp() {
        intersection = new ConditionalArrowIntersection(new TwoLaneFixedController());
    }

    @Test
    void testGreenLightProceed() {
        Car car = new Car("car1", RoadType.NORTH, Direction.FORWARD, 0);
        intersection.putCar(car);
        intersection.getLightStatus().put(RoadType.NORTH, List.of(LightState.GREEN));

        List<Car> leavingCars = intersection.step();

        assertFalse(leavingCars.contains(car));
    }

    @Test
    void testRedLightProceedRightTurn() {

        Car car = new Car("car2", RoadType.NORTH, Direction.RIGHT, 0);
        intersection.putCar(car);
        intersection.getLightStatus().put(RoadType.NORTH, List.of(LightState.RED));

        intersection.getRoads().get(RoadType.WEST).clear();
        intersection.getRoads().get(RoadType.EAST).clear();

        List<Car> leavingCars = intersection.step();

        assertFalse(leavingCars.contains(car));
    }

    @Test
    void testRedLightNoRightTurnIfLeftBlocked() {
        Car car = new Car("car3", RoadType.NORTH, Direction.RIGHT, 0);
        intersection.putCar(car);
        intersection.getLightStatus().put(RoadType.NORTH, List.of(LightState.RED));

        Car leftCar = new Car("car4", RoadType.WEST, Direction.LEFT, 0);
        intersection.getRoads().get(RoadType.WEST).add(leftCar);

        List<Car> leavingCars = intersection.step();

        assertFalse(leavingCars.contains(car));
    }

    @Test
    void testGreenLightWithOppositeCarBlocked() {
        Car car = new Car("car5", RoadType.NORTH, Direction.FORWARD, 0);
        intersection.putCar(car);
        intersection.getLightStatus().put(RoadType.NORTH, List.of(LightState.GREEN));

        Car oppositeCar = new Car("car6", RoadType.SOUTH, Direction.FORWARD, 0);
        intersection.getRoads().get(RoadType.SOUTH).add(oppositeCar);

        intersection.getRoads().get(RoadType.WEST).clear();

        List<Car> leavingCars = intersection.step();

        assertFalse(leavingCars.contains(car));
    }

    @Test
    void testNoCarLeavesWhenGreenLightAndOppositeCarDirectionBlocked() {
        Car car = new Car("car7", RoadType.NORTH, Direction.FORWARD, 0);
        intersection.putCar(car);
        intersection.getLightStatus().put(RoadType.NORTH, List.of(LightState.GREEN));

        Car oppositeCar = new Car("car8", RoadType.SOUTH, Direction.LEFT, 0);
        intersection.getRoads().get(RoadType.SOUTH).add(oppositeCar);

        List<Car> leavingCars = intersection.step();

        assertFalse(leavingCars.contains(car));
    }
}
