package ar.edu.itba.pf.domain.engine;

import ar.edu.itba.pf.domain.environment.CellularAutomaton;

import java.util.function.Consumer;

public interface Evolver {

    boolean stopCondition();

    void start();

    void continueEvolving(int times);

    void setTimeConsumer(Consumer<CellularAutomaton> consumer);

}
