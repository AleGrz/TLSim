package org.alegrz.trafficsimulation.ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.alegrz.trafficsimulation.model.RoadType;
import org.alegrz.trafficsimulation.model.controller.LightState;

import java.util.List;
import java.util.Objects;

public class ConditionalTrafficLightObject extends TrafficLightObject {
    public ConditionalTrafficLightObject(ImageView bg, RoadType roadType) {
        super(bg, roadType);
        setImage(new Image(Objects.requireNonNull(getClass().getResource("red-conditional.png")).toExternalForm()));
    }

    public void changeState(List<LightState> state) {
        String filename = switch (state.get(0)) {
            case RED -> "red-conditional.png";
            case GREEN -> "green-conditional.png";
            case YELLOW -> "yellow-conditional.png";
        };

        setImage(new Image(Objects.requireNonNull(getClass().getResource(filename)).toExternalForm()));
    }
}
