package ar.edu.itba.pf.domain.engine.impl;

import ar.edu.itba.pf.domain.engine.Evolver;
import ar.edu.itba.pf.domain.environment.CellularAutomaton;

import java.util.function.Function;

public class EvolverImpl implements Evolver {

    private CellularAutomaton cellularAutomaton;
    private Function<CellularAutomaton, Boolean> stopCondition;

    public EvolverImpl(CellularAutomaton cellularAutomaton, Function<CellularAutomaton, Boolean> stopCondition) {
        this.cellularAutomaton = cellularAutomaton;
        this.stopCondition = stopCondition;
    }

    @Override
    public boolean stopCondition() {
        return stopCondition.apply(cellularAutomaton);
    }

    @Override
    public void start() {
        while (!stopCondition()){
            cellularAutomaton.evolve();
        }
    }

    @Override
    public void continueEvolving(int times) {
        for(int i = 1 ; i <= times; i++){
            cellularAutomaton.evolve();
        }
    }
}
