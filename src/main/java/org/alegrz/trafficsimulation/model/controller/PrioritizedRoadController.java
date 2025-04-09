package org.alegrz.trafficsimulation.model.controller;

import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.RoadType;
import org.alegrz.trafficsimulation.model.listener.LightStateChangeListener;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import static org.alegrz.trafficsimulation.model.controller.LightState.*;

public class PrioritizedRoadController extends IntersectionController {

    private static final int PRIORITY_DURATION = 7;
    private static final int NON_PRIORITY_DURATION = 3;

    @Override
    public void processLightStates(EnumMap<RoadType, List<LightState>> lightStatus, EnumMap<RoadType, LinkedList<Car>> roads, int timeStep) {
        if (timeStep%(PRIORITY_DURATION+NON_PRIORITY_DURATION) < PRIORITY_DURATION) {
            setLight(lightStatus, GREEN, GREEN, RED, RED, RED, RED, RED, RED);
        } else if (timeStep%(PRIORITY_DURATION+NON_PRIORITY_DURATION) < PRIORITY_DURATION + NON_PRIORITY_DURATION - 1) {
            setLight(lightStatus, RED, RED, GREEN, GREEN, RED, RED, RED, RED);
        } else {
            yellowAll(lightStatus);
        }
        notifyLightStateChange(lightStatus);
    }
}
