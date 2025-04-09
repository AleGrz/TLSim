package org.alegrz.trafficsimulation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.alegrz.trafficsimulation.model.intersection.NoCollisionIntersection;
import org.alegrz.trafficsimulation.ui.SimulationPresenter;
import org.alegrz.trafficsimulation.ui.TwoLaneSimulationPresenter;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        stage.setMinHeight(700);
        stage.setMinWidth(800);
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("simulation.fxml"));
        if (SimulationPresenter.intersection instanceof NoCollisionIntersection) {
            fxmlLoader.setController(new TwoLaneSimulationPresenter());
        } else {
            fxmlLoader.setController(new SimulationPresenter());
        }
        Scene scene = new Scene(fxmlLoader.load(), 920, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        stage.setTitle("Simulation");
        stage.setScene(scene);
        stage.show();
    }

    public static void run() {
        launch();
    }
}