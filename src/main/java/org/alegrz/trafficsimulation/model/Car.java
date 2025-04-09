package org.alegrz.trafficsimulation.model;

public record Car(String id, RoadType origin, Direction direction, int arrivalTime) {}
