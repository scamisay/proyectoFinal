package ar.edu.itba.pf.web.domain;

import ar.edu.itba.pf.domain.drone.Drone;

public class DroneInfo {

    int id;
    double water;
    double energy;

    public DroneInfo() {
    }

    public DroneInfo(Drone drone){
        this.id = drone.getId();
        this.water = drone.getWater();
        this.energy = drone.getEnergy();
    }
}
