package ar.edu.itba.pf.web.service;

import ar.edu.itba.pf.web.domain.Simulation;
import ar.edu.itba.pf.web.domain.SimulationInstant;

import java.util.List;

public interface SimulationService {

    void createSimulation(Simulation simulation);

    void addInstant(SimulationInstant instant);

    void updateSimulation(Simulation simulation);

    List<SimulationInstant> findInstantsByRange(long simulationId, long from, long offset);

    Simulation findSimulationById(long simulationId);
}
