package org.alegrz.trafficsimulation.util;

import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.listener.CarDepartureListener;
import org.alegrz.trafficsimulation.model.listener.SimulationFinishedListener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import static java.lang.System.exit;


public class JsonWriter implements CarDepartureListener, SimulationFinishedListener {
    JSONArray stepArray = new JSONArray();
    private final File output;

    public JsonWriter(File output) {
        this.output = output;
    }

    @Override
    public void onCarsDepart(List<Car> cars) {
        List<String> carIds = cars.stream().map(Car::id).toList();

        JSONObject stepObject = new JSONObject();
        stepObject.put("leftVehicles", carIds);

        stepArray.put(stepObject);
    }

    @Override
    public void onSimulationFinished() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("stepStatuses", stepArray);
        try (FileWriter writer = new FileWriter(output)) {
            writer.write(jsonObject.toString(4));
            System.out.println("JSON file created successfully: " + output.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error writing JSON file: " + e.getMessage());
            exit(1);
        }
    }
}
