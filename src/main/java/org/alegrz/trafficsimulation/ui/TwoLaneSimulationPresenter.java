package org.alegrz.trafficsimulation.ui;

import javafx.application.Platform;

import javafx.scene.image.Image;

import javafx.util.Pair;
import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.Direction;
import org.alegrz.trafficsimulation.model.RoadType;

import java.net.URL;
import java.util.*;

import static org.alegrz.trafficsimulation.model.Direction.LEFT;

public class TwoLaneSimulationPresenter extends SimulationPresenter {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        bg.setImage(new Image(Objects.requireNonNull(getClass().getResource("twolaneintersection.png")).toExternalForm()));
    }

    @Override
    public void onCarsArrive(EnumMap<RoadType, LinkedList<Car>> roads) {
        for (RoadType roadType : RoadType.values()) {
            List<Car> carsLeft = roads.get(roadType).stream()
                    .filter((Car c) -> c.direction() == Direction.LEFT)
                    .limit(2).toList();

            List<Car> carsForwardRight = roads.get(roadType).stream()
                    .filter(c -> c.direction() != Direction.LEFT)
                    .limit(2).toList();

            for (int i = 0; i<carsLeft.size(); i++) {
                Car c = carsLeft.get(i);
                if (!carsMap.containsKey(c)) {

                    CarObject carObject = new TwoLaneCarObject(bg, c, i==0, s.getTickRate());


                    carsMap.put(c, carObject);
                    Platform.runLater(() -> container.getChildren().add(carObject));
                }
            }

            for (int i = 0; i<carsForwardRight.size(); i++) {
                Car c = carsForwardRight.get(i);
                if (!carsMap.containsKey(c)) {
                    CarObject carObject = new TwoLaneCarObject(bg, c, i==0, s.getTickRate());
                    carsMap.put(c, carObject);
                    Platform.runLater(() -> container.getChildren().add(carObject));
                }
            }
        }
    }

    @Override
    public void onCarsDepart(List<Car> carList) {
        Set<Pair<RoadType, Boolean>> departedMask = new HashSet<>();
        for (Car car : carList) {
            CarObject carObject = carsMap.get(car);
            departedMask.add(new Pair<>(carObject.getCar().origin(), carObject.getCar().direction() == LEFT));
            Thread t = new Thread(() -> {
                try {
                    carObject.drive(s.getTickRate());
                    Thread.sleep(s.getTickRate()* 2L);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                Platform.runLater(() -> container.getChildren().remove(carObject));
            });
            t.start();
        }
        Thread t = new Thread(() -> {

            for (CarObject carObject : carsMap.values()) {
                if (!carObject.isFirst() && departedMask.contains(new Pair<>(carObject.getCar().origin(), carObject.getCar().direction() == LEFT)) ){
                    carObject.animateForward(s.getTickRate()*.0005f, CarObject.FIRST_CAR);
                    carObject.setFirst();
                }
            }
        });
        t.start();
    }

}
