package org.alegrz.trafficsimulation.model.listener;

import org.alegrz.trafficsimulation.model.RoadType;
import org.alegrz.trafficsimulation.model.controller.LightState;

import java.util.EnumMap;
import java.util.List;

public interface LightStateChangeListener {
    void onLightStateChange(EnumMap<RoadType, List<LightState>> lightStatus);
}
