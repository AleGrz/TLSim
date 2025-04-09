package org.alegrz.trafficsimulation.model.controller;

import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.RoadType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import static org.alegrz.trafficsimulation.model.controller.LightState.*;
import static org.junit.jupiter.api.Assertions.*;

class FixedTimedControllerTest {

    private FixedTimedController controller;
    private EnumMap<RoadType, List<LightState>> lightStatus;
    private EnumMap<RoadType, LinkedList<Car>> roads;

    @BeforeEach
    void setUp() {
        controller = new FixedTimedController();
        lightStatus = new EnumMap<>(RoadType.class);
        roads = new EnumMap<>(RoadType.class);
        for (RoadType type : RoadType.values()) {
            lightStatus.put(type, List.of(RED, RED));
            roads.put(type, new LinkedList<>());
        }
    }

    @Test
    void testGreenPhase1() {
        controller.processLightStates(lightStatus, roads, 1);
        assertLightStates(lightStatus, List.of(GREEN, RED, RED, RED, GREEN, RED, RED, RED));
    }

    @Test
    void testGreenPhase2() {
        controller.processLightStates(lightStatus, roads, 8);
        assertLightStates(lightStatus, List.of(RED, RED, GREEN, RED, RED, RED, GREEN, RED));
    }

    @Test
    void testYellowTransition() {
        controller.processLightStates(lightStatus, roads, 7);
        assertYellowAll(lightStatus);
    }

    @Test
    void testAlternatingPhases() {
        controller.processLightStates(lightStatus, roads, 1);
        assertLightStates(lightStatus, List.of(GREEN, RED, RED, RED, GREEN, RED, RED, RED));
        controller.processLightStates(lightStatus, roads, 7);
        assertYellowAll(lightStatus);
        controller.processLightStates(lightStatus, roads, 8);
        assertLightStates(lightStatus, List.of(RED, RED, GREEN, RED, RED, RED, GREEN, RED));
        controller.processLightStates(lightStatus, roads, 14);
        assertYellowAll(lightStatus);
        controller.processLightStates(lightStatus, roads, 15);
        assertLightStates(lightStatus, List.of(GREEN, RED, RED, RED, GREEN, RED, RED, RED));
    }

    private void assertLightStates(EnumMap<RoadType, List<LightState>> lightStatus,
                                   List<LightState> expectedStates) {
        List<LightState> actualStates = lightStatus.values().stream()
                .flatMap(List::stream)
                .toList();
        assertEquals(expectedStates, actualStates);
    }

    private void assertYellowAll(EnumMap<RoadType, List<LightState>> lightStatus) {
        for (List<LightState> states : lightStatus.values()) {
            assertEquals(List.of(YELLOW, YELLOW), states);
        }
    }
}
