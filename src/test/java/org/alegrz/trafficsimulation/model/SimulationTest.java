package org.alegrz.trafficsimulation.model;

import org.alegrz.trafficsimulation.model.intersection.NoCollisionIntersection;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class SimulationTest {
    @Test
    void testAddVehicleCommandParsing() {
        File mockFile = new File("examples/input.json");
        Simulation simulation = new Simulation(mockFile, new NoCollisionIntersection(), 1000);
        assertNotNull(simulation);
    }
}