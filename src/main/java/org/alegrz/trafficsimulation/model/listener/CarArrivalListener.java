package org.alegrz.trafficsimulation.model.listener;

import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.RoadType;

import java.util.EnumMap;
import java.util.LinkedList;


public interface CarArrivalListener {
    void onCarsArrive(EnumMap<RoadType, LinkedList<Car>> roads);
}
