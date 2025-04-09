package org.alegrz.trafficsimulation.util;

import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.Direction;
import org.alegrz.trafficsimulation.model.RoadType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest {

    private File outputFile;
    private JsonWriter jsonWriter;

    @BeforeEach
    void setUp() {
        outputFile = new File("out.json");
        jsonWriter = new JsonWriter(outputFile);
    }

    @Test
    void testOnCarsDepart() {
        Car car1 = new Car("car1", RoadType.NORTH, Direction.FORWARD, 0);
        Car car2 = new Car("car2", RoadType.SOUTH, Direction.FORWARD, 1);
        jsonWriter.onCarsDepart(List.of(car1, car2));

        assertTrue(outputFile.exists());
    }

    @Test
    void testOnSimulationFinished() throws Exception {
        Car car1 = new Car("car1", RoadType.NORTH, Direction.FORWARD, 0);
        jsonWriter.onCarsDepart(List.of(car1));
        jsonWriter.onSimulationFinished();

        assertTrue(outputFile.exists());
        String jsonContent = new String(Files.readAllBytes(Paths.get(outputFile.toURI())));
        assertTrue(jsonContent.contains("leftVehicles"));
        assertTrue(jsonContent.contains("car1"));

    }
}
