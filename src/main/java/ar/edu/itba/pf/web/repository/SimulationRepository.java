package ar.edu.itba.pf.web.repository;

import ar.edu.itba.pf.web.domain.Simulation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SimulationRepository extends MongoRepository<Simulation, Long> {

    Page<Simulation> findAll(Pageable pageable);

    List<Simulation> findAll();
}
