
# TLSim - Traffic Light Simulator

**TLSim** is a traffic light simulator with both a graphical (JavaFX) and command-line interface. It's designed for experimenting with different traffic light control strategies in various road configurations.

---

## Features

### Graphical Visualization
See vehicle movements and traffic light changes in real-time.

### JSON input and output
Input and output are handled in JSON format. Input uses `addVehicle` and  `step` commands to control the simulation. Output contains the list of vehicle IDs that left the intersection in a given step.

#### Input example
```json
{
  "commands": [
    { 
      "type": "addVehicle",
      "vehicleId": "vehicle1",
      "startRoad": "north",
      "endRoad": "south"
    },
    { "type": "step" }
  ]
}
```

#### Output example

```json
{
  "stepStatuses": [
    {"leftVehicles": ["vehicle1"]}
  ]
}
```


###  Road Layouts
TLSim comes with three built-in road and traffic light arrangements compliant with Polish law to simulate different real-world intersections:

1. **Collision Intersection**\
A standard 4-way intersection with one lane in each direction. Vehicles must yield to oncoming traffic when turning left on green.

2. **Conditional Right-Turn**\
Similar to the collision intersection, but cars can turn right on red if there's no oncoming traffic.

3. **Non-collision Intersection**\
Two-lane configuration (only supports fixed algorithm):
   - Lane 1: Forward and right turns
   - Lane 2: Left turns only

---

### Traffic Light Control Algorithms
TLSim supports multiple traffic light control strategies:
1. **Fixed Time**\
   Traffic lights alternate every 5 turns regardless of traffic.

2. **Greedy**\
   Lights switch only when there are no vehicles currently crossing on green.

3. **Prioritized Road**\
   Similar to fixed time, but favors one direction (North-South) with a 7-turn green vs. a 3-turn green for East-West.

4. **Queue-Aware**\
   Always switches the lights to the direction with the most vehicles waiting.

5. **Smart**\
   Switches the lights to the direction with the highest total wait time, dampened with a constant, with a minimum green light duration to prevent
rapid cycling, and a maximum duration to prevent starvation.


I could add better algorithms than these heuristics, but they would require knowledge of the future traffic of the simulation,
which i think is not along the spirit of this project.
---

## Building and running

```bash
mvn package
```

```bash
java -jar target/tlsim-1.0.jar <input_file> <output_file> <intersection_type> <controller> <mode>
```

- input_file: JSON file with the input commands
- output_file: JSON file to write to
- intersection_type: type of intersection to simulate (basic, conditional, nocollision)
- controller: type of controller to use (fixed, greedy, priority, queue, smart)
- mode: mode of the simulation (gui, cli)