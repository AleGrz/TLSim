package org.alegrz.trafficsimulation.model.listener;

import org.alegrz.trafficsimulation.model.Car;

import java.util.List;

public interface CarDepartureListener {
    void onCarsDepart(List<Car> cars);
}
