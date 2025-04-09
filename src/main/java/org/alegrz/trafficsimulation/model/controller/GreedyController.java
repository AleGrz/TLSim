package org.alegrz.trafficsimulation.model.controller;

import org.alegrz.trafficsimulation.model.Car;

import org.alegrz.trafficsimulation.model.Direction;
import org.alegrz.trafficsimulation.model.RoadType;
import org.alegrz.trafficsimulation.model.listener.LightStateChangeListener;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public class GreedyController extends IntersectionController {

    private RoadType currentGreen = RoadType.NORTH;

    @Override
    public void processLightStates(EnumMap<RoadType, List<LightState>> lightStatus, EnumMap<RoadType, LinkedList<Car>> roads, int timeStep) {

        boolean greenHasCars = !roads.get(currentGreen).isEmpty() ||
                !roads.get(RoadType.fromDirection(currentGreen, Direction.FORWARD)).isEmpty();
        boolean redHasCars = !roads.get(RoadType.fromDirection(currentGreen, Direction.LEFT)).isEmpty() ||
                !roads.get(RoadType.fromDirection(currentGreen, Direction.RIGHT)).isEmpty();

        if (!greenHasCars && redHasCars) {
            currentGreen = RoadType.fromDirection(currentGreen, Direction.LEFT);
            yellowAll(lightStatus);
            notifyLightStateChange(lightStatus);
            return;
        }

        allowPerRoad(lightStatus, currentGreen);
        notifyLightStateChange(lightStatus);
    }
}