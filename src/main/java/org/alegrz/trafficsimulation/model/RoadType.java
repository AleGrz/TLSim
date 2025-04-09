package org.alegrz.trafficsimulation.model;

public enum RoadType {
    NORTH, EAST, SOUTH, WEST;

    public static RoadType parseName(String name) {
        return switch (name.toLowerCase()) {
            case "north" -> NORTH;
            case "south" -> SOUTH;
            case "east" -> EAST;
            case "west" -> WEST;
            default -> throw new IllegalArgumentException("Invalid road name: " + name);
        };
    }

    public static RoadType fromDirection(RoadType start, Direction direction) {
        return switch (direction) {
            case FORWARD -> RoadType.values()[(start.ordinal() + 2) % RoadType.values().length];
            case RIGHT -> RoadType.values()[(start.ordinal() + 1) % RoadType.values().length];
            case LEFT -> RoadType.values()[(start.ordinal() - 1 + RoadType.values().length) % RoadType.values().length];
        };
    }
}
