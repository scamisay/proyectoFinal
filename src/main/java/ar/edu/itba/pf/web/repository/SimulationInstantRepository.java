package ar.edu.itba.pf.web.repository;

import ar.edu.itba.pf.web.domain.SimulationInstant;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SimulationInstantRepository extends MongoRepository<SimulationInstant, String> {

    SimulationInstant findBySimulationIdAndTime(long simulationId, long time);
}
