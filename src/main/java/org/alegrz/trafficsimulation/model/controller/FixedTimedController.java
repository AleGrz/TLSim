package org.alegrz.trafficsimulation.model.controller;

import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.RoadType;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import static org.alegrz.trafficsimulation.model.controller.LightState.*;

public class FixedTimedController extends IntersectionController {

    public static final int DURATION = 7;

    @Override
    public void processLightStates(EnumMap<RoadType, List<LightState>> lightStatus, EnumMap<RoadType, LinkedList<Car>> roads, int timeStep) {
        if (timeStep%DURATION != 0) {
            if ((timeStep/DURATION)%2 == 0) {
                setLight(lightStatus, GREEN, GREEN, RED, RED, RED, RED, RED, RED);
            }
            else {
                setLight(lightStatus, RED, RED, GREEN, GREEN, RED, RED, RED, RED);
            }
        } else {
            yellowAll(lightStatus);
        }
        notifyLightStateChange(lightStatus);
    }
}
