package ar.edu.itba.pf.web.domain;

import ar.edu.itba.pf.domain.drone.Drone;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DroneGroup {
    int x;
    int y;
    List<DroneInfo> list = new ArrayList<>();

    public DroneGroup() {
    }

    public DroneGroup(int x, int y, List<Drone> drones) {
        this.list = drones.stream().map(DroneInfo::new).collect(Collectors.toList());
        this.x = x;
        this.y = y;
    }
}
