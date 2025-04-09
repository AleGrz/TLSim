package org.alegrz.trafficsimulation.ui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.Direction;
import org.alegrz.trafficsimulation.model.RoadType;


public class TwoLaneCarObject extends CarObject {

    public static final float PER_LANE_ADJUSTMENT = .15f;
    public static final float PER_LEFT_LANE_ADJUSTMENT = .0f;
    public static final float FIRST_CAR = .30f;
    public static final float SECOND_CAR = .47f;

    public TwoLaneCarObject(ImageView bg, Car car, boolean isFirst, int tickRate) {
        super(bg, car, isFirst, tickRate);
    }

    protected void animateInitialPosition(int tickRate) {
        float offset = isFirst ? FIRST_CAR : SECOND_CAR;

        float y_initial = car.direction() == Direction.LEFT ? PER_LEFT_LANE_ADJUSTMENT : PER_LANE_ADJUSTMENT;

        rotation.set(switch(car.origin()) {
            case NORTH -> 90;
            case SOUTH -> 270;
            case EAST -> 0;
            case WEST -> 180;
        });

        int is_horizontal = switch (currentRoadType) {
            case NORTH, SOUTH -> 1;
            case EAST, WEST -> -1;
        };

        getSidewaysProperty().set(getMovementSign()*y_initial*is_horizontal);
        getForwardProperty().set(getMovementSign()*0.7);

        animateForward(tickRate*.001f, offset);
    }

    protected void animateTurn(float durationSeconds, boolean is_right) {
        int sign = is_right ? 1 : -1;

        int is_horizontal = switch (currentRoadType) {
            case NORTH, SOUTH -> 1;
            case EAST, WEST -> -1;
        };

        KeyFrame keyFrame = new KeyFrame(
                Duration.seconds(durationSeconds),

                new javafx.animation.KeyValue(getForwardProperty(),
                        -PER_LANE_ADJUSTMENT * getMovementSign() * sign,
                        new CircularInterpolator(CircularInterpolator.Direction.UP)),

                new javafx.animation.KeyValue(getSidewaysProperty(),
                        -FIRST_CAR * getMovementSign() * sign * is_horizontal,
                        new CircularInterpolator(CircularInterpolator.Direction.DOWN)),

                new javafx.animation.KeyValue(rotation,
                        rotation.get() - sign * 90,
                        Interpolator.LINEAR)
        );

        Timeline timeline = new Timeline(keyFrame);

        Platform.runLater(timeline::play);

    }


}
