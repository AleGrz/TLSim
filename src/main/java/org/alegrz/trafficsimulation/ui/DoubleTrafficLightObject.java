package org.alegrz.trafficsimulation.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.alegrz.trafficsimulation.model.RoadType;
import org.alegrz.trafficsimulation.model.controller.LightState;

import java.util.List;
import java.util.Objects;

public class DoubleTrafficLightObject extends TrafficLightObject {

    private Image combineImagesSideBySide(Image img1, Image img2) {
        int width1 = (int) img1.getWidth();
        int height = (int) img1.getHeight();
        int width2 = (int) img2.getWidth();

        WritableImage combinedImage = new javafx.scene.image.WritableImage(width1 + width2 + 100, height);

        Canvas canvas = new Canvas(width1 + width2 + 100, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.drawImage(img1, 0, 0);

        gc.drawImage(img2, width1 + 100, 0);

        return canvas.snapshot(null, combinedImage);
    }

    public DoubleTrafficLightObject(ImageView bg, RoadType roadType) {
        super(bg, roadType);
        Image left = new Image(Objects.requireNonNull(getClass().getResource("red-left.png")).toExternalForm());
        Image right = new Image(Objects.requireNonNull(getClass().getResource("red-forward.png")).toExternalForm());
        Image combinedImage = combineImagesSideBySide(left, right);
        setImage(combinedImage);
    }

    public void changeState(List<LightState> state) {
        String left = switch (state.get(1)) {
            case RED -> "red-left.png";
            case GREEN -> "green-left.png";
            case YELLOW -> "yellow-left.png";
        };
        String right = switch (state.get(0)) {
            case RED -> "red-forward.png";
            case GREEN -> "green-forward.png";
            case YELLOW -> "yellow-forward.png";
        };
        Image leftImage = new Image(Objects.requireNonNull(getClass().getResource(left)).toExternalForm());
        Image rightImage = new Image(Objects.requireNonNull(getClass().getResource(right)).toExternalForm());
        Image combinedImage = combineImagesSideBySide(leftImage, rightImage);
        setImage(combinedImage);
    }
}
