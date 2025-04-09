package org.alegrz.trafficsimulation;

import org.alegrz.trafficsimulation.model.controller.*;
import org.alegrz.trafficsimulation.model.intersection.ConditionalArrowIntersection;
import org.alegrz.trafficsimulation.model.intersection.Intersection;
import org.alegrz.trafficsimulation.model.Simulation;
import org.alegrz.trafficsimulation.model.intersection.NoCollisionIntersection;
import org.alegrz.trafficsimulation.ui.SimulationPresenter;
import org.alegrz.trafficsimulation.util.JsonWriter;

import java.io.File;
import java.io.FileNotFoundException;

import static java.lang.System.exit;


public class Main {

    public static void main(String[] args) {
        if(args.length != 5) {
            System.err.println("Usage: java -jar trafficsimulation.jar <input_file> <output_file> <intersection_type> <controller> <mode>");
            exit(1);
        }

        File inputFile = new File(args[0]);
        File outputFile = new File(args[1]);

        String controller = args[3];

        IntersectionController ic = switch(controller) {
            case "fixed" -> new FixedTimedController();
            case "greedy" -> new GreedyController();
            case "queue" -> new QueueAwareController();
            case "priority" -> new PrioritizedRoadController();
            case "smart" -> new SmartController();
            default -> {
                System.err.println("Invalid controller: " + controller);
                exit(1);
                yield null;
            }
        };

        Intersection intersection = switch(args[2]) {
            case "basic" -> new Intersection(ic);
            case "conditional" -> new ConditionalArrowIntersection(ic);
            case "nocollision" -> {
                if (!args[3].equals("fixed")) {
                    System.err.println("NoCollisionIntersection only supports fixed controller");
                    exit(1);
                    yield null;
                }
                yield new NoCollisionIntersection();
            }
            default -> {
                System.err.println("Invalid intersection type: " + args[2]);
                exit(1);
                yield null;
            }
        };

        if (args[4].equals("gui")) {
            SimulationPresenter.setArguments(inputFile, outputFile, intersection);
            App.run();
        } else if (args[4].equals("cli")) {
            Simulation s = new Simulation(inputFile, intersection, 0);
            JsonWriter writer = new JsonWriter(outputFile);
            s.addCarDepartureListener(writer);
            s.addSimulationFinishedListener(writer);
            s.run();
        } else {
            System.err.println("Invalid mode: " + args[4]);
            exit(1);
        }
    }
}