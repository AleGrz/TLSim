package org.alegrz.trafficsimulation.model.controller;

import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.RoadType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.alegrz.trafficsimulation.model.controller.LightState.*;
import static org.junit.jupiter.api.Assertions.*;

class SmartControllerTest {

    private SmartController controller;
    private EnumMap<RoadType, List<LightState>> lightStatus;
    private EnumMap<RoadType, LinkedList<Car>> roads;

    @BeforeEach
    void setUp() {
        controller = new SmartController();
        lightStatus = new EnumMap<>(RoadType.class);
        roads = new EnumMap<>(RoadType.class);
        for (RoadType type : RoadType.values()) {
            lightStatus.put(type, List.of(RED, RED));
            roads.put(type, new LinkedList<>());
        }
    }

    @Test
    void testInitialGreenPhase() {
        controller.processLightStates(lightStatus, roads, 0);
        assertLightStates(lightStatus, Set.of(RoadType.NORTH, RoadType.SOUTH));
    }

    @Test
    void testSwitchingBasedOnQueueSize() {
        roads.get(RoadType.NORTH).add(new Car("car1", RoadType.NORTH, null, 0));
        roads.get(RoadType.SOUTH).add(new Car("car2", RoadType.SOUTH, null, 0));
        roads.get(RoadType.EAST).add(new Car("car3", RoadType.EAST, null, 0));

        controller.processLightStates(lightStatus, roads, 5);
        assertLightStates(lightStatus, Set.of(RoadType.NORTH, RoadType.SOUTH));

        roads.get(RoadType.WEST).add(new Car("car4", RoadType.WEST, null, 0));
        roads.get(RoadType.WEST).add(new Car("car4", RoadType.WEST, null, 0));
        roads.get(RoadType.WEST).add(new Car("car4", RoadType.WEST, null, 0));
        roads.get(RoadType.WEST).add(new Car("car4", RoadType.WEST, null, 0));
        controller.processLightStates(lightStatus, roads, 6);

        assertLightStates(lightStatus, Set.of(RoadType.NORTH, RoadType.SOUTH));
    }

    private void assertLightStates(EnumMap<RoadType, List<LightState>> lightStatus, Set<RoadType> greenGroup) {
        for (RoadType road : RoadType.values()) {
            List<LightState> expected = greenGroup.contains(road) ? List.of(GREEN, GREEN) : List.of(RED, RED);
            assertEquals(expected, lightStatus.get(road));
        }
    }
}
