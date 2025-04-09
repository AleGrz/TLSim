package org.alegrz.trafficsimulation.model.controller;

import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.Direction;
import org.alegrz.trafficsimulation.model.RoadType;
import org.alegrz.trafficsimulation.model.listener.LightStateChangeListener;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public abstract class IntersectionController {
    private final List<LightStateChangeListener> listeners = new ArrayList<>();

    public abstract void processLightStates(EnumMap<RoadType, List<LightState>> lightStatus, EnumMap<RoadType, LinkedList<Car>> roads, int timeStep);

    public void addLightStateChangeListener(LightStateChangeListener listener) {
        listeners.add(listener);
    }

    protected  void notifyLightStateChange(EnumMap<RoadType, List<LightState>> lightStatus) {
        for (LightStateChangeListener listener : listeners) {
            listener.onLightStateChange(lightStatus);
        }
    }

    protected void yellowAll(EnumMap<RoadType, List<LightState>> lightStatus) {
        for (RoadType road : RoadType.values()) {
            lightStatus.put(road, List.of(LightState.YELLOW, LightState.YELLOW));
        }
    }

    protected void allowPerRoad(EnumMap<RoadType, List<LightState>> lightStatus, RoadType road) {
        for (RoadType r : RoadType.values()) {
            if (r == road || r == RoadType.fromDirection(road, Direction.FORWARD)) {
                lightStatus.put(r, List.of(LightState.GREEN, LightState.GREEN));
            } else {
                lightStatus.put(r, List.of(LightState.RED, LightState.RED));
            }
        }
    }

    protected void setLight(EnumMap<RoadType, List<LightState>> lightStatus,
                  LightState n,
                  LightState s,
                  LightState e,
                  LightState w,
                  LightState nt,
                  LightState st,
                  LightState et,
                  LightState wt) {
        lightStatus.put(RoadType.NORTH, List.of(n, nt));
        lightStatus.put(RoadType.SOUTH, List.of(s, st));
        lightStatus.put(RoadType.EAST, List.of(e, et));
        lightStatus.put(RoadType.WEST, List.of(w, wt));
    }
}
