package org.alegrz.trafficsimulation.model;

import org.alegrz.trafficsimulation.model.intersection.Intersection;
import org.alegrz.trafficsimulation.model.listener.CarArrivalListener;
import org.alegrz.trafficsimulation.model.listener.CarDepartureListener;
import org.alegrz.trafficsimulation.model.listener.SimulationFinishedListener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

public class Simulation implements Runnable {
    private final Intersection intersection;
    private final List<SimulationStep> steps;
    private final int tickRate;

    private float totalWaitingTime = 0;
    private float totalCars = 0;

    private final List<CarDepartureListener> carDepartureListeners = new ArrayList<>();
    private final List<CarArrivalListener> carArrivalListeners = new ArrayList<>();
    private final List<SimulationFinishedListener> simulationFinishedListeners = new ArrayList<>();


    public Simulation(File jsonFile, Intersection intersection, int tickRate) {
        steps = new ArrayList<>();
        parseJson(jsonFile);
        this.intersection = intersection;
        this.tickRate = tickRate;
    }

    @Override
    public void run() {
        int c = 0;
        for (SimulationStep step : steps) {
            step.putCars(intersection);
            for (CarArrivalListener listener : carArrivalListeners) {
                listener.onCarsArrive(intersection.getRoads());
            }
            List<Car> leavingCars = intersection.step();

            try {
                Thread.sleep(tickRate);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            int finalC = c;
            totalWaitingTime += leavingCars.stream()
                    .map(car -> finalC - car.arrivalTime())
                    .reduce(0, Integer::sum);

            for (CarDepartureListener listener : carDepartureListeners) {
                listener.onCarsDepart(leavingCars);
            }
            c++;
        }

        for (RoadType road: RoadType.values()) {
            List<Car> roadCars = intersection.getRoads().get(road);
            for (Car car : roadCars) {
                totalWaitingTime += c - car.arrivalTime();
            }
        }

        System.out.println("Average waiting time: " + (totalWaitingTime / totalCars));

        for (SimulationFinishedListener listener : simulationFinishedListeners) {
            listener.onSimulationFinished();
        }
    }

    public void addCarDepartureListener(CarDepartureListener listener) {
        carDepartureListeners.add(listener);
    }

    public void addCarArrivalListener(CarArrivalListener listener) {
        carArrivalListeners.add(listener);
    }

    public void addSimulationFinishedListener(SimulationFinishedListener listener) {
        simulationFinishedListeners.add(listener);
    }

    public int getTickRate() {
        return tickRate;
    }

    private void parseJson(File jsonFile) {
        try (Scanner scanner = new Scanner(jsonFile)) {
            StringBuilder jsonContent = new StringBuilder();

            while (scanner.hasNextLine()) {
                jsonContent.append(scanner.nextLine());
            }

            JSONObject input = new JSONObject(jsonContent.toString());
            JSONArray commands = input.getJSONArray("commands");
            int len = commands.length();
            SimulationStep currentStep = new SimulationStep(0);
            for (int i = 0; i < len; i++) {
                JSONObject command = commands.getJSONObject(i);
                String type = command.getString("type");
                if (type.equals("step")) {
                    steps.add(currentStep);
                    currentStep = new SimulationStep(steps.size());
                } else if (type.equals("addVehicle")) {
                    currentStep.addCar(
                            command.getString("vehicleId"),
                            RoadType.parseName(command.getString("startRoad")),
                            RoadType.parseName(command.getString("endRoad"))
                    );
                    totalCars++;
                } else {
                    throw new IllegalArgumentException("Unknown command type: " + type);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + jsonFile.getAbsolutePath());
            exit(1);
        }
    }
}
