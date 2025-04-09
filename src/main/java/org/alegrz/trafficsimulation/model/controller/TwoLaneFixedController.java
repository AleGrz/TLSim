package org.alegrz.trafficsimulation.model.controller;

import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.RoadType;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import static org.alegrz.trafficsimulation.model.controller.LightState.*;

public class TwoLaneFixedController extends IntersectionController {
    public static final int DURATION = 5;

    @Override
    public void processLightStates(EnumMap<RoadType, List<LightState>> lightStatus, EnumMap<RoadType, LinkedList<Car>> roads, int timeStep) {
        if (timeStep%DURATION != 0) {
            switch((timeStep / DURATION) % 4) {
                case 0 -> setLight(lightStatus, GREEN, GREEN, RED, RED, RED, RED, RED, RED);
                case 1 -> setLight(lightStatus, RED, RED, RED, RED, GREEN, GREEN, RED, RED);
                case 2 -> setLight(lightStatus, RED, RED, GREEN, GREEN, RED, RED, RED, RED);
                case 3 -> setLight(lightStatus, RED, RED, RED, RED, RED, RED, GREEN, GREEN);
            }
        } else {
            yellowAll(lightStatus);
        }
        notifyLightStateChange(lightStatus);
    }

}
