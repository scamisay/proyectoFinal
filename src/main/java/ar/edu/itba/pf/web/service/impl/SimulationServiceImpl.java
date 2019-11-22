package ar.edu.itba.pf.web.service.impl;

import ar.edu.itba.pf.web.domain.Simulation;
import ar.edu.itba.pf.web.domain.SimulationInstant;
import ar.edu.itba.pf.web.repository.SimulationInstantRepository;
import ar.edu.itba.pf.web.repository.SimulationRepository;
import ar.edu.itba.pf.web.service.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SimulationServiceImpl implements SimulationService {

    @Autowired
    private SimulationInstantRepository instantRepository;

    @Autowired
    private SimulationRepository simulationRepository;

    @Override
    public void createSimulation(Simulation simulation) {
        simulation.setId(findNextSequenceIdSegment());
        simulationRepository.save(simulation);
    }

    @Override
    public void addInstant(SimulationInstant instant) {
        instantRepository.save(instant);
    }

    private Long findNextSequenceIdSegment() {
        Pageable p =  PageRequest.of(0, 1, Sort.Direction.DESC, "id");
        try {
            Long sequenceId = simulationRepository.findAll(p).getContent().get(0).getId() + 1;
            return  sequenceId;
        }catch (Exception e){
            return 1L;
        }
    }

    @Override
    public void updateSimulation(Simulation simulation) {
        simulationRepository.save(simulation);
    }

    @Override
    public List<SimulationInstant> findInstantsByRange(long simulationId, long from, long offset) {
        Simulation simulation = simulationRepository.findById(simulationId).get();
        if(simulation == null){
            return null;
        }

        List<SimulationInstant> list = new ArrayList<>();

        for(long t = from; t < (from+offset) && (t <= simulation.getEndingTime()); t++){
            SimulationInstant instant = instantRepository.findBySimulationIdAndTime(simulationId, t);
            list.add(instant);
        }
        return list;
    }

    @Override
    public Simulation findSimulationById(long simulationId) {
        return simulationRepository.findById(simulationId).get();
    }
}
