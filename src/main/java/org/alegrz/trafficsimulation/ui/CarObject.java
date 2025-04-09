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
import org.alegrz.trafficsimulation.model.RoadType;

import java.util.Objects;


public class CarObject extends ImageView {

    public static final float PER_LANE_ADJUSTMENT = .08f;
    public static final float FIRST_CAR = .3f;
    public static final float SECOND_CAR = .45f;

    protected final DoubleProperty x = new SimpleDoubleProperty();
    protected final DoubleProperty y = new SimpleDoubleProperty();
    protected final DoubleProperty rotation = new SimpleDoubleProperty();

    protected final Car car;
    protected boolean isFirst;
    protected RoadType currentRoadType;

    public CarObject(ImageView bg, Car car, boolean isFirst, int tickRate) {
        super();
        this.car = car;
        this.isFirst = isFirst;
        currentRoadType = car.origin();

        setPreserveRatio(true);

        setImage(new Image(Objects.requireNonNull(getClass().getResource(switch (car.direction()) {
                    case FORWARD -> "car-forward.png";
                    case LEFT -> "car-left.png";
                    case RIGHT -> "car-right.png";
                }
        )).toExternalForm()));
        setId(car.id());

        NumberBinding scale = Bindings.min(bg.fitWidthProperty(), bg.fitHeightProperty());

        fitWidthProperty().bind(scale.divide(5));
        translateXProperty().bind(scale.multiply(x));
        translateYProperty().bind(scale.multiply(y));

        rotateProperty().bind(rotation);

        animateInitialPosition(tickRate);

    }

    public Car getCar() {
        return car;
    }

    protected void animateInitialPosition(int tickRate) {
        float offset = isFirst ? FIRST_CAR : SECOND_CAR;

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

        getSidewaysProperty().set(getMovementSign()*PER_LANE_ADJUSTMENT*is_horizontal);
        getForwardProperty().set(getMovementSign()*0.7);

        animateForward(tickRate*.001f, offset);
    }

    public RoadType getCurrentRoadType() {
        return currentRoadType;
    }
    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst() {
        isFirst = true;
    }

    public void drive(int tickRate) throws InterruptedException {

        switch(car.direction()) {
            case FORWARD -> {
                animateForward(tickRate*0.002f, -1);
                Thread.sleep(tickRate*2L);
            }
            case LEFT -> {
                animateTurn(tickRate*0.001f, true);
                Thread.sleep(tickRate);
                currentRoadType = RoadType.fromDirection(currentRoadType, car.direction());
                animateForward(tickRate*0.001f, 1);
            }
            case RIGHT -> {
                animateTurn(tickRate*0.0008f, false);
                Thread.sleep((long) (tickRate*0.8));
                currentRoadType = RoadType.fromDirection(currentRoadType, car.direction());
                animateForward(tickRate*0.001f, 1);
            }
        }

    }

    protected DoubleProperty getForwardProperty() {
        return switch (currentRoadType) {
            case NORTH, SOUTH -> y;
            case EAST, WEST -> x;
        };
    }

    protected DoubleProperty getSidewaysProperty() {
        return switch (currentRoadType) {
            case NORTH, SOUTH -> x;
            case EAST, WEST -> y;
        };
    }

    protected int getMovementSign() {
        return switch (currentRoadType) {
            case WEST, SOUTH -> -1;
            case EAST, NORTH -> 1;
        };
    }

    public void animateForward(float durationSeconds, float dest) {
        KeyFrame keyFrame = new KeyFrame(
                Duration.seconds(durationSeconds),
                new javafx.animation.KeyValue(getForwardProperty(), dest*getMovementSign())
        );

        Timeline timeline = new Timeline(keyFrame);

        Platform.runLater(timeline::play);
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
