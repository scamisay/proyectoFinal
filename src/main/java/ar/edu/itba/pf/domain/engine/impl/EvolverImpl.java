package ar.edu.itba.pf.domain.engine.impl;

import ar.edu.itba.pf.domain.engine.Evolver;
import ar.edu.itba.pf.domain.environment.CellularAutomaton;

import java.util.function.Consumer;
import java.util.function.Function;

public class EvolverImpl implements Evolver {

    private CellularAutomaton cellularAutomaton;
    private Function<CellularAutomaton, Boolean> stopCondition;
    private Consumer<CellularAutomaton> consumerPerTime;

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
            if(consumerPerTime != null){
                consumerPerTime.accept(cellularAutomaton);
            }
        }
    }

    @Override
    public void continueEvolving(int times) {
        for(int i = 1 ; i <= times; i++){
            cellularAutomaton.evolve();
            if(consumerPerTime != null){
                consumerPerTime.accept(cellularAutomaton);
            }
        }
    }

    @Override
    public void setTimeConsumer(Consumer<CellularAutomaton> consumer) {
        this.consumerPerTime = consumer;
    }
}
