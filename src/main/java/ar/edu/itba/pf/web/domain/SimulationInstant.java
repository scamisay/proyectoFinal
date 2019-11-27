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

    private List<DroneInfo> drones;

    public SimulationInstant(long simulationId, Long time, Map<Integer, Map<Integer, String>> objects) {
        this.time = time;
        this.simulationId = simulationId;
        this.objects = objects;
    }

    public void setFires(Map<Integer, Map<Integer, String>> fires) {
        this.fires = fires;
    }

    public void setDrones(List<DroneInfo> drones) {
        this.drones = drones;
    }
}
