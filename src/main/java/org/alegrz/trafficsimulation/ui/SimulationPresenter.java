package org.alegrz.trafficsimulation.ui;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.StackPane;
import org.alegrz.trafficsimulation.model.controller.*;
import org.alegrz.trafficsimulation.model.intersection.ConditionalArrowIntersection;
import org.alegrz.trafficsimulation.model.intersection.Intersection;
import org.alegrz.trafficsimulation.model.intersection.NoCollisionIntersection;
import org.alegrz.trafficsimulation.model.listener.CarArrivalListener;
import org.alegrz.trafficsimulation.model.listener.CarDepartureListener;
import org.alegrz.trafficsimulation.model.listener.LightStateChangeListener;
import org.alegrz.trafficsimulation.model.Car;
import org.alegrz.trafficsimulation.model.RoadType;
import org.alegrz.trafficsimulation.model.Simulation;
import org.alegrz.trafficsimulation.util.JsonWriter;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;



public class SimulationPresenter implements Initializable, CarArrivalListener, CarDepartureListener, LightStateChangeListener {

    @FXML
    protected Button startButton;

    @FXML
    protected Slider speedSlider;

    @FXML
    protected Label speedLabel;

    @FXML
    protected BorderPane rootPane;

    @FXML
    protected StackPane container;

    @FXML
    protected ImageView bg;

    protected final Map<Car, CarObject> carsMap = new ConcurrentHashMap<>();
    protected final Map<RoadType, TrafficLightObject> trafficLights = new HashMap<>();

    protected Simulation s;

    public static Intersection intersection;
    public static File inputFile;
    public static File outputFile;

    public static void setArguments(File inputFile, File outputFile, Intersection intersection) {
        SimulationPresenter.inputFile = inputFile;
        SimulationPresenter.outputFile = outputFile;
        SimulationPresenter.intersection = intersection;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        intersection.getController().addLightStateChangeListener(this);


        bg.fitHeightProperty().bind(rootPane.heightProperty());

        bg.fitWidthProperty().bind(
                Bindings.createDoubleBinding(() ->
                                Math.max(0, rootPane.getWidth() - 250),
                        rootPane.widthProperty()
                )
        );

        for (RoadType roadType : RoadType.values()) {
            TrafficLightObject trafficLightObject;
            if (intersection instanceof ConditionalArrowIntersection) {
                trafficLightObject = new ConditionalTrafficLightObject(bg, roadType);
            } else if (intersection instanceof NoCollisionIntersection) {
                trafficLightObject = new DoubleTrafficLightObject(bg, roadType);
            } else {
                trafficLightObject = new TrafficLightObject(bg, roadType);

            }
            trafficLights.put(roadType, trafficLightObject);
            Platform.runLater(() -> container.getChildren().add(trafficLightObject));
        }

        speedLabel.setText("1.00s");

        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            speedLabel.setText(decimalFormat.format(newValue) + "s");
        });

    }


    @Override
    public void onCarsArrive(EnumMap<RoadType, LinkedList<Car>> roads) {
        for (RoadType roadType : RoadType.values()) {
            List<Car> cars = roads.get(roadType).stream().limit(2).toList();
            for (int i = 0; i<cars.size(); i++) {
                Car c = cars.get(i);

                if (!carsMap.containsKey(c)) {
                    CarObject carObject = new CarObject(bg, c, i==0, s.getTickRate());
                    carsMap.put(c, carObject);
                    Platform.runLater(() -> container.getChildren().add(carObject));
                }
            }
        }
    }

    @Override
    public void onCarsDepart(List<Car> carList) {
        Set<RoadType> departedMask = new HashSet<>();
        for (Car car : carList) {
            CarObject carObject = carsMap.get(car);
            departedMask.add(carObject.getCar().origin());
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
                if (!carObject.isFirst() && departedMask.contains(carObject.getCar().origin())){
                    carObject.animateForward(s.getTickRate()*.0005f, CarObject.FIRST_CAR);
                    carObject.setFirst();
                }
            }
        });
        t.start();
    }

    @Override
    public void onLightStateChange(EnumMap<RoadType, List<LightState>> lightStatus) {
        for (RoadType roadType : RoadType.values()) {
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep((int)(s.getTickRate()*0.5));
                    List<LightState> lightState = lightStatus.get(roadType);
                    TrafficLightObject trafficLightObject = trafficLights.get(roadType);
                    Platform.runLater(() -> trafficLightObject.changeState(lightState));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            t.start();
        }
    }

    @FXML
    protected void startSimulation() {
        IntersectionController controller = new SmartController();
        controller.addLightStateChangeListener(this);
        s = new Simulation(
                inputFile,
                intersection,
                (int) (speedSlider.getValue() * 1000)
        );

        JsonWriter w = new JsonWriter(outputFile);
        s.addCarDepartureListener(w);
        s.addSimulationFinishedListener(w);
        s.addCarDepartureListener(this);
        s.addCarArrivalListener(this);
        Thread simulationThread = new Thread(s);
        simulationThread.setDaemon(true);
        simulationThread.start();
        startButton.setDisable(true);
    }
}