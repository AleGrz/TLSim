package org.alegrz.trafficsimulation.model.controller;

import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.RoadType;

import java.util.*;

public class QueueAwareController extends IntersectionController {


    private RoadType currentGreen = RoadType.NORTH;
    private RoadType maxQueueRoad = RoadType.NORTH;
    private boolean isSwapping = true;

    @Override
    public void processLightStates(EnumMap<RoadType, List<LightState>> lightStatus, EnumMap<RoadType, LinkedList<Car>> roads, int timeStep) {

        if (isSwapping) {
            isSwapping = false;
            currentGreen = maxQueueRoad;
            allowPerRoad(lightStatus, currentGreen);
            notifyLightStateChange(lightStatus);
            return;
        }

        int northSouthQueue = roads.get(RoadType.NORTH).size() + roads.get(RoadType.SOUTH).size();
        int eastWestQueue = roads.get(RoadType.EAST).size() + roads.get(RoadType.WEST).size();

        if (northSouthQueue > eastWestQueue) {
            maxQueueRoad = RoadType.NORTH;
        } else if (northSouthQueue < eastWestQueue) {
            maxQueueRoad = RoadType.EAST;
        }

        if (currentGreen != maxQueueRoad) {
            isSwapping = true;
            yellowAll(lightStatus);
            notifyLightStateChange(lightStatus);
        }
    }
}
