package ar.edu.itba.pf.web.controller;

import ar.edu.itba.pf.domain.drone.Drone;
import ar.edu.itba.pf.domain.engine.Evolver;
import ar.edu.itba.pf.domain.engine.impl.EvolverImpl;
import ar.edu.itba.pf.domain.environment.CellularAutomaton;
import ar.edu.itba.pf.domain.environment.Pair;
import ar.edu.itba.pf.domain.environment.impl.Cell;
import ar.edu.itba.pf.domain.environment.windengine.impl.PolarWind;
import ar.edu.itba.pf.web.SimulationUtil;
import ar.edu.itba.pf.web.domain.DroneGroup;
import ar.edu.itba.pf.web.domain.Simulation;
import ar.edu.itba.pf.web.domain.SimulationInstant;
import ar.edu.itba.pf.web.service.SimulationService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.PI;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = "/simulationApi")
public class SimulationApiController {

    @Autowired
    private SimulationService simulationService;

    @GetMapping("create")
    public void createSimulation(){

        int x=10;
        int y=x;
        int size = x*2+1;
        int TREE_HEIGHT = 5;

        int drones = 120;
        int dronesx = size -1;
        List<Pair> dronesPositions = IntStream.rangeClosed(1, drones).boxed()
                                        .map(i -> new Pair(dronesx,i%size))
                                        .collect(toList());
        double temperatureSelector = .2;
        int minDistanceFromTarget = 4;
        double winningMinTrend = 8;
        double probToGiveUpOnAFight = .15;

        CellularAutomaton a = new SimulationUtil().createTreesFireAndDrones(size, size,
                Arrays.asList(new Pair(2,4), new Pair(5,9), new Pair(2,14), new Pair(0,9)
        ,new Pair(10,9) ,new Pair(10,4) ,new Pair(10,14)
                ),
                dronesPositions, temperatureSelector, minDistanceFromTarget, winningMinTrend, probToGiveUpOnAFight
                );

        //creo la simulacion
        Simulation simulation = new Simulation(a.getWidth(), a.getHeight());
        simulationService.createSimulation(simulation);

        //seteo el guardado de momentos evolucionados
        //Evolver e = new EvolverImpl(a, c -> c.getTime() > 110);
        Evolver e = new EvolverImpl(a, c -> !c.isOnFire());
        e.setTimeConsumer( aut -> {
            SimulationInstant instant = new SimulationInstant(
                    simulation.getId(),
                    aut.getTime(),
                    aut.buildMatrix( c->c.getTopFieldObject() ),
                    aut.getTotalTemperature()
            );

            instant.setFires( aut.buildMatrix( c->c.getTopFireIfExists() ) );

            List<DroneGroup> droneGroups =
                    aut.getDrones().stream()
                    .collect(Collectors.groupingBy( Drone::getCell, Collectors.toList() ))
                    .entrySet().stream()
                    .map(entry -> new DroneGroup(entry.getKey().getX(), entry.getKey().getY(), entry.getValue()))
                    .collect(Collectors.toList());

            instant.setDroneGroups( droneGroups );
            simulationService.addInstant(instant);
        });

        double angle = (1./2)*PI;
        a.addWindStrategy(new PolarWind(50, angle));
        e.start();

        //actualizo la simulacion
        simulation.setEndingTime(a.getTime());
        simulationService.updateSimulation(simulation);

    }

    @GetMapping("findSimulation")
    public String findSimulation(@RequestParam(value = "simulationId") long simulationId){
        return new Gson().toJson(simulationService.findSimulationById(simulationId));
    }

    @GetMapping("findInstants")
    public String findInstants(
            @RequestParam(value = "simulationId") long simulationId,
            @RequestParam(value = "from") long from,
            @RequestParam(value = "offset") long offset ){
        return new Gson().toJson( simulationService.findInstantsByRange(simulationId, from, offset));
    }

}
