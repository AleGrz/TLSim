package org.alegrz.trafficsimulation.model.controller;

import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.Direction;
import org.alegrz.trafficsimulation.model.RoadType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.alegrz.trafficsimulation.model.controller.LightState.*;
import static org.junit.jupiter.api.Assertions.*;

class GreedyControllerTest {

    private GreedyController controller;
    private EnumMap<RoadType, List<LightState>> lightStatus;
    private EnumMap<RoadType, LinkedList<Car>> roads;

    @BeforeEach
    void setUp() {
        controller = new GreedyController();
        lightStatus = new EnumMap<>(RoadType.class);
        roads = new EnumMap<>(RoadType.class);
        for (RoadType type : RoadType.values()) {
            lightStatus.put(type, List.of(RED, RED));
            roads.put(type, new LinkedList<>());
        }
    }

    @Test
    void testKeepsCurrentGreenWhenCarsPresent() {
        roads.get(RoadType.NORTH).add(new Car("car1", RoadType.NORTH, Direction.FORWARD, 0));
        controller.processLightStates(lightStatus, roads, 0);
        assertLightStates(lightStatus, Set.of(RoadType.NORTH, RoadType.SOUTH));
    }

    @Test
    void testSwitchesGreenWhenCurrentEmptyAndOthersNot() {
        RoadType left = RoadType.fromDirection(RoadType.NORTH, Direction.LEFT);
        roads.get(left).add(new Car("car2", left, Direction.FORWARD, 0));
        controller.processLightStates(lightStatus, roads, 0);
        assertYellowAll(lightStatus);
    }

    @Test
    void testNoSwitchWhenNoCarsAnywhere() {
        controller.processLightStates(lightStatus, roads, 0);
        assertLightStates(lightStatus, Set.of(RoadType.NORTH, RoadType.SOUTH));
    }

    private void assertLightStates(EnumMap<RoadType, List<LightState>> lightStatus, Set<RoadType> greenGroup) {
        for (RoadType road : RoadType.values()) {
            List<LightState> expected = greenGroup.contains(road) ? List.of(GREEN, GREEN) : List.of(RED, RED);
            assertEquals(expected, lightStatus.get(road));
        }
    }

    private void assertYellowAll(EnumMap<RoadType, List<LightState>> lightStatus) {
        for (List<LightState> states : lightStatus.values()) {
            assertEquals(List.of(YELLOW, YELLOW), states);
        }
    }
}
