package ar.edu.itba.pf.web.domain;

import ar.edu.itba.pf.domain.drone.Drone;

public class DroneInfo {
    int id;
    double x;
    double y;

    public DroneInfo() {
    }

    public DroneInfo(Drone drone) {
        this.id = drone.getId();
        this.x = drone.getX();
        this.y = drone.getY();
    }
}
