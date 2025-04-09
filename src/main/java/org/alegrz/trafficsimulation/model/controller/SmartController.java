package org.alegrz.trafficsimulation.model.controller;

import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.RoadType;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public class SmartController extends IntersectionController {

    private RoadType currentGreenDirection = RoadType.NORTH;
    private int timeInCurrentGreenPhase = 0;
    private boolean wasYellowLastStep = false;

    public static final int MIN_GREEN_TIME = 5;
    public static final int MAX_GREEN_TIME = 20;
    public static final int DELAY_SWITCH_THRESHOLD = 30;

    private int getWaitingTimeSum(LinkedList<Car> queue, int currentTime) {
        int sum = 0;
        for (Car car : queue) {
            sum += currentTime - car.arrivalTime();
        }
        return sum;
    }

    @Override
    public void processLightStates(EnumMap<RoadType, List<LightState>> lightStatus, EnumMap<RoadType, LinkedList<Car>> roads, int timeStep) {

        if (wasYellowLastStep) {
            wasYellowLastStep = false;
            timeInCurrentGreenPhase = 1;
            allowPerRoad(lightStatus, currentGreenDirection);
            notifyLightStateChange(lightStatus);
            return;
        }

        timeInCurrentGreenPhase++;

        if (timeInCurrentGreenPhase <= MIN_GREEN_TIME && timeInCurrentGreenPhase > 1) {
            allowPerRoad(lightStatus, currentGreenDirection);
            notifyLightStateChange(lightStatus);
            return;
        }

        int northSouthQueue = getWaitingTimeSum(roads.get(RoadType.NORTH), timeStep) +
                getWaitingTimeSum(roads.get(RoadType.SOUTH), timeStep);

        int eastWestQueue = getWaitingTimeSum(roads.get(RoadType.EAST), timeStep) +
                getWaitingTimeSum(roads.get(RoadType.WEST), timeStep);

        int currentGreenQueue;
        int otherQueue;

        if (currentGreenDirection == RoadType.NORTH) {
            currentGreenQueue = northSouthQueue;
            otherQueue = eastWestQueue;
        } else {
            currentGreenQueue = eastWestQueue;
            otherQueue = northSouthQueue;
        }

        boolean shouldSwitch = false;

        if (timeInCurrentGreenPhase > MIN_GREEN_TIME) {
            if (timeInCurrentGreenPhase >= MAX_GREEN_TIME && otherQueue > 0) {
                shouldSwitch = true;
            } else if (otherQueue > currentGreenQueue + DELAY_SWITCH_THRESHOLD) {
                shouldSwitch = true;
            } else if (currentGreenQueue == 0 && otherQueue > 0) {
                shouldSwitch = true;
            }
        }

        if (shouldSwitch) {
            yellowAll(lightStatus);
            currentGreenDirection = (currentGreenDirection == RoadType.NORTH) ? RoadType.EAST : RoadType.NORTH;
            wasYellowLastStep = true;
        } else {
            allowPerRoad(lightStatus, currentGreenDirection);
        }

        notifyLightStateChange(lightStatus);
    }
}
