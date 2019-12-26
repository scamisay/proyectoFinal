package ar.edu.itba.pf.web;

import ar.edu.itba.pf.domain.drone.Drone;
import ar.edu.itba.pf.domain.engine.Evolver;
import ar.edu.itba.pf.domain.engine.impl.EvolverImpl;
import ar.edu.itba.pf.domain.environment.CellularAutomaton;
import ar.edu.itba.pf.domain.environment.Pair;
import ar.edu.itba.pf.domain.environment.impl.CellularAutomatonImpl;
import ar.edu.itba.pf.domain.environment.objects.GroundObject;
import ar.edu.itba.pf.domain.environment.objects.combustible.CombustibleObject;
import ar.edu.itba.pf.domain.environment.objects.combustible.TreeCO;
import ar.edu.itba.pf.domain.environment.windengine.impl.PolarWind;
import ar.edu.itba.pf.web.domain.DroneGroup;
import ar.edu.itba.pf.web.domain.Simulation;
import ar.edu.itba.pf.web.domain.SimulationInstant;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.PI;
import static java.util.stream.Collectors.toList;

public class SimulationUtil {

    private static long ID = 1;

    public Simulation createSimulation(
            int width, List<Pair> treesPositions,
            int drones, double temperatureSelector,
            int minDistanceFromTarget, double winningMinTrend,
            double probToGiveUpOnAFight
    ){
        int size = width;

        List<Pair> dronesPositions = IntStream.rangeClosed(1, drones).boxed()
                .map(i -> new Pair(size -1,i%size))
                .collect(toList());

        CellularAutomaton a = createTreesFireAndDrones(size, size, treesPositions, dronesPositions,
                temperatureSelector, minDistanceFromTarget, winningMinTrend, probToGiveUpOnAFight);
        //creo la simulacion
        Simulation simulation = new Simulation(a.getWidth(), a.getHeight());
        List<SimulationInstant> instants = new ArrayList<>();

        //seteo el momentos evolucionados
        Evolver e = new EvolverImpl(a, c -> !c.isOnFire());
        e.setTimeConsumer( aut -> {
            SimulationInstant instant = new SimulationInstant(
                    ID++,
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
            instants.add(instant);
        });

        double angle = (1./2)*PI;
        a.addWindStrategy(new PolarWind(50, angle));
        e.start();

        //actualizo la simulacion
        simulation.setEndingTime(a.getTime());
        simulation.addInstants(instants);
        return simulation;
    }

    public CellularAutomaton createTreesFireAndDrones(int width, int height,
                                                       List<Pair> treesPositions,
                                                       List<Pair> dronesPositions,
                                                       double temperatureSelector,
                                                       int minDistanceFromTarget,
                                                       double winningMinTrend,
                                                       double probToGiveUpOnAFight
    ) {
        int TREE_HEIGHT = 5;
        CellularAutomaton cellularAutomaton = createScenario(width, height);
        cellularAutomaton.iterate().forEach( pair -> {
            if(treesPositions.contains(pair)){
                CombustibleObject combustibleObject = new TreeCO(10,100, TREE_HEIGHT);
                combustibleObject.setFireOn();
                cellularAutomaton.addElement(pair, combustibleObject);
            }

        });
        int droneId = 1;
        double droneMass = 50;
        double droneWater = 30;
        double droneEnergy = 100;
        for(Pair position : dronesPositions){
            Drone drone = new Drone(++droneId, droneMass, droneWater, droneEnergy, 1);
            drone.setTemperatureSelector(temperatureSelector);
            drone.setMinDistanceFromTarget(minDistanceFromTarget);
            drone.setWinningMinTrend(winningMinTrend);
            drone.setProbToGiveUpOnAFight(probToGiveUpOnAFight);
            cellularAutomaton.addDrone( position.x,position.y, drone);
        }
        return cellularAutomaton;
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

    CellularAutomaton createCellularAutomaton(int width, int height){
        return new CellularAutomatonImpl(width,height);
    }

}
