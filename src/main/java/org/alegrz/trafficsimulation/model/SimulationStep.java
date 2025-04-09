package org.alegrz.trafficsimulation.model;

import org.alegrz.trafficsimulation.model.intersection.Intersection;

import java.util.ArrayList;
import java.util.List;

public class SimulationStep {
    final List<Car> cars = new ArrayList<>();
    private final int stepTime;

    public SimulationStep(int stepTime) {
        this.stepTime = stepTime;
    }

    public void addCar(String id, RoadType origin, RoadType destination) {
        cars.add(new Car(id, origin, Direction.fromRoadType(origin, destination), stepTime));
    }

    public void putCars(Intersection intersection) {
        for (Car car : cars) {
            intersection.putCar(car);
        }
    }
}
