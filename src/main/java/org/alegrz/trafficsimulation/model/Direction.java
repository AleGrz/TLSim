package org.alegrz.trafficsimulation.model;

public enum Direction {
    FORWARD, RIGHT, LEFT;

    static Direction fromRoadType(RoadType entry, RoadType exit) {
        int difference = (exit.ordinal() - entry.ordinal() + RoadType.values().length) % RoadType.values().length;
        return switch (difference) {
            case 2 -> FORWARD;
            case 1 -> RIGHT;
            case 3 -> LEFT;
            default -> throw new IllegalArgumentException("Invalid road transition");
        };
    }
}
