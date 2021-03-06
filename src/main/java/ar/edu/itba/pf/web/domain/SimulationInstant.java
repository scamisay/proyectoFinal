package ar.edu.itba.pf.web.domain;

import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Map;

public class SimulationInstant {

    @Id
    private String id;

    private long time;

    private long simulationId;

    private Map<Integer, Map<Integer, String>> objects;

    private Map<Integer, Map<Integer, String>> fires;

    private List<DroneGroup> droneGroups;

    private double totalTemperature;

    public SimulationInstant(long simulationId,
                             Long time,
                             Map<Integer, Map<Integer, String>> objects,
                             double totalTemperature) {
        this.time = time;
        this.simulationId = simulationId;
        this.objects = objects;
        this.totalTemperature = totalTemperature;
    }

    public void setFires(Map<Integer, Map<Integer, String>> fires) {
        this.fires = fires;
    }

    public void setDroneGroups(List<DroneGroup> droneGroups) {
        this.droneGroups = droneGroups;
    }

    public List<DroneGroup> getDroneGroups() {
        return droneGroups;
    }
}
