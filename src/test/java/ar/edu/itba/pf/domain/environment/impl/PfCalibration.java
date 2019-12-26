package ar.edu.itba.pf.domain.environment.impl;

import ar.edu.itba.pf.domain.environment.Pair;
import ar.edu.itba.pf.web.SimulationUtil;
import ar.edu.itba.pf.web.domain.Simulation;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class PfCalibration {

    @Test
    public void test1(){

        int size = 11;


        List<Pair> trees = Arrays.asList(new Pair(2,4), new Pair(5,9), new Pair(2,14), new Pair(0,9)
                ,new Pair(10,9) ,new Pair(10,4) ,new Pair(10,14)
        );

        double temperatureSelector = .2;
        int minDistanceFromTarget = 4;
        double winningMinTrend = 8;
        double probToGiveUpOnAFight = .15;

        int samples = 10;
        List<DataFrame> dataFrames = new ArrayList<>();
        for(int drones = 5 ; drones <= 200 ; drones+=5){
            List<Simulation> simulations = new ArrayList<>();
            for(int i = 0 ; i < samples ; i++){
                simulations.add(
                        new SimulationUtil().createSimulation(size, trees, drones,
                                temperatureSelector, minDistanceFromTarget, winningMinTrend, probToGiveUpOnAFight)
                );
            }
            dataFrames.add(new DataFrame(drones, simulations));
        }

        String output = dataFrames.stream().map(DataFrame::toString).collect(Collectors.joining("\n"));
        System.out.println(output);
    }

    class DataFrame{
        int drones;
        MinAvgMax endingTime;
        MinAvgMax groups;

        public DataFrame(int drones, List<Simulation> simulations) {
            this.drones = drones;
            this.endingTime = findEndingTimeMam(simulations);
            this.groups = findGroupsMam(simulations);
        }


        @Override
        public String toString() {
            return String.format("%d , %s , %s",drones,endingTime.toString(), groups.toString());
        }

        private MinAvgMax findEndingTimeMam(List<Simulation> simulations){
            return new MinAvgMax(simulations.stream().mapToDouble(Simulation::getEndingTime).min().getAsDouble(),
                     simulations.stream().mapToDouble(Simulation::getEndingTime).average().getAsDouble(),
                    simulations.stream().mapToDouble(Simulation::getEndingTime).max().getAsDouble());
        }

        private MinAvgMax findGroupsMam(List<Simulation> simulations){
            return new MinAvgMax(
                    simulations.stream().map(Simulation::getInstants).flatMap(ii->ii.stream()).mapToInt(i->i.getDroneGroups().size()).min().getAsInt(),
                    simulations.stream().map(Simulation::getInstants).flatMap(ii->ii.stream()).mapToInt(i->i.getDroneGroups().size()).average().getAsDouble(),
                    simulations.stream().map(Simulation::getInstants).flatMap(ii->ii.stream()).mapToInt(i->i.getDroneGroups().size()).max().getAsInt());
        }
    }

    class MinAvgMax {
        double min;
        double avg;
        double max;

        public MinAvgMax(double min, double avg, double max) {
            this.min = min;
            this.avg = avg;
            this.max = max;
        }

        @Override
        public String toString() {
            return String.format("%5.2f, %5.2f, %5.2f",min, avg, max);
        }
    }
}
