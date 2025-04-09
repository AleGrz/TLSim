package org.alegrz.trafficsimulation.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import org.alegrz.trafficsimulation.model.RoadType;
import org.alegrz.trafficsimulation.model.controller.LightState;

import java.util.List;
import java.util.Objects;

public class TrafficLightObject extends ImageView {

    private static final double ROAD_OFFSET_X = 0.3;
    private static final double ROAD_OFFSET_Y = 0.4;

    public TrafficLightObject(ImageView bg, RoadType roadType) {
        super();

        setImage(new Image(Objects.requireNonNull(getClass().getResource("red.png")).toExternalForm()));

        rotateProperty().set(switch(roadType) {
            case NORTH -> 0;
            case SOUTH -> 180;
            case EAST -> 270;
            case WEST -> 90;
        });

        NumberBinding scale = Bindings.min(bg.fitWidthProperty(), bg.fitHeightProperty());

        fitHeightProperty().bind(scale.divide(10));
        translateXProperty().bind(scale.multiply(
                switch (roadType) {
                    case NORTH -> ROAD_OFFSET_X;
                    case SOUTH -> -ROAD_OFFSET_X;
                    case EAST -> ROAD_OFFSET_Y;
                    case WEST -> -ROAD_OFFSET_Y;
                }
        ));

        translateYProperty().bind(scale.multiply(
                switch (roadType) {
                    case NORTH -> ROAD_OFFSET_Y;
                    case SOUTH -> -ROAD_OFFSET_Y;
                    case EAST -> -ROAD_OFFSET_X;
                    case WEST -> ROAD_OFFSET_X;
                }
        ));

        setPreserveRatio(true);
    }

    public void changeState(List<LightState> state) {
        String filename = switch (state.get(0)) {
            case RED -> "red.png";
            case GREEN -> "green.png";
            case YELLOW -> "yellow.png";
        };

        setImage(new Image(Objects.requireNonNull(getClass().getResource(filename)).toExternalForm()));
    }
}


