package org.alegrz.trafficsimulation.model;

import org.alegrz.trafficsimulation.model.controller.FixedTimedController;
import org.alegrz.trafficsimulation.model.intersection.Intersection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulationStepTest {

    private SimulationStep simulationStep;
    private Intersection intersection;

    @BeforeEach
    void setUp() {
        simulationStep = new SimulationStep(0);
        intersection = new Intersection(new FixedTimedController());
    }

    @Test
    void testAddCar() {
        simulationStep.addCar("car1", RoadType.NORTH, RoadType.SOUTH);
        simulationStep.addCar("car2", RoadType.WEST, RoadType.EAST);

        assertEquals(2, simulationStep.cars.size());
    }

    @Test
    void testPutCars() {
        simulationStep.addCar("car1", RoadType.NORTH, RoadType.SOUTH);
        simulationStep.putCars(intersection);

        List<Car> carsOnRoad = intersection.getRoads().get(RoadType.NORTH);
        assertFalse(carsOnRoad.isEmpty());
        assertEquals("car1", carsOnRoad.get(0).id());
    }
}
