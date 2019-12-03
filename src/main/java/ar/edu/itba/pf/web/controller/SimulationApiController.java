package ar.edu.itba.pf.web.controller;

import ar.edu.itba.pf.domain.drone.Drone;
import ar.edu.itba.pf.domain.engine.Evolver;
import ar.edu.itba.pf.domain.engine.impl.EvolverImpl;
import ar.edu.itba.pf.domain.environment.CellularAutomaton;
import ar.edu.itba.pf.domain.environment.Pair;
import ar.edu.itba.pf.domain.environment.PairDouble;
import ar.edu.itba.pf.domain.environment.impl.Cell;
import ar.edu.itba.pf.domain.environment.impl.CellularAutomatonImpl;
import ar.edu.itba.pf.domain.environment.objects.GroundObject;
import ar.edu.itba.pf.domain.environment.objects.combustible.CombustibleObject;
import ar.edu.itba.pf.domain.environment.objects.combustible.GrassCO;
import ar.edu.itba.pf.domain.environment.objects.combustible.TreeCO;
import ar.edu.itba.pf.domain.environment.windengine.impl.PolarWind;
import ar.edu.itba.pf.web.domain.DroneInfo;
import ar.edu.itba.pf.web.domain.Simulation;
import ar.edu.itba.pf.web.domain.SimulationInstant;
import ar.edu.itba.pf.web.service.SimulationService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

        int x=5;
        int y=x;
        int size = x*2+1;
        int TREE_HEIGHT = 5;

        int drones = 5;
        double dronesx = size * .8;
        double dronesdy = size/(drones+1.0);
        List<PairDouble> dronesPositions = IntStream.rangeClosed(1, drones).boxed()
                                        .map(i -> new PairDouble(dronesx,i*dronesdy))
                                        .collect(toList());

        CellularAutomaton a = createTreesFireAndDrones(size, size,
                Arrays.asList(new Pair(2,4), new Pair(5,9)),
                dronesPositions);

        //creo la simulacion
        Simulation simulation = new Simulation(a.getWidth(), a.getHeight());
        simulationService.createSimulation(simulation);

        //seteo el guardado de momentos evolucionados
        Evolver e = new EvolverImpl(a, c -> c.getTime() > 60);
        e.setTimeConsumer( aut -> {
            SimulationInstant instant = new SimulationInstant(
                    simulation.getId(),
                    aut.getTime(),
                    aut.buildMatrix( c->c.getTopFieldObject() ));

            instant.setFires( aut.buildMatrix( c->c.getTopFireIfExists() ) );
            instant.setDrones( aut.getDrones().stream().map(DroneInfo::new).collect(toList()));
            simulationService.addInstant(instant);
        });

        double angle = (1./2)*PI;
        a.addWindStrategy(new PolarWind(50, angle));
        e.start();

        //actualizo la simulacion
        simulation.setEndingTime(a.getTime());
        simulationService.updateSimulation(simulation);

        Cell north = a.getCell(x,y+1);
        Cell south = a.getCell( x, y-1);
        Cell west = a.getCell(x-1,y);
        Cell east = a.getCell(x+1,y);

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


    //todo: hacer libreria

    CellularAutomaton createCellularAutomaton(int width, int height){
        return new CellularAutomatonImpl(width,height);
    }

    private CellularAutomaton createScenario(int width, int height){
        CellularAutomaton cellularAutomaton = createCellularAutomaton(width,height);
        for(int x = 0; x<width; x++){
            for(int y = 0; y < height; y++){
                cellularAutomaton.addElement(x,y,new GroundObject(2));
            }
        }
        return cellularAutomaton;
    }

    private CellularAutomaton createTreesFireAndDrones(int width, int height,
                                                       List<Pair> treesPositions,
                                                       List<PairDouble> dronesPositions) {
        int TREE_HEIGHT = 5;
        CellularAutomaton cellularAutomaton = createScenario(width, height);
        cellularAutomaton.iterate().forEach( pair -> {
            if(treesPositions.contains(pair)){
                CombustibleObject combustibleObject = new TreeCO(10,100, TREE_HEIGHT);
                combustibleObject.setOnFire();
                cellularAutomaton.addElement(pair, combustibleObject);
            }

        });
        int droneId = 1;
        double droneMass = 50;
        double droneWater = 30;
        double droneEnergy = 100;
        for(PairDouble position : dronesPositions){
            Drone drone = new Drone(++droneId, droneMass, droneWater, droneEnergy, 1, position.x,position.y);
            cellularAutomaton.addDrone(drone);
        }
        return cellularAutomaton;
    }

    private CellularAutomaton createManyTreesOnFireAndGrass(int width, int height, List<Pair> treesPositions, int treeHeight){
        CellularAutomaton cellularAutomaton = createScenario(width, height);
        cellularAutomaton.iterate().forEach( pair -> {
            if(treesPositions.contains(pair)){
                CombustibleObject combustibleObject = new TreeCO(10,100, treeHeight);
                combustibleObject.setOnFire();
                cellularAutomaton.addElement(pair, combustibleObject);
            }else {
                cellularAutomaton.addElement(pair, new GrassCO(1,5));
            }
        });
        return cellularAutomaton;
    }

}
